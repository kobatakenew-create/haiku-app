function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
  })[character]);
}

export function buildMeetingPrintHtml(meeting, haikus) {
  const sortedHaikus = [...haikus].sort((a, b) => {
    if (a.points == null && b.points == null) return b.meetingHaikuId - a.meetingHaikuId;
    if (a.points == null) return 1;
    if (b.points == null) return -1;
    return b.points - a.points || b.meetingHaikuId - a.meetingHaikuId;
  });
  const pages = [];
  for (let index = 0; index < sortedHaikus.length; index += 10) {
    const poems = sortedHaikus.slice(index, index + 10).map((haiku) =>
      '<p class="poem">' + escapeHtml(haiku.text.replace(/[0-9]/g, (digit) =>
        '０１２３４５６７８９'[Number(digit)])) + '</p>',
    ).join('');
    pages.push('<section class="sheet">' + poems + '</section>');
  }
  return '<!doctype html><html lang="ja"><head><meta charset="utf-8">' +
    '<meta name="viewport" content="width=device-width, initial-scale=1">' +
    '<title>' + escapeHtml(meeting.meetingName) + '</title><style>' +
    '@page { size: A4 portrait; margin: 18mm; }' +
    '* { box-sizing: border-box; } body { margin: 0; color: #000; background: #fff; }' +
    '.toolbar { padding: 16px; font: 14px sans-serif; background: #f3f3f3; }' +
    '.toolbar button { padding: 10px 18px; cursor: pointer; }' +
    '.toolbar p { margin: 10px 0 0; }' +
    '.sheet { display: flex; flex-direction: row-reverse; justify-content: flex-start;' +
    'width: 174mm; height: 250mm; margin: 18mm auto; gap: 0;' +
    'break-after: page; page-break-after: always; }' +
    '.sheet:last-child { break-after: auto; page-break-after: auto; }' +
    '.poem { writing-mode: vertical-rl; text-orientation: mixed; white-space: pre-wrap;' +
    'font-family: "Yu Mincho", "Hiragino Mincho ProN", "Noto Serif CJK JP", serif;' +
    'font-size: 18pt; line-height: 1.8; margin: 0; width: 17.4mm; height: 250mm;' +
    'flex: 0 0 17.4mm; overflow-wrap: anywhere; }' +
    '@media screen { main { overflow-x: auto; } }' +
    '@media print { .toolbar { display: none; } .sheet { margin: 0; } }' +
    '</style></head><body><div class="toolbar">' +
    '<button type="button" id="back-button">句会一覧へ戻る</button> ' +
    '<button type="button" id="print-button">PDF保存・印刷</button>' +
    '<p>印刷画面の保存先で「PDFに保存」を選んでください。俳句だけを出す場合は「ヘッダーとフッター」をオフにしてください。</p>' +
    '</div><main>' + pages.join('') + '</main></body></html>';
}

export function showMeetingPrintWindow(printWindow, meeting, haikus) {
  printWindow.document.open();
  printWindow.document.write(buildMeetingPrintHtml(meeting, haikus));
  printWindow.document.close();
  const print = async () => {
    await printWindow.document.fonts.ready;
    if (!printWindow.closed) {
      printWindow.focus();
      printWindow.print();
    }
  };
  printWindow.document.getElementById('print-button').addEventListener('click', print);
  printWindow.document.getElementById('back-button').addEventListener('click', () => {
    printWindow.close();
  });
}
