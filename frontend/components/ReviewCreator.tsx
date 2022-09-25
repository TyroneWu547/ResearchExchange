import React, { useState } from "react";
import { Document, Page, PDFPageProxy } from "react-pdf";
import { SubmitHandler, useFieldArray, useForm } from "react-hook-form";
import { FaTimes } from "react-icons/fa";

import type { ArticlePost, PdfSelection, ReviewStatus, SelectionRect } from "../types";
import { getAuthToken, postReview } from "../utils/api";
import { statusIcon } from "../utils/jsxUtils";
import PdfViewerLayout, { getUniqueId, Highlight } from "./PdfViewerLayout";

import styles from "../styles/PdfViewer.module.scss";
import { allStatuses } from "../utils/constants";
import useUser from "../hooks/useUser";
import { useRouter } from "next/router";

interface Inputs {
  mainComment: string;
  status: ReviewStatus;
  inlineComments: {
    value: string;
  }[];
}

interface Props {
  articleInfo: ArticlePost;
}

export default function ReviewCreator({ articleInfo }: Props) {
  const router = useRouter();
  const user = useUser();

  const { id: articleId, pdfUrl } = articleInfo;

  const { register, control, handleSubmit } = useForm<Inputs>();
  const inlineCommentsFieldArray = useFieldArray({ control, name: "inlineComments" });

  const [selectedHighlight, setSelectedHighlight] = useState<PdfSelection | null>(null);
  const [addCommentBtnPos, setAddCommentBtnPos] = useState<{ x: number; y: number; } | null>(null);
  const [highlights, setHighlights] = useState<Highlight[]>([]);

  const onSubmit: SubmitHandler<Inputs> = async (data) => {
    const inlineComments = data.inlineComments
      .map(({ value: comment }, i) => ({ comment, selection: highlights[i].selection }))
      .map(x => ({ content: x.comment, ...x.selection }));

    await postReview(articleId, {
      author: user.sub,
      content: data.mainComment,
      inlineComments,
      status: data.status
    }, getAuthToken());
    router.push(`/articles/${articleId}`);
  };

  function handleSelectText(event: React.MouseEvent<Element, MouseEvent>, page: PDFPageProxy) {
    const selection = window.getSelection();
    if (selection !== null && selection.rangeCount > 0 && selection.toString().trim() !== "") {
      const pageRect = event.currentTarget.getBoundingClientRect();

      const selectionDOMRectLists = selection.getRangeAt(0).getClientRects();
      const selectionRects: SelectionRect[] = [];
      for (let i = 0; i < selectionDOMRectLists.length; i++) {
        const domRect = selectionDOMRectLists[i];
        const x = domRect.left - pageRect.left;
        const y = domRect.top - pageRect.top;
        selectionRects.push({ x: x, y: y, width: domRect.width, height: domRect.height });
      }

      const highlight: PdfSelection = {
        pageNum: page.pageNumber,
        selectedContent: selection.toString(),
        highlightSections: selectionRects
      };
      setSelectedHighlight(highlight);

      const boundingRect = selection.getRangeAt(0).getBoundingClientRect();
      setAddCommentBtnPos({ x: boundingRect.x + (boundingRect.width / 2) - 40, y: boundingRect.y - 25 });
    } else {
      setAddCommentBtnPos(null);
    }
  }

  function addComment() {
    if (selectedHighlight) {
      const newHLId = getUniqueId(selectedHighlight);

      const insertIndex = highlights.findIndex(hl => newHLId > hl.uniqueId);
      inlineCommentsFieldArray.insert(insertIndex, { value: "" });

      setHighlights([
        ...highlights.slice(0, insertIndex),
        {
          uniqueId: newHLId,
          selection: selectedHighlight,
          commentRef: React.createRef(),
          highlightBoxRef: React.createRef()
        },
        ...highlights.slice(insertIndex)
      ]);
      setSelectedHighlight(null);
      setAddCommentBtnPos(null);
    }
  }

  function removeComment(index: number) {
    inlineCommentsFieldArray.remove(index);
    setHighlights([...highlights.slice(0, index), ...highlights.slice(index + 1)]);
  }

  function scrollToHighlight(highlight: Highlight) {
    const ref = highlights.find(hl => hl.uniqueId === highlight.uniqueId)!.highlightBoxRef;
    const firstHighlight = ref.current!.getElementsByClassName(styles.highlight)[0];
    firstHighlight.scrollIntoView({ behavior: "smooth", block: "center" });
    ref.current!.classList.add(styles.blinkAnimationHighlight);
    setTimeout(() => ref.current && ref.current.classList.remove(styles.blinkAnimationHighlight), 1500);
  }

  const statusButtonClass = (status: ReviewStatus) => ({
    "Approved": styles.acceptStatusButton,
    "Needs Work": styles.needsWorkStatusButton,
    "Rejected": styles.rejectStatusButton
  }[status]);

  const commentsBarContent = (
    <form className={styles.createReviewForm} onSubmit={handleSubmit(onSubmit)}>
      <div>
        <h2>Review</h2>
        <div className={styles.commentBarItem}>
          <textarea
            className={styles.mainCommentTextArea}
            placeholder="Write a review. Be as thorough as possible."
            {...register("mainComment", { required: true })}
          />
        </div>

        {highlights.length > 0
          ? (
            <>
              <h2>Inline Comments</h2>
              {highlights.map((hl, i) => (
                <>
                  <div key={hl.uniqueId} className={styles.commentBarItem} ref={hl.commentRef}>
                    <div className={styles.referencingSection} onClick={() => scrollToHighlight(hl)}>
                      <p>
                        <b>Referencing: </b>
                        {hl.selection.selectedContent.length > 110 ? hl.selection.selectedContent.substring(0, 110) + "..." : hl.selection.selectedContent}
                      </p>
                      <button onClick={() => removeComment(i)}><FaTimes /></button>
                    </div>
                    <textarea
                      className={styles.inlineCommentTextArea}
                      placeholder="Comment on this portion of the document."
                      {...register(`inlineComments.${i}.value`, { required: true })}
                    />
                  </div>
                  {i !== highlights.length - 1 && <hr />}
                </>
              ))}
            </>
          ) : (
            <p className={styles.commentBarItem}>Highlight areas of the document to make inline comments.</p>
          )}
      </div>

      <div className={`${styles.submitReviewSection} ${styles.commentBarItem}`}>
        <h4 className={styles.selectStatus}>Select an approval status:</h4>
        <div className={styles.statuses}>
          {allStatuses.map(status => (
            <div key={status} className={styles.statusContainer}>
              <input type="radio" id={status} value={status} {...register("status")} />
              <label htmlFor={status} className={`${styles.status} ${statusButtonClass(status)}`}>{statusIcon(status)} {status}</label>
            </div>
          ))}
        </div>
        <input type="submit" value="Submit Review" className={styles.submitReview} />
      </div>
    </form>
  );

  const extraDocumentContent = addCommentBtnPos && (
    <button
      className={styles.addCommentBtn}
      style={{ left: addCommentBtnPos.x, top: addCommentBtnPos.y }}
      onClick={addComment}
    >
      Add Comment
    </button>
  );

  return (
    <PdfViewerLayout
      pdfUrl={pdfUrl}
      highlights={highlights}
      commentsBarContent={commentsBarContent}
      extraDocumentContent={extraDocumentContent}
      onPageClick={handleSelectText}
    />
  );
}