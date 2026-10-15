import HaikuTitle from "../components/HaikuTitle";
import { useState } from "react";
import { deleteHaiku, updateHaiku } from "../api/haikuApi";

const POINT_OPTIONS = Array.from({ length: 20 }, (_, index) => index + 1);

function HaikuEditPage({ haiku, onSaved, onDeleted, onBack, onLoginRequired }) {
  const [text, setText] = useState(haiku.text);
  const [season, setSeason] = useState(haiku.season);
  const [status, setStatus] = useState(haiku.status);
  const [selfEvaluation, setSelfEvaluation] = useState(
    haiku.selfEvaluation ?? "無印",
  );
  const [points, setPoints] = useState(
    haiku.points == null ? "" : String(haiku.points),
  );
  const [memo, setMemo] = useState(haiku.memo ?? "");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const isBusy = isSubmitting || isDeleting;

  function handleError(error) {
    if (error.status === 401) {
      onLoginRequired();
      return;
    }

    setError(
      error instanceof TypeError
        ? "サーバーに接続できませんでした。"
        : error.message,
    );
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (isBusy) return;

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
      await updateHaiku(
        haiku.haikuId,
        text,
        season,
        status,
        selfEvaluation,
        points === "" ? null : Number(points),
        memo,
      );
      onSaved({ haikuId: haiku.haikuId, season });
    } catch (error) {
      handleError(error);
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete() {
    if (isBusy) return;

    const confirmed = window.confirm(
      `「${haiku.text}」を削除しますか？\n削除すると元に戻せません。`,
    );

    if (!confirmed) return;

    setError("");
    setIsDeleting(true);

    try {
      await deleteHaiku(haiku.haikuId);
      onDeleted();
    } catch (error) {
      handleError(error);
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <HaikuTitle />
        <h2>俳句編集</h2>

        <form onSubmit={handleSubmit}>
          <label htmlFor="edit-haiku-text">俳句</label>
          <input
            id="edit-haiku-text"
            type="text"
            value={text}
            onChange={(event) => setText(event.target.value)}
            required
            aria-describedby="edit-haiku-text-help"
            disabled={isBusy}
          />
          <small id="edit-haiku-text-help">20文字以内・空白や改行なし</small>

          <label htmlFor="edit-haiku-season">季節</label>
          <select
            id="edit-haiku-season"
            value={season}
            onChange={(event) => setSeason(event.target.value)}
            required
            disabled={isBusy}
          >
            <option value="">季節を選んでください</option>
            <option value="春">春</option>
            <option value="夏">夏</option>
            <option value="秋">秋</option>
            <option value="冬">冬</option>
          </select>

          <label htmlFor="edit-haiku-status">状態</label>
          <select
            id="edit-haiku-status"
            value={status}
            onChange={(event) => setStatus(event.target.value)}
            disabled={isBusy}
          >
            <option value="推敲中">推敲中</option>
            <option value="完成">完成</option>
          </select>

          <label htmlFor="edit-haiku-evaluation">自己評価</label>
          <select
            id="edit-haiku-evaluation"
            value={selfEvaluation}
            onChange={(event) => setSelfEvaluation(event.target.value)}
            disabled={isBusy}
          >
            <option value="無印"></option>
            <option value="◎">◎</option>
            <option value="○">○</option>
          </select>

          <label htmlFor="edit-haiku-points">点句</label>
          <select
            id="edit-haiku-points"
            value={points}
            onChange={(event) => setPoints(event.target.value)}
            disabled={isBusy}
          >
            <option value="">－</option>
            {POINT_OPTIONS.map((point) => (
              <option key={point} value={point}>
                {point}
              </option>
            ))}
          </select>

          <label htmlFor="edit-haiku-memo">メモ（任意）</label>
          <textarea
            id="edit-haiku-memo"
            rows={4}
            value={memo}
            onChange={(event) => setMemo(event.target.value)}
            disabled={isBusy}
          />

          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          <button type="submit" disabled={isBusy}>
            {isSubmitting ? "保存中…" : "変更を保存する"}
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={onBack}
            disabled={isBusy}
          >
            一覧へ戻る
          </button>

          <button
            type="button"
            className="danger-button"
            onClick={handleDelete}
            disabled={isBusy}
          >
            {isDeleting ? "削除中…" : "この俳句を削除する"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default HaikuEditPage;

