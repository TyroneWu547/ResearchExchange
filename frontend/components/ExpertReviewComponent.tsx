import Link from "next/link";
import dynamic from "next/dynamic";
import { Document, Page } from "react-pdf";

import type { ArticlePost, ExpertReview, ExpertReview as ExpertReviewType, ReviewStatus } from "../types";
import TopLevelComment from "./TopLevelComment";
import { statusClass, statusIcon } from "../utils/jsxUtils";

import styles from "../styles/ExpertReview.module.scss";
import EditReviewStatus from "./EditReviewStatus";

const DocumentPreview = dynamic(() => import('./DocumentPreview'), { ssr: false });

interface Props {
  articleInfo: ArticlePost;
  review: ExpertReviewType;
}

export default function ExpertReviewComponent({ articleInfo, review }: Props) {
  return (
    <div key={review.author.username} className={styles.expertReview}>
      <div className={styles.expertReviewMetadata}>
        {statusIcon(review.status, `${statusClass(review.status)} ${styles.statusIcon}`)}
        <p className={`${styles.status} ${statusClass(review.status)}`}>{review.status}</p>
        {!articleInfo.approved && <EditReviewStatus review={review} statusClass={styles.editStatusButton} />}
      </div>

      <TopLevelComment comment={review} articleId={articleInfo.id} compressed={false} />

      {review.inlineComments && review.inlineComments.length > 0 && (
        <Link href={`/view-review/${articleInfo.id}/${review.id}`}>
          <a className={styles.documentPreviewContainer} >
            <DocumentPreview className={styles.documentPreview} pdfUrl={articleInfo.pdfUrl} height={200} />
            <div className={styles.inlineCommentsNote}>
              This review references specific portions of the paper.
              <div className={styles.viewDoc}>View Document ➥</div>
            </div>
          </a>
        </Link>
      )}
    </div>
  );
}