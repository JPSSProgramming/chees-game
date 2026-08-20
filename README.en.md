<!-- HEADER AND BADGES -->
<div align="center">

<h1>
Board Games♛</h1>
<p>A game app with two classic board games in one window</p>

<p>
<img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License">
<img src="https://img.shields.io/badge/Java-21+-blue.svg" alt="Java Version">
<img src="https://img.shields.io/badge/GUI-Java%20Swing-orange.svg" alt="Java Swing">
<img src="https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey.svg" alt="Platform">
<img src="https://img.shields.io/badge/status-completed-success.svg" alt="Status">
</p>

</div>
Language versions:

- Ukrainian: `README.md`
- English: `README.en.md`
## About the project 🎲
This small application is a set of two board games "Chess" and "Draughts". The game in both cases is designed for two players (from one device) and for a game with AI. The game has several levels of difficulty, a convenient interface and an interesting design.

The entire game is **one full-screen window**. No separate pop-ups when switching between the menu, campaign or battle - everything switches instantly and smoothly.

The language used in the game is English (for convenience)

---
## Technology stack ✯
| Component | Technology | Details and engineering solutions |
| :--- | :--- | :--- |
| **Language & Platform** | **Java 21 (LTS)** | Modern syntax: `switch` statements with arrow syntax, pattern matching (`case KING -> ...`), and `var` type inference. |
| **Graphical Interface** | **Pure Java Swing** | **Zero-Dependency UI**: No external frameworks (no JavaFX or FlatLaf). Single-window architecture (`AppFrame`) with `CardLayout` for instant screen switching. |
| **Design & L&F** | **Customized Nimbus** | Base `Nimbus LookAndFeel`, fully customized via `UIManager` to support a deep dark theme with gold accents. |
| **Vector Graphics** | **Java 2D (`Graphics2D`)** | Custom UI elements from scratch: rounded buttons (`RoundedButton`), drop-shadowed menu cards (`MenuCard`), and gradient backgrounds (`GradientPanel`). |
| **Shape visualization** | **Native Unicode (Serif)** | Shapes are drawn with font characters (♔, ♛, ♜, ♟). This eliminates the need for raster assets and maintains clarity at any resolution. |
| **Asynchrony** | **`SwingWorker`** | Background execution of complex AI calculations (Minimax / Negamax with alpha-beta clipping) without blocking the main UI thread. |
| **Project build** | **Direct `javac` CLI** | Build directly through the standard Java compiler without the configuration overhead of Maven or Gradle. |
| **Data architecture** | **In-Memory State** | An autonomous offline-first approach without binding to databases or local save files. |
---
## 📁 Project structure

```text
.
├── src/
│ └── com/
│ └── boardgames/
│ ├── Main.java # Entry point (AppFrame launch)
│ │
│ ├── ui/ # Shared UI components and styling
│ │ ├── AppFrame.java # Single application window (CardLayout)
│ │ ├── MenuPanel.java # Game selection start menu
│ │ └── theme/ # Custom themes, rounded buttons and gradients
│ │
│ ├── checkers/ # Independent module: Ukrainian checkers
│ │ ├── model/ # State model (Board, Piece, Move, PlayerColor)
│ │ ├── logic/ # Move validation, mandatory captures, rules
│ │ ├── ai/ # AI (Minimax / Negamax with alpha-beta)
│ │ └── ui/ # Board rendering, settings dialogs
│ │
│ └── chess/ # Independent module: Classical Chess
│ ├── model/ # State model (ChessBoard, ChessPiece, Side)
│ ├── logic/ # Checkmate, castling, en passant, 50-move rule
│ ├── ai/ # AI (Iterative Deepening, MVV-LVA, background thread)
│ └── ui/ # Board rendering with Unicode characters, pawn conversion
│
├── out/ # Generated .class files (after compilation)
└── README.md
```
---
## AI Characteristics✨

### The artificial intelligence in the game is made very high quality, so when choosing a higher difficulty, the game becomes quite difficult
#### AI characteristics at each difficulty level

| Level | Depth / Time | Behavioral features |
| :--- | :--- | :--- |
| **Beginner** | 2 moves / ~0.3 s | Evaluates only the basic material; periodically makes random moves from the top 3 to simulate human errors. |
| **Easy** | 3–4 moves / ~0.5 s | Starts to take into account center control and pawn/checker advancement. |
| **Medium** | 5–6 moves / ~1.0 s | Balanced game; uses iterative deepening (*Iterative Deepening*). |
| **Difficult** | 7–8 moves / ~2.5 s | Deep analysis with Alpha-Beta cutoff and move ordering (MVV-LVA). |
| **Expert** | 9+ moves / ~5.0 s | Maximum calculation depth with a hard time limit for complex positions. |
---
## Launch the game

### Requirements
* **JDK 21** or later.

### Compiling and running from the console is very simple

```bash
# 1. Clone the repository
git clone https://github.com/JPSSProgramming/chees-game.git
cd board-games

# 2. Run the application
java -cp out com.boardgames.Main
```
---
# Important nuances of the game

- The game does not save the state of the game between application restarts (**DB - absent**)
- In the current version, there is no display of the move history (PGN) on the screen
- In chess, there is no automatic check for three repetitions of a position (only the 50-move rule is implemented)
- The transformation of a checker into a queen occurs after the completion of the entire move, and not in the middle of the chain of captures (a slight simplification)

---
## 📄 License
````
Distributed under the MIT license. Details are in the `LICENSE` file.
````