# Smart Daily Expense Tracker

## 📌 App Overview
Smart Daily Expense Tracker is a Jetpack Compose Android app that helps users log daily expenses, view reports for the last 7 days, and categorize spending.  
It supports dynamic charts, category summaries, and export to PDF/CSV.

## 🤖 AI Usage Summary
I used **ChatGPT (GPT-5)** extensively for:
- Designing UI layouts in Jetpack Compose
- Implementing MVVM architecture
- Creating dynamic chart drawing logic
- Debugging Room database issues

Prompts were focused on:
- UI improvements
- Dynamic data handling
- Graph drawing with Canvas

## 📜 Prompt Logs

## ✅ Features Implemented
- Expense entry with title, amount, category, notes
- Real-time “Total Spent Today”
- Dynamic expense list with category/time grouping
- Expense report (last 7 days) with dynamic charts
- Export mock PDF/CSV
- MVVM architecture with Room database

## 📦 APK Download
[Download APK](./app-debug.apk)

## 📄 Resume
See [resume.pdf](./Adesh_Bhoyar_Android_Developer.pdf)

---

## project structure 

SmartDailyExpenseTracker/
│
├── README.md
├── AdeshBhoyar_AndroidDeveloper.pdf               # or .docx / .txt
├── app-release.apk          # built signed APK
├── /screenshots/            # images of key screens
│    ├── home_screen.png
│    ├── expense_entry.png
│    └── report_screen.png
│
├── /app/                    # full Android Studio project
│    ├── src/main/java/...
│    ├── src/main/res/...
│    ├── build.gradle
│    └── ...
└── ...
