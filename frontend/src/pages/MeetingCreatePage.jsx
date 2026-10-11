import HaikuTitle from "../components/HaikuTitle";
import { useState } from "react";
import { createMeeting } from "../api/meetingApi";

export default function MeetingCreatePage({
  onSaved,
  onBack,
  onLoginRequired,
}) {
  const [meetingName, setMeetingName] = useState("");
  const [meetingDate, setMeetingDate] = useState("");
  const [theme, setTheme] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (isSubmitting) return;

    setError("");

    const normalizedName = meetingName.trim();
    const normalizedTheme = theme.trim();

    if (!normalizedName) {
      setError("句会名を入力してください。");
      return;
    }

    if (Array.from(normalizedName).length > 10) {
      setError("句会名は十文字以内にしてください。");
      return;
    }

    if (!meetingDate) {
      setError("開催日を入力してください。");
      return;
    }

    if (Array.from(normalizedTheme).length > 2) {
      setError("兼題は二文字以内にしてください。");
      return;
    }

    setIsSubmitting(true);

    try {
      const result = await createMeeting(
        normalizedName,
        meetingDate,
        normalizedTheme || null,
      );
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
      <section className="auth-card" aria-labelledby="meeting-create-title">
        <HaikuTitle />
        <h2 id="meeting-create-title">句会登録</h2>

        <form onSubmit={handleSubmit}>
          <label htmlFor="meeting-name">句会名</label>
          <input
            id="meeting-name"
            type="text"
            value={meetingName}
            onChange={(event) => setMeetingName(event.target.value)}
            required
            disabled={isSubmitting}
            aria-describedby="meeting-name-help"
          />
          <small id="meeting-name-help">十文字以内</small>

          <label htmlFor="meeting-date">開催日</label>
          <input
            id="meeting-date"
            type="date"
            value={meetingDate}
            onChange={(event) => setMeetingDate(event.target.value)}
            required
            disabled={isSubmitting}
          />

          <label htmlFor="meeting-theme">兼題（任意）</label>
          <input
            id="meeting-theme"
            type="text"
            value={theme}
            onChange={(event) => setTheme(event.target.value)}
            disabled={isSubmitting}
            aria-describedby="meeting-theme-help"
          />
          <small id="meeting-theme-help">二文字以内</small>

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
            句会一覧へ戻る
          </button>
        </form>
      </section>
    </main>
  );
}

