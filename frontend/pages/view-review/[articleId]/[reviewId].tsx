import { GetServerSideProps, NextPage } from "next";
import dynamic from "next/dynamic";

import { ArticlePost, ExpertReview } from "../../../types";
import { getArticleInfoWithComments, getArticleInfoWithoutComments, getReview } from "../../../utils/api";

const ReviewViewer = dynamic(() => import('../../../components/ReviewViewer'), { ssr: false });

interface Props {
  articleInfo: ArticlePost;
  review: ExpertReview;
}

const ViewReview: NextPage<Props> = ({ articleInfo, review }) => {
  return (
    <ReviewViewer articleInfo={articleInfo} review={review} />
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const params = context.params! as { reviewId: string; articleId: string };
  const reviewId = parseInt(params.reviewId);
  const articleId = parseInt(params.articleId);
  if (isNaN(reviewId) || isNaN(articleId)) {
    return {
      notFound: true
    };
  }

  const articleInfo = await getArticleInfoWithoutComments(articleId);
  const review = await getReview(reviewId);

  if (articleInfo === null || review === null) {
    return {
      notFound: true
    };
  }

  return {
    props: { articleInfo, review }
  };
};

export default ViewReview;