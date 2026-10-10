import { useState } from "react";
import { deleteMeeting, updateMeeting } from "../api/meetingApi";

function MeetingEditPage({ meeting, onSaved, onBack, onLoginRequired }) {
  const [meetingName, setMeetingName] = useState(meeting.meetingName);
  const [meetingDate, setMeetingDate] = useState(meeting.meetingDate);
  const [theme, setTheme] = useState(meeting.theme ?? "");
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

    const normalizedName = meetingName.trim();
    const normalizedTheme = theme.trim();

    if (!normalizedName) {
      setError("句会名を入力してください");
      return;
    }

    if ([...normalizedName].length > 10) {
      setError("句会名は十文字以内にしてください");
      return;
    }

    if (!meetingDate) {
      setError("開催日を入力してください");
      return;
    }

    if ([...normalizedTheme].length > 2) {
      setError("兼題は二文字以内にしてください");
      return;
    }

    setError("");
    setIsSubmitting(true);

    try {
      await updateMeeting(
        meeting.meetingId,
        normalizedName,
        meetingDate,
        normalizedTheme || null,
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
      `「${meeting.meetingName}」を削除しますか？\n\n` +
        "この句会への俳句の登録と点句も削除されます。\n" +
        "元の俳句は俳句帳に残ります。\n" +
        "削除した句会は元に戻せません。",
    );

    if (!confirmed) return;

    setError("");
    setIsDeleting(true);

    try {
      await deleteMeeting(meeting.meetingId);
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
        <h1>俳句帳</h1>
        <h2>句会を編集</h2>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="meeting-name">句会名</label>
          <input
            id="meeting-name"
            type="text"
            value={meetingName}
            onChange={(event) => setMeetingName(event.target.value)}
            disabled={isBusy}
            required
          />
          <p>十文字以内で入力してください。</p>

          <label htmlFor="meeting-date">開催日</label>
          <input
            id="meeting-date"
            type="date"
            value={meetingDate}
            onChange={(event) => setMeetingDate(event.target.value)}
            disabled={isBusy}
            required
          />

          <label htmlFor="meeting-theme">兼題（任意）</label>
          <input
            id="meeting-theme"
            type="text"
            value={theme}
            onChange={(event) => setTheme(event.target.value)}
            disabled={isBusy}
          />
          <p>二文字以内で入力してください。</p>

          <button type="submit" disabled={isBusy}>
            {isSubmitting ? "保存しています…" : "保存する"}
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={onBack}
            disabled={isBusy}
          >
            句会一覧へ戻る
          </button>

          <button
            type="button"
            className="danger-button"
            onClick={handleDelete}
            disabled={isBusy}
          >
            {isDeleting ? "削除しています…" : "句会を削除する"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default MeetingEditPage;
