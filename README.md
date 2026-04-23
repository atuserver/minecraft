# Forge 1.20.1 Boss Mod: Luna Valdis

このリポジトリは **Minecraft Forge 1.20.1** 向けに、
ボス「**蒼月の守護騎士 ルナ＝ヴァルディス**」を実装するためのプロジェクトです。

## 実装済み（初期版）
- ボスEntity `examplemod:luna_valdis`
- フェーズ遷移（HP 50%でPhase2）
- ボスバー表示（Phase1青 -> Phase2紫）
- テレグラフ付き攻撃
  - Moonfall（リングAoE）
  - Cross Slash（十字AoE）
- スポーンエッグ `examplemod:luna_valdis_spawn_egg`

## ゲーム内で試す
1. Forge 1.20.1 で起動
2. クリエイティブのスポーンエッグタブからスポーンエッグを取得
3. 召喚して戦闘確認

またはコマンド:
```mcfunction
/summon examplemod:luna_valdis ~ ~ ~
```

## ローカルビルド
```bash
gradle build
```

## 動作要件
- Java 17
- Minecraft 1.20.1
- Forge 47.2.0

## ボス実装ドキュメント
- `docs/BOSS_IMPLEMENTATION_GUIDE_JA.md`
- `docs/ASSET_CHECKLIST_JA.md`

GeckoLibを使った拡張（専用モデル/モーション/演出/BGM/ダンジョン）方針をまとめています。
