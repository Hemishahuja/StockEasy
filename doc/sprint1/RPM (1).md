Release Planning Meeting (RPM)

Course: EECS 3311 – Software Design    
Project: StockEasy    
Team: StockEasy    
Date/Time: 2025-11-02 at 10 am    
Location/Platform: Discord

\---

 1\. Participants

| Team Member   | Role(s)                             |  
|---------------|-------------------------------------|  
| Hemish Ahuja  | Scrum Master, Developer             |  
| Lama Tamer    | Scrum / Project Tracking, Developer |  
| Arushi Bisht  | Backend Developer (trades/history)  |  
| Divy Parikh   | Developer, UX/Research              |  
| Mark Feng     | Developer, QA/DevEx                 |

\> All team members were present and participated.

\---

 2\. Purpose of Meeting  
Define the Release 1 goal, agree on the scope (epics / key features), and map them to user stories that will be created in Trello and pulled into Sprint 1\.

\---

 3\. Release Goal  
Release 1 will deliver a usable first version of StockEasy that allows a user to:

1\. Log in to the system.    
2\. Sell or buy a stock (record a trade).    
3\. View their trade / purchase history.    
4\. Use the existing UI to show Sprint 1 features.

This release should be demoable at the end of Sprint 1 and runnable locally using the project README.    
Testing for Sprint 1 will be done with JUnit in Eclipse.

\---

 4\. Scope (Epics / Key Features)

\- EP-1: Authentication  
  \- Basic login  
  \- Simple identity handling for demo

\- EP-2: Core Trading  
  \- US-2 Sell stock (record a SELL transaction)  
  \- US-3 Buy stock (record a BUY transaction)  
  \- Store trades in memory for Sprint 1

\- EP-3: History / Reporting (future)  
  \- View trade / purchase history for a given user    
  \- Planned for a later sprint, not Sprint 1

\- EP-4: UI (Sprint 1 version)  
  \- Use teammate’s frontend (login \+ main view)  
  \- Wire it / demo it against the backend 

\- EP-5: Project Infrastructure  
  \- Trello board with all user stories (Sprint 1 \+ future)  
  \- Docs in \`doc/sprint1/\` (\`RPM.md\`, \`sprint1.md\`)  
  \- Branch/PR rules

\- EP-6: Extended Product Backlog (future, not Sprint 1\)  
  \- Viewing Market Data  
  \- Transaction History (extended)  
  \- Analytics and Insights  
  \- Starting Virtual Balance  
  \- Educational Feedback and Tutorials  
  \- Simple, Clean Interface  
  \- Search and Symbol Detail  
  \- Trade Form Validation  
  \- Cash vs Buying Power Display  
  \- Holdings with Statistics  
  \- Filter and Reorder Trade History  
  \- Quick Trade from Holdings  
  \- Star Trades for Review  
  \- First-Time Guided Tour  
  \- Basics Page  
  \- Reset Sandbox

\> These were added to Trello because the rubric said to create the user story cards for all planned features, but they are not committed for Sprint 1\.

\---

  5\. User Stories Selected for This Release / Sprint 1

(These must exist in Trello and be at the top of the Product Backlog / Sprint list.)

\- US-1 – As a registered user, I want to log in so that I can access my account.    
\- US-2 – As a user, I want to sell a stock so that my portfolio reflects what I actually hold.    
\- US-3 – As a user, I want to buy a stock so that StockEasy can record my purchase.    
\- SPIKE-1 – As a developer, I want to investigate which stock price API or data source we can use so that future sprints can show realistic or mock prices in a consistent way.    
\- UI-1 – As a user, I want to use the existing StockEasy UI (register, log in, see mock prices, buy/sell, see my portfolio change) so I can interact with the system during the demo.    
\- DOC-1 – As a team, we want Sprint 1 docs (RPM.md, sprint1.md, Trello link) so the TA can mark it.

\> The full “dashboard / portfolio snapshot” feature is kept in Trello as a future story (not Sprint 1).

\---

 6\. Release Constraints & Assumptions  
\- All work will be tracked on Trello (Backlog → Sprint 1 → In Progress → Review/Testing → Done).  
\- Each Sprint 1 story will have acceptance criteria and story points in Trello.  
\- Code will be developed in Eclipse (some setup done in VS Code) and tested with JUnit.  
\- Each task should have a matching GitHub branch / PR named after the story (e.g. \`feature/US-2-sell-stock\`).  
\- GitHub branches must not be deleted until marking is done.  
\- UI will be shown from the existing frontend, not from Postman.

\---

 7\. Risks / Open Questions  
\- Mixed IDEs (Eclipse \+ VS Code) → must keep the package name \`com.example.stockeasy\`.  
\- External stock / market data not yet selected → to be addressed in SPIKE-1.  
\- If SELL is finished but BUY is not, history must still work with whatever trades exist.  
\- Need to confirm which routes the UI is calling (\`/api/auth/login\`, \`/api/trades/...\`).

\---

8\. Decisions

\- We will implement US-1 (login), US-2 (sell), US-3 (buy) and UI-1 first to get an end-to-end demo.    
\- We will run SPIKE-1 during this release to decide how to handle stock prices (real API vs mock).    
\- We will create \`doc/sprint1/sprint1.md\` right after this meeting to capture sprint-level planning.    
\- We will keep the extra Trello user stories (market data, analytics, tutorials) but label them Future / Not in Sprint 1    
\- We will demo using the existing UI built by the teammate.    
\- We will post 3 standups during this sprint (what I did / what I will do / blockers).    
\- Trello will be the source of truth for Sprint 1 scope.

Prepared by: Lama Abdelfattah  
Approved by: StockEasy team  
Date: 2025-11-02

