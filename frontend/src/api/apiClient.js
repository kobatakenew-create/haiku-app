export async function sendRequest(path, method = "GET", body) {
  const response = await fetch(path, {
    method,
    credentials: "same-origin",
    headers: body !== undefined ? { "Content-Type": "application/json" } : {},
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    const messages = errorBody?.messages;

    const error = new Error(
      Array.isArray(messages) && messages.length > 0
        ? messages.join("\n")
        : `処理に失敗しました（${response.status}）`,
    );

    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
