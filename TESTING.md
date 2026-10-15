# 俳句アプリのテスト

## 準備

Java 21とNode.js 24を使います。以下はPowerShellでの実行例です。

テストは本番サイト・Aiven・PC内のMySQLを使用しません。DockerやIDEでバックエンドを起動する必要もありません。

## バックエンド

```powershell
cd C:\Users\kobat\IdeaProjects\haiku-app
$env:JAVA_HOME = 'C:\pleiades\2026-06\java\21'
.\mvnw.cmd test
```

- 俳句：20文字の境界、Unicode文字、空白・改行、季節、状態、自己評価、点句の制限。
- 句会：名前10文字、兼題2文字、開催日の必須入力。
- 登録・認証：パスワードのハッシュ保存、重複ID、誤ったパスワード、セッション復元、未ログインの拒否。
- データ保護：他人の俳句・句会へのアクセス拒否、句会から外しても原句を保持、原句と句会の点句の独立。
- ログイン維持：トークンのハッシュ化、30日の期限、期限ちょうどでの失効、不正形式の拒否。

DB統合テストと起動テストは `@IsolatedDatabaseTest` により、接続先をメモリー内H2へ固定します。テスト後はデータが消えます。新たなDBテストにもこの注釈を付けてください。

結果は `target/surefire-reports/` に保存されます。

## フロントエンドの小さいテスト

```powershell
cd C:\Users\kobat\IdeaProjects\haiku-app\frontend
npm ci
npm test
```

PDF用本文の全句出力、10句単位の改ページ、HTML特殊文字、並び順、原データ保持、印刷前のフォント待機、戻る操作を確認します。

## ブラウザーで操作するテスト

初回のみブラウザーを準備します。

```powershell
npx playwright install chromium
```

実行します。

```powershell
npm run test:e2e
```

すでにインストール済みのChromeを使う場合は、ブラウザーの追加インストールに代えて次を実行できます。

```powershell
$env:PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH = 'C:\Program Files\Google\Chrome\Application\chrome.exe'
npm run test:e2e
```

- 編集・保存後とキャンセル後のページ復元。
- 点句順での順位変更、季節変更後の掲載ページ。
- 21文字の入力拒否、ログイン期限切れ。
- PDFの全句表示、縦書き、枠なし、印刷時の操作ボタン非表示、一覧へ戻る。
- 空の句会、通信失敗、ポップアップ拒否、スマホ幅でのPDFボタン。
- 登録時の確認パスワード、連続送信防止、入力変更によるエラー解除。

専用の開発サーバーを `127.0.0.1:5187` で自動起動・停止します。5187番ポートを空けてください。すべての `/api/` 通信はテストデータで応答し、実際のバックエンドへ転送しません。

結果の表示：

```powershell
npx playwright show-report
```

## まとめて実行

フロントエンドのテストをまとめて実行：

```powershell
npm run test:all
npm run lint
npm run build
```

バックエンドは別途、アプリのルートで ` .\mvnw.cmd test ` を実行します。

## このテストで保証しないこと

- H2はMySQLそのものではないため、MySQL固有の照合順序（IDの大文字・小文字など）は別途確認が必要です。
- ブラウザーテストはChromiumで実行します。iPhone実機のSafariや、OSのPDF保存画面は自動操作しません。
- PDF本文の構成・画面の動作を確認しますが、実際の保存ファイルの見た目は印刷プレビューでも確認してください。
- 本番の接続、公開作業、同時アクセス時の性能は対象外です。

## 確認済みの実行結果（2026年10月15日）

- バックエンド：65件成功（既存の6件を含む）。
- PDFの処理：10件成功。
- ブラウザー操作：13件成功（PCのChrome使用。スマホは画面幅375pxで確認）。
- 合計：88件成功、失敗0件。
- ESLintと本番用ビルドも成功。

テストコードはアプリ内の `src/test/` と `frontend/tests/` に保存しています。テストの追加によって、本番の画面や機能は変わりません。
