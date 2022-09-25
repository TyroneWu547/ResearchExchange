import { GetServerSideProps, NextPage } from "next";
import dynamic from "next/dynamic";
import Link from "next/link";
import { IconContext } from "react-icons";
import { FaCode, FaDatabase, FaLink } from "react-icons/fa";

import { ArticlePost, ExpertReview } from "../../types";
import Vote from "../../components/Vote";
import ExpertReviewComponent from "../../components/ExpertReviewComponent";
import TopLevelComment from "../../components/TopLevelComment";
import CardLayout from "../../components/CardLayout";
import { getArticleInfoWithComments } from "../../utils/api";
import CommentMaker from "../../components/CommentMaker";
import useUser from "../../hooks/useUser";
import { formatDate } from "../../utils/formatting";
import { splitParagraphs, statusClass, statusIcon } from "../../utils/jsxUtils";

import styles from "../../styles/Article.module.scss";
import { useMemo } from "react";

const DocumentPreview = dynamic(() => import("../../components/DocumentPreview"), { ssr: false });

interface Props {
  articleInfo: ArticlePost;
}

function getNumApprovedReviews(expertReviews: ExpertReview[]) {
  var approved = 0;
  expertReviews.forEach(review => {
    if (review.status == "Approved") {
      approved++;
    }
  });
  return approved;
}

const Article: NextPage<Props> = ({ articleInfo }) => {
  const user = useUser();

  const { name: title, articleAbstract, pdfUrl, repoUrl, dataUrl, links: extraLinks, mainField, subField, tags, datePosted, expertReviews, comments, approved, authors } = articleInfo;

  const numApprovedReviews = getNumApprovedReviews(expertReviews);

  const canWriteReview = useMemo(
    () => user && user.roles.includes("Expert") && !expertReviews.some(r => r.author.username === user.sub) && numApprovedReviews < 3,
    [user, expertReviews, numApprovedReviews]
  );

  return (
    <CardLayout width={1200}>
      <div className={styles.article}>
        <Vote post={articleInfo} compressed={false} article={true} />

        <div className={styles.articleInfo}>
          <h1 className={styles.articleTitle}>{title}</h1>
          <div>
            <span className={styles.subject}>{mainField} ❱ {subField}</span>
            {!approved ? null : (
              <span className={`${styles.approvedMessage} ${statusClass("Approved")}`}>
                {statusIcon("Approved", styles.approvedStatIcon)}
                Approved for publication
              </span>
            )}
            <span></span>
          </div>

          <p className={styles.abstract}><b>Abstract:</b> {splitParagraphs(articleAbstract)}</p>

          <div className={styles.links}>
            <div>
              <IconContext.Provider value={{ className: styles.linkIcon }}><FaCode /></IconContext.Provider>
              <b className={styles.linkMargin}>Source Code URL:</b> {repoUrl ? <a href={repoUrl}>{repoUrl}</a> : <span className={styles.noUrlProvided}>(none provided)</span>}
            </div>
            <div>
              <IconContext.Provider value={{ className: styles.linkIcon }}><FaDatabase /></IconContext.Provider>
              <b className={styles.linkMargin}>Dataset URL:</b> {dataUrl ? <a href={dataUrl}>{dataUrl}</a> : <span className={styles.noUrlProvided}>(none provided)</span>}
            </div>
            {extraLinks && extraLinks.map(url => (
              <div key={url}>
                <IconContext.Provider value={{ className: styles.linkIcon }}><FaLink /></IconContext.Provider>
                <b className={styles.linkMargin}>Additional URL:</b> <a href={url}>{url}</a>
              </div>
            ))}
          </div>

          <p><b>Posted on:</b> {formatDate(datePosted)}</p>

          {tags && tags.length > 0 && (
            <ul className={styles.multiElement}>
              <b className={styles.multiLabel}>Tags:</b>
              {tags.map(tag => <li key={tag}>{tag}</li>)}
            </ul>
          )}

          {(approved && authors) ? (
            <div>
              <b>Authors:</b>{' '}
              {authors.map((author, i) => (
                <span key={author.username}>
                  {author.name} (<Link href={`/users/${author.username}`}><a className={styles.authorLink}>{author.username}</a></Link>)
                  {i !== authors.length - 1 && ", "}
                </span>
              ))}
            </div>
          ) : null}
        </div>

        <Link href={`/view-article/${articleInfo.id}`}>
          <a className={styles.documentPreview}>
            <DocumentPreview pdfUrl={pdfUrl} height={460} />
            <hr />
            <p className={styles.viewArticle}>View article</p>
          </a>
        </Link>
      </div>

      <hr className={styles.sectionDivider} />

      {expertReviews.length > 0 && (
        <>
          <h2>Expert Reviews</h2>
          <div className={styles.items}>
            {expertReviews.map(review => <ExpertReviewComponent key={review.id} articleInfo={articleInfo} review={review} />)}
          </div>
        </>
      )}

      {canWriteReview && <Link href={`/create-review/${articleInfo.id}`}><a className={styles.createContentBtn}>Write a Review</a></Link>}

      {(expertReviews.length > 0 || canWriteReview) && (
        <hr className={styles.sectionDivider} />
      )}

      {comments.length > 0 && (
        <>
          <h2>Comments</h2>
          <div className={styles.items}>
            {comments.map(comment => <TopLevelComment key={comment.id} comment={comment} articleId={articleInfo.id} compressed={false} />)}
          </div>
          <hr className={styles.sectionDivider} />
        </>
      )}

      {user ? (
        <>
          <h3 className={styles.writeCommentTitle}>Write a comment</h3>
          <CommentMaker articleId={articleInfo.id} rootPost={null} followupReplyingTo={null} />
        </>
      ) : (
        <Link href="/login">
          <a className={styles.createContentBtn}>Log in to write a comment</a>
        </Link>
      )}
    </CardLayout>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const params = context.params! as { articleId: string };
  const articleId = parseInt(params.articleId);
  if (isNaN(articleId)) {
    return {
      notFound: true
    };
  }

  const articleInfo = await getArticleInfoWithComments(articleId);
  if (articleInfo === null) {
    return {
      notFound: true
    };
  }

  return {
    props: { articleInfo }
  };
};

export default Article;