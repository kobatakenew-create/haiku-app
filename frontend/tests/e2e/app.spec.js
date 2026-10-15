import { test, expect } from '@playwright/test';

// Every API request is intercepted. These tests never need Spring Boot or MySQL.
async function mockApp(page, { loggedIn = true, meetingCount = 21, pdfStatus = 200 } = {}) {
  const poems = Array.from({ length: 12 }, (_, index) => ({
    haikuId: index + 1, text: `確認用の句${index + 1}`, season: '春',
    status: '完成', selfEvaluation: '無印', points: 20 - index, memo: '',
  }));
  const saves = [];
  let expire = false;
  await page.context().route(url => url.pathname.startsWith('/api/'), async (route) => {
    const path = new URL(route.request().url()).pathname;
    const method = route.request().method();
    if (path === '/api/session') return route.fulfill({ status: loggedIn ? 200 : 401, json: { userId: 1, loginId: 'tester' } });
    if (expire) return route.fulfill({ status: 401, json: {} });
    if (path === '/api/haikus' && method === 'GET') return route.fulfill({ json: poems });
    if (path.startsWith('/api/haikus/') && method === 'PUT') {
      const body = route.request().postDataJSON();
      Object.assign(poems.find((poem) => poem.haikuId === Number(path.split('/').at(-1))), body);
      saves.push(body);
      return route.fulfill({ status: 204 });
    }
    if (path === '/api/meetings') return route.fulfill({ json: [{ meetingId: 1, meetingName: '秋の句会', meetingDate: '2026-10-15', theme: '秋風' }] });
    if (path === '/api/meetings/1/haikus') return route.fulfill({
      status: pdfStatus,
      json: pdfStatus === 200 ? Array.from({ length: meetingCount }, (_, i) => ({
        meetingHaikuId: i + 1, haikuId: i + 1, text: 'あいうえおかきくけこさしすせそたちつてと', season: '秋', points: i,
      })) : {},
    });
    throw new Error(`Unexpected API: ${method} ${path}`);
  });
  await page.goto('/');
  if (loggedIn) {
    await expect(page.getByRole('heading', { name: 'ホーム', exact: true })).toBeVisible();
    await expect(page.getByRole('status')).toContainText('登録済み');
  }
  return { saves, expireSession: () => { expire = true; } };
}

async function openSecondPage(page) {
  await page.getByRole('button', { name: '「俳句」一覧', exact: true }).click();
  await page.getByRole('button', { name: '次へ', exact: true }).click();
  await expect(page.getByText('2 / 3ページ')).toBeVisible();
  await page.getByRole('button', { name: '編集', exact: true }).first().click();
}

test('保存後は編集した句が載る2ページ目へ戻る', async ({ page }) => {
  const app = await mockApp(page);
  await openSecondPage(page);
  await page.getByLabel('俳句', { exact: true }).fill('変更した句');
  await page.getByRole('button', { name: '変更を保存する' }).click();
  await expect(page.getByText('2 / 3ページ')).toBeVisible();
  await expect(page.locator('.haiku-card-text').filter({ hasText: '変更した句' })).toBeVisible();
  expect(app.saves).toHaveLength(1);
});

test('保存せず一覧へ戻っても元のページを保持する', async ({ page }) => {
  const app = await mockApp(page);
  await openSecondPage(page);
  await page.getByRole('button', { name: '一覧へ戻る', exact: true }).click();
  await expect(page.getByText('2 / 3ページ')).toBeVisible();
  expect(app.saves).toHaveLength(0);
});

test('点句変更で順位が変わった場合は新しい掲載ページへ戻る', async ({ page }) => {
  await mockApp(page);
  await page.getByRole('button', { name: '「俳句」一覧', exact: true }).click();
  await page.getByRole('button', { name: '点句順', exact: true }).click();
  await page.getByRole('button', { name: '次へ', exact: true }).click();
  await page.getByRole('button', { name: '編集', exact: true }).first().click();
  await page.getByLabel('点句', { exact: true }).selectOption('20');
  await page.getByRole('button', { name: '変更を保存する' }).click();
  await expect(page.getByRole('button', { name: '点句順', exact: true })).toHaveAttribute('aria-pressed', 'true');
  await expect(page.getByText('1 / 3ページ')).toBeVisible();
  await expect(page.locator('.haiku-card-text').filter({ hasText: '確認用の句６' })).toBeVisible();
});

test('季節を変更した句は変更後の季節で表示する', async ({ page }) => {
  await mockApp(page);
  await page.getByRole('button', { name: '「俳句」一覧', exact: true }).click();
  await page.getByRole('button', { name: '季節別', exact: true }).click();
  await page.getByRole('button', { name: '次へ', exact: true }).click();
  await page.getByRole('button', { name: '編集', exact: true }).first().click();
  await page.getByLabel('季節', { exact: true }).selectOption('冬');
  await page.getByRole('button', { name: '変更を保存する' }).click();
  await expect(page.getByRole('button', { name: '冬', exact: true })).toHaveAttribute('aria-pressed', 'true');
  await expect(page.locator('.haiku-card-text')).toHaveText(['確認用の句６']);
});

test('21文字の句は保存要求を送らずエラーにする', async ({ page }) => {
  const app = await mockApp(page);
  await openSecondPage(page);
  await page.getByLabel('俳句', { exact: true }).fill('あ'.repeat(21));
  await page.getByRole('button', { name: '変更を保存する' }).click();
  await expect(page.getByRole('alert')).toContainText('20文字以内');
  expect(app.saves).toHaveLength(0);
});

test('セッションが切れたらログイン画面に戻る', async ({ page }) => {
  const app = await mockApp(page);
  app.expireSession();
  await page.getByRole('button', { name: '「句会」一覧', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'ログイン', exact: true })).toBeVisible();
  await expect(page.getByRole('status')).toContainText('有効期限');
});

test('PDFは全21句だけを縦書きで出し、戻るボタンで元の一覧に戻る', async ({ page }) => {
  await mockApp(page);
  await page.getByRole('button', { name: '「句会」一覧', exact: true }).click();
  const popupPromise = page.waitForEvent('popup');
  await page.getByRole('button', { name: '秋の句会の全俳句をPDFにする' }).click();
  const popup = await popupPromise;
  await expect(popup.locator('.poem')).toHaveCount(21);
  await expect(popup.locator('.sheet')).toHaveCount(3);
  await expect(popup.locator('main')).not.toContainText(/秋の句会|2026|秋風|点句/);
  await expect(popup.locator('.poem').first()).toHaveCSS('writing-mode', 'vertical-rl');
  await expect(popup.locator('.poem').first()).toHaveCSS('border-top-width', '0px');
  await popup.emulateMedia({ media: 'print' });
  await expect(popup.locator('.toolbar')).toBeHidden();
  await popup.emulateMedia({ media: 'screen' });
  await popup.evaluate(() => { window.print = () => { window.printRequested = true; }; });
  await popup.getByRole('button', { name: 'PDF保存・印刷' }).click();
  await expect.poll(() => popup.evaluate(() => window.printRequested)).toBe(true);
  const closed = popup.waitForEvent('close');
  await popup.getByRole('button', { name: '句会一覧へ戻る' }).click();
  await closed;
  await expect(page.getByRole('heading', { name: '句会作品一覧' })).toBeVisible();
});

test('俳句がない句会のPDFは説明を表示する', async ({ page }) => {
  await mockApp(page, { meetingCount: 0 });
  await page.getByRole('button', { name: '「句会」一覧', exact: true }).click();
  await page.getByRole('button', { name: '秋の句会の全俳句をPDFにする' }).click();
  await expect(page.getByRole('alert')).toContainText('まだ俳句が登録されていません');
});

test('PDFデータの取得失敗から再操作できる', async ({ page }) => {
  await mockApp(page, { pdfStatus: 500 });
  await page.getByRole('button', { name: '「句会」一覧', exact: true }).click();
  const button = page.getByRole('button', { name: '秋の句会の全俳句をPDFにする' });
  await button.click();
  await expect(page.getByRole('alert')).toContainText('読み込めませんでした');
  await expect(button).toBeEnabled();
});

test('ポップアップを拒否された場合は案内を表示する', async ({ page }) => {
  await mockApp(page);
  await page.getByRole('button', { name: '「句会」一覧', exact: true }).click();
  await page.evaluate(() => { window.open = () => null; });
  await page.getByRole('button', { name: '秋の句会の全俳句をPDFにする' }).click();
  await expect(page.getByRole('alert')).toContainText('ポップアップ');
});

test('スマホ幅でもPDFボタンが画面内に収まる', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await mockApp(page);
  await page.getByRole('button', { name: '「句会」一覧', exact: true }).click();
  const button = page.getByRole('button', { name: '秋の句会の全俳句をPDFにする' });
  await expect(button).toBeVisible();
  const box = await button.boundingBox();
  expect(box.x).toBeGreaterThanOrEqual(0);
  expect(box.x + box.width).toBeLessThanOrEqual(375);
});

test('確認用パスワードが一致しないと登録しない', async ({ page }) => {
  await mockApp(page, { loggedIn: false });
  await page.getByRole('button', { name: '新規ユーザー登録' }).click();
  await page.getByLabel('ログインID', { exact: true }).fill('newuser');
  await page.getByLabel('パスワード', { exact: true }).fill('test12345');
  await page.getByLabel('パスワード（確認）', { exact: true }).fill('other1234');
  await page.getByRole('button', { name: '登録する', exact: true }).click();
  await expect(page.getByRole('alert')).toContainText('一致しません');
  await page.getByLabel('パスワード（確認）', { exact: true }).fill('test12345');
  await expect(page.getByRole('alert')).toHaveCount(0);
});

test('登録の連続送信を防ぎ、重複IDのエラーを入力変更で消す', async ({ page }) => {
  await mockApp(page, { loggedIn: false });
  let requests = 0;
  let release;
  const gate = new Promise(resolve => { release = resolve; });
  await page.context().route(url => url.pathname === '/api/users', async route => {
    requests += 1;
    await gate;
    await route.fulfill({ status: 409, json: { messages: ['このログインIDは使用されています'] } });
  });
  await page.getByRole('button', { name: '新規ユーザー登録' }).click();
  await page.getByLabel('ログインID', { exact: true }).fill('newuser');
  await page.getByLabel('パスワード', { exact: true }).fill('test12345');
  await page.getByLabel('パスワード（確認）', { exact: true }).fill('test12345');
  // Two submits in the same event turn exercise the synchronous submission lock.
  await page.locator('form').evaluate(form => { form.requestSubmit(); form.requestSubmit(); });
  await expect.poll(() => requests).toBe(1);
  await expect(page.getByRole('button', { name: '登録中…' })).toBeDisabled();
  release();
  await expect(page.getByRole('alert')).toContainText('使用されています');
  expect(requests).toBe(1);
  await page.getByLabel('ログインID', { exact: true }).fill('different');
  await expect(page.getByRole('alert')).toHaveCount(0);
});
