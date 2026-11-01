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
