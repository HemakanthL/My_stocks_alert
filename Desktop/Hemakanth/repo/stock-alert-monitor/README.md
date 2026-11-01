# Stock Alert Monitor

A Spring Boot service that monitors selected stocks and triggers alerts when the market price crosses a configured threshold.

## Features

- Create, enable, disable, and list stock alerts
- Monitor selected symbols in the background
- Trigger alerts only inside the application dashboard/API
- Expose REST endpoints for alert management and market snapshot queries
- Show active and triggered alerts in a built-in dashboard at http://localhost:8081/
- Refresh prices on demand and search symbols instantly in the UI
- Change the stock data source directly from the dashboard (mock, Alpha Vantage, Yahoo Finance)
- Uses an in-memory H2 database for local development
- Supports Alpha Vantage live quotes and reports provider errors rather than silently substituting mock prices

## Market data source configuration

The app can be configured to use different websites or data providers. Supported values for `stock.data.provider` are:

- `mock`
- `alphavantage`
- `yahoo`

Example configuration:

```properties
stock.data.provider=alphavantage
stock.data.api-key=${STOCK_DATA_API_KEY:}
stock.data.base-url=https://www.alphavantage.co
```

Set `STOCK_DATA_API_KEY` in the environment before starting the app to keep the key available after restarts without storing it in source files. The dashboard can also change the key while the app is running; those dashboard changes are temporary and reset when the app restarts.

Yahoo Finance is currently a sample-data provider in this application; it does not fetch live Yahoo quotes.

Or:

```properties
stock.data.provider=yahoo
stock.data.base-url=https://finance.yahoo.com
```

For live quotes, select **Alpha Vantage** in the dashboard, enter a valid API key, set the base URL to `https://www.alphavantage.co`, and click **Save source**. A missing key, invalid provider response, or provider/network error is reported rather than silently displaying mock prices.

Automatic monitoring defaults to every six hours to help stay within Alpha Vantage's 25-request daily free quota for the three seeded symbols. Each symbol quote counts as a request; extra symbols or manual refreshes can exceed the quota. Configure `stock.monitor.fixed-rate-ms` to change the interval. Provider quota or key errors are shown using the provider's response message.

## Prerequisites

- Java 21 or higher
- Maven 3.9+

## Run locally

```bash
mvn spring-boot:run
```

The app will be available at http://localhost:8081/ and the dashboard allows you to add and remove stock alerts directly in the browser.

To build an installable JAR package:

```bash
mvn package
```

This creates a runnable application at `target/stock-alert-monitor-0.0.1-SNAPSHOT.jar`.

## Example REST calls

```bash
curl http://localhost:8081/api/alerts
curl http://localhost:8081/api/alerts/triggered
curl -X POST http://localhost:8081/api/alerts \
  -H "Content-Type: application/json" \
  -d '{"symbol":"AAPL","direction":"ABOVE","threshold":230.00}'

curl "http://localhost:8081/api/market/quotes?symbols=AAPL,MSFT,NVDA"
```

This app is designed for in-app alerts only. It does not send push notifications, SMS, or emails.

## Default seeded alerts

The app seeds a few example alerts when it starts:

- AAPL above 230.00
- NVDA below 100.00
- MSFT above 420.00

This makes the system immediately useful without manual setup.

## Technical specifications

### CR-2026-002: Harden stock market data configuration and refresh

- **Purpose:** Ensure provider selection and manual/background price refreshes use the configured market-data service and do not misrepresent sample data as live quotes.
- **Affected components:** `DataSourceProperties`, `ConfiguredMarketDataService`, market-data provider services, `StockAlertService`, dashboard and datasource controllers, datasource response DTO, dashboard template, and application configuration.
- **Design and behavior:** Normalize provider identifiers, inject provider services directly, and report Alpha Vantage missing-key, HTTP, network, invalid-price, and provider quota/response errors rather than silently returning mock prices. The dashboard displays refresh errors and masks API-key input; datasource API responses expose only whether a key is configured. Automatic polling defaults to six hours (`stock.monitor.fixed-rate-ms`).
- **Configuration and API impacts:** Configure credentials through `STOCK_DATA_API_KEY` or the dashboard. The dashboard datasource form accepts a blank key to retain the current key. `GET` and `POST /api/datasource` return `apiKeyConfigured` rather than the API key. Yahoo Finance remains a sample-data provider.

### CR-2026-001: Document change-requested work

- **Purpose:** Keep a concise technical record for each requested code or process change.
- **Affected component:** Copilot custom agent instructions in `.github/agents/stock-alert-monitor.agent.md`.
- **Process:** For each code or process change, add or update a spec entry here with its CR number, purpose, affected components, intended design/behavior, and relevant configuration or API impacts. Use a developer-provided CR number when available; otherwise assign the next unused `CR-YYYY-NNN` for the current year by checking this section. Do not invent or reuse an existing ID.
