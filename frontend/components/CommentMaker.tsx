import React, { useState } from "react";
import { FaReply, FaTrashAlt } from "react-icons/fa";

import { Comment } from "../types";

import styles from "../styles/CommentMaker.module.scss";
import { getAuthToken, postComment } from "../utils/api";
import useUser from "../hooks/useUser";
import { useRouter } from "next/router";

interface Props {
  rootPost: Comment | null;
  followupReplyingTo: Comment | null;
  articleId: number;
  onClose?: () => void;
}

export default function CommentMaker({ rootPost, followupReplyingTo, articleId, onClose }: Props) {
  const router = useRouter();

  const user = useUser();

  const [comment, setComment] = useState("");

  async function handleReply(e: React.FormEvent) {
    e.preventDefault();

    if (comment.trim() != "") {
      await postComment(articleId, {
        rootThreadId: rootPost?.id ?? null,
        replyToId: followupReplyingTo?.id ?? null,
        author: user.sub,
        content: comment
      }, getAuthToken());
      router.reload();
    }
  }

  return (
    <div className={styles.commentMaker}>
      {onClose && (
        <div className={styles.topBar}>
          <div className={styles.replyingTo}>
            <FaReply /> Replying to{' '}
            <b>{followupReplyingTo !== null ? followupReplyingTo.author.username : rootPost!.author.username}</b>
          </div>
          <button className={styles.discardButton} onClick={onClose}>Discard <FaTrashAlt /></button>
        </div>
      )}
      <form onSubmit={handleReply}>
        <textarea value={comment} onChange={e => setComment(e.target.value)} />
        <input type="submit" value="Post" />
      </form>
    </div>
  );
}