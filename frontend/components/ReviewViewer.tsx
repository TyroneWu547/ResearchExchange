import React from "react";

import type { ArticlePost, ExpertReview } from "../types";
import { statusClass, statusIcon } from "../utils/jsxUtils";
import TopLevelComment from "./TopLevelComment";
import PdfViewerLayout, { getUniqueId, Highlight } from "./PdfViewerLayout";
import EditReviewStatus from "./EditReviewStatus";

import styles from "../styles/PdfViewer.module.scss";
import Link from "next/link";
import Vote from "./Vote";

interface Props {
  articleInfo: ArticlePost;
  review: ExpertReview;
}

export default function ReviewViewer({ articleInfo, review }: Props) {
  const { pdfUrl, id: articleId } = articleInfo;

  const highlights = (review.inlineComments === undefined) ? [] : review.inlineComments.map((comment): Highlight => ({
    uniqueId: getUniqueId(comment),
    selection: comment,
    commentRef: React.createRef(),
    highlightBoxRef: React.createRef()
  }));

  function scrollToHighlight(highlight: Highlight) {
    const ref = highlights.find(hl => hl.uniqueId === highlight.uniqueId)!.highlightBoxRef;
    const firstHighlight = ref.current!.getElementsByClassName(styles.highlight)[0];
    firstHighlight.scrollIntoView({ behavior: "smooth", block: "center" });
    ref.current!.classList.add(styles.blinkAnimationHighlight);
    setTimeout(() => ref.current && ref.current.classList.remove(styles.blinkAnimationHighlight), 1500);
  }

  const commentsBarContent = (
    <>
      <h2>
        <div style={{ display: "flex" }}>
          <Vote post={review} article={false} compressed={false} />
          <div>
            <Link href={`/users/${review.author.username}`}>
              <a className={styles.link}>{review.author.username}</a>
            </Link>&apos;s Review of{" "}
            <Link href={`/articles/${articleInfo.id}`}>
              <a className={styles.link}>{articleInfo.name}</a>
            </Link>

            <div className={styles.reviewStatus}>
              Status: <span className={statusClass(review.status)}>{review.status} {statusIcon(review.status)}</span>
            </div>
            {!articleInfo.approved && <EditReviewStatus review={review} statusClass={styles.editStatusButton} />}
          </div>
        </div>

      </h2>
      <div className={styles.commentBarItem}>
        <TopLevelComment comment={review} articleId={articleId} compressed={true} />
      </div>

      {review.inlineComments !== undefined && (
        <>
          <h2>Inline Comments</h2>
          {review.inlineComments.map((comment, i) => (
            <>
              <div key={comment.id} className={styles.commentBarItem} ref={highlights[i].commentRef}>
                <div className={styles.referencingSection} onClick={() => scrollToHighlight(highlights[i])}>
                  <p>
                    <b>Referencing: </b>
                    {highlights[i].selection.selectedContent.length > 110 ? highlights[i].selection.selectedContent.substring(0, 110) + "..." : highlights[i].selection.selectedContent}
                  </p>
                </div>
                <TopLevelComment comment={comment} articleId={articleId} compressed />
              </div>
              {i !== review.inlineComments.length - 1 && <hr />}
            </>
          ))}
        </>
      )}
    </>
  );

  return (
    <PdfViewerLayout
      pdfUrl={pdfUrl}
      highlights={highlights}
      commentsBarContent={commentsBarContent}
    />
  );
}