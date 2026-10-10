import { useEffect, useState } from "react";
import { getHaikus } from "../api/haikuApi";

export default function HomePage({
  notice,
  onCreate,
  onList,
  onMeetings,
  onLogout,
  onLoginRequired,
  logoutError,
  isLoggingOut,
}) {
  const [haikuCount, setHaikuCount] = useState(null);
  const [countError, setCountError] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [loadVersion, setLoadVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function loadCount() {
      try {
        const haikus = await getHaikus();

        if (!cancelled) {
          setHaikuCount(haikus.length);
        }
      } catch (error) {
        if (cancelled) return;

        if (error.status === 401) {
          onLoginRequired();
          return;
        }

        setCountError("俳句数を取得できませんでした。");
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    loadCount();

    return () => {
      cancelled = true;
    };
  }, [loadVersion, onLoginRequired]);

  function retryLoad() {
    setCountError("");
    setIsLoading(true);
    setLoadVersion((version) => version + 1);
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <h1>俳句帳</h1>
        <h2>ホーム</h2>

        {notice && <p role="status">{notice}</p>}

        {isLoading ? (
          <p role="status">俳句数を確認しています…</p>
        ) : countError ? (
          <>
            <p className="error-message" role="alert">
              {countError}
            </p>
            <button
              type="button"
              className="secondary-button"
              onClick={retryLoad}
              disabled={isLoggingOut}
            >
              再試行
            </button>
          </>
        ) : (
          <p role="status">登録済みの俳句：{haikuCount}句</p>
        )}

        <button type="button" onClick={onCreate} disabled={isLoggingOut}>
          俳句を登録する
        </button>

        <button
          type="button"
          className="secondary-button"
          onClick={onList}
          disabled={isLoggingOut}
        >
          俳句一覧を見る
        </button>

        <button
          type="button"
          className="secondary-button"
          onClick={onMeetings}
          disabled={isLoggingOut}
        >
          句会一覧を見る
        </button>

        {logoutError && (
          <p className="error-message" role="alert">
            {logoutError}
          </p>
        )}

        <button
          type="button"
          className="secondary-button"
          onClick={onLogout}
          disabled={isLoggingOut}
        >
          {isLoggingOut ? "ログアウト中…" : "ログアウト"}
        </button>
      </section>
    </main>
  );
}
