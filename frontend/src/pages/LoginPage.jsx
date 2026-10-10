import { useState } from "react";
import { loginUser } from "../api/authApi";

export default function LoginPage({ onLogin, onRegister }) {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (isSubmitting) return;

    setError("");
    setIsSubmitting(true);

    try {
      const user = await loginUser(loginId, password, rememberMe);
      setPassword("");
      onLogin(user);
    } catch (error) {
      setError(
        error instanceof TypeError
          ? "通信できませんでした。接続を確認してください。"
          : error.message,
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card" aria-labelledby="login-title">
        <h1>俳句帳</h1>
        <h2 id="login-title">ログイン</h2>

        <form onSubmit={handleSubmit}>
          <label htmlFor="login-id">ログインID</label>
          <input
            id="login-id"
            value={loginId}
            onChange={(event) => setLoginId(event.target.value)}
            autoComplete="username"
            maxLength={10}
            required
            disabled={isSubmitting}
          />

          <label htmlFor="login-password">パスワード</label>
          <input
            id="login-password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            autoComplete="current-password"
            maxLength={10}
            required
            disabled={isSubmitting}
          />

          <label className="remember-me" htmlFor="remember-me">
            <input
              id="remember-me"
              type="checkbox"
              checked={rememberMe}
              onChange={(event) => setRememberMe(event.target.checked)}
              disabled={isSubmitting}
            />
            <span>ログイン状態を維持する</span>
          </label>

          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? "ログイン中…" : "ログイン"}
          </button>
        </form>

        <button
          type="button"
          className="secondary-button"
          onClick={onRegister}
          disabled={isSubmitting}
        >
          新規ユーザー登録
        </button>
      </section>
    </main>
  );
}
