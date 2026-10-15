import { useContext } from "react";
import { HaikuTitleContext } from "./HaikuTitleContext";

export default function HaikuTitle() {
  const title = useContext(HaikuTitleContext);

  return <h1 style={{ overflowWrap: "anywhere" }}>{title}</h1>;
}
