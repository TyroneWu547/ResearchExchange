import { GetServerSideProps, NextPage } from "next";
import { useEffect, useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import CardLayout from "../components/CardLayout";

import MultiSelection from "../components/MultiSelection";
import { getAuthToken, postArticle } from "../utils/api";
import useUser from "../hooks/useUser";

import styles from "../styles/PostArticle.module.scss";
import { subjects, subsubjects } from "../utils/constants";
import { useRouter } from "next/router";
import Link from "next/link";

interface Inputs {
  articlePdf: FileList;
  title: string;
  articleAbstract: string;
  mainField: string;
  subField: string;
  repoUrl: string;
  dataUrl: string;
}

const PostArticle: NextPage = () => {
  const router = useRouter();

  const user = useUser();

  // TODO maybe refactor to somehow hook into the useForm
  const [authors, setAuthors] = useState<string[]>([]);
  const [tags, setTags] = useState<string[]>([]);
  const [extraLinks, setExtraLinks] = useState<string[]>([]);

  const [extraLinksError, setExtraLinksError] = useState("");

  const { register, watch, formState: { errors }, handleSubmit } = useForm<Inputs>();
  const watchSubject = watch("mainField", "-- Select a field --");

  useEffect(() => {
    if (!user) {
      router.push("/login");
    } else {
      setAuthors([user.sub]);
    }
  }, [user, router]);

  const onSubmit: SubmitHandler<Inputs> = async (data) => {
    console.log(extraLinks);

    if (extraLinks.some(url => !urlRegex.test(url))) {
      setExtraLinksError("All extra links must be valid URLs");
      return;
    }

    const articlePdf = data.articlePdf[0];

    const formData = new FormData();
    formData.append("articlePdf", articlePdf, articlePdf.name);
    formData.append("tags", tags.join(","));
    formData.append("authors", authors.join(","));
    formData.append("extraLinks", extraLinks.join(","));
    Object.entries(data).filter(x => x[0] !== "articlePdf").forEach(x => {
      const [k, v] = x as any;
      formData.append(k, v);
    });
    const res = await postArticle(formData, getAuthToken());
    if (res.ok) {
      const newArticleId = await res.text();
      router.push(`/articles/${newArticleId}`);
    } else { }
  };

  const urlRegex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/;

  return (
    <CardLayout width={1000}>
      <h1>Post an Article</h1>

      <h2 className={styles.section}>Article Information</h2>

      <div className={styles.info}>
        <p>Before you submit an article, please ensure that:</p>

        <ol className={styles.before}>
          <li>The work is original. The article must not have been published previously or be under consideration or review by another journal.</li>
          <li>All methodological details and relevant data are made available to allow others to replicate the study.</li>
          <li>The names of the authors don&apos;t appear in the article.</li>
        </ol>

        <p>For more information on how to prepare your article, please see our <Link href="/guidelines"><a>Guidelines & Policies</a></Link>.</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className={styles.postTextField}>
          {errors.articlePdf && <p className={styles.errorMessage}>An article PDF must be selected</p>}
          <label htmlFor="article" className={styles.requiredLabel}>Upload Article PDF</label>
          <input className={styles.upload} type="file" accept=".pdf" {...register("articlePdf", { required: true })} />
        </div>

        <div className={styles.postTextField}>
          {errors.title && <p className={styles.errorMessage}>The article must have a title</p>}
          <label htmlFor="title" className={styles.requiredLabel}>Title</label>
          <p className={styles.info}>Please provide a concise and specific title that clearly reflects the content of the article.</p>
          <input type="text" {...register("title", { required: true })} />
        </div>

        <div className={styles.postTextField}>
          {errors.articleAbstract && <p className={styles.errorMessage}>The article must have an abstract</p>}
          <label htmlFor="articleAbstract" className={styles.requiredLabel}>Abstract</label>
          <p className={styles.info}>Abstracts should provide a succinct summary of the article. Citations should not be used in the abstract. We suggest that abstracts are structured into Background, Methods, Results, and Conclusions.</p>
          <textarea {...register("articleAbstract", { required: true })} />
        </div>

        <div className={styles.subjectSelection}>
          <div>
            {errors.mainField && <p className={styles.errorMessage}>A research field must be selected</p>}
            <label htmlFor="mainField" className={styles.requiredLabel}>Research Field</label>
            <select className={styles.selectSubject} {...register("mainField", { validate: val => val !== "-- Select a field --" })}>
              {["-- Select a field --", ...subjects, "Other"].map(subject => <option key={subject} value={subject}>{subject}</option>)}
            </select>
          </div>

          {watchSubject !== "Other" && watchSubject !== "-- Select a field --" && (
            <div>
              <label htmlFor="subField" className={styles.requiredLabel}>Research Subfield</label>
              <select className={styles.selectSubject} {...register("subField", { required: true })}>
                {["General/Other", ...subsubjects[watchSubject]].map(subject => <option key={subject} value={subject}>{subject}</option>)}
              </select>
            </div>
          )}
        </div>

        <p className={styles.requiredLabel}>Author Usernames</p>
        <p className={styles.info}>Please list the usernames of all authors in the order that they should appear on the final publication. Note that all authors must create an account with Research Exchange in order to be listed. Please make sure that each author&apos;s account has been updated so that their full names are exactly as they should appear on the final publication.</p>
        <p className={styles.info}>In order to reduce bias, the authors will be anonymous if the article has not yet been approved for publication. The authors will be visible once the article has been approved.</p>
        <p className={styles.info}>For more information on Research Exchange&apos;s policies for article publication, please see our <Link href="/guidelines"><a>Guidelines & Policies</a></Link>.</p>
        <MultiSelection values={authors} setValues={setAuthors} name="username" style="post-article" />

        <p className={styles.tags}>Tags</p>
        <p className={styles.info}>We encourage you to use tags that describe the subject of your article. Tags help improve searchability and increase the likelihood of your work being discovered.</p>
        <MultiSelection values={tags} setValues={setTags} name="tag" style="post-article" />

        <br /><br />
        <h2 className={styles.section}>References</h2>

        <p className={styles.info}>For the sake of maintaining Research Exchange&apos;s goal of data transparency, we highly encourage you to include links to your source code, datasets, and any other additional references relevant to your research.</p>
        <p className={styles.info}>For more information on how to select and structure repositories, please see our <Link href="/guidelines"><a>Guidelines & Policies</a></Link>.</p>

        <div className={styles.postTextField}>
          {errors.repoUrl && <p className={styles.errorMessage}>Source code URL must be a valid URL</p>}
          <label htmlFor="repoUrl">Source Code URL</label>
          <p className={styles.info}>If any relevant software has been coded by the authors of the article, the source code should be made available.</p>
          <input className={styles.url} type="text" placeholder="E.g. https://github.com/..." {...register("repoUrl", { pattern: urlRegex })} />
        </div>

        <div className={styles.postTextField}>
          {errors.dataUrl && <p className={styles.errorMessage}>Dataset URL must be a valid URL</p>}
          <label htmlFor="repoUrl">Dataset URL</label>
          <p className={styles.info}>If there is any resulting data relevant to your research, the data should be made available.</p>
          <input className={styles.url} type="text" placeholder="E.g. https://datadryad.org/..." {...register("dataUrl", { pattern: urlRegex })} />
        </div>

        {extraLinksError && <p className={styles.errorMessage}>{extraLinksError}</p>}
        <p className={styles.additionalLinks}>Additional URLs</p>
        <MultiSelection values={extraLinks} setValues={setExtraLinks} name="additional URL" style="post-article" />

        <input type="submit" className={styles.submitPost} value="Create Post" />
      </form>
    </CardLayout>
  );
};

export default PostArticle;