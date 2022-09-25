import Link from "next/link";
import { FaSearch } from "react-icons/fa";
import { HiLightBulb } from "react-icons/hi";

import IconInputContainer from "./IconInputContainer";

import styles from "../styles/Header.module.scss";
import React, { useState } from "react";
import { useRouter } from "next/router";
import useUser from "../hooks/useUser";
import { removeCookies } from "cookies-next";

const Header = () => {
  const router = useRouter();

  const [searchQuery, setSearchQuery] = useState("");
  const user = useUser();

  function handleSubmitSearchQuery(e: React.KeyboardEvent) {
    if (e.key === "Enter") {
      router.push({ pathname: "/articles", query: { searchValue: searchQuery } });
    }
  }

  function handleLogout() {
    removeCookies("auth");
    router.push('/');
  }

  return (
    <header className={styles.header}>
      <span className={styles.headerPrimary}>
        <Link href="/">
          <a className={styles.title}>Research Exchange</a>
        </Link>
        <Link href="/articles">
          <a>View Articles</a>
        </Link>
        <Link href="/guidelines">
          <a>Guidelines & Policies</a>
        </Link>
        {user && user.roles.includes("Admin") && (
          <Link href="/manage-experts">
            <a>Manage Experts</a>
          </Link>
        )}
      </span>

      <IconInputContainer icon={<FaSearch />} className={styles.searchBar}>
        <input
          type="search"
          placeholder="Search for articles..."
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
          onKeyUp={handleSubmitSearchQuery}
        />
      </IconInputContainer>

      <span className={styles.authLinks}>
        {user ? (
          <>
            <Link href={`/users/${user.sub}`}>
              <a>My Profile</a>
            </Link>

            <button className={styles.logoutButton} onClick={handleLogout}>
              Log Out
            </button>
          </>
        ) : (
          <>
            <Link href="/login">
              <a>Log In</a>
            </Link>

            <Link href="/signup">
              <a>Sign Up</a>
            </Link>
          </>
        )}
      </span>
    </header>
  );
};

export default Header;