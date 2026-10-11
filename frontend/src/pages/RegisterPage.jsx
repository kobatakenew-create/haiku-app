import { useRef, useState } from "react";
import { registerUser } from "../api/authApi";

export default function RegisterPage({ onRegistered, onBack }) {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const submissionLock = useRef(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (submissionLock.current) return;
    setError("");

    if (password !== passwordConfirmation) {
      setError("確認用パスワードが一致しません");
      return;
    }

    submissionLock.current = true;
    setIsSubmitting(true);

    try {
      await registerUser(loginId, password);
      setPassword("");
      setPasswordConfirmation("");
      onRegistered();
    } catch (error) {
      setError(
        error instanceof TypeError
          ? "通信できませんでした。接続を確認してください。"
          : error.message,
      );
    } finally {
      submissionLock.current = false;
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card" aria-labelledby="register-title">
        <h1>俳句帳</h1>
        <h2 id="register-title">ユーザー登録</h2>

        <form onSubmit={handleSubmit}>
          <label htmlFor="register-id">ログインID</label>
          <input
            id="register-id"
            value={loginId}
            onChange={(event) => {
              setLoginId(event.target.value);
              setError("");
            }}
            autoComplete="username"
            pattern="[a-zA-Z0-9]+"
            maxLength={10}
            aria-describedby="register-id-help"
            required
          />
          <small id="register-id-help">半角英数字・1～10文字</small>

          <label htmlFor="register-password">パスワード</label>
          <input
            id="register-password"
            type="password"
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
              setError("");
            }}
            autoComplete="new-password"
            pattern="[a-zA-Z0-9]+"
            minLength={8}
            maxLength={10}
            aria-describedby="register-password-help"
            required
          />
          <small id="register-password-help">半角英数字・8～10文字</small>

          <label htmlFor="register-confirmation">パスワード（確認）</label>
          <input
            id="register-confirmation"
            type="password"
            value={passwordConfirmation}
            onChange={(event) => {
              setPasswordConfirmation(event.target.value);
              setError("");
            }}
            autoComplete="new-password"
            minLength={8}
            maxLength={10}
            required
          />

          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? "登録中…" : "登録する"}
          </button>
        </form>

        <button
          type="button"
          className="secondary-button"
          onClick={onBack}
          disabled={isSubmitting}
        >
          ログイン画面へ戻る
        </button>
      </section>
    </main>
  );
}

