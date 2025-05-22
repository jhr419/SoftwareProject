import csv
import random
from datetime import datetime

# 正确分类及对应的项目名称
category_name_map = {
    "Travel": ["Flight", "Train", "Hotel", "Tour Package", "Rental Car", "Visa Fee", "Travel Insurance", "Luggage", "Taxi", "Travel Guide"],
    "Entertainment": ["Movie", "Concert", "Theater", "Amusement Park", "Streaming Service", "Game", "Exhibition", "KTV", "Bar", "Nightclub"],
    "Clothing": ["Shirt", "Pants", "Dress", "Jacket", "Shoes", "Hat", "Scarf", "Socks", "Sweater", "Suit"],
    "Education": ["Tuition", "Books", "Online Course", "Exam Fee", "Stationery", "Seminar", "Workshop", "Lecture", "Training", "Library Fee"],
    "Transportation": ["Bus", "Subway", "Taxi", "Fuel", "Parking", "Train Ticket", "Car Maintenance", "Toll", "Bicycle", "Ride Share"],
    "Medical": ["Medicine", "Doctor Visit", "Surgery", "Hospital", "Insurance", "Dental", "Vaccine", "Eye Exam", "Therapy", "Health Check"],
    "Home": ["Rent", "Mortgage", "Furniture", "Appliance", "Repair", "Utilities", "Internet", "Cleaning", "Decor", "Security"],
    "Food": ["Groceries", "Restaurant", "Fast Food", "Snacks", "Coffee", "Bakery", "Dinner", "Lunch", "Breakfast", "Delivery"],
    "Sports": ["Gym", "Yoga", "Swimming", "Football", "Basketball", "Tennis", "Running", "Cycling", "Fitness Class", "Equipment"],
    "Communication": ["Phone Bill", "Internet", "Mobile Recharge", "SIM Card", "Data Plan", "Calling Card", "Roaming", "Messenger App", "Fax", "VPN"],
    "Others": ["Gift", "Donation", "Pet", "Laundry", "Subscription", "Lottery", "Tips", "Fine", "Storage", "Miscellaneous"]
}

records = []
serial_no = 1

# 生成从 2020-05 到 2025-05（包含）的数据
for year in range(2020, 2026):
    for month in range(1, 13):
        if year == 2020 and month < 5:
            continue
        if year == 2025 and month > 5:
            break
        for _ in range(100):  # 每月生成 100 条记录
            category = random.choice(list(category_name_map.keys()))
            name = random.choice(category_name_map[category])
            day = random.randint(1, 28)
            date = f"{year}-{month:02d}-{day:02d}"
            amount = round(random.uniform(10, 9999), 2)
            records.append([serial_no, name, date, amount, category])
            serial_no += 1

# 写入 CSV
file_path = "/mnt/data/corrected_expenses_2020_2025.csv"
with open(file_path, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(["serialNo", "name", "date", "amount", "category"])
    writer.writerows(records)

file_path