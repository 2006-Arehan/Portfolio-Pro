-- ===================================================================
-- PortfolioPro: Stock Trading & Portfolio Management System
-- Relational Database Schema Definition (HCL Project Submission)
-- Compatible with H2, MySQL 8+, and PostgreSQL 15+
-- ===================================================================

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL DEFAULT 'demo123_hash',
    cash_balance DOUBLE PRECISION NOT NULL,
    initial_capital DOUBLE PRECISION NOT NULL,
    total_realized_pnl DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Stocks Equities Table
CREATE TABLE IF NOT EXISTS stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticker VARCHAR(20) NOT NULL UNIQUE,
    company_name VARCHAR(150) NOT NULL,
    sector VARCHAR(80) NOT NULL,
    current_price DOUBLE PRECISION NOT NULL,
    previous_close DOUBLE PRECISION NOT NULL,
    open_price DOUBLE PRECISION NOT NULL,
    day_high DOUBLE PRECISION NOT NULL,
    day_low DOUBLE PRECISION NOT NULL,
    volume BIGINT NOT NULL,
    market_cap DOUBLE PRECISION NOT NULL,
    pe_ratio DOUBLE PRECISION NOT NULL,
    dividend_yield DOUBLE PRECISION NOT NULL,
    fifty_two_week_high DOUBLE PRECISION NOT NULL,
    fifty_two_week_low DOUBLE PRECISION NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Portfolio Items (Holdings) Table
CREATE TABLE IF NOT EXISTS portfolio_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    avg_buy_price DOUBLE PRECISION NOT NULL,
    total_invested DOUBLE PRECISION NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_ticker UNIQUE (user_id, ticker)
);

-- 4. Transactions Ledger (Audit Trail) Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(40) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(20) NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    order_type VARCHAR(10) NOT NULL,      -- BUY or SELL
    order_category VARCHAR(10) NOT NULL,  -- MARKET or LIMIT
    quantity INT NOT NULL,
    execution_price DOUBLE PRECISION NOT NULL,
    gross_amount DOUBLE PRECISION NOT NULL,
    brokerage DOUBLE PRECISION NOT NULL,
    stt_tax DOUBLE PRECISION NOT NULL,
    gst_tax DOUBLE PRECISION NOT NULL,
    net_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL,          -- EXECUTED, REJECTED
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. User Watchlist Table
CREATE TABLE IF NOT EXISTS watchlist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(20) NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_watchlist_user_ticker UNIQUE (user_id, ticker)
);

-- Indices for Optimized Performance
CREATE INDEX IF NOT EXISTS idx_stocks_ticker ON stocks(ticker);
CREATE INDEX IF NOT EXISTS idx_portfolio_user ON portfolio_items(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_user ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_ticker ON transactions(ticker);
CREATE INDEX IF NOT EXISTS idx_watchlist_user ON watchlist_items(user_id);
