import HaikuTitle from "../components/HaikuTitle";
import { useEffect, useState } from "react";
import { getMeetings } from "../api/meetingApi";

export default function MeetingListPage({
  onCreate,
  onOpen,
  onEdit,
  onBack,
  onLoginRequired,
}) {
  const [meetings, setMeetings] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [loadVersion, setLoadVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function loadMeetings() {
      try {
        const result = await getMeetings();

        if (!cancelled) {
          setMeetings(result);
        }
      } catch (error) {
        if (cancelled) return;

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
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    loadMeetings();

    return () => {
      cancelled = true;
    };
  }, [loadVersion, onLoginRequired]);

  function retryLoad() {
    setError("");
    setIsLoading(true);
    setLoadVersion((version) => version + 1);
  }

  return (
    <main className="haiku-list-page">
      <header className="haiku-list-header">
        <HaikuTitle />
        <h2>句会作品一覧</h2>
      </header>

      <div className="haiku-list-actions">
        <button type="button" onClick={onCreate}>
          句会を登録する
        </button>
        <button type="button" className="secondary-button" onClick={onBack}>
          ホームへ戻る
        </button>
      </div>

      {isLoading ? (
        <p className="haiku-list-message" role="status">
          句会を読み込んでいます…
        </p>
      ) : error ? (
        <>
          <p className="error-message haiku-list-message" role="alert">
            {error}
          </p>
          <div className="haiku-list-actions">
            <button type="button" onClick={retryLoad}>
              再試行
            </button>
          </div>
        </>
      ) : meetings.length === 0 ? (
        <p className="haiku-list-message" role="status">
          まだ句会が登録されていません。
        </p>
      ) : (
        <>
          <p className="haiku-list-message">
            全{meetings.length}件・開催日が新しい順
          </p>

          <div className="meeting-list">
            {meetings.map((meeting) => (
              <article className="meeting-card" key={meeting.meetingId}>
                <h3>{meeting.meetingName}</h3>

                <dl>
                  <div>
                    <dt>開催日</dt>
                    <dd>
                      <time dateTime={meeting.meetingDate}>
                        {meeting.meetingDate.replaceAll("-", "/")}
                      </time>
                    </dd>
                  </div>

                  <div>
                    <dt>兼題</dt>
                    <dd>{meeting.theme || "－"}</dd>
                  </div>
                </dl>

                <div className="haiku-list-actions">
                  <button
                    type="button"
                    onClick={() => onOpen(meeting)}
                    aria-label={`${meeting.meetingName}の俳句を見る`}
                  >
                    俳句を詠む
                  </button>

                  <button
                    type="button"
                    className="secondary-button"
                    onClick={() => onEdit(meeting)}
                    aria-label={`${meeting.meetingName}を編集する`}
                  >
                    編集
                  </button>
                </div>
              </article>
            ))}
          </div>
        </>
      )}
    </main>
  );
}

