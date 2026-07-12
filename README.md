# 🎮 Marble City - A Physics-Based Turn-Based Game

[![Java](https://img.shields.io/badge/Java-100%25-orange)](https://www.java.com/)
[![JavaFX](https://img.shields.io/badge/Framework-JavaFX-blue)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> A dynamic 2D marble game with realistic physics simulation, turn-based gameplay mechanics, and interactive slingshot controls built with JavaFX.

## ✨ Features

- 🎯 **Turn-Based Gameplay** - Strategic marble placement and movement
- 🔫 **Interactive Slingshot Mechanics** - Intuitive marble launching system
- 🌍 **Realistic Physics Simulation** - True-to-life collision and motion physics
- 🎨 **JavaFX Graphics** - Smooth, responsive 2D visual rendering
- 🏗️ **SimBuilder Framework** - Custom simulation engine for advanced physics handling
- 🎲 **Dynamic Obstacles** - Challenging level design with interactive elements
- 📊 **Score System** - Track progress and compete for high scores

## 🚀 Getting Started

### Prerequisites

- **Java 11 or higher**
- **JavaFX SDK 17 or higher**
- **Maven** or **Gradle** (for dependency management)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Matela-cyber/Marble-Game.git
   cd Marble-Game
   ```

2. **Build the project**
   ```bash
   # Using Maven
   mvn clean install
   
   # Using Gradle
   gradle build
   ```

3. **Run the game**
   ```bash
   # Using Maven
   mvn javafx:run
   
   # Using Gradle
   gradle run
   ```

## 🎮 How to Play

1. **Select your marble** - Choose which marble to shoot
2. **Aim your slingshot** - Click and drag to set trajectory and power
3. **Launch** - Release to fire your marble across the board
4. **Navigate obstacles** - Overcome challenges to reach the goal
5. **Score points** - Earn points based on efficiency and accuracy

## 🏗️ Project Structure

```
Marble-Game/
├── src/
│   ├── main/java/
│   │   └── com/marblecity/
│   │       ├── game/          # Core game logic
│   │       ├── physics/       # Physics engine & SimBuilder
│   │       ├── ui/            # JavaFX UI components
│   │       └── utils/         # Utility classes
│   └── resources/
│       ├── styles/            # CSS stylesheets
│       └── assets/            # Game assets
├── pom.xml                    # Maven configuration
└── README.md
```

## 🔧 Technologies Used

| Technology | Purpose |
|-----------|---------|
| **Java** | Core game logic and backend |
| **JavaFX** | 2D graphics rendering and UI |
| **SimBuilder** | Advanced physics simulation |
| **Maven/Gradle** | Build automation and dependency management |

## 🧮 Physics Engine (SimBuilder)

The game utilizes a custom SimBuilder framework that handles:
- **Collision detection** - Accurate marble-to-marble and marble-to-wall interactions
- **Velocity calculations** - Realistic momentum and motion physics
- **Gravity simulation** - Environmental physics effects
- **Friction modeling** - Surface resistance and energy dissipation

## 📖 Documentation

### Key Classes

- `Game` - Main game controller and event handler
- `PhysicsEngine` - Core physics simulation using SimBuilder
- `Marble` - Individual marble entity with physics properties
- `GameBoard` - Playing field management and rendering
- `SlingShot` - Marble launch mechanism

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Matela-cyber**
- GitHub: [@Matela-cyber](https://github.com/Matela-cyber)

## 🙏 Acknowledgments

- JavaFX community for excellent documentation and examples
- Physics simulation inspiration from classic marble games
- Contributors and testers who helped refine the gameplay

---

<div align="center">

Made with ❤️ by Matela-cyber

[⬆ back to top](#-marble-city---a-physics-based-turn-based-game)

</div>
