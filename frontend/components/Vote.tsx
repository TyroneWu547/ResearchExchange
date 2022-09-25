import { useState } from "react";

import { Comment, ArticlePost } from "../types";
import { getAuthToken, makeVote } from "../utils/api";

import styles from "../styles/Vote.module.scss";

interface Props {
  post: Comment | ArticlePost;
  compressed: boolean;
  article: boolean;
}

export default function Vote({ post, compressed, article }: Props) {
  const [score, setScore] = useState(post.score);

  async function vote(which: "up" | "down") {
    await makeVote(post.id, which, article ? "articles" : "comment-posts", getAuthToken());
    setScore(score + (which === "up" ? 1 : -1));
  }

  return compressed ? (
    <div className={styles.compressedScore}>
      <div className={styles.scoreVote} onClick={() => vote("up")}>▲</div>
      <div>{score}</div>
      <div className={styles.scoreVote} onClick={() => vote("down")}>▼</div>
    </div>
  ) : (
    <div className={styles.uncompressedScore}>
      <div className={styles.scoreVote} onClick={() => vote("up")}>▲</div>
      <div>{score}</div>
      <div className={styles.scoreVote} onClick={() => vote("down")}>▼</div>
    </div>
  );
}