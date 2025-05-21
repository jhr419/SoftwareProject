import csv
import random
from datetime import datetime

categories = [
    "Travel", "Entertainment", "Clothing", "Education",
    "Transportation", "Medical", "Home", "Food",
    "Sports", "Communication", "Others"
]

names = ["Taxi", "Movie", "Shirt", "Course", "Bus", "Medicine", "Rent", "Groceries", "Gym", "Phone", "Misc"]

records = []
serial_no = 1

# 从 2020-05 到 2025-05（包括）
for year in range(2020, 2026):
    for month in range(1, 13):
        if year == 2020 and month < 5:
            continue
        if year == 2025 and month > 5:
            break
        for _ in range(100):  # 每月 100 条支出记录
            name = random.choice(names)
            day = random.randint(1, 28)
            date = f"{year}-{month:02d}-{day:02d}"
            amount = round(random.uniform(10, 9999), 2)
            category = random.choice(categories)
            records.append([serial_no, name, date, amount, category])
            serial_no += 1

# 保存到 CSV
csv_path = "/mnt/data/expenses_2020_2025.csv"
with open(csv_path, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(["serialNo", "name", "date", "amount", "category"])
    writer.writerows(records)

csv_path