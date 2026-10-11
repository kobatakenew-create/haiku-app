import HaikuTitle from "../components/HaikuTitle";
import { useState } from "react";
import {
  deleteMeetingHaiku,
  updateMeetingHaikuPoints,
} from "../api/meetingApi";

const POINT_OPTIONS = Array.from({ length: 20 }, (_, index) => index + 1);

function MeetingHaikuPointsEditPage({
  meeting,
  haiku,
  onSaved,
  onBack,
  onLoginRequired,
}) {
  const [points, setPoints] = useState(
    haiku.points == null ? "" : String(haiku.points),
  );
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const isBusy = isSubmitting || isDeleting;

  function showError(error) {
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
    setIsSubmitting(true);

    try {
      await updateMeetingHaikuPoints(
        meeting.meetingId,
        haiku.meetingHaikuId,
        points === "" ? null : Number(points),
      );

      onSaved();
    } catch (error) {
      showError(error);
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete() {
    if (isBusy) return;

    const confirmed = window.confirm(
      `「${haiku.text}」を「${meeting.meetingName}」から外しますか？\n\n` +
        "この句会での点句も削除されます。\n" +
        "元の俳句は俳句帳に残ります。",
    );

    if (!confirmed) return;

    setError("");
    setIsDeleting(true);

    try {
      await deleteMeetingHaiku(meeting.meetingId, haiku.meetingHaikuId);

      onSaved();
    } catch (error) {
      showError(error);
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <HaikuTitle />
        <h2>点句を変更</h2>

        <p>句会：{meeting.meetingName}</p>

        <p
          style={{
            fontSize: "1.1em",
            fontWeight: 700,
            overflowWrap: "anywhere",
          }}
        >
          俳句：{haiku.text}
        </p>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="meeting-haiku-points">点句</label>

          <select
            id="meeting-haiku-points"
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

          <button type="submit" disabled={isBusy}>
            {isSubmitting ? "保存しています…" : "保存する"}
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={onBack}
            disabled={isBusy}
          >
            句会の俳句一覧へ戻る
          </button>

          <button
            type="button"
            className="danger-button"
            onClick={handleDelete}
            disabled={isBusy}
          >
            {isDeleting ? "外しています…" : "句会から外す"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default MeetingHaikuPointsEditPage;

