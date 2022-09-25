import { GetServerSideProps, NextPage } from "next";
import Link from "next/link";
import { useRouter } from "next/router";
import { SubmitHandler, useForm } from "react-hook-form";

import CardLayout from "../components/CardLayout";
import { getRequestedExperts, getExperts, assignExpert, rejectExpert, removeExpert, getAuthToken } from "../utils/api";
import { subjects } from "../utils/constants";

import styles from "../styles/ManageExperts.module.scss";

interface ExpertInfo {
  username: string;
  field: string;
}

interface NewExpertInputs {
  username: string;
  field: string;
}

interface Props {
  expertRequests: {
    username: string;
    field: string;
    reason: string;
  }[];
  experts: ExpertInfo[];
}

const ManageExperts: NextPage<Props> = ({ expertRequests, experts }) => {
  const router = useRouter();

  const { register, handleSubmit } = useForm<NewExpertInputs>();

  async function assignExpertRole(username: string, field: string) {
    await assignExpert(username, field, getAuthToken());
    router.reload();
  }

  async function rejectExpertRequest(username: string) {
    await rejectExpert(username, getAuthToken());
    router.reload();
  }

  async function removeExpertRole(username: string) {
    await removeExpert(username, getAuthToken());
    router.reload();
  }

  const onSubmitExpertAssignment: SubmitHandler<NewExpertInputs> = async ({ username, field }) => {
    await assignExpert(username, field, getAuthToken());
    router.reload();
  };

  return (
    <CardLayout width={1200}>
      <h1>Manage Experts</h1>

      <h2 className={styles.expertTitle}>Assign a new expert</h2>
      <form className={styles.assignNewExpert} onSubmit={handleSubmit(onSubmitExpertAssignment)}>
        <input type="text" placeholder="Username to assign as expert" {...register("username", { required: true })} />
        <div>
          <label htmlFor="field">Research Field</label>
          <select className={styles.selectSubject} {...register("field", { required: true })}>
            {["-- Select a field --", ...subjects, "Other"].map(subject => <option key={subject} value={subject}>{subject}</option>)}
          </select>
        </div>
        <input type="submit" value="Assign as Expert" />
      </form>

      <h2 className={styles.expertTitle}>Expert assignment requests</h2>
      {expertRequests.length > 0 ? (
        <table className={styles.expertsTable}>
          <thead>
            <th>Username</th>
            <th>Field Requested</th>
            <th>Request</th>
            <th className={styles.assignExpertCell}></th>
          </thead>
          <tbody>
            {expertRequests.map(request => (
              <tr key={request.username} className={styles.expertRow}>
                <td><Link href={`/users/${request.username}`}><a className={styles.username}>{request.username}</a></Link></td>
                <td>{request.field}</td>
                <td className={styles.requestReasonCell}>{request.reason}</td>
                <td className={styles.assignExpertCell}>
                  <button className={styles.accept} onClick={() => assignExpertRole(request.username, request.field)}>Accept</button>
                  <button className={styles.reject} onClick={() => rejectExpertRequest(request.username)}>Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : <p>No users have requested to be assigned as experts</p>}

      <h2 className={styles.expertTitle}>Experts</h2>
      {experts.length > 0 ? (
        <table className={styles.expertsTable}>
          <thead>
            <th>Username</th>
            <th>Expert Field</th>
            <th className={styles.removeExpertCell}></th>
          </thead>
          <tbody>
            {experts.map(expert => (
              <tr key={expert.username} className={styles.expertRow}>
                <td><Link href={`/users/${expert.username}`}><a className={styles.username}>{expert.username}</a></Link></td>
                <td>{expert.field}</td>
                <td className={styles.removeExpertCell}><button onClick={() => removeExpertRole(expert.username)}>Remove Expert Role</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : <p>No experts have yet been assigned</p>}
    </CardLayout>
  );
};

export const getServerSideProps: GetServerSideProps<Props> = async (context) => {
  const token = getAuthToken(context);
  const experts = await getExperts(token);
  const expertRequests = await getRequestedExperts(token);
  if (!experts || !expertRequests) {
    return {
      redirect: {
        destination: "/login",
        permanent: false
      }
    };
  }

  return {
    props: { expertRequests, experts }
  };
};

export default ManageExperts;