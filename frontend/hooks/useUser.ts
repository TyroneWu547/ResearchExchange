import { useCallback, useEffect, useState } from "react";
import { getAuthToken } from "../utils/api";

interface UserInfo {
  sub: string;
  roles: string[];
  id: number;
}

export default function useUser(): UserInfo {
  const token = getAuthToken();

  const getDataFromToken = useCallback(() => {
    if (!token) {
      return null;
    }

    const encodedData = token?.split('.')[1];
    const decodedData = Buffer.from(encodedData, "base64").toString("utf-8");
    return JSON.parse(decodedData);
  }, [token]);

  const [user, setUser] = useState(getDataFromToken);

  useEffect(() => {
    setUser(getDataFromToken());
  }, [getDataFromToken]);

  return user;
}