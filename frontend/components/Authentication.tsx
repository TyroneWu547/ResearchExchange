import Link from "next/link";
import IconInputContainer from "./IconInputContainer";

import styles from "../styles/Authentication.module.scss";
import { FieldError } from "react-hook-form";
import React from "react";

interface Input {
  icon: React.ReactElement;
  input: React.ReactElement;
  errorMessage: string | undefined;
}

interface Props {
  inputInfos: Input[];
  isLogin: boolean;
  extraErrorMessage: string | undefined;
  onSubmit: React.FormEventHandler<HTMLFormElement>;
}

const Authentication = ({ extraErrorMessage, inputInfos, isLogin, onSubmit }: Props) => (
  <div className={styles.authBox}>
    <h1>{isLogin ? "Log In" : "Sign Up"}</h1>

    <p className={styles.otherAuth}>
      {isLogin ? (
        <>
          New User?{' '}
          <Link href="/signup">
            <a>Sign Up</a>
          </Link>
        </>
      ) : (
        <>
          Already have an account?{' '}
          <Link href="/login">
            <a>Log In</a>
          </Link>
        </>
      )}
    </p>
    <form onSubmit={onSubmit}>
      {inputInfos.map(({ icon, input, errorMessage }, i) => (
        <React.Fragment key={i}>
          {errorMessage && <p className={styles.errorMessage}>{errorMessage}</p>}
          <IconInputContainer icon={icon} className={styles.authInfoInput}>
            {input}
          </IconInputContainer>
        </React.Fragment>
      ))}

      {extraErrorMessage && <p className={styles.errorMessage}>{extraErrorMessage}</p>}

      <input type="submit" className={styles.authSubmit} value={isLogin ? "Log In" : "Sign Up"} />
    </form>
  </div>
);

export default Authentication;