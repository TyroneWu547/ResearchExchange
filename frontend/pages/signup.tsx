import { NextPage } from "next";
import { useRouter } from "next/router";
import { useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { FaRegEnvelope, FaRegUser } from "react-icons/fa";
import { BiLockOpenAlt } from "react-icons/bi";

import Authentication from "../components/Authentication";
import CardLayout from "../components/CardLayout";
import { signup } from "../utils/api";

interface Inputs {
  name: string;
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
}

const SignUp: NextPage = () => {
  const router = useRouter();

  const [extraErrorMessage, setExtraErrorMessage] = useState<string>();

  const { register, formState: { errors }, getValues, handleSubmit } = useForm<Inputs>();

  const emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;

  const onSubmit: SubmitHandler<Inputs> = async (data) => {
    const res = await signup(data);

    if (res.ok) {
      router.push("/login");
    } else {
      setExtraErrorMessage("A user with this username already exists");
    }
  };

  const inputInfos = [
    {
      icon: <FaRegUser />,
      input: <input type="text" {...register("name", { required: true, validate: () => extraErrorMessage })} placeholder="Full Name" />,
      errorMessage: errors.name && "Must include value for name"
    },
    {
      icon: <FaRegEnvelope />,
      input: <input type="text" {...register("email", { pattern: emailRegex })} placeholder="Email address" />,
      errorMessage: errors.email && "Email address invalid"
    },
    {
      icon: <FaRegUser />,
      input: <input type="text" {...register("username", { pattern: /\w{6,}/ })} placeholder="Username" />,
      errorMessage: errors.username && "Username must be at least 6 characters long comprised of letters, numbers, or underscores"
    },
    {
      icon: <BiLockOpenAlt />,
      input: <input type="password" {...register("password", { minLength: 8 })} placeholder="Password" />,
      errorMessage: errors.password && "Password must be at least 8 characters long"
    },
    {
      icon: <BiLockOpenAlt />,
      input: <input type="password" {...register("confirmPassword", { validate: val => val === getValues("password") })} placeholder="Confirm password" />,
      errorMessage: errors.confirmPassword && "Confirmation password mismatch"
    }
  ];

  return (
    <CardLayout width={550}>
      <Authentication
        extraErrorMessage={extraErrorMessage}
        inputInfos={inputInfos}
        isLogin={false}
        onSubmit={handleSubmit(onSubmit)}
      />
    </CardLayout>
  );
};

export default SignUp;