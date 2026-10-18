# 🚀 PortfolioPro - Stock Trading & Portfolio Management System
### Academic Capstone Project for Submission to HCL

[![Java](https://img.shields.io/badge/Java-21%20%2F%2025%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-H2%20Persistent%20RDBMS-blue.svg)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/Submission-HCL%20Academic%20Project-purple.svg)](https://github.com/2006-Arehan/Portfolio-Pro)

---

## 📌 Project Overview
**PortfolioPro** is a full-stack, enterprise-grade Stock Trading & Quantitative Portfolio Management System built specifically for college project submission and technical viva evaluation by **HCL**.

It features an institutional-grade FinTech dashboard, a real-time stochastic market price simulator, full buy/sell trade execution with realistic statutory tax schedules (Brokerage, STT, GST), institutional portfolio risk analytics (Sharpe Ratio, Portfolio Beta, Value at Risk), sector asset allocation charts, and an immutable transaction audit ledger.

---

## 🌟 Key Features

- **🔐 Secure User Authentication & Onboarding:**
  - Dedicated **Login & Registration Portal** ([`login.html`](https://github.com/2006-Arehan/Portfolio-Pro/blob/main/src/main/resources/static/login.html)) with SHA-256 password hashing.
  - **1-Click Demo Credentials Autofill** (`hcl_student_demo` / `demo123`) for viva evaluation.
  - Automatic credit of **₹10,00,000** virtual capital for every newly registered trader.
- **🌐 Dedicated Multi-Page FinTech Architecture:**
  - **📊 Portfolio Analytics** ([`index.html`](https://github.com/2006-Arehan/Portfolio-Pro/blob/main/src/main/resources/static/index.html)): Executive KPIs, Net worth growth curve, sector donut chart, institutional risk matrix.
  - **⚡ Market Screener & Terminal** ([`market.html`](https://github.com/2006-Arehan/Portfolio-Pro/blob/main/src/main/resources/static/market.html)): Full-width live equity screener with real-time Brownian motion price flashing, interactive charts, and instant trade modals.
  - **💼 Equity Holdings** ([`holdings.html`](https://github.com/2006-Arehan/Portfolio-Pro/blob/main/src/main/resources/static/holdings.html)): Asset breakdown, average buy price, current valuation, unrealized returns, and printable statement generation.
  - **📜 Order Book & Audit Log** ([`orders.html`](https://github.com/2006-Arehan/Portfolio-Pro/blob/main/src/main/resources/static/orders.html)): Transaction audit ledger with 1-click CSV export.
- **⚡ Real-Time Market Feed & Simulator:** Live Brownian motion price simulation for 16 Indian & Global blue-chip equities (highlighting **HCL Technologies**, TCS, Infosys, Reliance, HDFC Bank, Tata Motors, Apple, NVIDIA, and Microsoft).
- **💼 Interactive Trading Terminal:** Execute Market and Limit orders with instant statutory fee breakdowns: Flat ₹20 Brokerage + 0.1% STT + 18% GST on brokerage.
- **📊 Quantitative Portfolio Analytics:**
  - Real-time Portfolio Valuation, Total Invested, Unrealized P&L, Realized P&L, and Net Worth.
  - Interactive Chart.js Net Worth Growth Curve (1D, 1W, 1M, 1Y).
  - Asset Allocation by Sector Donut Chart.
  - Institutional Risk Matrix: **Sharpe Ratio**, **Portfolio Beta**, and **1-Day 95% Value at Risk (VaR)**.
- **📜 Immutable Audit Ledger & Order Book:** Searchable transaction history with 1-click CSV export and printable statement generation.
- **★ Watchlist Management:** Pin favorite equities with instant star toggles.
- **🌓 FinTech UI with Dark & Light Themes:** Bloomberg/Robinhood inspired glassmorphic aesthetic with flashing green/red ticker updates.
- **🎓 Viva & Evaluator Friendly:** Includes a **1-Click "Reset Demo"** button that restores the wallet to ₹10,00,000 and seeds sample initial holdings for live demonstrations.

---

## 🏗️ Technology Stack

| Layer | Technologies Used |
|---|---|
| **Backend** | Java 21 / 25 LTS, Spring Boot 3.3.4 (Spring Web MVC, Spring Data JPA, Hibernate, Bean Validation) |
| **Database** | H2 Embedded RDBMS (File-persisted at `./data/portfoliopro.mv.db`, H2 Web Console at `/h2-console`) |
| **Frontend** | Single-Page Architecture, HTML5, CSS3 Glassmorphism, Vanilla JavaScript ES6+, Chart.js 4.4 |
| **Testing** | JUnit 5, Spring Boot Test Suite (100% Passing Tests) |
| **Build Tool** | Apache Maven 3.9+ |

---

## 🚀 Quick Start Guide

### Prerequisites
- **JDK 17, 21, or 25** installed (`java -version`).

### Method 1: One-Click Startup (Windows)
Double-click:
```bat
run_project.bat
```
This starts the Spring Boot backend on `http://localhost:8080` and opens your default browser automatically!

### Method 2: Command Line (Maven)
Navigate to the project root directory and run:
```bash
# Compile and run via Maven
mvn spring-boot:run
```

Once started, open:
- **Application Dashboard:** [http://localhost:8080](http://localhost:8080)
- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:file:./data/portfoliopro`
  - User Name: `sa`
  - Password: *(leave blank)*

### Running Automated Test Suite
```bash
mvn test
```

---

## 📁 Project Directory Structure
```
portfoliopro/
├── pom.xml                               # Maven Project Descriptor
├── schema.sql                            # Relational Database SQL Schema
├── run_project.bat                       # 1-Click Windows Launcher Script
├── HCL_PROJECT_REPORT.md                 # Comprehensive HCL Submission Report
├── README.md                             # Project Documentation
├── src/
│   ├── main/
│   │   ├── java/com/portfoliopro/
│   │   │   ├── PortfolioProApplication.java     # Spring Boot Main Class (@EnableScheduling)
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java        # Pre-seeds 16 equities & demo portfolio
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # /api/auth register & login
│   │   │   │   ├── StockController.java        # /api/stocks REST endpoints
│   │   │   │   ├── TradingController.java      # /api/trade order execution
│   │   │   │   ├── PortfolioController.java    # /api/portfolio analytics
│   │   │   │   ├── WatchlistController.java    # /api/watchlist management
│   │   │   │   └── UserController.java         # /api/user profile & demo reset
│   │   │   ├── dto/
│   │   │   │   ├── AuthRequest.java            # Login credentials payload
│   │   │   │   ├── RegisterRequest.java        # Registration payload
│   │   │   │   ├── AuthResponse.java           # Authentication response & token
│   │   │   │   ├── ApiResponse.java            # Standard unified response envelope
│   │   │   │   ├── OrderRequest.java           # Buy/Sell payload
│   │   │   │   ├── QuoteCalculationDTO.java    # Fee & margin calculation
│   │   │   │   ├── PortfolioSummaryDTO.java    # Net worth & PnL metrics
│   │   │   │   ├── HoldingDTO.java             # Enriched stock holding
│   │   │   │   ├── SectorAllocationDTO.java    # Sector breakdown data
│   │   │   │   ├── RiskMetricsDTO.java         # Sharpe, Beta, VaR data
│   │   │   │   └── PricePointDTO.java          # Time-series chart points
│   │   │   ├── model/
│   │   │   │   ├── User.java                   # User entity with password hash
│   │   │   │   ├── Stock.java                  # Stock market equity entity
│   │   │   │   ├── PortfolioItem.java          # Holdings entity
│   │   │   │   ├── Transaction.java            # Executed order entity
│   │   │   │   └── WatchlistItem.java          # Watchlist item entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── StockRepository.java
│   │   │   │   ├── PortfolioItemRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   └── WatchlistRepository.java
│   │   │   └── service/
│   │   │       ├── AuthService.java            # Registration, login, password hashing
│   │   │       ├── StockMarketService.java     # Real-time Brownian motion ticker
│   │   │       ├── TradingService.java         # ACID order executor & tax calculator
│   │   │       ├── PortfolioService.java       # Valuation & quantitative risk engine
│   │   │       ├── WatchlistService.java       # Watchlist service
│   │   │       └── UserService.java            # Account & seed reset
│   │   └── resources/
│   │       ├── application.properties          # Server & H2 Database configuration
│   │       └── static/
│   │           ├── login.html                  # Dedicated Login & Registration Portal
│   │           ├── index.html                  # Dedicated Portfolio Analytics Dashboard
│   │           ├── market.html                 # Dedicated Market Screener & Terminal
│   │           ├── holdings.html               # Dedicated Portfolio Holdings Ledger
│   │           ├── orders.html                 # Dedicated Order Book & Audit Log
│   │           ├── css/
│   │           │   └── style.css               # Professional FinTech Dark/Light stylesheet
│   │           └── js/
│   │               └── app.js                  # Multi-page client script & Chart.js graphs
│   └── test/
│       └── java/com/portfoliopro/
│           └── PortfolioProApplicationTests.java # 5 Automated Integration Tests
```

---

## 🎯 Viva & Evaluator Demonstration Checklist
When presenting this project to HCL evaluators, follow this recommended walkthrough:

1. **Dashboard Overview:** Point out the live ticker ribbon at the top, showing real-time price fluctuations.
2. **Flagship Equity:** Show `HCL Technologies Ltd.` (HCLTECH) prominently listed with real market metrics.
3. **Interactive Trading:**
   - Click "Trade" on any stock (e.g., HCLTECH).
   - Enter quantity 10.
   - Point out the **real-time statutory fee calculation** (Flat ₹20 brokerage, 0.1% STT, 18% GST).
   - Click "Execute Buy Order" and observe the instant toast confirmation, updated cash balance, and recalculated portfolio P&L.
4. **Quantitative Risk Metrics:** Highlight the **Sharpe Ratio (1.25)**, **Portfolio Beta (1.08)**, and **Value at Risk (VaR)** calculated in the Risk Matrix card.
5. **Database Web Console:** Open [http://localhost:8080/h2-console](http://localhost:8080/h2-console) to show the relational tables (`USERS`, `STOCKS`, `PORTFOLIO_ITEMS`, `TRANSACTIONS`) and execute `SELECT * FROM TRANSACTIONS;` to prove persistent audit logging.
6. **Code Architecture:** Show the clean package structure following MVC, Spring Data JPA repositories, and unit tests passing 100%.
7. **Demo Reset:** Demonstrate the "↻ Reset Demo" button, which instantly restores baseline holdings and ₹10,00,000 cash for repeat demonstrations.
