import Link from "next/link";
import { useRouter } from "next/router";

import styles from "../styles/ViewArticlesPagination.module.scss";

interface FilterQuery {
  subject?: string;
  subsubject?: string;
  tags?: string[];
}

interface Props {
  currPage: number;
  itemsPerPage: number;
  numArticlesTotal: number;
  filterQuery: FilterQuery;
}

export function ViewArticlesPagination({ currPage, itemsPerPage, numArticlesTotal, filterQuery }: Props) {
  const router = useRouter();

  const numPages = Math.ceil(numArticlesTotal / itemsPerPage);
  const pageRange = [...Array(numPages)].map((_, i) => i + 1);

  return (
    <div className={styles.pagination}>
      <div className={styles.itemsPerPage}>
        <label htmlFor="page-num-items">Items Per Page</label>
        <select name="page-num-items" onChange={e => router.push({
          pathname: "/articles",
          query: { ...filterQuery, articlesPerPage: e.target.value }
        })}>
          <option value={10}>10</option>
          <option value={25}>25</option>
          <option value={50}>50</option>
          <option value={100}>100</option>
        </select>
      </div>

      <div className={styles.pageButtons}>
        <PageSwitchButton currPage={currPage} value="prev" enabled={currPage > 1} filterQuery={filterQuery} />
        {pageRange.map(pageNum =>
          <PageSwitchButton key={pageNum} currPage={currPage} value={pageNum} enabled filterQuery={filterQuery} />
        )}
        <PageSwitchButton currPage={currPage} value="next" enabled={currPage < numPages} filterQuery={filterQuery} />
      </div>
    </div>
  );
}

interface PageSwitchButtonProps {
  currPage: number;
  value: number | "next" | "prev";
  enabled: boolean;
  filterQuery: FilterQuery;
}

function PageSwitchButton({ currPage, value, enabled, filterQuery }: PageSwitchButtonProps) {
  let switchTo: number;
  let displayed: string;
  if (typeof value === "number") {
    switchTo = value;
    displayed = value.toString();
  } else if (value === "next") {
    switchTo = currPage + 1;
    displayed = "Next ❯";
  } else {
    switchTo = currPage - 1;
    displayed = "❮ Prev";
  }

  const positionClass = switchTo < currPage ? styles.leftOfCurrent : (switchTo === currPage ? styles.currentPage : styles.rightOfCurrent);

  return enabled
    ? (
      <Link href={{
        pathname: "/articles",
        query: { ...filterQuery, page: switchTo }
      }}>
        <a className={`${styles.pageButton} ${positionClass}`}>{displayed}</a>
      </Link>
    ) : (
      <div className={`${styles.pageButton} ${styles.disabledArrow}`}>{displayed}</div>
    );
}