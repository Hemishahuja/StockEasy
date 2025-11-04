 Sprint 1 Planning Meeting (\`sprint1.md\`)

Course: EECS 3311 – Software Design    
Project: StockEasy    
Team: StockEasy    
Date/Time: 2025-10-02, 3:30 PM    
Location/Platform: Discord  

Attendees:    
\- Hemish Ahuja – Scrum Master / Developer    
\- Lama Abdelfattah– Scrum / Project tracking / Backend coordination    
\- Arushi Bisht – Backend   
\- Divy Parikh – Dashboard / UX handoff    
\- Mark Feng – QA / Project Ops / Documentation

\> All team members were present and participated.  
—--  
 1\. Sprint Goal

Deliver a demoable first slice of StockEasy that lets a user:

1\. log in,  
2\. sell a stock,  
3\. record a buy transaction,  
4\. have the Sprint 1 docs in \`doc/sprint1/\`.

All features must be runnable locally and testable with JUnit in Eclipse (team chose JUnit instead of Postman).

This goal matches the Release 1 goal in \`RPM.md\`. :contentReference\[oaicite:0\]{index=0}

\---

 2\. Sprint Duration & Standups

\- Sprint window: 2025-10-02 → 2025-10-16 (2 weeks)    
\- Standups: 3 minimum, posted on GitHub issues / discussions in the required format    
  1\. What did you work on?    
  2\. What will you do next?    
  3\. When will you be done?    
  4\. Blockers    
  (see Sprint 1 handout) :contentReference\[oaicite:1\]{index=1}

\---

 3\. Team Capacity

| Team member    | Est. hours for Sprint 1 | Notes |  
|----------------|-------------------------|--------|  
| Hemish Ahuja   | 14 h                     | Implement buy and sell and Portfolio value updation, API integration |  
| Lama Abdelfattah    | 12 h                     | Trello, Meeting docs, login security verification, page protection testing, Sprint1.md |  
| Arushi Bisht   |  8h                     | Backend implementation, System Design,Trello|  
| Divy Parikh    | 8 h                     | dashboard endpoint , UI notes, API integration  |  
| Mark Feng      | 11 h                     | System Design, Diagram, Trello, user stories |  
| Total      |  53h                | sufficient for 4 stories

(If a member’s hours change, update this table — TA just needs to see that capacity was recorded.)

\---

 4\. Sprint Backlog (stories selected)

These are the stories that we put in Trello → SPRINT TO DO.

1\. US-1 – Login to StockEasy (3 pts)    
   As a registered user, I want to log in so that I can access my account.

2\. US-2 – Sell Stock (2 pts)\*\*    
   As a user, I want to sell a stock so that my portfolio reflects what I actually hold.

3\. US-3 – Buy stock (create purchase) (3 pts)    
   As a user, I want to purchase virtual shares of a stock so that my portfolio updates and I can track the trade’s performance.

4\. SPIKE-1 – Investigate stock price API (1 pt)    
   As a developer, I want to investigate which stock price API or data source we can use so that future sprints can show realistic or mock prices.

5.DOC-1 – Sprint 1 docs (RPM.md, sprint1.md, system design placeholder) (1 pt)  

6\. UI-1 – Integrate existing StockEasy UI with backend (1 pt)    
   As a user, I want to use the existing UI (registration, login, mock prices, buy/sell, dashboard updates) so I don’t have to use tools like Postman.

Total planned story points: 11 pts  

These stories match what we put in the Release Planning Meeting (\`RPM.md\`): auth \+ core trading (sell, buy) \+ spike (API) \+ docs \+ UI.

—---  
Full Product Backlog on Trello

Per the project rubric, we created Trello user-story cards for **all** planned features of StockEasy, not just Sprint 1 items. This is why the Trello **Backlog** list contains additional stories such as:

\- Viewing Market Data  
\- Transaction History  
\- Analytics and Insights  
\- Starting Virtual Balance  
\- Educational Feedback and Tutorials  
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

These cards are **documented** now but are not **committed** for Sprint 1\.    
Only the following were selected for Sprint 1:

1\. US-1 – Login to StockEasy    
2\. US-2 – Sell Stock    
3\. US-3 – Buy stock (create purchase)    
4\. SPIKE-1 – Investigate stock price API    
5\. DOC-1 – Sprint 1 docs    
6\. UI-1 – Integrate existing StockEasy UI with backend  

All other cards are marked in Trello with the label **“Future / Not in Sprint 1.”**

—--

 5\. Tasks Breakdown

 US-1 – Login to StockEasy

- [ ] Define request/response for login (\`username\`, \`password\`)  
- [ ] Eclipse: implement login method / controller  
- [ ] Eclipse: write JUnit test – valid credentials → pass  
- [ ] Eclipse: write JUnit test – invalid credentials → fail/false  
- [ ] Attach JUnit run screenshot to Trello card  
- [ ] Push branch \`feature/US-1-login\` and open PR

Acceptance Criteria  
\- Login callable from backend  
\- JUnit success \+ failure tests pass  
\- Code/PR linked to Trello

\---

 US-2 – Sell stock

- [ ] Define SELL request/params: \`username\`, \`symbol\`, \`quantity\`, \`price\`  
- [ ] Eclipse: update trade/purchase model to support \`type \= SELL\`  
- [ ] Eclipse: implement \`sell(...)\` in service  
- [ ] Eclipse: expose sell in controller/endpoint  
- [ ] Eclipse: JUnit test that creates a SELL and asserts \`type \= SELL\`, symbol, and quantity  
- [ ] Link Trello card \+ PR

Acceptance Criteria  
\- Backend can record a SELL for user \`demo\`  
\- JUnit test passes in Eclipse  
\- SELL shows up later in history (US-4)

\---

 US-3 – Buy stock (create purchase)

- [ ]  Define \`Purchase/Trade\` model: id, username, symbol, quantity, price, timestamp, type=\`BUY\`  
- [ ] Eclipse: in-memory repository for trades  
- [ ] Eclipse: service method \`buy(...)\`  
- [ ] Eclipse: controller / endpoint \`.../trades/buy\` (name to be finalized by Eclipse dev)  
- [ ] Eclipse: JUnit test that creates a BUY and asserts it is saved  
- [ ] Link Trello card \+ PR

Acceptance Criteria  
\- A BUY can be created for \`demo\`  
\- Saved in memory  
\- Appears in history (US-4)  
\- JUnit green

\---

 DOC-1 – Sprint 1 docs

- [ ] Save \`RPM.md\` in \`doc/sprint1/\`  
- [ ]  Save this file as \`doc/sprint1/sprint1.md\`  
- [ ] Update \`README.md\` with “How to run Spring Boot” (already in repo) :contentReference\[oaicite:3\]{index=3}  
- [ ] Add link to Trello board

\---  
SPIKE-1 – Investigate stock price API

- [ ] List possible data sources (e.g., free stock APIs, mock JSON data, static CSV)    
- [ ] Check if they are free, easy to use, and allowed for the course    
- [ ] Decide whether Sprint 1 will:  
- [ ]   use mock prices only, or    
- [ ]   connect to a specific free API    
- [ ] Write a short summary of:  
- [ ]   which option we chose    
- [ ]   how we will use it (now or in later sprints)    
- [ ] Attach/link the summary to this Trello card (or store in \`doc/sprint1/\`)  

Acceptance Criteria  
\- At least two options were considered    
\- A decision is clearly recorded (mock-only or specific API)    
\- Summary is attached or linked on SPIKE-1 Trello card    
—----

UI-1 – Integrate existing StockEasy UI with backend

- [ ] Place the existing UI in the project / correct folder (or confirm separate frontend repo)  
- [ ] Make sure UI works while the Spring Boot backend is running  
- [ ] Confirm the user can:  
- [ ]   create an account  
- [ ]   log in using that account  
- [ ]   see mock stock prices  
- [ ]   buy different stocks  
- [ ]   sell different stocks  
- [ ]   see their portfolio/dashboard values change  
- [ ] Capture 1–2 screenshots or a short GIF of the UI  
- [ ] Attach the screenshot/GIF to the UI-1 Trello card  
- [ ] 

Acceptance Criteria  
\- UI runs locally together with the backend  
\- User can register, log in, see mock prices, buy & sell stocks, and see portfolio/dashboard update  
\- At least one screenshot/GIF is attached to the UI-1 Trello card

\---

 6\. Decisions Made

\- We will test with JUnit in Eclipse, not Postman, for Sprint 1\.  
\- We will track all user stories in Trello (already created columns).  
\- We will implement buy and purchase history.  
\- Each PR must come from a separate branch named after the story (ex. \`feature/US-3-buy-stock\`).  
\- We will keep all remote branches until Sprint 1 marking is finished (per handout). :contentReference\[oaicite:4\]{index=4}

\---

 7\. Risks / Blockers

\- Some devs are using Eclipse and one is using VS Code → agree to keep package names consistent (\`com.example.stockeasy\`) so PRs don’t clash.  
\- If buy/history is not finished in Sprint 1, we will add the unfinished items to SR1.md and move them to Sprint 2 (per handout). :contentReference\[oaicite:5\]{index=5}

\---

 8\. Links

\- Trello board: https://trello.com/b/FREEXWB4/stockeasy  
\- Repo: https://github.com/EECS3311F25/StockEasy/tree/docs/sprint1/doc/sprint1  
\- Folder: \`doc/sprint1/\` (this file, \`RPM.md\`, system design doc)

\---

Prepared by: Lama Abdelfattah   
Approved by: StockEasy team    
Date: 2025-11-02  
