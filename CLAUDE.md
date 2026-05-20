# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# BallArena — Claude Code 專案說明

## 專案簡介
JavaFX 雙人對戰遊戲。兩顆球在封閉方塊內移動、反彈並互相攻擊。
玩家在開始前選擇球種，不同球種有不同技能。

## 技術棧
- 語言：Java
- UI 框架：JavaFX 21.0.11 + FXML
- 版面設計工具：Scene Builder
- IDE：IntelliJ IDEA（原生專案，無 Maven/Gradle）
- JavaFX SDK 路徑：`C:\Program Files\Java\javafx-sdk-21.0.11\lib`

## 建置與執行

本專案為 IntelliJ IDEA 原生專案，無 Maven/Gradle。建議直接在 IntelliJ 中執行。

**在 IntelliJ 執行：**
- 主類別：`BallArena.App`
- VM options（須加入 Run Configuration）：
  ```
  --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls,javafx.fxml
  ```

**命令列編譯與執行：**
```bash
# 編譯
javac -d out --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls,javafx.fxml src/BallArena/**/*.java

# 執行
java --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls,javafx.fxml -cp out BallArena.App
```

本專案**無測試套件**。

## 專案結構

```
src/
└── BallArena/
    ├── App.java                  ← 主程式入口，載入第一個 FXML，視窗 800×600
    ├── ui/                       ← Controller，邏輯要薄，委派給 model
    │   ├── MenuController.java
    │   ├── SelectController.java ← 選球畫面，玩家選完後敵方隨機選並啟動遊戲
    │   ├── GameController.java   ← AnimationTimer 遊戲主迴圈在這
    │   └── ResultController.java
    ├── model/                    ← 純 Java，不依賴 JavaFX
    │   ├── GameState.java        ← 遊戲階段 enum：MENU / SELECT / PLAYING / RESULT
    │   ├── BallStyle.java        ← 球色 enum：BLUE("藍球") / RED("紅球")
    │   ├── BallType.java         ← 球種 enum：SWORD / SPIKE（無實例下指定建立哪種球）
    │   ├── Ball.java             ← 抽象基底類別（位置、速度、HP、BallStyle）
    │   ├── SwordBall.java
    │   ├── SpikeBall.java
    │   ├── PhysicsEngine.java    ← 移動、反彈、球與球碰撞、傷害計算
    │   └── ArenaConfig.java      ← 場地常數：WIDTH=300, HEIGHT=300, BALL_RADIUS=20
    ├── ability/                  ← Strategy 模式，每個技能獨立
    │   ├── Ability.java          ← 介面：update(deltaTime), onBounce()
    │   ├── RotatingSword.java
    │   └── WallSpike.java
    └── renderer/                 ← 只負責繪製，不含邏輯
        ├── ArenaRenderer.java    ← 繪製總協調者
        ├── BallRenderer.java
        ├── AbilityRenderer.java
        ├── HudRenderer.java
        └── BallColorMap.java     ← BallStyle → JavaFX Color 映射（BLUE→CORNFLOWERBLUE, RED→TOMATO）

resources/
└── BallArena/
    ├── menu.fxml
    ├── select.fxml               ← 選球畫面（兩張球種卡片）
    ├── game.fxml                 ← 含 300×300 Canvas
    └── result.fxml
```

## 架構規則（必須遵守）

- **model 層**不可以 import 任何 `javafx.*`
- **renderer 層**只讀取 model 的狀態，不修改任何資料
- **FXML 的 `fx:controller`** 要填完整 package 路徑，例如 `BallArena.ui.MenuController`
- Controller 邏輯要薄，複雜邏輯委派給 model 層處理

## 新增球種的標準流程

新增一個球種需要且只需要異動以下幾處，不可破壞其他層：

1. `model/` — 新增 `Ball` 的子類別，實作 `updateAbility()`、`onBounce()`、`getTypeName()`
2. `ability/` — 新增對應的 `Ability` 實作，實作 `update()` 和 `onBounce()`
3. `model/BallType.java` — 新增 enum 值（含顯示名稱與描述）
4. `ui/GameController.createBall()` — 加上對應的 switch case
5. `resources/BallArena/select.fxml` + `ui/SelectController.java` — 新增該球種的選擇卡片與按鈕
6. `renderer/AbilityRenderer.java` — 在 `render()` 加上對應型別的繪製邏輯

## 現有球種

### SwordBall（`model/SwordBall.java`）
- 技能：`RotatingSword`（`ability/RotatingSword.java`）
- 行為：一把劍以固定角速度（1.5π rad/s）繞球旋轉
- 劍距球心 40px，劍長 50px

### SpikeBall（`model/SpikeBall.java`）
- 技能：`WallSpike`（`ability/WallSpike.java`）
- 行為：每次碰牆在碰撞點產生尖刺，尖刺目前永久存在直到遊戲結束
- 尖刺座標由 `PhysicsEngine` 碰牆後呼叫 `spikeBall.addSpike(x, y)` 傳入

## 物理引擎重點（`PhysicsEngine.java`）

- 球與牆壁：完全彈性反彈，碰牆後呼叫 `ball.onBounce(hitVertical, hitHorizontal)`
- 球與球：質量相等的彈性碰撞，交換法線方向速度分量
- deltaTime 上限 0.05 秒，避免視窗拖移造成大跳躍
- 碰牆後若是 `SpikeBall`，由 PhysicsEngine 負責呼叫 `addSpike()`
- **傷害系統**：劍碰撞 5 HP／次，尖刺碰撞 3 HP／次，同一對球 0.5 秒冷卻；兩球 HP 均為 100，歸零即判負

## 遊戲流程

```
MENU → SELECT → PLAYING → RESULT
```

- `GameState.java` 定義四個階段
- 畫面切換由各 Controller 透過 FXML 載入完成
- `SelectController` 把玩家球種與隨機選出的敵方球種透過 `GameController.setBallTypes()` 傳遞
- `GameController.initialize()` 只建立 physics / renderer；`setBallTypes()` 才會建球並啟動 AnimationTimer

## 程式碼風格

- 程式碼註解使用**繁體中文**
- 優先使用符合上述架構的寫法
- 如果修改會影響架構設計，請先說明再給程式碼
