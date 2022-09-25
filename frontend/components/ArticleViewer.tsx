import { useState } from "react";
import { Document, Page } from "react-pdf";

import styles from "../styles/ArticleViewer.module.scss";

interface Props {
  pdfUrl: string;
}

export default function ArticleViewer({ pdfUrl }: Props) {
  const [numPages, setNumPages] = useState<number | null>(null);

  return (
    <div className={styles.pdfContainer}>
      <Document file={pdfUrl} onLoadSuccess={({ numPages }) => setNumPages(numPages)}>
        {numPages !== null && ([...Array(numPages)].map((_, i) => (
          <div key={i} className={styles.pageContainer}>
            <Page
              key={i}
              className={styles.pdfPage}
              pageNumber={i + 1}
              width={1000}
            />
          </div>
        )))}
      </Document>
    </div>
  );
}