/**
 * PortfolioPro - Stock Trading & Portfolio Management Client Application
 * Enterprise Grade FinTech Multi-Page Dashboard Script
 */

// State Management
const state = {
    user: null,
    summary: null,
    stocks: [],
    holdings: [],
    transactions: [],
    watchlist: new Set(),
    selectedStock: null,
    orderType: 'BUY',
    chartTimeframe: '1M',
    stockDetailTimeframe: '1D',
    portfolioChart: null,
    sectorChart: null,
    stockDetailChart: null
};

// Currency Formatter for INR (Indian Rupee)
const fmtINR = (val) => {
    if (val === undefined || val === null || isNaN(val)) return '₹0.00';
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    }).format(val);
};

const fmtNum = (val) => {
    if (val === undefined || val === null || isNaN(val)) return '0';
    return new Intl.NumberFormat('en-IN').format(val);
};

// Authenticated API Fetch wrapper that attaches user identity
function apiFetch(url, options = {}) {
    options.headers = options.headers || {};
    if (state.user && state.user.id) {
        options.headers['X-User-Id'] = state.user.id;
    }
    return fetch(url, options);
}

// Authentication Check Guard
function checkAuth() {
    const userStr = localStorage.getItem('portfoliopro_user');
    if (!userStr) {
        window.location.href = 'login.html';
        return false;
    }
    try {
        state.user = JSON.parse(userStr);
        return true;
    } catch (e) {
        localStorage.removeItem('portfoliopro_user');
        window.location.href = 'login.html';
        return false;
    }
}

function handleLogout() {
    localStorage.removeItem('portfoliopro_user');
    localStorage.removeItem('portfoliopro_token');
    window.location.href = 'login.html';
}

// Document Ready
document.addEventListener('DOMContentLoaded', () => {
    // If not authenticated, redirect to login
    if (!checkAuth()) return;

    initTheme();
    setupCommonEventListeners();
    loadInitialData();

    // Start live polling every 3.5 seconds to refresh price changes smoothly
    setInterval(pollMarketUpdates, 3500);
});

// Theme Management
function initTheme() {
    const savedTheme = localStorage.getItem('portfoliopro_theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);
}

function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') || 'dark';
    const next = current === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', next);
    localStorage.setItem('portfoliopro_theme', next);
    updateThemeIcon(next);
    if (state.portfolioChart) state.portfolioChart.update();
    if (state.sectorChart) state.sectorChart.update();
}

function updateThemeIcon(theme) {
    const btn = document.getElementById('themeToggleBtn');
    if (btn) {
        btn.innerHTML = theme === 'dark' ? '☀️' : '🌙';
    }
}

// Event Listeners across pages
function setupCommonEventListeners() {
    document.getElementById('themeToggleBtn')?.addEventListener('click', toggleTheme);
    document.getElementById('logoutBtn')?.addEventListener('click', handleLogout);
    document.getElementById('resetDemoBtn')?.addEventListener('click', resetDemoAccount);
    document.getElementById('depositBtn')?.addEventListener('click', () => openModal('depositModal'));
    document.getElementById('hclInfoBtn')?.addEventListener('click', () => openModal('hclInfoModal'));

    // Market Search
    document.getElementById('marketSearchInput')?.addEventListener('input', (e) => filterMarketTable(e.target.value));

    // Market category filters
    document.querySelectorAll('.market-filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.market-filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            renderMarketTable(btn.getAttribute('data-filter'));
        });
    });

    // Timeframe selector for Portfolio chart
    document.querySelectorAll('.portfolio-tf-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.portfolio-tf-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            state.chartTimeframe = btn.getAttribute('data-tf');
            loadPortfolioChartData();
        });
    });

    // Timeframe selector for Stock Detail chart
    document.querySelectorAll('.stock-tf-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.stock-tf-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            state.stockDetailTimeframe = btn.getAttribute('data-tf');
            if (state.selectedStock) loadStockChart(state.selectedStock.ticker);
        });
    });

    // Trading Modal Switches
    document.getElementById('buyToggleBtn')?.addEventListener('click', () => setTradeMode('BUY'));
    document.getElementById('sellToggleBtn')?.addEventListener('click', () => setTradeMode('SELL'));
    document.getElementById('orderQtyInput')?.addEventListener('input', calculateOrderQuote);
    document.getElementById('orderCategorySelect')?.addEventListener('change', (e) => {
        const isLimit = e.target.value === 'LIMIT';
        const limitGroup = document.getElementById('limitPriceGroup');
        if (limitGroup) limitGroup.style.display = isLimit ? 'block' : 'none';
        calculateOrderQuote();
    });
    document.getElementById('limitPriceInput')?.addEventListener('input', calculateOrderQuote);

    // Quantity Chips
    document.querySelectorAll('.qty-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const qtyInput = document.getElementById('orderQtyInput');
            const add = parseInt(chip.getAttribute('data-add'), 10);
            qtyInput.value = (parseInt(qtyInput.value || 0, 10)) + add;
            calculateOrderQuote();
        });
    });

    // Forms & Export Buttons
    document.getElementById('orderForm')?.addEventListener('submit', handleOrderSubmit);
    document.getElementById('depositForm')?.addEventListener('submit', handleDepositSubmit);
    document.getElementById('exportOrdersBtn')?.addEventListener('click', exportOrdersCSV);
    document.getElementById('exportStatementBtn')?.addEventListener('click', printStatement);
}

// Initial Data Loading
async function loadInitialData() {
    try {
        await Promise.all([
            loadUserProfile(),
            loadStocks(),
            loadPortfolioSummary(),
            loadHoldings(),
            loadWatchlist(),
            loadTransactions()
        ]);

        renderTickerTape();

        // Page specific initializations based on which page is currently open
        const path = window.location.pathname.toLowerCase();

        if (path.includes('market.html')) {
            renderMarketTable();
        } else if (path.includes('holdings.html')) {
            renderHoldingsTable();
        } else if (path.includes('orders.html')) {
            renderTransactionsTable();
        } else {
            // Analytics Dashboard (index.html)
            renderTopGainersLosers();
            loadPortfolioChartData();
            loadSectorAllocation();
            loadRiskMetrics();
        }
    } catch (err) {
        console.error("Initialization error:", err);
    }
}

// API Calls
async function loadUserProfile() {
    const res = await apiFetch('/api/user/profile');
    const json = await res.json();
    if (json.success) {
        state.user = json.data;
        const balEl = document.getElementById('walletBalanceDisplay');
        if (balEl) balEl.textContent = fmtINR(state.user.cashBalance);
        const nameEl = document.getElementById('userNameDisplay');
        if (nameEl) nameEl.textContent = state.user.fullName;
    }
}

async function loadStocks() {
    const res = await apiFetch('/api/stocks');
    const json = await res.json();
    if (json.success) {
        state.stocks = json.data;
    }
}

async function loadPortfolioSummary() {
    const res = await apiFetch('/api/portfolio/summary');
    const json = await res.json();
    if (json.success) {
        state.summary = json.data;
        updateSummaryUI(state.summary);
    }
}

async function loadHoldings() {
    const res = await apiFetch('/api/portfolio/holdings');
    const json = await res.json();
    if (json.success) {
        state.holdings = json.data;
        const badge = document.getElementById('holdingsCountBadge');
        if (badge) badge.textContent = state.holdings.length;
        if (window.location.pathname.includes('holdings.html')) {
            renderHoldingsTable();
        }
    }
}

async function loadWatchlist() {
    const res = await apiFetch('/api/watchlist');
    const json = await res.json();
    if (json.success) {
        state.watchlist = new Set(json.data.map(s => s.ticker));
    }
}

async function loadTransactions() {
    const res = await apiFetch('/api/trade/transactions');
    const json = await res.json();
    if (json.success) {
        state.transactions = json.data;
        const badge = document.getElementById('ordersCountBadge');
        if (badge) badge.textContent = state.transactions.length;
        if (window.location.pathname.includes('orders.html')) {
            renderTransactionsTable();
        }
    }
}

// Periodic Background Market Tick
async function pollMarketUpdates() {
    try {
        const res = await apiFetch('/api/stocks');
        const json = await res.json();
        if (json.success) {
            const oldStockMap = new Map(state.stocks.map(s => [s.ticker, s.currentPrice]));
            state.stocks = json.data;

            // Flash updated rows in table if on market table
            state.stocks.forEach(s => {
                const oldPrice = oldStockMap.get(s.ticker);
                if (oldPrice && oldPrice !== s.currentPrice) {
                    const row = document.getElementById(`stock-row-${s.ticker}`);
                    if (row) {
                        const cell = row.querySelector('.stock-price-cell');
                        if (cell) {
                            cell.classList.remove('flash-green', 'flash-red');
                            void cell.offsetWidth; // trigger reflow
                            cell.classList.add(s.currentPrice > oldPrice ? 'flash-green' : 'flash-red');
                            cell.textContent = fmtINR(s.currentPrice);
                        }
                    }
                }
            });

            renderTickerTape();
            loadPortfolioSummary();
        }
    } catch (e) {
        // Tolerated
    }
}

// UI Renderers
function updateSummaryUI(s) {
    if (!s) return;
    const nwEl = document.getElementById('netWorthValue');
    if (nwEl) nwEl.textContent = fmtINR(s.totalNetWorth);
    const pvEl = document.getElementById('portfolioValue');
    if (pvEl) pvEl.textContent = fmtINR(s.totalPortfolioValue);
    const tiEl = document.getElementById('totalInvestedValue');
    if (tiEl) tiEl.textContent = fmtINR(s.totalInvested);
    const cbEl = document.getElementById('cashBalanceCard');
    if (cbEl) cbEl.textContent = fmtINR(s.cashBalance);
    const rpEl = document.getElementById('realizedPnlValue');
    if (rpEl) rpEl.textContent = fmtINR(s.totalRealizedPnl);

    const unrealizedEl = document.getElementById('unrealizedPnlValue');
    const unrealizedBadge = document.getElementById('unrealizedPnlBadge');
    if (unrealizedEl && unrealizedBadge) {
        unrealizedEl.textContent = fmtINR(s.unrealizedPnl);
        const isProfit = s.unrealizedPnl >= 0;
        unrealizedEl.style.color = isProfit ? 'var(--profit-green)' : 'var(--loss-red)';
        unrealizedBadge.textContent = `${isProfit ? '+' : ''}${s.unrealizedPnlPercent}%`;
        unrealizedBadge.className = `tag ${isProfit ? 'up' : 'down'}`;
    }

    const dayPnlBadge = document.getElementById('dayPnlBadge');
    if (dayPnlBadge) {
        const isDayProfit = s.dayPnl >= 0;
        dayPnlBadge.textContent = `Today: ${isDayProfit ? '+' : ''}${fmtINR(s.dayPnl)} (${s.dayPnlPercent}%)`;
        dayPnlBadge.className = `tag ${isDayProfit ? 'up' : 'down'}`;
    }
}

function renderTickerTape() {
    const track = document.getElementById('tickerTrack');
    if (!track || state.stocks.length === 0) return;

    const items = [...state.stocks, ...state.stocks];
    track.innerHTML = items.map(s => {
        const isUp = s.change >= 0;
        return `
            <div class="ticker-item" onclick="openStockDetailModal('${s.ticker}')">
                <span class="sym">${s.ticker}</span>
                <span class="price">${fmtINR(s.currentPrice)}</span>
                <span class="change ${isUp ? 'up' : 'down'}">${isUp ? '+' : ''}${s.changePercent}%</span>
            </div>
        `;
    }).join('');
}

function renderTopGainersLosers() {
    const gainersContainer = document.getElementById('topGainersList');
    const losersContainer = document.getElementById('topLosersList');
    if (!gainersContainer || !losersContainer) return;

    const sorted = [...state.stocks].sort((a, b) => b.changePercent - a.changePercent);
    const topGainers = sorted.slice(0, 3);
    const topLosers = sorted.slice(-3).reverse();

    gainersContainer.innerHTML = topGainers.map(s => renderMiniStockItem(s, true)).join('');
    losersContainer.innerHTML = topLosers.map(s => renderMiniStockItem(s, false)).join('');
}

function renderMiniStockItem(s, isUp) {
    return `
        <div class="mini-stock-item" onclick="openStockDetailModal('${s.ticker}')" style="display:flex; justify-content:space-between; align-items:center; padding:8px 0; border-bottom:1px solid var(--border-subtle); cursor:pointer;">
            <div>
                <strong style="font-size:13px; font-family:var(--font-mono);">${s.ticker}</strong>
                <div style="font-size:11px; color:var(--text-muted);">${s.companyName.substring(0, 20)}...</div>
            </div>
            <div style="text-align:right;">
                <div style="font-weight:600; font-size:13px;">${fmtINR(s.currentPrice)}</div>
                <span class="tag ${isUp ? 'up' : 'down'}" style="font-size:11px;">${isUp ? '+' : ''}${s.changePercent}%</span>
            </div>
        </div>
    `;
}

function renderMarketTable(filter = 'ALL') {
    const tbody = document.getElementById('marketTableBody');
    if (!tbody) return;

    let filtered = [...state.stocks];
    if (filter === 'GAINERS') {
        filtered = filtered.filter(s => s.changePercent > 0).sort((a, b) => b.changePercent - a.changePercent);
    } else if (filter === 'LOSERS') {
        filtered = filtered.filter(s => s.changePercent < 0).sort((a, b) => a.changePercent - b.changePercent);
    } else if (filter === 'WATCHLIST') {
        filtered = filtered.filter(s => state.watchlist.has(s.ticker));
    } else if (filter === 'IT') {
        filtered = filtered.filter(s => s.sector.includes('Information Technology'));
    } else if (filter === 'BANKING') {
        filtered = filtered.filter(s => s.sector.includes('Banking'));
    }

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align:center; padding:32px; color:var(--text-muted);">No stocks matching filter criteria.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(s => {
        const isUp = s.change >= 0;
        const isWatched = state.watchlist.has(s.ticker);
        const dayRangePct = Math.min(100, Math.max(0, ((s.currentPrice - s.dayLow) / ((s.dayHigh - s.dayLow) || 1)) * 100));

        return `
            <tr id="stock-row-${s.ticker}">
                <td style="width:40px;">
                    <button class="icon-btn btn-sm" onclick="toggleWatchlist('${s.ticker}')" title="${isWatched ? 'Remove from Watchlist' : 'Add to Watchlist'}">
                        ${isWatched ? '★' : '☆'}
                    </button>
                </td>
                <td>
                    <div class="ticker-cell" style="cursor:pointer;" onclick="openStockDetailModal('${s.ticker}')">
                        <div class="stock-avatar">${s.ticker.substring(0, 3)}</div>
                        <div class="stock-meta">
                            <span class="sym">${s.ticker} ${s.ticker === 'HCLTECH' ? '<span class="hcl-badge">HCL</span>' : ''}</span>
                            <span class="company">${s.companyName}</span>
                        </div>
                    </div>
                </td>
                <td><span class="tag tag-sector">${s.sector}</span></td>
                <td class="stock-price-cell" style="font-family:var(--font-mono); font-weight:700;">${fmtINR(s.currentPrice)}</td>
                <td>
                    <span class="tag ${isUp ? 'up' : 'down'}" style="font-weight:700;">
                        ${isUp ? '▲ +' : '▼ '}${s.changePercent}%
                    </span>
                </td>
                <td style="min-width:140px;">
                    <div style="display:flex; justify-content:space-between; font-size:10px; color:var(--text-muted); margin-bottom:2px;">
                        <span>L: ${fmtINR(s.dayLow)}</span>
                        <span>H: ${fmtINR(s.dayHigh)}</span>
                    </div>
                    <div style="height:4px; background:var(--bg-elevated); border-radius:2px; overflow:hidden;">
                        <div style="height:100%; width:${dayRangePct}%; background:var(--brand-primary);"></div>
                    </div>
                </td>
                <td style="font-family:var(--font-mono); font-size:12px; color:var(--text-secondary);">${fmtNum(s.volume)}</td>
                <td style="text-align:right;">
                    <button class="btn btn-primary btn-sm" onclick="openTradeModal('${s.ticker}', 'BUY')">Trade</button>
                    <button class="btn btn-outline btn-sm" onclick="openStockDetailModal('${s.ticker}')">Chart</button>
                </td>
            </tr>
        `;
    }).join('');
}

function filterMarketTable(query) {
    const q = (query || '').toLowerCase().trim();
    const rows = document.querySelectorAll('#marketTableBody tr');
    rows.forEach(r => {
        const text = r.textContent.toLowerCase();
        r.style.display = text.includes(q) ? '' : 'none';
    });
}

function renderHoldingsTable() {
    const tbody = document.getElementById('holdingsTableBody');
    if (!tbody) return;

    if (state.holdings.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding:32px; color:var(--text-muted);">No equity holdings currently. Explore the Market Screener to place trades!</td></tr>`;
        return;
    }

    tbody.innerHTML = state.holdings.map(h => {
        const isProfit = h.unrealizedPnl >= 0;
        return `
            <tr>
                <td>
                    <div class="ticker-cell">
                        <div class="stock-avatar">${h.ticker.substring(0, 3)}</div>
                        <div class="stock-meta">
                            <span class="sym">${h.ticker}</span>
                            <span class="company">${h.companyName}</span>
                        </div>
                    </div>
                </td>
                <td style="font-family:var(--font-mono); font-weight:700;">${h.quantity}</td>
                <td style="font-family:var(--font-mono);">${fmtINR(h.avgBuyPrice)}</td>
                <td style="font-family:var(--font-mono); font-weight:700;">${fmtINR(h.currentPrice)}</td>
                <td style="font-family:var(--font-mono);">${fmtINR(h.investedAmount)}</td>
                <td style="font-family:var(--font-mono); font-weight:700;">${fmtINR(h.currentValue)}</td>
                <td>
                    <div style="font-family:var(--font-mono); font-weight:700; color:${isProfit ? 'var(--profit-green)' : 'var(--loss-red)'};">
                        ${isProfit ? '+' : ''}${fmtINR(h.unrealizedPnl)}
                    </div>
                    <span class="tag ${isProfit ? 'up' : 'down'}" style="font-size:10px;">${isProfit ? '+' : ''}${h.unrealizedPnlPercent}%</span>
                </td>
                <td>
                    <span class="tag tag-sector" style="font-family:var(--font-mono);">${h.portfolioWeightPercent}%</span>
                </td>
                <td style="text-align:right;">
                    <button class="btn btn-success btn-sm" onclick="openTradeModal('${h.ticker}', 'BUY')">Buy +</button>
                    <button class="btn btn-danger btn-sm" onclick="openTradeModal('${h.ticker}', 'SELL')">Sell</button>
                </td>
            </tr>
        `;
    }).join('');
}

function renderTransactionsTable() {
    const tbody = document.getElementById('ordersTableBody');
    if (!tbody) return;

    if (state.transactions.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding:32px; color:var(--text-muted);">No order transactions recorded yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = state.transactions.map(t => {
        const isBuy = t.orderType === 'BUY';
        return `
            <tr>
                <td style="font-family:var(--font-mono); font-size:11px; color:var(--text-muted);">${t.orderId}</td>
                <td style="font-size:12px; color:var(--text-secondary);">${t.executedAt ? t.executedAt.replace('T', ' ').substring(0, 19) : ''}</td>
                <td>
                    <strong style="font-family:var(--font-mono);">${t.ticker}</strong>
                    <div style="font-size:10px; color:var(--text-muted);">${t.companyName}</div>
                </td>
                <td>
                    <span class="tag ${isBuy ? 'up' : 'down'}" style="font-weight:700;">${t.orderType}</span>
                </td>
                <td><span class="tag tag-sector">${t.orderCategory}</span></td>
                <td style="font-family:var(--font-mono); font-weight:600;">${t.quantity}</td>
                <td style="font-family:var(--font-mono);">${fmtINR(t.executionPrice)}</td>
                <td style="font-family:var(--font-mono); font-size:11px; color:var(--text-secondary);">
                    ₹${(t.brokerage + t.sttTax + t.gstTax).toFixed(2)}
                </td>
                <td style="font-family:var(--font-mono); font-weight:700;">${fmtINR(t.netAmount)}</td>
            </tr>
        `;
    }).join('');
}

// Watchlist Toggle
async function toggleWatchlist(ticker) {
    try {
        const res = await apiFetch(`/api/watchlist/toggle/${ticker}`, { method: 'POST' });
        const json = await res.json();
        if (json.success) {
            if (json.data.watched) {
                state.watchlist.add(ticker);
                showToast(`Added ${ticker} to watchlist`, 'success');
            } else {
                state.watchlist.delete(ticker);
                showToast(`Removed ${ticker} from watchlist`, 'info');
            }
            if (window.location.pathname.includes('market.html')) {
                renderMarketTable();
            }
        }
    } catch (e) {
        showToast('Failed to update watchlist', 'error');
    }
}

// Chart.js Visualizations
async function loadPortfolioChartData() {
    try {
        const res = await apiFetch(`/api/portfolio/performance-history?timeframe=${state.chartTimeframe}`);
        const json = await res.json();
        if (json.success) {
            renderPortfolioChart(json.data);
        }
    } catch (e) {
        console.error(e);
    }
}

function renderPortfolioChart(dataPoints = []) {
    const ctx = document.getElementById('portfolioGrowthChart')?.getContext('2d');
    if (!ctx) return;

    if (state.portfolioChart) {
        state.portfolioChart.destroy();
    }

    const labels = dataPoints.map(p => p.time);
    const values = dataPoints.map(p => p.price);

    const gradient = ctx.createLinearGradient(0, 0, 0, 300);
    gradient.addColorStop(0, 'rgba(59, 130, 246, 0.45)');
    gradient.addColorStop(1, 'rgba(59, 130, 246, 0.0)');

    state.portfolioChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total Net Worth (₹)',
                data: values,
                borderColor: '#3b82f6',
                borderWidth: 2.5,
                backgroundColor: gradient,
                fill: true,
                tension: 0.35,
                pointRadius: 2,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: (ctx) => ` Net Worth: ${fmtINR(ctx.raw)}`
                    }
                }
            },
            scales: {
                x: {
                    grid: { color: 'rgba(255,255,255,0.04)' },
                    ticks: { color: '#94a3b8', font: { family: 'Inter', size: 11 } }
                },
                y: {
                    grid: { color: 'rgba(255,255,255,0.05)' },
                    ticks: {
                        color: '#94a3b8',
                        font: { family: 'JetBrains Mono', size: 11 },
                        callback: (v) => '₹' + (v >= 100000 ? (v/100000).toFixed(1) + 'L' : v)
                    }
                }
            }
        }
    });
}

async function loadSectorAllocation() {
    try {
        const res = await apiFetch('/api/portfolio/sector-allocation');
        const json = await res.json();
        if (json.success) {
            renderSectorChart(json.data);
        }
    } catch (e) {
        console.error(e);
    }
}

function renderSectorChart(sectors = []) {
    const ctx = document.getElementById('sectorAllocationChart')?.getContext('2d');
    if (!ctx) return;

    if (state.sectorChart) {
        state.sectorChart.destroy();
    }

    const labels = sectors.map(s => s.sector);
    const values = sectors.map(s => s.totalValue);
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#06b6d4', '#ec4899', '#6366f1'];

    state.sectorChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.slice(0, labels.length),
                borderWidth: 2,
                borderColor: document.documentElement.getAttribute('data-theme') === 'light' ? '#ffffff' : '#151d2e'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { color: '#94a3b8', boxWidth: 12, font: { family: 'Inter', size: 11 } }
                },
                tooltip: {
                    callbacks: {
                        label: (ctx) => ` ${ctx.label}: ${fmtINR(ctx.raw)}`
                    }
                }
            },
            cutout: '70%'
        }
    });
}

async function loadRiskMetrics() {
    try {
        const res = await apiFetch('/api/portfolio/risk-metrics');
        const json = await res.json();
        if (json.success) {
            const r = json.data;
            const sharpeEl = document.getElementById('riskSharpeVal');
            if (sharpeEl) sharpeEl.textContent = r.sharpeRatio;
            const betaEl = document.getElementById('riskBetaVal');
            if (betaEl) betaEl.textContent = r.portfolioBeta;
            const ratingEl = document.getElementById('riskRatingVal');
            if (ratingEl) ratingEl.textContent = r.riskRating;
            const varEl = document.getElementById('riskVarVal');
            if (varEl) varEl.textContent = fmtINR(r.valueAtRisk95);
            const scoreEl = document.getElementById('diversificationScoreVal');
            if (scoreEl) scoreEl.textContent = `${r.diversificationScore}/100`;
            const progEl = document.getElementById('diversificationProgress');
            if (progEl) progEl.style.width = `${r.diversificationScore}%`;
            const recEl = document.getElementById('riskRecommendationText');
            if (recEl) recEl.textContent = r.recommendation;
        }
    } catch (e) {
        console.error(e);
    }
}

// Interactive Stock Deep Dive Modal
async function openStockDetailModal(ticker) {
    const stock = state.stocks.find(s => s.ticker === ticker);
    if (!stock) return;

    state.selectedStock = stock;
    const titleEl = document.getElementById('modalStockTitle');
    if (titleEl) titleEl.textContent = `${stock.companyName} (${stock.ticker})`;
    const priceEl = document.getElementById('modalStockPrice');
    if (priceEl) priceEl.textContent = fmtINR(stock.currentPrice);

    const isUp = stock.change >= 0;
    const changeBadge = document.getElementById('modalStockChange');
    if (changeBadge) {
        changeBadge.textContent = `${isUp ? '+' : ''}${stock.changePercent}% (${isUp ? '+' : ''}${fmtINR(stock.change)})`;
        changeBadge.className = `tag ${isUp ? 'up' : 'down'}`;
    }

    // Fill metadata
    const secEl = document.getElementById('stockDetailSector');
    if (secEl) secEl.textContent = stock.sector;
    const peEl = document.getElementById('stockDetailPe');
    if (peEl) peEl.textContent = stock.peRatio;
    const divEl = document.getElementById('stockDetailDiv');
    if (divEl) divEl.textContent = `${stock.dividendYield}%`;
    const capEl = document.getElementById('stockDetailCap');
    if (capEl) capEl.textContent = `₹${fmtNum(stock.marketCap)} Cr`;
    const highEl = document.getElementById('stockDetail52High');
    if (highEl) highEl.textContent = fmtINR(stock.fiftyTwoWeekHigh);
    const lowEl = document.getElementById('stockDetail52Low');
    if (lowEl) lowEl.textContent = fmtINR(stock.fiftyTwoWeekLow);

    openModal('stockDetailModal');
    loadStockChart(ticker);
}

async function loadStockChart(ticker) {
    try {
        const res = await apiFetch(`/api/stocks/${ticker}/history?timeframe=${state.stockDetailTimeframe}`);
        const json = await res.json();
        if (json.success) {
            renderStockDetailChart(json.data);
        }
    } catch (e) {
        console.error(e);
    }
}

function renderStockDetailChart(points = []) {
    const ctx = document.getElementById('stockDetailCanvas')?.getContext('2d');
    if (!ctx) return;

    if (state.stockDetailChart) {
        state.stockDetailChart.destroy();
    }

    const labels = points.map(p => p.time);
    const values = points.map(p => p.price);
    const isUp = values[values.length - 1] >= values[0];
    const lineColor = isUp ? '#10b981' : '#ef4444';

    const gradient = ctx.createLinearGradient(0, 0, 0, 260);
    gradient.addColorStop(0, isUp ? 'rgba(16, 185, 129, 0.3)' : 'rgba(239, 68, 68, 0.3)');
    gradient.addColorStop(1, 'rgba(0, 0, 0, 0)');

    state.stockDetailChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Price',
                data: values,
                borderColor: lineColor,
                borderWidth: 2,
                backgroundColor: gradient,
                fill: true,
                tension: 0.25,
                pointRadius: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: { label: (c) => ` Price: ${fmtINR(c.raw)}` }
                }
            },
            scales: {
                x: { ticks: { color: '#94a3b8', font: { size: 10 } } },
                y: { ticks: { color: '#94a3b8', font: { size: 10 }, callback: (v) => fmtINR(v) } }
            }
        }
    });
}

// Order Terminal Modal
function openTradeModal(ticker, mode = 'BUY') {
    const stock = state.stocks.find(s => s.ticker === ticker);
    if (!stock) return;

    state.selectedStock = stock;
    const nameEl = document.getElementById('tradeModalStockName');
    if (nameEl) nameEl.textContent = `${stock.companyName} (${stock.ticker})`;
    const priceEl = document.getElementById('tradeModalUnitPrice');
    if (priceEl) priceEl.textContent = fmtINR(stock.currentPrice);
    const hiddenEl = document.getElementById('orderTickerHidden');
    if (hiddenEl) hiddenEl.value = stock.ticker;
    const qtyEl = document.getElementById('orderQtyInput');
    if (qtyEl) qtyEl.value = 5;

    setTradeMode(mode);
    openModal('tradeModal');
    calculateOrderQuote();
}

function setTradeMode(mode) {
    state.orderType = mode;
    const buyBtn = document.getElementById('buyToggleBtn');
    const sellBtn = document.getElementById('sellToggleBtn');
    const submitBtn = document.getElementById('executeOrderSubmitBtn');

    if (mode === 'BUY') {
        buyBtn?.classList.add('buy-active');
        sellBtn?.classList.remove('sell-active');
        if (submitBtn) {
            submitBtn.className = 'btn btn-success';
            submitBtn.textContent = 'Execute Buy Order';
        }
    } else {
        sellBtn?.classList.add('sell-active');
        buyBtn?.classList.remove('buy-active');
        if (submitBtn) {
            submitBtn.className = 'btn btn-danger';
            submitBtn.textContent = 'Execute Sell Order';
        }
    }
    calculateOrderQuote();
}

async function calculateOrderQuote() {
    const ticker = document.getElementById('orderTickerHidden')?.value;
    const qty = parseInt(document.getElementById('orderQtyInput')?.value || 1, 10);
    if (!ticker || qty <= 0) return;

    try {
        const res = await apiFetch(`/api/trade/quote-calculator?ticker=${ticker}&orderType=${state.orderType}&quantity=${qty}`);
        const json = await res.json();
        if (json.success) {
            const q = json.data;
            const grossEl = document.getElementById('calcGrossVal');
            if (grossEl) grossEl.textContent = fmtINR(q.grossAmount);
            const brkEl = document.getElementById('calcBrokerageVal');
            if (brkEl) brkEl.textContent = fmtINR(q.brokerage);
            const sttEl = document.getElementById('calcSttVal');
            if (sttEl) sttEl.textContent = fmtINR(q.sttTax);
            const gstEl = document.getElementById('calcGstVal');
            if (gstEl) gstEl.textContent = fmtINR(q.gstTax);
            const netEl = document.getElementById('calcNetTotalVal');
            if (netEl) netEl.textContent = fmtINR(q.netTotal);

            const marginStatus = document.getElementById('marginStatusBadge');
            const submitBtn = document.getElementById('executeOrderSubmitBtn');
            if (marginStatus && submitBtn) {
                if (q.sufficientMargin) {
                    marginStatus.className = 'tag up';
                    marginStatus.textContent = state.orderType === 'BUY' ? '✓ Sufficient Cash Margin' : '✓ Shares Available in Portfolio';
                    submitBtn.disabled = false;
                } else {
                    marginStatus.className = 'tag down';
                    marginStatus.textContent = state.orderType === 'BUY' ? '✕ Insufficient Funds' : '✕ Insufficient Shares in Holdings';
                    submitBtn.disabled = true;
                }
            }
        }
    } catch (e) {
        console.error(e);
    }
}

async function handleOrderSubmit(e) {
    e.preventDefault();
    const ticker = document.getElementById('orderTickerHidden').value;
    const qty = parseInt(document.getElementById('orderQtyInput').value, 10);
    const category = document.getElementById('orderCategorySelect')?.value || 'MARKET';
    const limitPrice = category === 'LIMIT' ? parseFloat(document.getElementById('limitPriceInput').value) : null;

    try {
        const submitBtn = document.getElementById('executeOrderSubmitBtn');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Executing...';
        }

        const res = await apiFetch('/api/trade/order', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                ticker: ticker,
                orderType: state.orderType,
                orderCategory: category,
                quantity: qty,
                limitPrice: limitPrice
            })
        });

        const json = await res.json();
        if (json.success) {
            showToast(`Order executed: ${json.data.orderId}`, 'success');
            closeModal('tradeModal');
            // Refresh state
            await Promise.all([
                loadUserProfile(),
                loadPortfolioSummary(),
                loadHoldings(),
                loadTransactions()
            ]);
        } else {
            showToast(json.message, 'error');
        }
    } catch (err) {
        showToast('Error communicating with order server', 'error');
    } finally {
        const submitBtn = document.getElementById('executeOrderSubmitBtn');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = state.orderType === 'BUY' ? 'Execute Buy Order' : 'Execute Sell Order';
        }
    }
}

// Deposit Virtual Funds
async function handleDepositSubmit(e) {
    e.preventDefault();
    const amount = parseFloat(document.getElementById('depositAmountInput').value);
    if (!amount || amount <= 0) return;

    try {
        const res = await apiFetch('/api/user/deposit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: amount })
        });
        const json = await res.json();
        if (json.success) {
            showToast(json.message, 'success');
            closeModal('depositModal');
            await Promise.all([loadUserProfile(), loadPortfolioSummary()]);
        } else {
            showToast(json.message, 'error');
        }
    } catch (err) {
        showToast('Deposit failed', 'error');
    }
}

// Demo Account Reset
async function resetDemoAccount() {
    if (!confirm('Are you sure you want to reset the demo account? This restores ₹10,00,000 cash and baseline holdings for college viva presentations.')) {
        return;
    }
    try {
        const res = await apiFetch('/api/user/reset', { method: 'POST' });
        const json = await res.json();
        if (json.success) {
            showToast('Account successfully reset for demo viva', 'success');
            await loadInitialData();
        }
    } catch (e) {
        showToast('Reset failed', 'error');
    }
}

// Export Statement (Print Simulation)
function printStatement() {
    window.print();
}

// Export CSV for Order Book
function exportOrdersCSV() {
    if (state.transactions.length === 0) {
        showToast('No orders to export', 'info');
        return;
    }

    const headers = ["Order ID", "Timestamp", "Ticker", "Company", "Type", "Category", "Quantity", "Price", "Gross Amount", "Brokerage", "STT", "GST", "Net Amount", "Status"];
    const rows = state.transactions.map(t => [
        t.orderId,
        t.executedAt,
        t.ticker,
        `"${t.companyName}"`,
        t.orderType,
        t.orderCategory,
        t.quantity,
        t.executionPrice,
        t.grossAmount,
        t.brokerage,
        t.sttTax,
        t.gstTax,
        t.netAmount,
        t.status
    ]);

    const csvContent = "data:text/csv;charset=utf-8," + [headers.join(","), ...rows.map(e => e.join(","))].join("\n");
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `PortfolioPro_Transactions_${new Date().toISOString().substring(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast("Transaction statement exported to CSV", "success");
}

// Modal Helpers
function openModal(id) {
    document.getElementById(id)?.classList.add('active');
}

function closeModal(id) {
    document.getElementById(id)?.classList.remove('active');
}

// Toast Notifications
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <span style="font-weight:700;">${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}</span>
        <span>${message}</span>
    `;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}
