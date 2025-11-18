# Product Backlog: Stock Easy — Sprint 1

## Release Objective
Create a functional, educational, and user-friendly stock trading simulator that allows users to practice buying/selling, track portfolios, and analyze outcomes in a risk‑free environment.

## Legend
- **Priority**: H = High, M = Medium, L = Low
- **Status**: Done, In Progress, Not Started, Deferred
- **Sprint**: Target sprint for completion (actuals shown in Status)

## Sprint 1 Summary (Implemented)
- User registration and login (basic auth flow)
- View mock market prices (simulated data)
- Buy stocks and sell stocks
- Portfolio/dashboard updates reflect transactions

## Backlog Changes After Sprint 1
- Marked implemented stories as **Done**.
- Added explicit auth stories (Registration, Login) to capture implemented scope.
- Re‑prioritized remaining items for Sprint 2 focus.
- Added estimated points and prioritization.

---

## Product Backlog Table

| ID  | User Story (concise)                                                                       | Priority | Points | Sprint |   Status    |
| --- | ------------------------------------------------------------------------------------------ | :------: | :----: | :----: | :---------: |
| A1  | As a user, I can **create an account** to access StockEasy.                                |    H     |   3    |   1    |    Done     |
| A2  | As a user, I can **log in** and maintain a session.                                        |    H     |   3    |   1    |    Done     |
| 1   | As a user, I can **view stock prices**                                                     |    H     |   5    |   2    |  Deferred   |
| 2   | As a user, I can **buy stocks** so I can practice trading.                                 |    H     |   3    |   1    |    Done     |
| 3   | As a user, I can **sell stocks** so I can realize P/L in the simulator.                    |    H     |   2    |   1    |    Done     |
| 4   | As a user, I can see a **portfolio overview** (balance, holdings).                         |    H     |   5    |   1    |    Done     |
| 5   | As a user, I can view **transaction history** (timestamp, price, qty, side).               |    H     |   3    |   2    | Not Started |
| 6   | As a user, I can see **analytics/insights** (profit %, risk, frequency).                   |    M     |   8    |   3    | Not Started |
| 7   | As a user, I start with a **virtual balance** (e.g., $1,000).                              |    M     |   2    |   2    | Not Started |
| 8   | As a user, I can **switch between live and simulated data**.                               |    M     |   8    |   3    | Not Started |
| 9   | As a user, I receive **educational tips** after trades.                                    |    M     |   5    |   3    | Not Started |
| 10  | As a user, I get a **simple, clean interface**                                             |    M     |   8    |  2-3   | Not Started |
| 11  | As a user, I can **search by symbol/name** and open a **symbol detail** with trade ticket. |    H     |   6    |   2    | Not Started |
| 12  | As a user, I see **form validation** errors (invalid qty, insufficient cash).              |    H     |   3    |   2    | Not Started |
| 13  | As a user, I can see **cash vs. buying power** with tooltips.                              |    M     |   3    |   2    | Not Started |
| 14  | As a user, each **holding shows stats** (shares, avg cost, price, value).                  |    M     |   5    |   2    | Not Started |
| 15  | As a user, I can **filter/sort trade history** (date, symbol, side).                       |    M     |   5    |  2-3   | Not Started |
| 16  | As a user, I can **quick trade from holdings** (contextual buy/sell).                      |    M     |   5    |   3    | Not Started |
| 17  | As a user, I can **star trades** for later review.                                         |    L     |   3    |   3    | Not Started |
| 18  | As a user, I see a **first‑time guided tour** (3–5 steps).                                 |    L     |   5    |   3    | Not Started |
| 19  | As a user, I can view a **Basics page** (plain‑language definitions).                      |    L     |   2    |   3    | Not Started |
| 20  | As a user, I can **reset my sandbox** to starting balance.                                 |    M     |   3    |   2    | Not Started |
| 21  | As a user, I can **log out** to end my session.                                            |    M     |   2    |   2    | Not Started |

