import { GetServerSideProps, NextPage } from "next";
import dynamic from "next/dynamic";

import { ArticlePost, ExpertReview } from "../../types";
import { getArticleInfoWithoutComments } from "../../utils/api";

const ArticleViewer = dynamic(() => import('../../components/ArticleViewer'), { ssr: false });

interface Props {
  articleInfo: ArticlePost;
}

const ViewArticle: NextPage<Props> = ({ articleInfo }) => {
  return (
    <ArticleViewer pdfUrl={articleInfo.pdfUrl} />
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

  const articleInfo = await getArticleInfoWithoutComments(articleId);

  if (articleInfo === null) {
    return {
      notFound: true
    };
  }

  return {
    props: { articleInfo }
  };
};

export default ViewArticle;