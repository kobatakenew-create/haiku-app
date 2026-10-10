import { sendRequest } from "./apiClient";

export function registerUser(loginId, password) {
  return sendRequest("/api/users", "POST", { loginId, password });
}

export function loginUser(loginId, password, rememberMe = false) {
  return sendRequest("/api/login", "POST", {
    loginId,
    password,
    rememberMe,
  });
}

export function getCurrentSession() {
  return sendRequest("/api/session");
}

export function logoutUser() {
  return sendRequest("/api/logout", "POST");
}
