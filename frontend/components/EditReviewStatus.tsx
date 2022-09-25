import useUser from "../hooks/useUser";
import { ExpertReview, ReviewStatus } from "../types";

import styles from "../styles/EditReviewStatus.module.scss";
import { useState } from "react";
import { allStatuses } from "../utils/constants";
import { statusIcon } from "../utils/jsxUtils";
import { useRouter } from "next/router";
import { editReviewStatus, getAuthToken } from "../utils/api";

interface Props {
  review: ExpertReview;
  statusClass: string;
}

export default function EditReviewStatus({ review, statusClass }: Props) {
  const router = useRouter();
  const user = useUser();

  const [editStatusEnabled, setEditStatusEnabled] = useState(false);

  async function editStatus(newStatus: ReviewStatus) {
    await editReviewStatus(review.id, newStatus, getAuthToken());
    router.reload();
  }

  const statusButtonClass = (status: ReviewStatus) => ({
    "Approved": styles.acceptStatusButton,
    "Needs Work": styles.needsWorkStatusButton,
    "Rejected": styles.rejectStatusButton
  }[status]);

  return (!user || user.sub !== review.author.username) ? null : (
    <div>
      <button onClick={() => setEditStatusEnabled(!editStatusEnabled)}>{editStatusEnabled ? "Cancel" : "Edit Status"}</button>
      {editStatusEnabled && (
        <div className={styles.statuses}>
          {allStatuses.map(status => (
            <button key={status} className={`${statusClass} ${statusButtonClass(status)}`} onClick={() => editStatus(status)}>
              {statusIcon(status)}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}