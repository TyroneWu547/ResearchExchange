import { useState } from "react";
import type { GetServerSideProps, NextPage } from "next";
import { useRouter } from "next/router";
import useUser from "../../hooks/useUser";
import { SubmitHandler, useForm } from "react-hook-form";

import CardLayout from "../../components/CardLayout";
import MultiSelection from "../../components/MultiSelection";
import { ViewArticlesPagination } from "../../components/ViewArticlesPagination";
import { ArticlePost, ArticlePreview } from "../../types";
import { subjects, subsubjects } from "../../utils/constants";
import ArticleRow from "../../components/ArticleRow";

import styles from "../../styles/ViewAllPosts.module.scss";
import { getArticles, getTotalNumArticles } from "../../utils/api";
import Link from "next/link";

interface FilterQuery {
  searchValue?: string;
  mainField?: string;
  subField?: string;
  tags?: string[];
  approved?: string;
}

interface FilterInputs {
  mainField: string;
  subField: string;
}

interface Props {
  articlesShown: ArticlePreview[];
  pageNum: number;
  articlesPerPage: number;
  numArticlesTotal: number;
  filterQuery: FilterQuery
}

const ViewAllPosts: NextPage<Props> = ({ articlesShown, pageNum, articlesPerPage, numArticlesTotal, filterQuery }) => {
  const router = useRouter();
  const user = useUser();

  const [tags, setTags] = useState<string[]>([]);

  const { register, watch, handleSubmit } = useForm<FilterInputs>();
  const watchSubject = watch("mainField", "Any subject");

  const onSubmitFilters: SubmitHandler<FilterInputs> = async ({ mainField, subField }) => {
    router.push({ pathname: "/articles", query: { mainField, subField, tags } });
  };

  return (
    <CardLayout width={1000}>
      <div className={styles.header}>
        <h1>View Articles</h1>

        {user ? (
          <Link href="/post-article"><a className={styles.postArticleButton}>Post an Article</a></Link>
        ) : (
          <Link href="/login"><a className={styles.postArticleButton}>Post an Article</a></Link>
        )}
      </div>

      <div className={styles.filtersContainer}>
        <h3>Filters</h3>
        <form onSubmit={handleSubmit(onSubmitFilters)}>
          <div className={styles.filters}>
            <div className={styles.fieldSelect}>
              <label htmlFor="mainField" className={styles.filterLabel}>Research Field</label>
              <select {...register("mainField")}>
                {["Any subject", ...subjects, "Other"].map(x => <option key={x} value={x}>{x}</option>)}
              </select>
            </div>

            <div className={styles.fieldSelect}>
              {watchSubject !== "Other" && watchSubject !== "Any subject" && (
                <>
                  <label htmlFor="subField" className={styles.filterLabel}>Research Subfield</label>
                  <select {...register("subField")}>
                    {["General/Other", ...subsubjects[watchSubject]].map(x => <option key={x} value={x}>{x}</option>)}
                  </select>
                </>
              )}
            </div>

            <div>
              <p className={styles.filterLabel}>Tags (Match Any)</p>
              <div className={styles.tagSelection}>
                <MultiSelection values={tags} setValues={setTags} name="tag" style="filters" />
              </div>
            </div>
          </div>

          <input className={styles.filterSubmit} type="submit" value="Filter" />
        </form>
      </div>

      {articlesShown.length === 0 ? <h3 className={styles.noneFound}>No articles found matching requested parameters.</h3> : articlesShown.map((article, i) => (
        <ArticleRow key={article.id} article={article} />
      ))}

      <ViewArticlesPagination currPage={pageNum} itemsPerPage={articlesPerPage} numArticlesTotal={numArticlesTotal} filterQuery={filterQuery} />
    </CardLayout>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const page = parseInt(context.query.page?.toString() ?? "1");
  const articlesPerPage = parseInt(context.query.articlesPerPage?.toString() ?? "10");
  const searchValue = context.query.searchValue?.toString() ?? "";
  const mainField = context.query.mainField?.toString() ?? "Any subject";
  const subField = context.query.subField?.toString() ?? "";
  const tags = (typeof context.query.tags === "string" ? [context.query.tags] : context.query.tags) ?? [];
  const approved = context.query.approved?.toString() ?? "false";
  if (isNaN(page) || isNaN(articlesPerPage) || (approved !== "false" && approved !== "true")) {
    return {
      notFound: true
    };
  }

  const articlesShown = await getArticles({ page, articlesPerPage, searchValue, mainField, subField, tags, approved });

  const numArticlesTotal = await getTotalNumArticles();

  const filterQuery: FilterQuery = {
    searchValue: context.query.searchValue as string,
    mainField: context.query.mainField as string,
    subField: context.query.subField as string,
    tags: context.query.tags as string[],
    approved: context.query.approved as string
  };
  (Object.keys(filterQuery) as ("searchValue" | "mainField" | "subField" | "tags")[]).forEach(key => {
    if (filterQuery[key] === undefined) {
      delete filterQuery[key];
    }
  });

  return {
    props: {
      articlesShown,
      pageNum: page,
      articlesPerPage,
      numArticlesTotal,
      filterQuery
    }
  };
};

export default ViewAllPosts;