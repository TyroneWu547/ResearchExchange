import { Document, Page } from "react-pdf";

interface Props {
  pdfUrl: string;
  height: number;
  className?: string;
}

const DocumentPreview = ({ pdfUrl, height, className }: Props) => (
  <Document file={pdfUrl} className={className}>
    <Page pageNumber={1} height={height} />
  </Document>
);

export default DocumentPreview;