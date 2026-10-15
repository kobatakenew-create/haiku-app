import { test } from 'node:test';
import assert from 'node:assert/strict';
import { buildMeetingPrintHtml, showMeetingPrintWindow } from '../../src/utils/meetingPdf.js';

const meeting = { meetingName: '秋の句会' };
function poems(count) {
  return Array.from({ length: count }, (_, i) => ({ meetingHaikuId: i + 1, text: `句${i + 1}`, points: null }));
}

for (const [count, pageCount] of [[1, 1], [10, 1], [11, 2], [20, 2], [21, 3]]) {
  test(`${count}句を欠落・重複なしで${pageCount}ページへ分割する`, () => {
    const html = buildMeetingPrintHtml(meeting, poems(count));
    assert.equal((html.match(/class="poem"/g) ?? []).length, count);
    assert.equal((html.match(/class="sheet"/g) ?? []).length, pageCount);
    for (const poem of poems(count)) {
      const fullWidth = poem.text.replace(/[0-9]/g, digit => '０１２３４５６７８９'[Number(digit)]);
      assert.equal(html.split(`>${fullWidth}</p>`).length - 1, 1);
    }
  });
}

test('未登録の場合は空の本文を作る', () => {
  assert.match(buildMeetingPrintHtml(meeting, []), /<main><\/main>/);
});

test('句会名と俳句のHTML特殊文字をエスケープする', () => {
  const html = buildMeetingPrintHtml({ meetingName: '</title><script>alert(1)</script>' }, [
    { meetingHaikuId: 1, text: '<img src=x onerror="x">&\'', points: 1 },
  ]);
  assert.ok(!html.includes('<script>'));
  assert.ok(!html.includes('<img'));
  assert.ok(html.includes('&lt;img src=x onerror=&quot;x&quot;&gt;&amp;&#39;'));
  assert.ok(html.includes('&lt;/title&gt;'));
});

test('点句順・同点時の登録番号順で並べ、元データを変更しない', () => {
  const data = [
    { meetingHaikuId: 1, text: '点句なし', points: null },
    { meetingHaikuId: 2, text: '同点の先', points: 3 },
    { meetingHaikuId: 3, text: '同点の後', points: 3 },
    { meetingHaikuId: 4, text: '最高点', points: 20 },
  ];
  const before = structuredClone(data);
  const html = buildMeetingPrintHtml(meeting, data);
  assert.ok(html.indexOf('>最高点</p>') < html.indexOf('>同点の後</p>'));
  assert.ok(html.indexOf('>同点の後</p>') < html.indexOf('>同点の先</p>'));
  assert.ok(html.indexOf('>同点の先</p>') < html.indexOf('>点句なし</p>'));
  assert.deepEqual(data, before);
});

test('印刷本文に句会名・日付・兼題・点句を出さない', () => {
  const html = buildMeetingPrintHtml({ meetingName: '秋の句会', meetingDate: '2026-10-15', theme: '秋風' }, poems(1));
  const main = html.split('<main>')[1].split('</main>')[0];
  assert.ok(!/秋の句会|2026|秋風|点句/.test(main));
  assert.ok(html.includes('writing-mode: vertical-rl'));
  assert.ok(html.includes('@media print { .toolbar { display: none; }'));
});

test('印刷はフォントの読込完了後に実行し、戻る操作でタブを閉じる', async () => {
  const handlers = {};
  const calls = [];
  let ready;
  const printWindow = {
    closed: false,
    document: {
      open() {}, write() {}, close() {},
      fonts: { ready: new Promise(resolve => { ready = resolve; }) },
      getElementById(id) { return { addEventListener(event, handler) { handlers[id] = handler; } }; },
    },
    focus() { calls.push('focus'); },
    print() { calls.push('print'); },
    close() { this.closed = true; },
  };
  showMeetingPrintWindow(printWindow, meeting, poems(1));
  const printing = handlers['print-button']();
  assert.deepEqual(calls, []);
  ready();
  await printing;
  assert.deepEqual(calls, ['focus', 'print']);
  handlers['back-button']();
  assert.equal(printWindow.closed, true);
});
