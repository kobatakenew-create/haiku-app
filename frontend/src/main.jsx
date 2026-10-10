import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
    <footer
      style={{
        textAlign: "center",
        padding: "16px",
        fontSize: "12px",
        color: "#626b5e",
      }}
    >
      © GAKU
    </footer>
  </StrictMode>,
);
