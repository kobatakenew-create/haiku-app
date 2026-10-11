import { createContext, useContext } from "react";

export const HaikuTitleContext = createContext("俳句帳");

export default function HaikuTitle() {
  const title = useContext(HaikuTitleContext);

  return <h1 style={{ overflowWrap: "anywhere" }}>{title}</h1>;
}
