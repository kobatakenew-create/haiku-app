import { useState } from "react";
import { createHaiku } from "../api/haikuApi";

function HaikuCreatePage({ onSaved, onBack, onLoginRequired }) {
  const [text, setText] = useState("");
  const [season, setSeason] = useState("");
  const [status, setStatus] = useState("推敲中");
  const [memo, setMemo] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (isSubmitting) return;

    setError("");

    if (!text.trim()) {
      setError("俳句を入力してください。");
      return;
    }

    if (Array.from(text).length > 20) {
      setError("俳句は20文字以内にしてください。");
      return;
    }

    if (/\s/u.test(text)) {
      setError("俳句に空白や改行を入れないでください。");
      return;
    }

    setIsSubmitting(true);

    try {
      const result = await createHaiku(text, season, status, memo);
      onSaved(result);
    } catch (error) {
      if (error.status === 401) {
        onLoginRequired();
        return;
      }

      setError(
        error instanceof TypeError
          ? "サーバーに接続できませんでした。"
          : error.message,
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <h1>俳句帳</h1>
        <h2>俳句登録</h2>

        <form onSubmit={handleSubmit}>
          <label htmlFor="haiku-text">俳句</label>
          <input
            id="haiku-text"
            type="text"
            value={text}
            onChange={(event) => setText(event.target.value)}
            required
            aria-describedby="haiku-text-help"
          />
          <small id="haiku-text-help">20文字以内・空白や改行なし</small>

          <label htmlFor="haiku-season">季節</label>
          <select
            id="haiku-season"
            value={season}
            onChange={(event) => setSeason(event.target.value)}
            required
          >
            <option value="">季節を選んでください</option>
            <option value="春">春</option>
            <option value="夏">夏</option>
            <option value="秋">秋</option>
            <option value="冬">冬</option>
          </select>

          <label htmlFor="haiku-status">状態</label>
          <select
            id="haiku-status"
            value={status}
            onChange={(event) => setStatus(event.target.value)}
          >
            <option value="推敲中">推敲中</option>
            <option value="完成">完成</option>
          </select>

          <label htmlFor="haiku-memo">メモ（任意）</label>
          <textarea
            id="haiku-memo"
            rows={4}
            value={memo}
            onChange={(event) => setMemo(event.target.value)}
          />

          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? "保存中…" : "保存する"}
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={onBack}
            disabled={isSubmitting}
          >
            ホームへ戻る
          </button>
        </form>
      </section>
    </main>
  );
}

export default HaikuCreatePage;
