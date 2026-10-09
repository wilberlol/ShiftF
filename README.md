# ShiftF

**Shift + F 快捷鍵：潛行時按「交換副手」鍵就執行指令，並取消原本的切換副手動作。**
Run a command when a sneaking player presses the swap-hand key (`F`), and cancel the vanilla hand swap so items don't move to the off-hand.

This is a small, clean reimplementation of the idea behind the original *ShiftF 1.0* plugin, with the missing part added: the swap is now **cancelled**.

## 功能 / Features

- 潛行 + F → 執行設定的指令（玩家或後台身分）/ Sneak + F runs a configurable command (as player or console)
- **取消切換副手**（`cancel-swap: true`，預設開啟）/ **Cancels the vanilla swap-hand action** (default on)
- 一般 F（沒潛行）維持原版行為 / Plain `F` without sneaking stays vanilla
- 權限、世界黑／白名單、冷卻 / Optional permission, world black/white list, cooldown
- `%player%` 變數 / `%player%` placeholder
- `/shiftfreload`（別名 `/laereload`）重新載入設定 / reload command

## 設定 / Config (`plugins/ShiftF/config.yml`)

```yaml
command: "dm open fgmenu"      # 不用加 / ；%player% = 玩家名稱
run-as: player                 # player | console
cancel-swap: true              # 取消切換副手
cancel-swap-even-when-blocked: false
permission: ""                 # 留空 = 所有人
worlds: []                     # 預設為黑名單
worlds-as-whitelist: false
cooldown-ms: 0
cooldown-message: ""
```

## 需求 / Requirements

- Spigot / Paper / Purpur 1.13+（已在 Purpur 26.2 測試載入）
- Java 17+

## 編譯 / Build

```bash
mvn package
# target/ShiftF-1.1.0.jar
```

## 權限 / Permissions

| 權限 | 說明 |
|---|---|
| `shiftf.admin` | 使用 `/shiftfreload`（預設 OP）|
| 自訂 `permission` | 設定檔中的 `permission` 選項 |

## 授權 / License

MIT
