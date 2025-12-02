 Product Backlog: StockEasy Sprint 3

 Release Objective  
Create a functional, educational, and user-friendly stock trading simulator that allows users to practice buying/selling, track portfolios, and analyze outcomes in a risk-free environment.

 Legend  
\- Priority: H \= High, M \= Medium, L \= Low    
\- Status: Done, In Progress, Not Started, Deferred    
\- Sprint: Target sprint for completion (actual completion shown in Status)

\---

 Sprint 1 Summary

\- User registration and login (basic authentication flow) \-A1, A2  
\- Buy and sell stocks using a basic simulated market \-2, 3  
\- Portfolio/dashboard updates reflect transactions \-4  
\- Initial mock market prices were displayed using placeholder data \-1

\---

 Sprint 2 Summary

In Sprint 2, we moved from a basic mock market to a realistic, API-driven market system and improved the trading flow significantly.

 Key Improvements

\- Migrated to Finnhub API \-1  
  \- Replaced the simulated market with Finnhub for more reliable real-time data.  
  \- Enabled support for more stocks, richer metadata, and future analytics.

\- Expanded Stocks & Categories \-17  
  \- Added additional symbols and sectors.  
  \- Introduced category/industry browsing.

\- Stock Filtering (Market Page) \-16  
  \- Implemented filters (sector, industry, price ranges) to improve discoverability.

\- Stock Details Page Connected to API \-8, 17  
  \- Stock detail view now retrieves live information from Finnhub.  
  \- Supports launching the buy workflow directly.

\- Buy Button Auto-Populates Buy Page \-8  
  \- Buying from the details page now pre-fills the trade ticket (symbol, fields, etc.).

\- Change Balance / Reset Sandbox \-12, 25, 26, 27  
  \- Users can change their virtual balance.  
  \- Supports resetting, adding funds, removing funds, or choosing a new starting balance.

\---

 Backlog Changes After Sprint 2 & Sprint 3 Planning

 Stories Marked as Done After Sprint 2

The following stories were completed through Sprint 2 work:

\- ID 1 \-Real stock prices now powered by Finnhub    
\- ID 6 \-Virtual starting balance    
\- ID 12, 25, 26, 27 \-Reset sandbox / add funds / remove funds / pick starting balance    
\- ID 13 \-Logout functionality    
\- ID 14–17 \-Watchlist, alerts, filtering, detailed stock info    
\- ID 22 \-Real-time dashboard price updates    
\- ID 24 \-Profit/loss analysis    
\- ID 30–38 \-Market history, cached data clearing, AJAX, performance, backups, etc.    
\- ID 31 \-Contextual help and trading tips  

These features now form the foundation for Sprint 3 improvements.

\---

 Stories Marked In Progress Entering Sprint 3

These stories were partially implemented in Sprint 2 and continue in Sprint 3:

\- ID 7 \-Clean, simple interface    
\- ID 8 \-Search \+ symbol detail (search UI is Sprint 3\)    
\- ID 9 \-Form validation improvements    
\- ID 11 \-Basics page (definitions)    
\- ID 33 \-Mobile-friendly UI    
\- ID 44 \-Persistent breadcrumbs    
\- ID 45 \-Glossary of terms    
\- ID 21 \-Demo posts for the community feed  

\---

 Stories Re-targeted to Sprint 3 (were not completed in Sprint 2\)

\- ID 11 \-Basics Page    
\- ID 18 \-Portfolio analysis educational guide    
\- ID 19 \-Community forum (complete in Sprint 3\)    
\- ID 20 \-Social-media-style community posts    
\- ID 21 \-Fake demo posts    
\- ID 45 \-Glossary of trading terms    
\- ID 44 \-Breadcrumb navigation    
\- ID 33 \-Mobile UX polish  

\---

 New Items Completed in Sprint 3

Sprint 3 includes a strong emphasis on education, community, and UX, so new items completed include:

\- ID 19 \-Community Forum    
\- ID 20 \-Social Media Posts    
\- ID 21 \-Demo Activity Posts    
\- ID 18 \-Portfolio Analysis Guide    
\- ID 11 \-Basics Page    
\- ID 45 \-Glossary    
\- ID 33 \-Mobile Responsive Layout    
\- ID 44 \-Breadcrumb Navigation  

\---  
Product Backlog Table

| ID  | User Story                                                                | Priority | Points | Sprint |   Status    |  
| \--- | \------------------------------------------------------------------------------------- | :------: | :----: | :----: | :---------: |  
| A1  | As a user, I can create an account to access StockEasy.                              |    H     |   3    |   1    |    Done     |  
| A2  | As a user, I can log in and maintain a session.                                      |    H     |   3    |   1    |    Done     |  
| 1   | As a user, I can view stock prices (now via Finnhub, with richer data).             |    H     |   5    |   2    |    Done     |  
| 2   | As a user, I can buy stocks so I can practise trading.                               |    H     |   3    |   1    |    Done     |  
| 3   | As a user, I can sell stocks so I can realise profit/loss in the simulator.          |    H     |   2    |   1    |    Done     |  
| 4   | As a user, I can see a portfolio overview (balance, holdings).                       |    H     |   5    |   1    |    Done     |  
| 5   | As a user, I can view transaction history (timestamp, price, quantity, side).       |    H     |   3    |   2    |    Done     |  
| 6   | As a user, I start with a virtual balance (e.g., \\$1,000).                           |    M     |   2    |   2    |    Done     |  
| 7   | As a user, I get a simple, clean interface.                                          |    M     |   8    |  2–3   |    Done     |  
| 8   | As a user, I can search by symbol/name and open a symbol detail with trade ticket.   |    H     |   6    |  2–3   |    Done     |  
| 9   | As a user, I see form validation errors (invalid quantity, insufficient cash).       |    H     |   3    |  2–3   |    Done     |  
| 10  | As a user, I see a first-time guided tour (3–5 steps).                               |    L     |   5    |   2    |    Done     |  
| 11  | As a user, I can view a Basics page (plain-language definitions).                    |    L     |   2    |   3    | In Progress |  
| 12  | As a user, I can reset my sandbox to starting balance / adjust balance for simulation.|   M     |   3    |   2    |    Done     |  
| 13  | As a user, I can log out to end my session.                                          |    M     |   2    |   2    |    Done     |  
| 14  | As a user, I can create and manage a watchlist to track favourite stocks.            |    H     |   5    |   2    |    Done     |  
| 15  | As a user, I can set price alerts for stocks in my watchlist.                        |    M     |   3    |   2    |    Done     |  
| 16  | As a user, I can filter stocks by sector, industry, and price ranges.                |    H     |   3    |   2    |    Done     |  
| 17  | As a user, I can view detailed stock information with company profiles.              |    H     |   4    |   2    |    Done     |  
| 18  | As a user, I can access an educational portfolio analysis guide.                     |    M     |   3    |   2    |    Done     |  
| 19  | As a user, I can participate in a community forum for trading discussions.           |    M     |   8    |   3    |    Done     |  
| 20  | As a user, I can create social-media-style posts in the community.                   |    M     |   3    |   3    |    Done     |  
| 21  | As a user, I see fake demonstration posts to make the community feel active.         |    L     |   2    |   3    | In Progress |  
| 22  | As a user, I experience real-time price updates on the dashboard.                    |    M     |   5    |   2    |    Done     |  
| 23  | As a user, I can access contextual help and trading tips.                            |    L     |   2    |   2    |    Done     |  
| 24  | As a user, I can see profit/loss analysis for my portfolio holdings.                 |    M     |   5    |   2    |    Done     |  
| 25  | As a user, I can reset my virtual balance to start over.                             |    M     |   3    |   2    |    Done     |  
| 26  | As a user, I can add or remove funds from my virtual account.                        |    M     |   3    |   2    |    Done     |  
| 27  | As a user, I can set a custom starting balance for simulation.                       |    M     |   3    |   2    |    Done     |  
| 28  | As a user, I can see up-to-date market data with refresh capabilities.               |    H     |   5    |   2    |    Done     |  
| 29  | As a user, I can check market data history information.                 |    M     |   4    |   2    |    Done     |  
| 30  | As a user, I can clear cached market data for fresh updates.                         |    M     |   2    |   2    |    Done     |  
| 31  | As a user, I am protected by secure authentication and session management.           |    H     |   5    |   1    |    Done     |  
| 32  | As a user, I see comprehensive error messages and feedback.                          |    H     |   3    |   2    |    Done     |  
| 33  | As a user, I can use the interface on mobile devices seamlessly.                     |    M     |   8    |   2    | In Progress |  
| 34  | As a user, I can save my trading history for future reference.                       |    M     |   5    |   2    |    Done     |  
| 35  | As a user, I can track the average cost of my stock purchases.                       |    M     |   4    |   2    |    Done     |  
| 36  | As a user, I can benefit from AJAX-based interactions for faster page updates.       |    M     |   3    |   2    |    Done     |  
| 37  | As a user, I can see portfolio diversification metrics.                              |    M     |   3    |   2    |    Done     |  
| 38  | As a user, I experience fast page load times with caching.                           |    M     |   4    |   2    |    Done     |  
| 39  | As a user, my data is automatically backed up and validated.                         |    M     |   3    |   2    |    Done     |  
| 40  | As a developer, I can monitor application health and metrics.                        |    M     |   3    |   2    |    Done     |  
| 41  | As a user, I can schedule automated price updates.                                   |    L     |   3    |   2    |    Done     |  
| 42  | As a user, I can update my user profile information.                                 |    L     |   3    |   2    |    Done     |  
| 43  | As a user, I can see my portfolio's total market value.                              |    M     |   3    |   2    |    Done     |  
| 44  | As a user, I can navigate between sections with persistent breadcrumbs.              |    M     |   2    |   2    | In Progress |  
| 45  | As a user, I can access a glossary of trading terms.                                 |    L     |   4    |   3    | In Progress |

\---

Sprint 3 Focus (for the TA)

Sprint 3 primarily focuses on:

\- Education & guidance    
  \- 11 – Basics page (plain-language explanations)    
  \- 18 – Educational portfolio analysis guide    
  \- 45 – Glossary of trading terms  

\- Community & engagement    
  \- 19 – Community forum for trading discussions    
  \- 20 – Social-media-style posts in the community    
  \- 21 – Fake demonstration posts to make the community feel active  

\- UX & responsiveness    
  \- 33 – Mobile-friendly interface improvements    
  \- 44 – Persistent breadcrumbs for easier navigation  

