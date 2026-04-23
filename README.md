# Luna Valdis Boss Mod (Forge 1.20.1)

> 「蒼月の守護騎士 ルナ＝ヴァルディス」ボス戦 + ダンジョン + 専用モブ + アイテム + ブロックをまとめて実装するプロジェクト。

## 重要: GitHubのREADMEが更新されない場合
ローカルで更新しても、**pushしないと GitHub には反映されません**。

```bash
git remote add origin https://github.com/atuserver/minecraft.git  # 初回のみ
git push -u origin work:main
```

## 実装済み
### ボス
- `examplemod:luna_valdis`
- Phase2移行（HP50%）
- ボスバー色変化
- 8系統攻撃（RUNE_SLASH / SHADOW_THRUST / MOON_BARRAGE / MOON_BLADE / MOONFALL / CROSS / RESONANCE / OATH）

### 道中モブ
- `examplemod:rune_archer`（3点バースト射撃）
- `examplemod:lamp_slime`（撃破時小範囲ダメージ）
- `examplemod:tomb_guard`（正面ダメージ軽減）
- `examplemod:vice_knight`（中ボス想定ダッシュ）

### アイテム
- `examplemod:moon_shard`（月殻片）
- `examplemod:luna_blade`（月誓剣ルナブレイド）
- `examplemod:azure_moon_mantle`（蒼月のマント）

### ブロック
- `examplemod:moonlamp_pillar`（月灯柱）

### 追加データ
- `sounds.json`（BGM/SEイベント）
- `worldgen`（Structure / StructureSet / Jigsaw Pool / ProcessorList）

## テスト方法
1. Forge 1.20.1 で起動
2. クリエイティブのスポーンエッグから各mobを出す
3. ボスを召喚して挙動確認

```mcfunction
/summon examplemod:luna_valdis ~ ~ ~
/summon examplemod:rune_archer ~2 ~ ~
/summon examplemod:lamp_slime ~-2 ~ ~
/summon examplemod:tomb_guard ~4 ~ ~
/summon examplemod:vice_knight ~6 ~ ~
```

## ビルド
```bash
gradle build
```

## 要件
- Java 17
- Minecraft 1.20.1
- Forge 47.2.0

## ドキュメント
- `docs/BOSS_IMPLEMENTATION_GUIDE_JA.md`
- `docs/ASSET_CHECKLIST_JA.md`
