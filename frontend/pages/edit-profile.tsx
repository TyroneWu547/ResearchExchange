import { NextPage } from "next";
import { useRouter } from "next/router";
import { useEffect, useMemo, useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import CardLayout from "../components/CardLayout";
import useUser from "../hooks/useUser";

import styles from "../styles/EditProfile.module.scss";
import { getAuthToken, getUserInfo, updateProfile } from "../utils/api";

interface Inputs {
  profilePicture: FileList;
  name: string;
  email: string;
  currPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

const EditProfile: NextPage = () => {
  const user = useUser();
  const router = useRouter();

  const { register, formState: { errors }, setValue, getValues, watch, handleSubmit } = useForm<Inputs>();

  const [error, setError] = useState("");

  useEffect(() => {
    if (!user) {
      router.push("/");
    }

    getUserInfo(user.sub).then(userInfo => {
      if (userInfo === null) {
        router.push("/");
      } else {
        setValue("name", userInfo.name);
        setValue("email", userInfo.email);
      }
    });
  }, [setValue, router, user]);

  const onSubmit: SubmitHandler<Inputs> = async ({ profilePicture, name, email, currPassword, newPassword }) => {
    const formData = new FormData();
    if (profilePicture.length > 0) {
      formData.append("profilePicture", profilePicture[0], profilePicture[0].name);
    }
    formData.append("name", name);
    formData.append("email", email);
    formData.append("oldPassword", currPassword);
    formData.append("newPassword", newPassword);

    const res = await updateProfile(user.sub, formData, getAuthToken());
    if (res.status === 400) {
      setError("Incorrect password");
    } else {
      router.push(`/users/${user.sub}`);
    }
  };

  const currPassword = watch("currPassword");
  const newPassword = watch("newPassword");
  const confirmNewPassword = watch("confirmNewPassword");

  const isChangingPassword = useMemo(
    () => currPassword || newPassword || confirmNewPassword,
    [currPassword, newPassword, confirmNewPassword]
  );

  const emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;

  return (
    <CardLayout width={1000}>
      <h1>Edit Profile</h1>
      <form className={styles.editProfile} onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label>Profile picture:</label>
          <input type="file" accept=".jpg,.png" {...register("profilePicture")} />
        </div>
        <div>
          {errors.name && <p className={styles.errorMessage}>Name must be specified</p>}
          <label>Name:</label>
          <input type="text" {...register("name", { required: true })} />
        </div>
        <div>
          {errors.email && <p className={styles.errorMessage}>Email address invalid</p>}
          <label>Email address:</label>
          <input type="text" {...register("email", { pattern: emailRegex })} />
        </div>

        <h2>Change password (optional)</h2>
        <div>
          {errors.currPassword && <p className={styles.errorMessage}>Must specify</p>}
          <label>Current password:</label>
          <input type="password" {...register("currPassword", { validate: (val) => !isChangingPassword || val.length > 0 })} />
        </div>
        <div>
          {errors.newPassword && <p className={styles.errorMessage}>New password must be at least 8 characters long</p>}
          <label>New password:</label>
          <input type="password" {...register("newPassword", { validate: val => !isChangingPassword || val.length > 8 })} />
        </div>
        <div>
          {errors.confirmNewPassword && <p className={styles.errorMessage}>Confirmation password mismatch</p>}
          <label>Confirm new password:</label>
          <input type="password" {...register("confirmNewPassword", { validate: val => !isChangingPassword || val === getValues("newPassword") })} />
        </div>

        {error !== "" && <p className={styles.errorMessage}>Error: {error}</p>}
        <input type="submit" value="Submit" />
      </form>
    </CardLayout>
  );
};

export default EditProfile;