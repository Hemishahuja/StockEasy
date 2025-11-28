import re

# Read the file
with open(r'c:\Users\hemis\Desktop\StockEasy-main\src\main\resources\templates\stocks\detail.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the broken Thymeleaf expression on line 101
# The issue is the string concatenation syntax
old_line = r'''th:text="\$\{stock\?\.priceChangePercent > 0 \? '\+' : ''\} \+ #numbers\.formatDecimal\(stock\.priceChangePercent, 1, 2\) \+ '%'"'''
new_line = r'''th:text="${(stock?.priceChangePercent != null and stock.priceChangePercent > 0 ? '+' : '') + #numbers.formatDecimal(stock?.priceChangePercent ?: 0, 1, 2) + '%'}"'''

content = re.sub(old_line, new_line, content)

# Write back
with open(r'c:\Users\hemis\Desktop\StockEasy-main\src\main\resources\templates\stocks\detail.html', 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed detail.html template syntax error")
