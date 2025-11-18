 Sprint 1 Retrospective (SR1.md)  
Team: StockEasy    
Date: 2025-10-16    
Location/Platform: Discord  

Attendees:    
\- Hemish Ahuja – Scrum Master / Developer    
\- Lama Abdelfattah – Scrum / Project tracking / Backend coordination    
\- Arushi Bisht – Backend    
\- Divy Parikh – Dashboard / UX handoff    
\- Mark Feng – QA / Project Ops / Documentation  

\> All team members were present and participated.

 What went well  
\- UI integrated with backend for login / buy / sell; local run \+ JUnit flow stable.    
\- Trello board and user stories followed consistent format with AC and estimates.    
\- Branch-per-user-story and linking PRs to Trello worked smoothly.

 What didn’t go well  
\- Mixed IDE configs (Eclipse vs VS Code) caused minor naming/config drift.    
\- Some late PRs and ad-hoc merges increased review pressure near the end.

 Continue doing  
\- Daily async standups in the required format.    
\- Separate branch per ticket 

 Start doing  
\- Enforce commit message prefix with ticket ID (e.g., \`US-3: …\`).    
\- Publish burndown mid-sprint and maintain a simple schedule diagram.

 Stop doing  
\- Last-minute merges without review.

 Incomplete / deferred work → moved to Sprint 2  
\- US-4 – View trade/purchase history (create & show list of BUY/SELL).

 Action items  
\- Adopt an in-memory DB (H2) \+ Spring Data for persistence.    
\- Introduce a \`PriceService\` interface and a deterministic \`MockPriceService\`.  
