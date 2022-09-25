import { GetServerSideProps, NextPage } from "next";
import dynamic from "next/dynamic";
import { useRouter } from "next/router";
import { useEffect } from "react";
import useUser from "../../hooks/useUser";

import { ArticlePost } from "../../types";
import { getArticleInfoWithComments, getArticleInfoWithoutComments } from "../../utils/api";

const ReviewCreator = dynamic(() => import('../../components/ReviewCreator'), { ssr: false });

interface Props {
  articleInfo: ArticlePost;
}

const CreateReview: NextPage<Props> = ({ articleInfo }) => {
  const user = useUser();
  const router = useRouter();

  useEffect(() => {
    if (!user || !user.roles.includes("Expert")) {
      router.push("/login");
    }
  }, [router, user]);

  return (
    <ReviewCreator articleInfo={articleInfo} />
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

  return {
    props: { articleInfo }
  };
};

export default CreateReview;