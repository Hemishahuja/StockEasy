-- SQL script to verify stock prices are correctly set
-- This can be used to check the database after the application runs
-- THIS IS A TEMPORARY FILE FOR TESTING PURPOSES ONLY NOT FOR GRADING OR USE CASES -- 
SELECT 
    symbol,
    company_name,
    current_price,
    previous_close,
    (current_price - previous_close) as price_change,
    ((current_price - previous_close) / previous_close * 100) as change_percentage
FROM stocks 
ORDER BY current_price DESC;

-- Expected results with the new realistic prices:
-- AAPL: $185.75 (Apple Inc.)
-- GOOGL: $148.30 (Alphabet Inc.)  
-- MSFT: $334.80 (Microsoft Corp.)
-- AMZN: $145.60 (Amazon.com Inc.)
-- TSLA: $248.90 (Tesla Inc.)
-- NVDA: $465.80 (NVIDIA Corp.)
-- META: $485.20 (Meta Platforms)
-- NFLX: $495.40 (Netflix Inc.)
