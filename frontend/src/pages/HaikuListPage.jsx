import { useEffect, useState } from "react";
import { getHaikus } from "../api/haikuApi";

const SEASONS = ["春", "夏", "秋", "冬"];
const PAGE_SIZE = 5;

function HaikuListPage({ onCreate, onEdit, onBack, onLoginRequired }) {
  const [haikus, setHaikus] = useState([]);
  const [view, setView] = useState("registered");
  const [season, setSeason] = useState("春");
  const [page, setPage] = useState(1);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [loadVersion, setLoadVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function loadHaikus() {
      try {
        const result = await getHaikus();

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
  }, [loadVersion, onLoginRequired]);

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

  let displayedHaikus = haikus;

  if (view === "season") {
    displayedHaikus = haikus.filter((haiku) => haiku.season === season);
  } else if (view === "points") {
    displayedHaikus = [...haikus].sort((a, b) => {
      if (a.points == null && b.points == null) return 0;
      if (a.points == null) return 1;
      if (b.points == null) return -1;
      return b.points - a.points;
    });
  }

  const pageCount = Math.max(1, Math.ceil(displayedHaikus.length / PAGE_SIZE));
  const currentPage = Math.min(page, pageCount);
  const startIndex = (currentPage - 1) * PAGE_SIZE;
  const pageHaikus = displayedHaikus.slice(startIndex, startIndex + PAGE_SIZE);

  return (
    <main className="haiku-list-page">
      <header className="haiku-list-header">
        <h1>俳句帳</h1>
        <h2>俳句一覧</h2>
      </header>

      <div className="haiku-list-actions">
        <button type="button" onClick={onCreate}>
          俳句を登録する
        </button>
        <button type="button" className="secondary-button" onClick={onBack}>
          ホームへ戻る
        </button>
      </div>

      <div className="haiku-filter-buttons" role="group" aria-label="表示方法">
        <button
          type="button"
          aria-pressed={view === "registered"}
          onClick={() => changeView("registered")}
        >
          登録順
        </button>
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
          まだ俳句が登録されていません。
        </p>
      ) : displayedHaikus.length === 0 ? (
        <p className="haiku-list-message" role="status">
          {season}の俳句はまだ登録されていません。
        </p>
      ) : (
        <>
          <p className="haiku-list-message" role="status">
            {view === "season" ? `${season}・` : ""}全{displayedHaikus.length}
            句・
            {view === "points" ? "点句が多い順" : "登録が新しい順"}
            <br />
            {startIndex + 1}〜{startIndex + pageHaikus.length}句目
          </p>

          <div
            className="haiku-list-scroll"
            tabIndex={0}
            role="region"
            aria-label="俳句一覧"
          >
            <div className="haiku-grid">
              {pageHaikus.map((haiku) => (
                <article className="haiku-card" key={haiku.haikuId}>
                  <p className="haiku-card-meta">
                    {haiku.season}・{haiku.status}
                    {haiku.selfEvaluation !== "無印" &&
                      haiku.selfEvaluation && (
                        <span>
                          {"\u3000"}
                          {haiku.selfEvaluation}
                        </span>
                      )}
                  </p>

                  <p className="haiku-card-meta">
                    点句：{haiku.points == null ? "－" : haiku.points}
                  </p>

                  <p className="haiku-card-text">{haiku.text}</p>

                  <button
                    type="button"
                    className="haiku-edit-button"
                    onClick={() => onEdit(haiku)}
                  >
                    編集する
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

export default HaikuListPage;
