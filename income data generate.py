import csv
import random
from datetime import datetime

income_categories = ["Salary", "Investment"]
income_names = ["Paycheck", "Bonus", "Stock", "Fund", "Interest", "Dividend"]

income_records = []
serial_no = 1

# 从 2020-05 到 2025-05（包括）
for year in range(2020, 2026):
    for month in range(1, 13):
        if year == 2020 and month < 5:
            continue
        if year == 2025 and month > 5:
            break
        for _ in range(10):  # 每月 10 条
            name = random.choice(income_names)
            day = random.randint(1, 28)  # 避免月末问题
            date = f"{year}-{month:02d}-{day:02d}"
            amount = round(random.uniform(1000, 100000), 2)
            category = random.choice(income_categories)
            income_records.append([serial_no, name, date, amount, category])
            serial_no += 1

# 保存到 CSV
csv_path = "/mnt/data/incomes_2020_2025.csv"
with open(csv_path, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(["serialNo", "name", "date", "amount", "category"])
    writer.writerows(income_records)

csv_path
