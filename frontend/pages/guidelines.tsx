import type { NextPage } from 'next';
import Link from 'next/link';
import "next/router";

import CardLayout from '../components/CardLayout';

import styles from "../styles/Guidelines.module.scss";

const Guidelines: NextPage = () => {
  return (
    <CardLayout width={1000}>
      <h1>Guidelines & Policies</h1>

      <h2 className={styles.sectionName}>How to Post an Article</h2>

      <div className={styles.sectionContent}>
        <ol>
          <li>
            Create a Research Exchange account.
            <ul><li>You must have an account with Research Exchange in order to post an article.</li></ul>
          </li>
          <li>
            Click the <Link href="/articles"><a>View Articles</a></Link> tab in the menu bar and find the <Link href="/post-article"><a>Post an Article</a></Link> button.
            <ul><li>Clicking the button will take you to our online article submission page.</li></ul>
          </li>
          <li>
            Follow the guidelines on our online article submission page and post your article.
            <ul><li>For more information on our data transparency policies and approval process, please see the corresponding sections below.</li></ul>
          </li>
          <li>
            Your article has been posted!
            <ul><li>Once you&apos;ve posted your article through our online article submission page, your article will be available for immediate viewing and discussion.</li></ul>
          </li>
        </ol>
      </div>

      <h2 className={styles.sectionName}>Data Transparency Policy</h2>

      <div className={styles.sectionContent}>
        <p>It is essential that others can see the raw data and source code to be able to replicate your study. We highly encourage that all articles include sufficient information to allow others to reproduce the work. This includes references to repositories that host the resulting data of the research as well as any relevant software has been coded by the authors of the article.</p>

        <p>Data repositories should be:</p>

        <ul>
          <li>
            Reusable by all researchers.
            <ul><li>As a general rule, anyone should be able to understand what the data is about using only the information provided</li></ul>
          </li>
          <li>
            Accessible without unnecessary restrictions.
            <ul><li>We recommend that the data be openly published under a license that allows for data reuse.</li></ul>
          </li>
          <li>
            Stable and provide long-term persistence.
            <ul><li>This ensures preservation so that your datasets continue to be available in a useable form in the future.</li></ul>
          </li>
        </ul>

        <p>Some suggestions for repositories to host general datasets alongside a Research Exchange article include:</p>

        <ul>
          <li><a href="https://figshare.com/">Figshare</a></li>
          <li><a href="https://datadryad.org/">Dryad</a></li>
          <li><a href="https://zenodo.org/">Zenodo</a></li>
          <li><a href="https://osf.io/">Open Science Framework</a></li>
          <li><a href="https://dataverse.harvard.edu/">Dataverse</a></li>
        </ul>

        <p>Some suggestions for repositories to host source code alongside a Research Exchange article include:</p>

        <ul>
          <li><a href="https://github.com/">GitHub</a></li>
          <li><a href="https://gitlab.com/">GitLab</a></li>
          <li><a href="https://bitbucket.org/">BitBucket</a></li>
        </ul>
      </div>

      <h2 className={styles.sectionName}>Approval Process</h2>

      <div className={styles.sectionContent}>
        <p>Once an article has been posted, any expert can write an expert review and select an approval status of either Approved, Rejected, or Needs Work. The expert review process is entirely transparent: each expert review, along with the approval status selected by the reviewer and comments that reference specific sections of the article, is published with the expert&apos;s name and research field alongside the article. For more information on how to become an expert, please see the corresponding section below.</p>

        <p>We strongly encourage the authors and other members of the community to address and discuss expert criticisms by commenting on expert reviews. In order to reduce bias, any comments made by the authors are anyonmous if the article has not yet been approved for publication. The author names will be visible immediately once the article has been approved.</p>

        <p>An article is approved for publication immediately after three experts have approved the article. You can track the approval status of any articles that you&apos;ve posted by clicking on your user profile in the menu bar.</p>

        <p>Expert reviews will no longer be received on articles that have been approved for publication, but to continue to facilitate open discussion and promote the exchange of ideas, comments may still be made on the article even after the article has been approved for publication.</p>
      </div>

      <h2 className={styles.sectionName}>How to Become an Expert</h2>

      <div className={styles.sectionContent}>
        <p>Experts are designated by the administrators of Research Exchange. If you have a Research Exchange account, you can request to become an expert through the expert request form in your user profile page. Submit the form and provide your field of expertise along with a detailed reason for requesting to become an expert. Once you submit the expert reqest form, the administrators will delibrate on your reuqest with the current panel of experts and a decision will be made to either deny or approve your expert request.</p>
      </div>
    </CardLayout>
  );
};

export default Guidelines;