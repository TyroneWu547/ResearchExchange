import { UserRole } from "../types";

import styles from "../styles/RoleIcon.module.scss";

interface Props {
  role: UserRole;
  where: "profile" | "comment"
}

export default function RoleIcon({ role, where }: Props) {
  let className = "";
  if (role.startsWith("Expert in ")) {
    className = styles.expert;
  } else if (role === "Admin") {
    className = styles.admin;
  } else if (role === "Author") {
    className = styles.author;
  } else {
    return null;
  }

  return <span className={`${styles.roleIcon} ${className} ${where === "profile" ? styles.inProfile : ""}`}>{role}</span>;
}