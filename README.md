# SpawnPlugin (Paper 1.20.1)

Paper 1.20.1 向けのシンプルなプラグインです。

## 機能
- プレイヤー参加時にメッセージ表示
- `/spawn` コマンドでスポーン地点へ移動
- `config.yml` で機能 ON/OFF 切り替え

## プロジェクト構成
- `pom.xml`
- `src/main/java/com/example/spawnplugin/`
  - `SpawnPlugin.java`
  - `JoinListener.java`
  - `SpawnCommand.java`
- `src/main/resources/`
  - `plugin.yml`
  - `config.yml`

## ビルド
```bash
mvn -DskipTests package
```

ビルド成果物は `target/spawn-plugin-1.0.0.jar` です。

## 導入
1. 生成された JAR を Paper サーバーの `plugins/` に配置
2. サーバー起動後に生成される `plugins/SpawnPlugin/config.yml` を必要に応じて編集
3. サーバー再起動または `/reload confirm`

## config.yml 例
```yml
join-message:
  enabled: true
  text: "&a%player% さん、サーバーへようこそ！"

spawn-command:
  enabled: true
  success-message: "&bスポーン地点に移動しました。"
  disabled-message: "&c現在 /spawn は無効です。"
```
