import re

# Read the file
with open(r'c:\Users\hemis\Desktop\StockEasy-main\src\main\resources\templates\stocks\list.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace the buy button
old_pattern = r'<button class="btn btn-sm btn-outline-primary flex-fill"\s+th:data-stock-id="\$\{stock\?\.id\}" onclick="quickBuyAjax\(this\)">\s+<i class="fas fa-shopping-cart"></i>\s+</button>'
new_content = '<a th:href="@{\'/portfolio/dashboard?stock=\' + ${stock?.symbol}}"\n                                        class="btn btn-sm btn-outline-primary flex-fill">\n                                        <i class="fas fa-shopping-cart"></i>\n                                    </a>'

content = re.sub(old_pattern, new_content, content, flags=re.DOTALL)

# Write back
with open(r'c:\Users\hemis\Desktop\StockEasy-main\src\main\resources\templates\stocks\list.html', 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed buy button in list.html")
