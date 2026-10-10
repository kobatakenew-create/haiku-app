import { sendRequest } from "./apiClient";

export function createMeeting(meetingName, meetingDate, theme) {
  return sendRequest("/api/meetings", "POST", {
    meetingName,
    meetingDate,
    theme,
  });
}

export function getMeetings() {
  return sendRequest("/api/meetings");
}

export function updateMeeting(meetingId, meetingName, meetingDate, theme) {
  return sendRequest(`/api/meetings/${meetingId}`, "PUT", {
    meetingName,
    meetingDate,
    theme,
  });
}

export function deleteMeeting(meetingId) {
  return sendRequest(`/api/meetings/${meetingId}`, "DELETE");
}

export function addMeetingHaiku(meetingId, haikuId) {
  return sendRequest(`/api/meetings/${meetingId}/haikus`, "POST", {
    haikuId,
  });
}

export function getMeetingHaikus(meetingId) {
  return sendRequest(`/api/meetings/${meetingId}/haikus`);
}

export function updateMeetingHaikuPoints(meetingId, meetingHaikuId, points) {
  return sendRequest(
    `/api/meetings/${meetingId}/haikus/${meetingHaikuId}/points`,
    "PUT",
    { points },
  );
}

export function deleteMeetingHaiku(meetingId, meetingHaikuId) {
  return sendRequest(
    `/api/meetings/${meetingId}/haikus/${meetingHaikuId}`,
    "DELETE",
  );
}
