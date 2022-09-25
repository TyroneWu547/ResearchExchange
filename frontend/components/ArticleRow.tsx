import Link from "next/link";

import { ArticlePost, ArticlePreview } from "../types";
import { allStatuses } from "../utils/constants";
import { statusClass, statusIcon } from "../utils/jsxUtils";

import styles from "../styles/ArticleRow.module.scss";
import { formatDate } from "../utils/formatting";

interface Props {
  article: ArticlePreview;
}

export default function ArticleRow({ article }: Props) {
  return (
    <div className={styles.article}>
      <div className={styles.titleSection}>
        <Link href={`/articles/${article.id}`}>
          <a className={styles.articleTitle}>{article.name}</a>
        </Link>
        <p className={styles.datePosted}>Posted on {formatDate(article.datePosted)}</p>
      </div>

      <div>
        <p className={styles.articleAbstract}>{article.articleAbstract}</p>
        <p className={styles.subject}>{article.mainField} ❱ {article.subField}</p>

        <ul className={styles.tags}>
          {article.tags && article.tags.map(tag => <li key={tag}>{tag}</li>)}
        </ul>
      </div>

      <div className={styles.scoreAndReviews}>
        <div>
          <b>Vote Score:</b> {article.score}
        </div>

        <b className={styles.expertReviews}>
          {article.approved ? (
            <span className={`${styles.statusInfo} ${statusClass("Approved")}`}>
              {statusIcon("Approved", styles.statIcon)}
              Approved for publication
            </span>
          ) : article.expertReviews && article.expertReviews.length > 0 && (
            <>
              {article.expertReviews.length} Expert Review{article.expertReviews.length !== 1 ? "s" : ""}:{' '}
              {allStatuses.map(status => article.expertReviews.includes(status) && (
                <span key={status} className={`${styles.statusInfo} ${statusClass(status)}`}>
                  {statusIcon(status, styles.statIcon)}
                  {article.expertReviews.filter(r => r === status).length} {status}
                </span>
              ))}
            </>
          )}
        </b>
      </div>
    </div >
  );
}