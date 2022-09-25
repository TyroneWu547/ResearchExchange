import { useState } from "react";
import { Document, Page, PDFPageProxy } from "react-pdf";

import { PdfSelection } from "../types";

import styles from "../styles/PdfViewer.module.scss";

export interface Highlight {
  uniqueId: string,
  selection: PdfSelection,
  commentRef: React.RefObject<HTMLDivElement>,
  highlightBoxRef: React.RefObject<HTMLDivElement>
}

export const getUniqueId = (hl: PdfSelection) => `${hl.pageNum},${hl.highlightSections.map(rect => `${rect.y},${rect.x}`).join(",")}`;

interface Props {
  pdfUrl: string;
  highlights: Highlight[];
  extraDocumentContent?: React.ReactElement | null;
  commentsBarContent: React.ReactElement;
  onPageClick?: (event: React.MouseEvent<Element, MouseEvent>, page: PDFPageProxy) => void;
}

export default function PdfViewerLayout({ pdfUrl, highlights, extraDocumentContent, commentsBarContent, onPageClick }: Props) {
  const [numPages, setNumPages] = useState<number | null>(null);

  function scrollToInlineComment(highlight: Highlight) {
    const ref = highlights.find(hl => hl.uniqueId === highlight.uniqueId)!.commentRef;
    ref.current!.scrollIntoView({ behavior: "smooth", block: "center" });
    ref.current!.classList.add(styles.blinkAnimationComment);
    setTimeout(() => ref.current && ref.current.classList.remove(styles.blinkAnimationComment), 1000);
  }

  return (
    <div className={styles.documentCommenter}>
      <div className={styles.commentsBar}>
        {commentsBarContent}
      </div>

      <div className={styles.pdfContainer}>
        <Document file={pdfUrl} onLoadSuccess={({ numPages }) => setNumPages(numPages)}>
          {extraDocumentContent}

          {numPages !== null && ([...Array(numPages)].map((_, i) => (
            <div key={i} className={styles.pageContainer}>
              <Page
                key={i}
                className={styles.pdfPage}
                pageNumber={i + 1}
                width={1000}
                onClick={onPageClick || (() => { })}
              />
              {highlights.filter(hl => hl.selection.pageNum === i + 1).map(hl => (
                <div
                  key={hl.selection.selectedContent}
                  ref={hl.highlightBoxRef}
                  className={styles.highlightGroup}
                  onClick={() => scrollToInlineComment(hl)}
                >
                  {hl.selection.highlightSections.map(rect => (
                    <div
                      key={`${rect.x},${rect.y}`}
                      className={styles.highlight}
                      style={{ left: rect.x, top: rect.y, width: rect.width, height: rect.height }}
                    ></div>
                  ))}
                </div>
              ))}
            </div>
          )))}
        </Document>
      </div>
    </div>
  );
}