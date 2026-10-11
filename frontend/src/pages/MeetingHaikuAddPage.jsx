import HaikuTitle from "../components/HaikuTitle";
import { useEffect, useState } from "react";
import { getHaikus } from "../api/haikuApi";
import { addMeetingHaiku, getMeetingHaikus } from "../api/meetingApi";

function MeetingHaikuAddPage({ meeting, onAdded, onBack, onLoginRequired }) {
  const [candidates, setCandidates] = useState([]);
  const [selectedHaikuId, setSelectedHaikuId] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [loadFailed, setLoadFailed] = useState(false);
  const [loadVersion, setLoadVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function loadCandidates() {
      try {
        const [haikus, meetingHaikus] = await Promise.all([
          getHaikus(),
          getMeetingHaikus(meeting.meetingId),
        ]);

        if (cancelled) return;

        const addedIds = new Set(meetingHaikus.map((haiku) => haiku.haikuId));

        setCandidates(haikus.filter((haiku) => !addedIds.has(haiku.haikuId)));
      } catch (error) {
        if (cancelled) return;

        if (error.status === 401) {
          onLoginRequired();
          return;
        }

        setLoadFailed(true);
        setError(
          error instanceof TypeError
            ? "サーバーに接続できませんでした。"
            : error.message,
        );
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    loadCandidates();

    return () => {
      cancelled = true;
    };
  }, [meeting.meetingId, loadVersion, onLoginRequired]);

  function retryLoad() {
    setError("");
    setLoadFailed(false);
    setSelectedHaikuId("");
    setIsLoading(true);
    setLoadVersion((version) => version + 1);
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (isSubmitting) return;

    if (!selectedHaikuId) {
      setError("追加する俳句を選んでください。");
      return;
    }

    setError("");
    setIsSubmitting(true);

    try {
      await addMeetingHaiku(meeting.meetingId, Number(selectedHaikuId));
      onAdded();
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

      if (error.status === 409) {
        setLoadFailed(true);
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <HaikuTitle />
        <h2>句会に俳句を追加</h2>
        <p>句会：{meeting.meetingName}</p>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        {isLoading ? (
          <p role="status">俳句を読み込んでいます…</p>
        ) : loadFailed ? (
          <button type="button" onClick={retryLoad}>
            俳句一覧を読み直す
          </button>
        ) : candidates.length === 0 ? (
          <p role="status">
            追加できる俳句がありません。
            俳句が未登録か、すべてこの句会に追加済みです。
          </p>
        ) : (
          <form className="auth-form" onSubmit={handleSubmit}>
            <label htmlFor="meeting-haiku-select">追加する俳句</label>
            <select
              id="meeting-haiku-select"
              value={selectedHaikuId}
              onChange={(event) => setSelectedHaikuId(event.target.value)}
              disabled={isSubmitting}
              required
            >
              <option value="">俳句を選んでください</option>
              {candidates.map((haiku) => (
                <option key={haiku.haikuId} value={haiku.haikuId}>
                  {haiku.text}（{haiku.season}）
                </option>
              ))}
            </select>

            <button type="submit" disabled={isSubmitting || !selectedHaikuId}>
              {isSubmitting ? "追加しています…" : "句会に追加する"}
            </button>
          </form>
        )}

        <button
          type="button"
          className="secondary-button"
          onClick={onBack}
          disabled={isSubmitting}
        >
          句会の俳句一覧へ戻る
        </button>
      </section>
    </main>
  );
}

export default MeetingHaikuAddPage;

