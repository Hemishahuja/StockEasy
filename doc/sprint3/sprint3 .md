 Sprint 3 Planning — StockEasy

Date/Time: 2025-11-30    
Sprint length: 2 weeks    
Note-taker: Lama & Divy

\---

 Participants

\- Hemish Ahuja    
\- Lama Abdelfattah    
\- Arushi Bisht    
\- Divy Parikh    
\- Mark Feng  

All members participated in planning.  
\---

 Sprint Goal

Built on the new Finnhub-powered market and filters to make StockEasy a smoother, more educational trading simulator.

In Sprint 3 we will:  
\- Use the existing Finnhub integration, categories, and filters to improve search \+ symbol detail.  
\- Introduce faster actions such as Quick Trade from Holdings.  
\- Improve learning features: educational feedback, tutorials, and a first-time guided tour.  
\- Enhance trade history with filters, sorting, and starred trades for review.

\---

 Context from Sprint 2

From Sprint 2, we already have:

\- Migration to Finnhub API (better market data).  
\- More stocks and stock categories.  
\- Stock filters working on the main list.  
\- Stock details page connected to the API.  
\- Buy stocks button that auto-populates fields on the buy page.  
\- Change balance feature for simulation.

Sprint 3 will not redo this work; we are building on top of it.

\---

 Spikes (Research Tasks)

\- Best approach for Quick Trade from Holdings using the existing buy form (inline vs modal).  
\- Structure for Starred trades (flag on transaction vs separate table).  
\- Lightweight options for a guided tour (reusable component vs simple stepwise modals).

Each spike will produce:  
\- A short summary in \`doc/sprint3/notes.md\`.  
\- A decision comment on the related Trello card.

\---

 Team Capacity

(Adjust hours as needed.)

\- Hemish — \~8–12 hours    
\- Lama — \~8–10 hours    
\- Arushi — \~7–10 hours    
\- Divy — \~7–10 hours    
\- Mark — \~8–10 hours  

Total: \~38–52 hours.

\---

 User Stories Selected for Sprint 3

Sprint 3 focuses on education, community, and polish built on top of the trading simulator.

We are targeting the following user stories:

\- ID 11 – Basics Page    
  As a user, I can view a Basics page (plain-language definitions).

\- ID 18 – Portfolio Analysis Guide    
  As a user, I can access an educational portfolio analysis guide.

\- ID 19 – Community Forum    
  As a user, I can participate in a community forum for trading discussions.

\- ID 20 – Social Posts    
  As a user, I can create social-media-style posts in the community.

\- ID 21 – Fake Demo Posts    
  As a user, I see fake demonstration posts to make the community feel active.

\- ID 33 – Mobile-Friendly Interface    
  As a user, I can use the interface on mobile devices seamlessly.

\- ID 44 – Persistent Breadcrumbs    
  As a user, I can navigate between sections with persistent breadcrumbs.

\- ID 45 – Glossary of Trading Terms    
  As a user, I can access a glossary of trading terms.

Other stories from previous sprints (e.g., market data, watchlist, transaction history, portfolio metrics) are already implemented and provide the foundation for these Sprint 3 enhancements.

All user stories:  
\- Are point-estimated and prioritized.  
\- Have acceptance criteria written in Trello.  
\- Are broken down into tasks with an assignee.

\---

 Task Breakdown

 1\. Basics Page (ID 11\)    
Goal: Provide easy-to-understand explanations of core stock terms.

\- Define a list of essential terms (stock, volatility, P/L, sectors, diversification, etc.).    
\- Implement Basics page view/template.    
\- Style the page for readability.    
\- Add a navigation link (“Basics” or “Learn”).    
\- Test accessibility on desktop & mobile.

\---

 2\. Portfolio Analysis Guide (ID 18\)    
Goal: Help users interpret their portfolio analytics.

\- Write short educational explanations for:  
  \- Portfolio value    
  \- Diversification    
  \- P/L metrics    
  \- Sector exposure    
\- Add a “Learn more” link inside the Portfolio page.    
\- Build a separate analysis guide page.    
\- Connect terminology to Glossary terms (ID 45).  

—

 3\. Community Forum (ID 19\)    
Goal: Create a simple space where users can read & write posts.

\- Build model for posts (title, content, author, timestamp).    
\- Implement:  
  \- Forum index page    
  \- Post creation form    
\- Display posts in newest-first order.    
\- Restrict forum access to logged-in users.    
\- Add “Community” to the navigation bar.

\---

 4\. Social Posts (ID 20\)    
Goal: Add short-form posts (“feed style”) inside the community.

\- Extend the forum model to support short posts.    
\- Implement posting widget (textarea \+ post button).    
\- Add a feed-style list with timestamps.    
\- Add validation (non-empty content).    
\- Automatically append user info \+ time.

\---

 5\. Fake Demo Posts (ID 21\)    
Goal: Make the forum feel active when new users arrive.

\- Create demo posts seeder or fixture.    
\- Insert 5–10 sample posts (e.g., “bought AAPL today”, “my first trade\!”).    
\- Style demo posts the same as real posts.    
\- Ensure demo posts do NOT block real users from posting.

\---

 7\. Breadcrumb Navigation (ID 44\)    
Goal: Help users understand where they are.

\- Define breadcrumb structure for:  
  \- Market → Stock    
  \- Community → Post    
  \- Portfolio → Analysis Guide    
\- Create a reusable breadcrumb component.    
\- Integrate it into templates.    
\- Test behavior across all major pages.

\---

 Process & Tracking

\- Trello  
  \- All Sprint 3 stories and tasks are on the Active Sprint board.  
  \- Every task has:  
    \- an assignee    
    \- clear acceptance criteria    
    \- story points at the story level    
  \- Each card links to its GitHub branch and PR.

\- Git Workflow  
  \- One branch per task.    
  \- Branch name format: \`idXX-description\` (e.g., \`id19-community-forum\`).    
  \- Commit messages begin with the story ID (e.g., \`ID19: add forum index\`).  

\- Burndown & Schedule  
  \- Sprint 3 burndown chart created on Day 1\.    
  \- Update mid-week and before the demo.    
  \- Schedule diagram includes dependencies:  
    \- ID 11 → ID 45    
    \- ID 18 → ID 45    
    \- ID 19 → ID 20 → ID 21  

\---

 Demo Plan

We will demonstrate Sprint 3 end-to-end:

 1\. Educational Features  
\- Open the Basics Page (ID 11\) and show definitions.    
\- Navigate to Portfolio → Analysis Guide (ID 18).    
\- Open the Glossary (ID 45\) and show linking with Basics/Guide.

 2\. Community Features  
\- Open the Community Forum (ID 19).    
\- Create a new social post (ID 20).    
\- Show demo posts (ID 21\) seeded in the feed.

 3\. UX Improvements  
\- Demonstrate breadcrumb navigation (ID 44\) on:  
  \- Market → Stock    
  \- Community → Post  

