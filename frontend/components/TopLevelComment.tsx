import { useState } from "react";
import { FaReply } from "react-icons/fa";

import { Author, Comment, InlineComment, TopLevelComment as TopLevelCommentType } from "../types";
import Vote from "./Vote";
import CommentMaker from "./CommentMaker";

import styles from "../styles/TopLevelComment.module.scss";
import Link from "next/link";
import { formatDate } from "../utils/formatting";
import RoleIcon from "./RoleIcon";
import useUser from "../hooks/useUser";
import { useRouter } from "next/router";
import { splitParagraphs } from "../utils/jsxUtils";

function renderCommentAuthor(author: Author) {
  return author.role === "Author" && author.name === "Anonymous" && author.username === "Anonymous"
    ? (
      <span>(Anonymous)</span>
    ) : (
      <Link href={`/users/${author.username}`}>
        <a className={styles.authorLink}>{author.username}</a>
      </Link>
    );
}

interface Props {
  comment: TopLevelCommentType;
  articleId: number;
  compressed: boolean;
}

export default function TopLevelComment({ comment, articleId, compressed }: Props) {
  const user = useUser();
  const router = useRouter();

  const [areFollowupsExpanded, setFollowupsExpanded] = useState(false);

  const [followupReplyingTo, setFollowupReplyingTo] = useState<Comment>(comment);
  const [isReplying, setReplying] = useState(false);

  function initiateReply(followupReplyingTo: Comment) {
    if (!user) {
      router.push("/login");
    }
    setFollowupReplyingTo(followupReplyingTo);
    setReplying(true);
  }

  return (
    <div className={styles.topLevelCommentContainer}>
      {!compressed && <Vote post={comment} compressed={false} article={false} />}

      <div className={styles.topLevelCommentWithReplies}>
        <div>
          {!compressed && (
            <p className={styles.postMetaData}>
              <RoleIcon role={comment.author.role} where="comment" />
              {renderCommentAuthor(comment.author)} commented on {formatDate(comment.datePosted)}
            </p>
          )}
          <p className={styles.content}>{splitParagraphs(comment.content)}</p>
          <button onClick={() => initiateReply(comment)}>Reply <FaReply /></button>
        </div>

        {comment.followups && comment.followups.length > 0 && (areFollowupsExpanded
          ? (
            <>
              {comment.followups.map(followup => (
                <div key={followup.id} className={styles.followupComment}>
                  <div>
                    <p className={styles.postMetaData}>
                      <RoleIcon role={followup.author.role} where="comment" />
                      {renderCommentAuthor(followup.author)} commented on {formatDate(followup.datePosted)}
                    </p>
                    <p className={styles.content}>{splitParagraphs(followup.content)}</p>
                    <div className={styles.belowContent}>
                      <Vote post={followup} compressed article={false} />
                      <button onClick={() => initiateReply(followup)}>Reply <FaReply /></button>
                    </div>
                  </div>
                </div>
              ))}

              <div
                className={styles.expandFollowups}
                onClick={() => setFollowupsExpanded(false)}
              >
                <b>Collapse follow-ups.</b>
              </div>
            </>
          ) : (
            <div
              className={styles.expandFollowups}
              onClick={() => setFollowupsExpanded(true)}
            >
              This comment has {comment.followups.length} follow-up{comment.followups.length > 1 ? "s" : ""}. <b>Expand follow-ups.</b>
            </div>
          )
        )}

        {isReplying && (
          <CommentMaker
            rootPost={comment}
            articleId={articleId}
            followupReplyingTo={followupReplyingTo}
            onClose={() => setReplying(false)}
          />
        )}
      </div>
    </div >
  );
}