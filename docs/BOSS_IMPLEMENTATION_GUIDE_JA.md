# Forge 1.20.1 ボス実装ガイド（初心者向け）

このガイドは、あなたが提案した
**蒼月の守護騎士《ルナ＝ヴァルディス》** を
Forge 1.20.1 + GeckoLib で段階的に実装するための手順書です。

---

## 0. まず結論（何を自分でやる必要があるか）

以下はほぼ確実に自分で用意・調整が必要です。

1. 3Dモデル・アニメーション（GeckoLib想定）
2. 独自BGM/SE（音声ファイル + events登録）
3. ボスバー表示名/色/進捗制御
4. ダンジョン構造（Structure / Jigsaw またはテンプレ配置）

このドキュメントでは「各項目の最小実装」と「拡張実装」を分けて説明します。

---

## 1. 全体実装ロードマップ（推奨順）

1. **Entityの土台**（AIなし、スポーンだけ）
2. **ボスバーとHP連動**
3. **攻撃パターン1つだけ**（例: ルーン斬り）
4. **テレグラフ床表示**（先に予兆、後で当たり判定）
5. **Phase遷移（50%）**
6. **GeckoLibアニメ連動**
7. **BGM切り替え**
8. **ダンジョン生成連動**

---

## 2. GeckoLibを使うための準備

### 2-1. 依存追加（build.gradle）

> バージョンは GeckoLib公式の 1.20.1 対応版に合わせて更新してください。

```gradle
repositories {
    maven { url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/' }
}

dependencies {
    implementation fg.deobf("software.bernie.geckolib:geckolib-forge-1.20.1:<VERSION>")
}
```

### 2-2. 実装で最低限必要なもの

- `GeoEntity` を実装したボスEntityクラス
- `registerControllers` で移動/攻撃/演出を分離
- `geo.json`（モデル）
- `.animation.json`（アニメ）
- テクスチャpng

### 2-3. おすすめController分割

- `controller_move`（idle/walk/run）
- `controller_combat`（slash/lunge/parry）
- `controller_phase`（break_helm / cut-in）
- `controller_defeat`（kneel）

---

## 3. 独自音楽・SEの実装

## 3-1. ファイル配置

- `src/main/resources/assets/examplemod/sounds/boss/luna_phase1.ogg`
- `src/main/resources/assets/examplemod/sounds/boss/luna_phase2.ogg`
- `src/main/resources/assets/examplemod/sounds/boss/luna_parry.ogg`

## 3-2. sounds.json

`src/main/resources/assets/examplemod/sounds.json`

```json
{
  "boss.luna.phase1": { "sounds": ["examplemod:boss/luna_phase1"] },
  "boss.luna.phase2": { "sounds": ["examplemod:boss/luna_phase2"] },
  "boss.luna.parry":  { "sounds": ["examplemod:boss/luna_parry"] }
}
```

## 3-3. SoundEvent登録

- `DeferredRegister<SoundEvent>` を作成
- `SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "boss.luna.phase1"))` で登録

## 3-4. Phase切替時のBGM

- Phase1開始: phase1を再生
- HP <= 50%イベントで phase1停止 + phase2再生
- クライアント同期は `level().isClientSide` でガード

---

## 4. ボスバー設定（独自名・色）

`ServerBossEvent` を使用します。

- 表示名: 「蒼月の守護騎士 ルナ＝ヴァルディス」
- 色: Phase1 `BLUE`, Phase2 `PURPLE`
- HP連動: `bossEvent.setProgress(this.getHealth() / this.getMaxHealth())`

実装要点:
- `startSeenByPlayer` / `stopSeenByPlayer` でプレイヤー参加管理
- `customServerAiStep` で毎tick progress更新
- Phase遷移時に `bossEvent.setColor(...)`

---

## 5. 攻撃を「見てから避けられる」設計で実装する方法

1. 予兆状態を先に付与（0.6〜1.6秒）
2. 予兆中は移動/攻撃ロック
3. 予兆終了tickで当たり判定発生
4. 当たり判定後に硬直を入れる

### 実装のコツ

- `enum BossActionState { IDLE, TELEGRAPH, EXECUTE, RECOVERY }`
- tickカウンタで時間管理
- 当たり判定はサーバー側のみ
- クライアントにはパーティクルをパケット同期

---

## 6. ダンジョン構造（蒼月封廟）を作る方法

Forge 1.20.1では、次の2パターンが現実的です。

### A. まずは簡単: コマンド生成/固定座標配置

- テスト中は構造生成を後回し
- コマンドでボス部屋テンプレを呼び出して調整

### B. 本実装: Structure + Jigsaw

- `data/examplemod/worldgen/structure/`
- `data/examplemod/worldgen/template_pool/`
- `data/examplemod/worldgen/processor_list/`

推奨:
- 前庭/回廊/月灯の間/中ボス/闘技場を別ピース化
- 接続点を `jigsaw` で管理
- レア度は低めに設定（探索の特別感）

---

## 7. 最低限の作業分担（あなたがやる部分）

### あなたが担当（クリエイティブ系）
- モデル作成（Blockbench）
- アニメ作成（GeckoLib形式）
- BGM/SE制作
- テクスチャ制作

### 実装担当（コード系）
- Entity/AI/Phase遷移
- 攻撃判定とテレグラフ
- 音再生タイミング
- ボスバー同期
- ダンジョン生成ロジック

---

## 8. 初心者向け: つまずきポイント

1. GeckoLibバージョン不一致
2. animation名とコード側文字列不一致
3. sounds.jsonキー名不一致
4. サーバー判定をクライアントで実行してしまう
5. ボスバーprogress更新忘れ

---

## 9. 次アクション（おすすめ）

1. 先に「ボス本体（見た目なし）」を実装
2. 1攻撃だけ完成（ルーン斬り）
3. ボスバーとPhase遷移だけ接続
4. その後GeckoLibモデルを流し込む

この順番だと、デバッグが圧倒的に楽です。
