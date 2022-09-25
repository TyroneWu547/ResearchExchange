import { IconContext } from "react-icons";
import { FaCheck, FaTimes } from "react-icons/fa";

import { ReviewStatus } from "../types";
import styles from "../styles/shared.module.scss";
import { IoChatbox } from "react-icons/io5";
import React from "react";

export const statusClass = (status: ReviewStatus) => ({
  "Approved": styles.acceptedStatus,
  "Rejected": styles.rejectedStatus,
  "Needs Work": styles.needsWorkStatus
}[status]);

export const statusIcon = (status: ReviewStatus, className: string = "") => ({
  "Approved": <IconContext.Provider value={{ className }}><FaCheck /></IconContext.Provider>,
  "Rejected": <IconContext.Provider value={{ className }}><FaTimes /></IconContext.Provider>,
  "Needs Work": <IconContext.Provider value={{ className }}><IoChatbox /></IconContext.Provider>
}[status]);

export const splitParagraphs = (text: string) =>
  text
    .split("\n")
    .map<React.ReactNode>((paragraph, i) => <span key={i}>{paragraph}</span>)
    .reduce((prev, curr, i) => [prev, <br key={`br${i}`} className={styles.paragraphBreak} />, curr]);