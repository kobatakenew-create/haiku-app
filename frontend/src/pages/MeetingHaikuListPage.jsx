import { useEffect, useState } from "react";
import { getMeetingHaikus } from "../api/meetingApi";

const SEASONS = ["春", "夏", "秋", "冬"];
const PAGE_SIZE = 5;

function MeetingHaikuListPage({
  meeting,
  onAdd,
  onEditPoints,
  onBack,
  onLoginRequired,
}) {
  const [haikus, setHaikus] = useState([]);
  const [view, setView] = useState("points");
  const [season, setSeason] = useState("春");
  const [page, setPage] = useState(1);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [loadVersion, setLoadVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function loadHaikus() {
      try {
        const result = await getMeetingHaikus(meeting.meetingId);

        if (!cancelled) {
          setHaikus(result);
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

    loadHaikus();

    return () => {
      cancelled = true;
    };
  }, [meeting.meetingId, loadVersion, onLoginRequired]);

  function retryLoad() {
    setError("");
    setIsLoading(true);
    setPage(1);
    setLoadVersion((version) => version + 1);
  }

  function changeView(nextView) {
    setView(nextView);
    setPage(1);
  }

  function changeSeason(nextSeason) {
    setSeason(nextSeason);
    setPage(1);
  }

  const sortedHaikus = [...haikus].sort((a, b) => {
    if (a.points == null && b.points == null) {
      return b.meetingHaikuId - a.meetingHaikuId;
    }
    if (a.points == null) return 1;
    if (b.points == null) return -1;

    return b.points - a.points || b.meetingHaikuId - a.meetingHaikuId;
  });

  const displayedHaikus =
    view === "season"
      ? sortedHaikus.filter((haiku) => haiku.season === season)
      : sortedHaikus;

  const pageCount = Math.max(1, Math.ceil(displayedHaikus.length / PAGE_SIZE));
  const currentPage = Math.min(page, pageCount);
  const startIndex = (currentPage - 1) * PAGE_SIZE;
  const pageHaikus = displayedHaikus.slice(startIndex, startIndex + PAGE_SIZE);

  return (
    <main className="haiku-list-page">
      <header className="haiku-list-header">
        <h1>俳句帳</h1>
        <h2>{meeting.meetingName}</h2>
        <p>
          開催日：{meeting.meetingDate.replaceAll("-", "/")}
          <br />
          兼題：{meeting.theme || "－"}
        </p>
      </header>

      <div className="haiku-list-actions">
        <button type="button" onClick={onAdd}>
          登録済みの俳句から追加する
        </button>
        <button type="button" className="secondary-button" onClick={onBack}>
          句会一覧へ戻る
        </button>
      </div>

      <div className="haiku-filter-buttons" role="group" aria-label="表示方法">
        <button
          type="button"
          aria-pressed={view === "points"}
          onClick={() => changeView("points")}
        >
          点句順
        </button>
        <button
          type="button"
          aria-pressed={view === "season"}
          onClick={() => changeView("season")}
        >
          季節別
        </button>
      </div>

      {view === "season" && (
        <div className="haiku-filter-buttons" role="group" aria-label="季節">
          {SEASONS.map((item) => (
            <button
              type="button"
              key={item}
              aria-pressed={season === item}
              onClick={() => changeSeason(item)}
            >
              {item}
            </button>
          ))}
        </div>
      )}

      {isLoading ? (
        <p className="haiku-list-message" role="status">
          俳句を読み込んでいます…
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
      ) : haikus.length === 0 ? (
        <p className="haiku-list-message" role="status">
          この句会には、まだ俳句が追加されていません。
        </p>
      ) : displayedHaikus.length === 0 ? (
        <p className="haiku-list-message" role="status">
          この句会には、{season}の俳句がありません。
        </p>
      ) : (
        <>
          <p className="haiku-list-message" role="status">
            {view === "season" ? `${season}・` : ""}全{displayedHaikus.length}
            句・点句が多い順
            <br />
            {startIndex + 1}〜{startIndex + pageHaikus.length}句目
          </p>

          <div
            className="haiku-list-scroll"
            tabIndex={0}
            role="region"
            aria-label="句会の俳句一覧"
          >
            <div className="haiku-grid">
              {pageHaikus.map((haiku) => (
                <article className="haiku-card" key={haiku.meetingHaikuId}>
                  <p className="haiku-card-meta">{haiku.season}</p>
                  <p className="haiku-card-meta">
                    点句：
                    {haiku.points == null ? "－" : haiku.points}
                  </p>
                  <p className="haiku-card-text">{haiku.text}</p>

                  <button
                    type="button"
                    className="haiku-edit-button"
                    onClick={() => onEditPoints(haiku)}
                  >
                    点句を変更
                  </button>
                </article>
              ))}
            </div>
          </div>

          <nav className="haiku-pagination" aria-label="一覧のページ切り替え">
            <button
              type="button"
              onClick={() => setPage(currentPage - 1)}
              disabled={currentPage === 1}
            >
              前へ
            </button>
            <span>
              {currentPage} / {pageCount}ページ
            </span>
            <button
              type="button"
              onClick={() => setPage(currentPage + 1)}
              disabled={currentPage === pageCount}
            >
              次へ
            </button>
          </nav>
        </>
      )}
    </main>
  );
}

export default MeetingHaikuListPage;
