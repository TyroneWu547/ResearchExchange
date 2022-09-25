import "../styles/globals.scss";
import "../styles/ReactPdfAnnotationLayer.css";

import type { AppProps } from "next/app";
import { pdfjs } from "react-pdf";
import Head from "next/head";

import Header from "../components/Header";

pdfjs.GlobalWorkerOptions.workerSrc = process.env.NODE_ENV === "production"
  ? `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.min.js`
  : `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>Research Exchange</title>
        <meta name="description" content="A public forum for sharing and commenting on research" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <Header />
      <Component {...pageProps} />
    </>
  );
}

export default MyApp;