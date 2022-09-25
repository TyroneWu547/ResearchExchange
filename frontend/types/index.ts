export type ReviewStatus = "Approved" | "Rejected" | "Needs Work";

export type UserRole = "User" | "Admin" | "Author" | `Expert in ${string}`;

export interface Author {
  name: string;
  username: string;
  role: UserRole;
}

export interface Comment {
  id: number;
  score: number;
  datePosted: string;
  content: string;
  author: Author;
}

interface FollowupComment extends Comment {
  replyingTo?: number;
}

export interface TopLevelComment extends Comment {
  followups?: FollowupComment[];
}

export interface SelectionRect {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface PdfSelection {
  pageNum: number;
  selectedContent: string;
  highlightSections: SelectionRect[];
}

export type InlineComment = TopLevelComment & PdfSelection;

export interface ExpertReview extends TopLevelComment {
  status: ReviewStatus;
  inlineComments: InlineComment[];
}

export type UserComment = Comment & {
  articleTitle: string;
  articleId: number;
};

export type UserReview = ExpertReview & {
  articleTitle: string;
  articleId: number;
};

export interface ArticlePost {
  id: number;
  score: number;
  datePosted: string;
  pdfUrl: string;
  name: string;
  articleAbstract: string;
  mainField: string;
  subField: string;
  tags: string[];
  repoUrl: string;
  dataUrl: string;
  links: string[];
  approved: boolean;
  authors: Author[];
  expertReviews: ExpertReview[];
  comments: TopLevelComment[];
}

export interface ArticlePreview {
  id: number;
  name: string;
  articleAbstract: string;
  mainField: string;
  subField: string;
  datePosted: string;
  score: number;
  tags: string[];
  approved: boolean;
  expertReviews: string[];
}