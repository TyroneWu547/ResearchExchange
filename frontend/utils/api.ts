import { getCookie } from "cookies-next";
import { ArticlePost, PdfSelection, ReviewStatus, Comment, ExpertReview, TopLevelComment, UserComment, UserReview, UserRole, ArticlePreview } from "../types";

export const getAuthToken = (context?: any) => (context ? getCookie("auth", { req: context.req, res: context.res }) : getCookie("auth")) as string | null;

const BASE_URL = "http://localhost:8080";

const postJson = <TBody>(path: string, body: TBody, authToken?: string | null) => fetch(`${BASE_URL}${path}`, {
  method: "POST",
  headers: { "Content-Type": "application/json", "Authorization": authToken ? `Bearer ${authToken}` : "" },
  body: JSON.stringify(body)
});

const putJson = <TBody>(path: string, body: TBody, authToken?: string | null) => fetch(`${BASE_URL}${path}`, {
  method: "PUT",
  headers: { "Content-Type": "application/json", "Authorization": authToken ? `Bearer ${authToken}` : "" },
  body: JSON.stringify(body)
});

const getJson = <TResData>(path: string, query?: Record<string, string>, authToken?: string | null) => fetch(`${BASE_URL}${path}?${new URLSearchParams(query)}`, {
  headers: { "Authorization": authToken ? `Bearer ${authToken}` : "" }
})
  .then(res => {
    if (!res.ok) {
      console.log(`API request to ${path} failed with status ${res.status}`);
      return null;
    }
    return res.json() as Promise<TResData>;
  });

const getJsonWithoutQuery = <TResData>(path: string) => fetch(`${BASE_URL}${path}`)
  .then(res => {
    if (!res.ok) {
      console.log(`API request to ${path} failed with status ${res.status}`);
      return null;
    }
    return res.json() as Promise<TResData>;
  });

interface LoginRequestData {
  username: string;
  password: string;
}

export const login = (data: LoginRequestData) => postJson("/login", data);

interface SignupRequestData {
  name: string;
  username: string;
  email: string;
  password: string;
}

export const signup = (data: SignupRequestData) => postJson("/signup", data);

export const getArticleInfoWithComments = async (articleId: number) => {
  const articleInfo = await getJson<ArticlePost>(`/articles/${articleId}`);
  if (articleInfo === null) {
    return null;
  }
  articleInfo.expertReviews = (await getJson<ExpertReview[]>(`/articles/${articleId}/expert-reviews`))!;
  articleInfo.comments = (await getJson<TopLevelComment[]>(`/articles/${articleId}/comments`))!;
  return articleInfo;
};

export const getArticleInfoWithoutComments = (articleId: number) => getJson<ArticlePost>(`/articles/${articleId}`);

export const getReview = (reviewId: number) => getJson<ExpertReview>(`/expert-reviews/${reviewId}`);

export const postArticle = (data: FormData, authToken: string | null) => fetch(`${BASE_URL}/articles`, {
  method: "POST",
  headers: { "Authorization": `Bearer ${authToken}` },
  body: data
});

interface GetArticlesQueryParams {
  page: number;
  articlesPerPage: number;
  searchValue?: string;
  mainField: string;
  subField?: string;
  tags: string[];
  approved: string;
}

export const getArticles = async (params?: GetArticlesQueryParams) => {
  let query: Record<string, string> | undefined;
  if (params) {
    const { page, articlesPerPage, searchValue, mainField, subField, tags, approved } = params;
    query = {
      pageNum: (page - 1).toString(),
      recordsPerPage: articlesPerPage.toString(),
      searchValue: searchValue ?? "",
      mainField: mainField === "Any subject" ? "" : mainField,
      subField: mainField === "Any subject" ? "" : (subField ?? ""),
      tags: tags.join(","),
      approved
    };
    for (const [k, v] of Object.entries(query)) {
      if (v.trim() === '') {
        delete query[k];
      }
    }
  }

  const articles = (await getJson<ArticlePreview[]>("/articles", query))!;
  return articles;
};

export const getTotalNumArticles = () => fetch(`${BASE_URL}/articles/numTotal`).then(res => res.text()).then(s => parseInt(s));

type InlineCommentData = { content: string; } & PdfSelection;

interface ReviewRequestData {
  author: string;
  content: string;
  status: ReviewStatus;
  inlineComments: InlineCommentData[];
}

export const postReview = (articleId: number, data: ReviewRequestData, authToken: string | null) => postJson(`/articles/${articleId}/review-article`, data, authToken);

export const editReview = (articleId: number, data: ReviewRequestData, authToken: string | null) => putJson(`/articles/${articleId}/review-article`, data, authToken);

interface PostCommentBody {
  rootThreadId: number | null;
  replyToId: number | null;
  author: string;
  content: string;
}

interface PostCommentBody {
  rootThreadId: number | null;
  replyToId: number | null;
  author: string;
  content: string;
}

export const postComment = (articleId: number, data: PostCommentBody, authToken: string | null) => postJson(`/articles/${articleId}/post-comment`, data, authToken);

export const makeVote = (postId: number, which: "up" | "down", rootUrl: String, authToken: string | null) => fetch(`${BASE_URL}/${rootUrl}/${postId}/vote`, {
  method: "POST",
  headers: { "Content-Type": "application/json", "Authorization": `Bearer ${authToken}` },
  body: which
});

interface UserContentResponseData {
  articlesPosted: ArticlePreview[];
  approvedArticlesPosted: ArticlePreview[];
  reviewsPosted: UserReview[];
  commentsPosted: UserComment[];
}

export const getUserContent = async (username: string, authToken: string | null): Promise<UserContentResponseData> => {
  let articlesPosted: ArticlePreview[];
  try {
    articlesPosted = (await getJson(`/articles/${username}/unapproved`, undefined, authToken)) ?? [];
  } catch (e) {
    articlesPosted = [];
  }

  const approvedArticlesPosted = (await getJson<ArticlePreview[]>(`/articles/${username}/approved`))!;
  const commentsPosted = (await getJson<UserComment[]>(`/users/${username}/comments`))!;
  const reviewsPosted = (await getJson<UserReview[]>(`/users/${username}/expert-reviews`))!;
  return {
    articlesPosted,
    approvedArticlesPosted,
    commentsPosted,
    reviewsPosted,
  };
};

interface UserInfoResponseData {
  profilePictureUrl: string | null;
  name: string;
  username: string;
  email: string;
  role: UserRole;
}

export const getUserInfo = (username: string) => getJson<UserInfoResponseData>(`/users/${username}/info`);

export const updateProfile = (username: string, newUserData: FormData, authToken: string | null) => fetch(`${BASE_URL}/users/${username}/edit-profile`, {
  method: "PUT",
  headers: { "Authorization": `Bearer ${authToken}` },
  body: newUserData
});

interface RequestExpertAssignmentData {
  username: string;
  reason: string;
  field: string;
}

export const requestExpertAssignment = (data: RequestExpertAssignmentData) => postJson("/expert-requests", data);

interface ExpertRequest {
  username: string;
  field: string;
  reason: string;
}

export const getRequestedExperts = (authToken: string | null) => getJson<ExpertRequest[]>("/expert-requests", undefined, authToken);

interface ExpertInfo {
  username: string;
  field: string;
}

export const getExperts = (authToken: string | null) => getJson<ExpertInfo[]>("/experts", undefined, authToken);

export const assignExpert = (username: string, field: string, authToken: string | null) => putJson(`/experts`, { username, field }, authToken);

export const rejectExpert = (username: string, authToken: string | null) => fetch(`${BASE_URL}/expert-requests/${username}`, {
  method: "DELETE",
  headers: { "Authorization": `Bearer ${authToken}` }
});

export const removeExpert = (username: string, authToken: string | null) => fetch(`${BASE_URL}/experts/${username}`, {
  method: "DELETE",
  headers: { "Authorization": `Bearer ${authToken}` }
});

export const expertAssignmentPending = (username: string) => fetch(`${BASE_URL}/expert-requests/${username}/has-requested`)
  .then(res => res.text()).then(x => x === "true");

export const editReviewStatus = (reviewId: number, newStatus: ReviewStatus, authToken: string | null) => fetch(`${BASE_URL}/expert-reviews/${reviewId}/change-status`, {
  method: "PUT",
  headers: { "Content-Type": "application/json", "Authorization": `Bearer ${authToken}` },
  body: newStatus
});

export const getTopArticles = () => getJsonWithoutQuery<ArticlePost>(`/articles/top`);