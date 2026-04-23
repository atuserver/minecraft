# Luna Valdis Boss Mod (Forge 1.20.1)

> 「蒼月の守護騎士 ルナ＝ヴァルディス」ボス戦 + ダンジョン雛形 + BGM/ボスバー基盤を一式で作成したプロジェクト。

## 重要: GitHubのREADMEが更新されない場合
ローカルで更新しても、**pushしないと GitHub には反映されません**。

```bash
git remote add origin https://github.com/atuserver/minecraft.git  # 初回のみ
git push -u origin work:main
```

## 現在の実装（1回でまとめて入れた内容）
- カスタムボス `examplemod:luna_valdis`
- フェーズ2移行（HP50%）
- ボスバー色変化（BLUE -> PURPLE）
- 8系統の攻撃状態を実装
  - RUNE_SLASH
  - SHADOW_THRUST
  - MOON_BARRAGE
  - MOON_BLADE
  - MOONFALL
  - CROSS
  - RESONANCE
  - OATH（一時強化）
- テレグラフ粒子表示と攻撃実行を分離
- スポーンエッグ `examplemod:luna_valdis_spawn_egg`
- EN/JAローカライズ
- BGM/SEイベント定義ファイル `sounds.json`
- ダンジョン構造の雛形データ（Structure/StructureSet/Jigsaw Pool）

## 遊び方
1. Forge 1.20.1 で起動
2. クリエイティブのスポーンエッグタブで卵を取得
3. 召喚して戦闘確認

またはコマンド:
```mcfunction
/summon examplemod:luna_valdis ~ ~ ~
```

## ビルド
```bash
gradle build
```

## 要件
- Java 17
- Minecraft 1.20.1
- Forge 47.2.0

## 追加ドキュメント
- `docs/BOSS_IMPLEMENTATION_GUIDE_JA.md`
- `docs/ASSET_CHECKLIST_JA.md`
