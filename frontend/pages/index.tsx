import type { GetServerSideProps, NextPage } from 'next';
import Image from 'next/image';
import Link from 'next/link';
import "next/router";

import CardLayout from '../components/CardLayout';
import DocumentPreview from '../components/DocumentPreview';

import styles from "../styles/Home.module.scss";
import { ArticlePost } from '../types';
import { getTopArticles } from '../utils/api';

interface Props {
  topArticles: ArticlePost[];
}

const Home: NextPage<Props> = ({ topArticles }) => {
  return (
    <CardLayout width={1000}>
      <div className={styles.card}>
        <div className={styles.intro}>
          <h2>Welcome to Research Exchange!</h2>

          <div className={styles.introContent}>
            <div className={styles.images}>
              <Image src="/homeCommunity.png" alt="Community" width={800} height={500} />
            </div>

            <div className={styles.text}>
              <p>Research Exchange is an open community platform where you can anonymously post research articles to be openly discussed by enthusiasts and quickly reviewed by experts.</p>

              <p>We offer rapid publication of research articles without editorial bias and with the benefit of transparent expert reviews.</p>

              <p>Research Exchange is transforming the traditional peer review process into more of a crowd review process.</p>
            </div>
          </div>
        </div>

        <hr />

        <div className={styles.browse}>
          <h2>Start browsing the trending articles right now!</h2>

          <div className={styles.topThree}>
            {topArticles.length === 0 ? <h3>No articles found.</h3> : topArticles.map((article) => (
              <div key={article.id} className={styles.documentPreviewContainer}>
                <Link href={`/articles/${article.id}`}>
                  <a>
                    <DocumentPreview className={styles.documentPreview} pdfUrl={article.pdfUrl} height={300} />
                    <div className={styles.articleNameContainer}>
                      {article.name.length <= 150 ? article.name : article.name.substring(0, 150) + "..."}
                    </div>
                  </a>
                </Link>
              </div>
            ))}
          </div>
        </div>

        <hr />

        <div className={styles.pillars}>
          <h2>Research Exchange communities are different. Here&apos;s how:</h2>

          <div className={styles.threeColumns}>
            <div>
              <Image src="/homeTransparency.png" alt="Data Transparency" width={250} height={150} />

              <h3>Data Transparency</h3>

              <p>Access research findings for a more unbiased, comprehensive background.</p>
            </div>

            <div>
              <Image src="/homeReview.png" alt="Expert Peer Reviews" width={250} height={150} />

              <h3>Expert Peer Reviews</h3>

              <p>View expert reviews to check the validity of research articles and their suitability for publication.</p>
            </div>

            <div>
              <Image src="/homeDiscussion.png" alt="Open Discussion" width={250} height={150} />

              <h3>Open Discussion</h3>

              <p>Discuss and challenge expert opinions to provide insight and refine quality ideas.</p>
            </div>
          </div>
        </div>

        <hr />

        <div className={styles.instructions}>
          <h2>How does Research Exchange work?</h2>

          <div className={styles.instructionContent}>
            <div className={styles.numbers}>
              <Image src="/homeCircle1.png" alt="Circle 1" width={100} height={100} />
            </div>

            <div className={styles.images}>
              <Image src="/homePost.png" alt="Post" width={400} height={300} />
            </div>

            <div className={styles.text}>
              <h3>Post</h3>

              <p>Post your research articles to have them reviewed by experts and members of the community.</p>
            </div>
          </div>

          <div className={styles.instructionContent}>
            <div className={styles.numbers}>
              <Image src="/homeCircle2.png" alt="Circle 2" width={100} height={100} />
            </div>

            <div className={styles.images}>
              <Image src="/homeComment.png" alt="Comment" width={400} height={300} />
            </div>

            <div className={styles.text}>
              <h3>Comment</h3>

              <p>Comment on an article&apos;s post or someone else&apos;s comment to discuss your opinions and trade ideas.</p>
            </div>
          </div>

          <div className={styles.instructionContent}>
            <div className={styles.numbers}>
              <Image src="/homeCircle3.png" alt="Circle 3" width={100} height={100} />
            </div>

            <div className={styles.images}>
              <Image src="/homeVote.png" alt="Vote" width={400} height={300} />
            </div>

            <div className={styles.text}>
              <h3>Vote</h3>

              <p>Upvote or downvote an article&apos;s post or someone else&apos;s comment to keep interesting content at the top.</p>
            </div>
          </div>

          <div className={styles.instructionContent}>
            <div className={styles.numbers}>
              <Image src="/homeCircle4.png" alt="Circle 4" width={100} height={100} />
            </div>

            <div className={styles.images}>
              <Image src="/homeApprove.png" alt="Review" width={400} height={250} />
            </div>
            <div className={styles.text}>
              <h3>Review</h3>

              <p>Review and provide feedback on other authors&apos; research articles to approve them for publication if you&apos;re an expert.</p>
            </div>
          </div>
        </div>

        <hr />

        <div className={styles.community}>
          <h2>Community is the heart of Research Exchange.</h2>

          <div className={styles.left}>
            <div className={styles.feature}>
              <div className={styles.image}>
                <Image src="/homeNonAccountUser.png" alt="Non-Account User" width={500} height={500} />
              </div>

              <div className={styles.text}>
                <p>A Non-Account User is anyone who doesn&apos;t have an account or has an account but is not currently logged in.</p>

                <h3>As a Non-Account User, you can...</h3>

                <ul>
                  <li>View user profiles along with their posts and comments</li>
                  <br />
                  <li>View articles, posts, and comments</li>
                  <br />
                  <li>Search for articles by name</li>
                  <br />
                  <li>Filter articles by field/subfield and tag(s)</li>
                </ul>
              </div>
            </div>
          </div>

          <div className={styles.right}>
            <div className={styles.feature}>
              <div className={styles.text}>
                <p>An Account User is anyone who has an account and is currently logged into their account.</p>
                <p>Account Users can do everything that Non-Account Users can do and more.</p>

                <h3>As an Account User, you can...</h3>

                <ul>
                  <li>Comment on posts, expert reviews, and other comments</li>
                  <br />
                  <li>View all posts and comments that you&apos;ve created</li>
                  <br />
                  <li>Upvote or downvote posts, expert reviews, and comments</li>
                  <br />
                  <li>Anonymously post articles to be reviewed and approved by experts in the community</li>
                </ul>
              </div>
              <div className={styles.image}>
                <Image src="/homeAccountUser.png" alt="Account User" width={500} height={350} />
              </div>
            </div>
          </div>

          <div className={styles.left}>
            <div className={styles.feature}>
              <div className={styles.image}>
                <Image src="/homeExpertUser.png" alt="Expert User" width={500} height={400} />
              </div>

              <div className={styles.text}>
                <p>An Expert User is someone who has an account and has been designated an expert in a specific field by an Admin.</p>
                <p>Expert Users can do everything that Account Users can do and more.</p>

                <h3>As an Expert, you can...</h3>

                <ul>
                  <li>Start an expert review with comments that reference highlighted sections of a research article</li>
                  <br />
                  <li>Approve, reject, or advise revision on a research article</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </CardLayout >
  );
};

export const getServerSideProps: GetServerSideProps = async () => {
  const topArticles = await getTopArticles();
  return {
    props: { topArticles }
  };
};

export default Home;