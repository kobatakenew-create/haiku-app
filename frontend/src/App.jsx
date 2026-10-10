import { useEffect, useState } from "react";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import HomePage from "./pages/HomePage";
import HaikuCreatePage from "./pages/HaikuCreatePage";
import HaikuListPage from "./pages/HaikuListPage";
import HaikuEditPage from "./pages/HaikuEditPage";
import MeetingCreatePage from "./pages/MeetingCreatePage";
import MeetingListPage from "./pages/MeetingListPage";
import MeetingEditPage from "./pages/MeetingEditPage";
import MeetingHaikuListPage from "./pages/MeetingHaikuListPage";
import MeetingHaikuAddPage from "./pages/MeetingHaikuAddPage";
import MeetingHaikuPointsEditPage from "./pages/MeetingHaikuPointsEditPage";
import { getCurrentSession, logoutUser } from "./api/authApi";
import "./App.css";

function App() {
  const [page, setPage] = useState("login");
  const [editingHaiku, setEditingHaiku] = useState(null);
  const [selectedMeeting, setSelectedMeeting] = useState(null);
  const [editingMeetingHaiku, setEditingMeetingHaiku] = useState(null);
  const [user, setUser] = useState(null);
  const [isCheckingSession, setIsCheckingSession] = useState(true);
  const [sessionError, setSessionError] = useState("");
  const [notice, setNotice] = useState("");
  const [logoutError, setLogoutError] = useState("");
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [sessionCheckVersion, setSessionCheckVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function checkSession() {
      try {
        const session = await getCurrentSession();

        if (!cancelled) {
          setUser(session);
          setPage("home");
        }
      } catch (error) {
        if (cancelled) return;

        if (error.status === 401) {
          setUser(null);
          setPage("login");
        } else {
          setSessionError(
            "ログイン状態を確認できませんでした。サーバーの起動と接続を確認してください。",
          );
        }
      } finally {
        if (!cancelled) {
          setIsCheckingSession(false);
        }
      }
    }

    checkSession();

    return () => {
      cancelled = true;
    };
  }, [sessionCheckVersion]);

  function retrySessionCheck() {
    setSessionError("");
    setIsCheckingSession(true);
    setSessionCheckVersion((version) => version + 1);
  }

  function clearSelections() {
    setEditingHaiku(null);
    setSelectedMeeting(null);
    setEditingMeetingHaiku(null);
  }

  function handleLogin(loggedInUser) {
    setUser(loggedInUser);
    clearSelections();
    setPage("home");
    setNotice("");
    setLogoutError("");
  }

  function handleRegistered() {
    setNotice("登録が完了しました。ログインしてください。");
    setPage("login");
  }

  function handleLoginRequired() {
    setUser(null);
    clearSelections();
    setPage("login");
    setLogoutError("");
    setNotice("ログインの有効期限が切れました。ログインしてください。");
  }

  function handleHaikuSaved() {
    setPage("home");
    setNotice("俳句を保存しました。");
  }

  function openCreatePage() {
    setNotice("");
    setPage("haiku-create");
  }

  function openListPage() {
    setNotice("");
    setPage("haiku-list");
  }

  function returnToList() {
    setEditingHaiku(null);
    setPage("haiku-list");
  }

  function openMeetingListPage() {
    setNotice("");
    setSelectedMeeting(null);
    setEditingMeetingHaiku(null);
    setPage("meeting-list");
  }

  function openMeetingCreatePage() {
    setNotice("");
    setPage("meeting-create");
  }

  function openMeetingEditPage(meeting) {
    setNotice("");
    setSelectedMeeting(meeting);
    setEditingMeetingHaiku(null);
    setPage("meeting-edit");
  }

  function openMeetingHaikuListPage(meeting) {
    setNotice("");
    setSelectedMeeting(meeting);
    setEditingMeetingHaiku(null);
    setPage("meeting-haiku-list");
  }

  function returnToMeetingHaikuList() {
    setEditingMeetingHaiku(null);
    setPage("meeting-haiku-list");
  }

  function openMeetingHaikuPointsEditPage(haiku) {
    setEditingMeetingHaiku(haiku);
    setPage("meeting-haiku-points-edit");
  }

  async function handleLogout() {
    setLogoutError("");
    setIsLoggingOut(true);

    try {
      await logoutUser();
      setUser(null);
      clearSelections();
      setPage("login");
      setNotice("ログアウトしました。");
    } catch {
      setLogoutError("ログアウトできませんでした。もう一度お試しください。");
    } finally {
      setIsLoggingOut(false);
    }
  }

  if (isCheckingSession) {
    return <p role="status">ログイン状態を確認しています…</p>;
  }

  if (sessionError) {
    return (
      <main className="auth-page">
        <section className="auth-card">
          <h1>俳句帳</h1>
          <p role="alert">{sessionError}</p>
          <button type="button" onClick={retrySessionCheck}>
            再試行
          </button>
        </section>
      </main>
    );
  }

  if (user) {
    if (page === "meeting-edit" && selectedMeeting) {
      return (
        <MeetingEditPage
          key={selectedMeeting.meetingId}
          meeting={selectedMeeting}
          onSaved={openMeetingListPage}
          onBack={openMeetingListPage}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (
      page === "meeting-haiku-points-edit" &&
      selectedMeeting &&
      editingMeetingHaiku
    ) {
      return (
        <MeetingHaikuPointsEditPage
          key={editingMeetingHaiku.meetingHaikuId}
          meeting={selectedMeeting}
          haiku={editingMeetingHaiku}
          onSaved={returnToMeetingHaikuList}
          onBack={returnToMeetingHaikuList}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "meeting-haiku-add" && selectedMeeting) {
      return (
        <MeetingHaikuAddPage
          key={selectedMeeting.meetingId}
          meeting={selectedMeeting}
          onAdded={returnToMeetingHaikuList}
          onBack={returnToMeetingHaikuList}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "meeting-haiku-list" && selectedMeeting) {
      return (
        <MeetingHaikuListPage
          key={selectedMeeting.meetingId}
          meeting={selectedMeeting}
          onAdd={() => setPage("meeting-haiku-add")}
          onEditPoints={openMeetingHaikuPointsEditPage}
          onBack={openMeetingListPage}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "meeting-create") {
      return (
        <MeetingCreatePage
          onSaved={openMeetingListPage}
          onBack={openMeetingListPage}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "meeting-list") {
      return (
        <MeetingListPage
          onCreate={openMeetingCreatePage}
          onOpen={openMeetingHaikuListPage}
          onEdit={openMeetingEditPage}
          onBack={() => setPage("home")}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "haiku-edit" && editingHaiku) {
      return (
        <HaikuEditPage
          key={editingHaiku.haikuId}
          haiku={editingHaiku}
          onSaved={returnToList}
          onDeleted={returnToList}
          onBack={returnToList}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "haiku-list") {
      return (
        <HaikuListPage
          onCreate={openCreatePage}
          onEdit={(haiku) => {
            setEditingHaiku(haiku);
            setPage("haiku-edit");
          }}
          onBack={() => setPage("home")}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    if (page === "haiku-create") {
      return (
        <HaikuCreatePage
          onSaved={handleHaikuSaved}
          onBack={() => setPage("home")}
          onLoginRequired={handleLoginRequired}
        />
      );
    }

    return (
      <HomePage
        key={user.userId}
        notice={notice}
        onCreate={openCreatePage}
        onList={openListPage}
        onMeetings={openMeetingListPage}
        onLogout={handleLogout}
        onLoginRequired={handleLoginRequired}
        logoutError={logoutError}
        isLoggingOut={isLoggingOut}
      />
    );
  }

  return (
    <>
      {notice && <p role="status">{notice}</p>}

      {page === "register" ? (
        <RegisterPage
          onRegistered={handleRegistered}
          onBack={() => setPage("login")}
        />
      ) : (
        <LoginPage
          onLogin={handleLogin}
          onRegister={() => {
            setNotice("");
            setPage("register");
          }}
        />
      )}
    </>
  );
}

export default App;
