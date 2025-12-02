 Sprint 2 Retrospective (SR2.md)    
Team: StockEasy      
Date: 2025-11-29   
Location/Platform: Discord \+ GitHub \+ Trello  

 Attendees  
\- Hemish Ahuja    
\- Lama Abdelfattah    
\- Arushi Bisht    
\- Divy Parikh    
\- Mark Feng  

All team members attended and participated.

\---

 Sprint 2 Goal 

Move from a basic, mock-market prototype to a realistic, API-driven stock simulator with clearer portfolio behaviour, better UX, and a foundation for education and community features.

In terms of user stories, Sprint 2 focused on:    
1, 5, 6, 14–18, 22–32, 34–39, 41–43.

\---

 What We Completed in Sprint 2

 Market & Data 

\- Real Market Data via Finnhub (ID 1, 22, 28, 29, 30\)    
  \- Migrated from mock data to Finnhub API for real-time prices.    
  \- Implemented auto-refresh / up-to-date data on the dashboard.    
  \- Added support for history and clearing cached data.

 Portfolio & Balance Behaviour

\- Portfolio & History (ID 4, 5, 24, 34, 35, 43\)    
  \- Portfolio shows holdings, total market value, profit/loss metrics.    
  \- Transaction history is stored and can be reviewed later.

\- Virtual Balance & Simulation Controls (ID 6, 12, 25, 26, 27\)    
  \- Users start with a virtual balance.    
  \- Users can reset the sandbox, add/remove funds, and choose a custom starting balance.

 Stock Browsing & Detail

\- Watchlist (ID 14, 15\)    
  \- Users can manage a watchlist of selected stocks.

\- Filters & Details (ID 16, 17\)    
  \- Users can filter stocks by sector, industry, and price range.    
  \- Detailed stock views show profiles and relevant information.

 UX, Feedback & Stability

\- Interface & Feedback (ID 7, 9, 23, 32, 36–38)    
  \- Improved layout and styling for a cleaner interface.    
  \- Added validation messages for invalid quantities, insufficient cash, etc.    
  \- Implemented AJAX-based interactions for smoother updates.    
  \- Improved performance via caching and better page load handling.    
  \- Added contextual help/trading tips and comprehensive error messages.

\- Security, Reliability & Monitoring (ID A1, A2, 31, 39, 40, 41, 42\)    
  \- Hardened authentication/session management.    
  \- Improved backup/validation of data and added monitoring for application health.    
  \- Enabled scheduled price updates and user profile editing.

\---

 What Went Well

\- API Migration & Data Layer    
  \- Moving to Finnhub worked well and is now the backbone of our data (ID 1, 22, 28–30).    
  \- Abstractions around pricing and history made it easier to test and extend.

\- Portfolio & Balance Flows    
  \- The combination of IDs 4, 5, 6, 12, 25–27, 34, 35, 43 gives a solid simulation loop: start, trade, analyse, reset, repeat.

\- User Feedback & Validation    
  \- Validation and error messages (IDs 9, 23, 32\) make the app feel safer and easier for beginners.

\- Team Coordination    
  \- Trello, GitHub, and Discord were used consistently to track tasks and PRs.    
  \- Branch-per-feature worked for most stories.

\---

 What Didn’t Go Well

\- Late Integration & Testing    
  \- API migration, filters, and parts of the portfolio analytics came together late.    
  \- Some edge cases (API failures, weird balances, empty history) were only caught right before the deadline.

\- Time Distribution    
  \- Many commits landed in the second week instead of being spread across the sprint.    
  \- This increased pressure and reduced time for polish.

\- UX Fine-Tuning    
  \- While the core UI (ID 7\) improved, and navigation aids (ID 44\) were not addressed yet.

\- Education & Community Delayed    
  \- Stories focused on education and community (IDs 11, 18–21, 45\) were mostly pushed to Sprint 3\.

\---

 Start / Stop / Continue

Start  
\- Using smaller, focused branches mapped clearly to a single story ID.    
\- Doing mid-sprint integration checks (especially around API \+ UI).    
\- Treating mobile layout and navigation (IDs 33, 44\) as first-class concerns, not “later” items.

Stop  
\- Bundling too many changes (API, UI, validation) into big PRs.    
\- Leaving educational and community stories as an afterthought at the end of the sprint.

Continue  
\- Basing features off clear user stories with IDs from the PB.    
\- Using real market data (ID 1, 22, 28–30) as the backbone for all trading flows.    
\- Surfacing blockers early via standups and Trello.

\---

 Action Items for Sprint 3

1\. Education & Guidance    
   \- Complete the Basics page (ID 11\) and Glossary (ID 45).    
   \- Finalise the Portfolio Analysis Guide (ID 18).

2\. Community & Engagement    
   \- Implement the Community Forum (ID 19\) and Social Posts (ID 20).    
   \- Seed and manage Demo Posts (ID 21\) to make the community feel alive.

3\. UX & Device Support    
   \- Improve mobile responsiveness (ID 33).    
   \- Add breadcrumb navigation (ID 44\) for better orientation.

4\. Process & Time Management    
   \- Aim for more commits and completed tasks in Week 1 of the sprint.    
   \- Keep branches small and ensure PRs are reviewed quickly.

\---

This retrospective will be used at the beginning of Sprint 3 planning to refine our process and guide which stories from the PB we commit to next.

