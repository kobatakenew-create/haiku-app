import { sendRequest } from "./apiClient";

export function createHaiku(text, season, status, memo) {
  return sendRequest("/api/haikus", "POST", {
    text,
    season,
    status,
    memo,
  });
}

export function getHaikus() {
  return sendRequest("/api/haikus");
}

export function updateHaiku(
  haikuId,
  text,
  season,
  status,
  selfEvaluation,
  points,
  memo,
) {
  return sendRequest(`/api/haikus/${haikuId}`, "PUT", {
    text,
    season,
    status,
    selfEvaluation,
    points,
    memo,
  });
}

export function deleteHaiku(haikuId) {
  return sendRequest(`/api/haikus/${haikuId}`, "DELETE");
}
