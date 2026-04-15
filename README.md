# Forge 1.20.1 Example Mod (Green Apple)

このリポジトリは **Minecraft Forge 1.20.1** 用の最小構成 Mod です。  
テストとして新規アイテム `green_apple`（青リンゴ）を追加しています。

## 追加内容
- アイテム: `examplemod:green_apple`
- クリエイティブタブ: 食べ物と飲み物に表示
- 言語ファイル / アイテムモデル / レシピを同梱

## ローカルビルド
```bash
gradle build
```

ビルド成功後、Jar は以下に出力されます。

- `build/libs/examplemod-1.0.0.jar`（環境によりファイル名に差異あり）

## GitHub Actions で Jar を作る
このリポジトリには `.github/workflows/build.yml` を含めています。  
Push または PR で自動ビルドされ、Artifacts から Jar をダウンロードできます。

## 動作要件
- Java 17
- Minecraft 1.20.1
- Forge 47.2.0

## メモ
この環境では外部 Maven へのアクセス制限により依存解決が失敗する可能性があります。  
その場合は GitHub Actions 上でのビルドを利用してください。
