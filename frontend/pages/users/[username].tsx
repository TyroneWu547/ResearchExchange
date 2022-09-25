import { GetServerSideProps, NextPage } from "next";
import Image from "next/image";
import Link from "next/link";
import { SubmitHandler, useForm } from "react-hook-form";
import { IconContext } from "react-icons";
import { AiOutlineUser } from "react-icons/ai";

import CardLayout from "../../components/CardLayout";
import { ArticlePost, ArticlePreview, UserComment, UserReview, UserRole } from "../../types";
import { statusClass, statusIcon } from "../../utils/jsxUtils";
import ArticleRow from "../../components/ArticleRow";
import { expertAssignmentPending, getAuthToken, getUserContent, getUserInfo, requestExpertAssignment } from "../../utils/api";
import { formatDate } from "../../utils/formatting";
import { subjects } from "../../utils/constants";
import useUser from "../../hooks/useUser";

import styles from "../../styles/UserProfile.module.scss";
import RoleIcon from "../../components/RoleIcon";
import { getCookie } from "cookies-next";
import { useEffect, useState } from "react";
import { useRouter } from "next/router";

interface ExpertRequestInputs {
  reason: string;
  field: string;
}

interface UserInfo {
  profilePictureUrl: string | null;
  name: string;
  username: string;
  email: string;
  role: UserRole;
}

interface ContentPosted {
  articlesPosted: ArticlePreview[];
  approvedArticlesPosted: ArticlePreview[];
  reviewsPosted: UserReview[];
  commentsPosted: UserComment[];
}

interface Props {
  userInfo: UserInfo;
  contentPosted: ContentPosted;
}

const UserProfile: NextPage<Props> = ({ userInfo, contentPosted }) => {
  const user = useUser();
  const router = useRouter();

  const [requestedExpert, setRequestedExpert] = useState(false);

  const { register, handleSubmit } = useForm<ExpertRequestInputs>();

  const { username, name, email, profilePictureUrl, role } = userInfo;
  const { articlesPosted, approvedArticlesPosted, reviewsPosted, commentsPosted } = contentPosted;

  useEffect(() => {
    if (user) {
      expertAssignmentPending(user.sub).then(setRequestedExpert);
    }
  }, [user]);

  const myProfile = user && user.sub === userInfo.username;

  const onSubmitRequestExpert: SubmitHandler<ExpertRequestInputs> = async (data) => {
    await requestExpertAssignment({ username: user.sub, ...data });
    router.reload();
  };

  const displayHeader = (text: string) => myProfile
    ? <h2>My posted {text}</h2>
    : <h2>{text[0].toUpperCase() + text.slice(1)} posted by <span className={styles.username}>{username}</span></h2>;

  return (
    <CardLayout width={1000}>
      <div className={styles.userInfo}>
        <div className={styles.imageContainer}>
          {profilePictureUrl
            ? <Image src={profilePictureUrl} alt={`${username}'s profile picture`} width={150} height={150} />
            : <IconContext.Provider value={{ className: styles.noProfilePic }}><AiOutlineUser /></IconContext.Provider>
          }
        </div>
        <div>
          <p className={styles.name}>{name} <RoleIcon role={role} where="profile" /></p>
          <p>{username}</p>
          <p>{email}</p>
          {myProfile && (
            <Link href="/edit-profile">
              <a className={styles.editProfileBtn}>Edit profile</a>
            </Link>
          )}
        </div>
      </div>
      <div className={styles.profileItems}>
        {articlesPosted.length > 0 && (
          <div>
            {displayHeader("articles")}
            <div className={styles.articlesPosted}>
              {articlesPosted.map(article => (
                <ArticleRow key={article.id} article={article} />
              ))}
            </div>
          </div>
        )}

        {approvedArticlesPosted.length > 0 && (
          <div>
            {displayHeader("approved articles")}
            <div className={styles.articlesPosted}>
              {approvedArticlesPosted.map(article => (
                <ArticleRow key={article.id} article={article} />
              ))}
            </div>
          </div>
        )}

        {reviewsPosted.length > 0 && (
          <div>
            {displayHeader("reviews")}
            <div className={styles.commentsPosted}>
              {reviewsPosted.map(review => (
                <div key={review.id} className={styles.comment}>
                  <div className={styles.commentPostedOn}>
                    Reviewed{' '}
                    <Link href={`/articles/${review.articleId}`}>
                      <a className={styles.articleLink}>{review.articleTitle}</a>
                    </Link>{' '}
                    on {formatDate(review.datePosted)}:{' '}
                    <span className={statusClass(review.status)}>{statusIcon(review.status)}{review.status}</span>
                  </div>
                  <div>{review.content}</div>
                  <div className={styles.scoreSection}><b>Vote Score:</b> {review.score}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {commentsPosted.length > 0 && (
          <div>
            {displayHeader("comments")}
            <div className={styles.commentsPosted}>
              {commentsPosted.map(comment => (
                <div key={comment.id} className={styles.comment}>
                  <div className={styles.commentPostedOn}>
                    Commented on{' '}
                    <Link href={`/articles/${comment.articleId}`}>
                      <a className={styles.articleLink}>{comment.articleTitle}</a>
                    </Link>{' '}
                    on {formatDate(comment.datePosted)}
                  </div>
                  <div>{comment.content}</div>
                  <div className={styles.scoreSection}><b>Vote Score:</b> {comment.score}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {myProfile && !user.roles.includes("Admin") && (
          <div>
            <h2>Request Expert Assignment</h2>
            {requestedExpert ? (
              <p>Your expert assignment request is under review.</p>
            ) : (
              <form onSubmit={handleSubmit(onSubmitRequestExpert)}>
                <div className={styles.requestSection}>
                  <label htmlFor="field">Research Field</label>
                  <select className={styles.selectSubject} {...register("field", { required: true })}>
                    {["-- Select a field --", ...subjects, "Other"].map(subject => <option key={subject} value={subject}>{subject}</option>)}
                  </select>
                </div>
                <div className={styles.requestSection}>
                  <label htmlFor="reason">Why do you believe you deserve to be marked as an expert in this field? Include a summary of your justification, and any supporting evidence (significant contributions, external proof of expertise, etc.) as necessary.</label>
                  <textarea placeholder="Justify your request..." {...register("reason", { required: true })} />
                </div>
                <input type="submit" value="Request" />
              </form>
            )}
          </div>
        )}
      </div>
    </CardLayout>
  );
};

export const getServerSideProps: GetServerSideProps<Props> = async (context) => {
  const params = context.params! as { username: string };
  const username = params.username;

  const authToken = getAuthToken(context);

  const userInfo = (await getUserInfo(username))!;
  const contentPosted = await getUserContent(username, authToken);

  return {
    props: { userInfo, contentPosted }
  };
};

export default UserProfile;