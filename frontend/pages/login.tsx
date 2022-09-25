
import { useState } from "react";
import { NextPage } from "next";
import Head from "next/head";
import { useRouter } from "next/router";
import { FaRegUser } from "react-icons/fa";
import { BiLockOpenAlt } from "react-icons/bi";
import { SubmitHandler, useForm } from "react-hook-form";

import CardLayout from "../components/CardLayout";
import Authentication from "../components/Authentication";
import { login } from "../utils/api";
import { getCookie, setCookies } from "cookies-next";

interface Inputs {
  username: string;
  password: string;
}

const Login: NextPage = () => {
  const router = useRouter();

  const [extraErrorMessage, setExtraErrorMessage] = useState<string>();

  const { register, formState: { errors }, handleSubmit } = useForm<Inputs>();

  const onSubmit: SubmitHandler<Inputs> = async (data) => {
    const res = await login(data);

    if (res.ok) {
      const body = await (res.json() as Promise<{ "access_token": string }>);
      const token = body["access_token"];

      const nextMonth = new Date();
      nextMonth.setMonth(nextMonth.getMonth() + 2);
      setCookies("auth", token, { expires: nextMonth });

      router.push("/");
    } else {
      setExtraErrorMessage("Incorrect login credentials.");
    }
  };

  const inputInfos = [
    {
      icon: <FaRegUser />,
      input: <input type="text" {...register("username", { required: true })} placeholder="Username" />,
      errorMessage: errors.username && "Please specify a username to log in with"
    },
    {
      icon: <BiLockOpenAlt />,
      input: <input type="password" {...register("password", { required: true })} placeholder="Password" />,
      errorMessage: errors.password && "Please specify a password to log in with"
    }
  ];

  return (
    <CardLayout width={550}>
      <Head>
        <title>Log In - Research Exchange</title>
      </Head>
      <Authentication
        extraErrorMessage={extraErrorMessage}
        inputInfos={inputInfos}
        isLogin={true}
        onSubmit={handleSubmit(onSubmit)}
      />
    </CardLayout>

  );
};

export default Login;