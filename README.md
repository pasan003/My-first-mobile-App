# SpendWise – Personal Expense Tracker

**SpendWise** is an offline, Android-only personal expense tracker built with **Kotlin**, **Material Design 3** and **Room Database**. It helps you take control of your spending by recording every expense, organising it into categories, and turning that data into clear, actionable insights — all without an internet connection.

> **Version 1.0.0** — Apache License 2.0

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Database Design](#database-design)
- [Screens](#screens)
- [Installation](#installation)
- [Future Improvements](#future-improvements)
- [License](#license)

---

## Project Overview

Tracking expenses is easy; understanding them is hard. Most people never get a clear picture of where their money goes because they rely on memory or scattered notes.

SpendWise solves this by putting a **complete, private money diary in your pocket**:

- **Track daily expenses** — record any purchase in seconds with title, amount, category, date, payment method and optional notes.
- **Organise expenses using categories** — use the nine built-in categories (Food, Transport, Shopping, Bills, Entertainment, Health, Education, Salary, Other) or create your own with custom icons and colors.
- **View spending analytics** — see where your money goes with period-based summaries, spending-by-category breakdowns, monthly trends and smart insights.
- **Manage a personal profile** — set your name, monthly income and preferred currency, all editable at any time.
- **Store everything locally** — all data lives in a Room (SQLite) database on the device. Nothing is uploaded anywhere.
- **Work completely offline** — SpendWise runs 100% on-device, so your financial data never leaves your phone.

The app follows the **MVVM** architecture (Repository + ViewModel + LiveData/Flow) with a **Material Design 3** interface built from XML layouts and **ViewBinding**.

---

## Features

### 🏠 Dashboard

- **Personalized greeting** — time-aware greeting (Good Morning / Afternoon / Evening) with the user's name
- **Monthly balance summary** — a balance card computed from real data: `Monthly Income − Total Expenses = Remaining Balance`
- **Monthly income** and **total expenses** shown on the balance card
- **Recent Transactions** — the five most recent expenses loaded from Room, rendered in a `RecyclerView`
- **View All** — opens Expense History, where search and category filters live
- **Floating Action Button (FAB)** — quick access to the Add Expense screen
- **Bottom Navigation** — four tabs (Home, Categories, Analytics, Profile)

### 💸 Expense Management

- **Add Expense** — full-screen form with title, amount, category dropdown, date picker, payment method and notes
- **Edit Expense** — the same form pre-fills and updates an existing expense
- **Delete Expense** — with a confirmation dialog
- **Expense Details** — a dedicated screen showing every field of a single transaction
- **Expense History** — the complete, searchable list of expenses
- **Date Picker** — Material `DatePickerDialog` (tap the field or the calendar icon)
- **Payment Method** — five options: Cash, Credit Card, Debit Card, Bank Transfer, Digital Wallet
- **Notes** — optional multi-line note attached to each expense
- **Validation** — inline error messages for required title, a positive amount and a valid category, plus a double-tap guard so Save cannot create duplicate rows

### 🗂️ Categories

- **Default categories** — nine seeded automatically on first launch (Food, Transport, Shopping, Bills, Entertainment, Health, Education, Salary, Other)
- **Create custom categories** — via the FAB or the empty-state button
- **Edit categories** — rename or change the icon and color
- **Delete categories** — long-press a row; categories still in use are moved to "Other" inside one atomic Room transaction before deletion
- **Category search** — a real-time search box filters categories by name as you type
- **Category statistics** — live transaction count and total amount spent per category, aggregated by SQL
- **Icon selection** — choose from a curated set of category icons
- **Color selection** — pick from eight Material 3 color accents
- **Dynamic category filters** — category filter chips on Expense History are generated from the database, so custom categories appear automatically
- **Sorting** — sort by name (A–Z), most/least used, or highest/lowest spending

### 📊 Analytics

- **Monthly Income, Total Expenses, Remaining Balance, Total Transactions** — four summary cards computed from Room
- **Spending by Category** — dynamic list grouped by category with colored indicators
- **Highest Spending Category** — a Top 5 ranking of categories by amount
- **Monthly Spending Summary** — per-month totals with progress bars across the current year
- **Smart spending insights** — most spent on, remaining balance, average daily spending and largest single expense
- **Period selector** — This Week / This Month / This Year, recomputing every section instantly
- **Empty state** — a friendly screen with an "Add Expense" action when no data exists

### 👤 Profile

- **First-time user setup** — a guided screen collects full name and monthly income before the dashboard is shown
- **Store user name, monthly income and currency** — kept in Room and observed by every screen
- **Edit Profile** — a dialog updates name, income and currency (8 currencies: LKR, USD, EUR, GBP, INR, JPY, AUD, CAD)
- **Member Since** — the profile creation date
- **Financial summary** — income, expenses and remaining balance, updating live
- **Storage information** — total categories and expenses with the local database status
- **About App, Privacy Policy, Help & Support** — informational dialogs
- **Application version** — displayed in the settings list (no logout, because the app is a single-user, fully offline application)

### 🗄️ Database

- Built on **Room Database** (SQLite)
- Three entities: **User Profile**, **Expenses**, **Categories**
- Full **CRUD** (Create, Read, Update, Delete) for expenses and categories, plus update for the single user profile
- Reactive `Flow` queries so every screen refreshes automatically when data changes
- Schema version **2** with a data-preserving migration, exported to `app/schemas/`

### 🔍 Search & Filtering

- **Search transactions in real time** — every keystroke on the Expense History search bar re-queries the database instantly; no Search button required
- **Case-insensitive matching** across **expense title**, **category name** and **notes** (implemented with SQL `LIKE` so filtering happens in the database, not in memory)
- **Filter by category** — Material filter chips (All + every custom category) narrow the list
- **Combine search and filters** — search and category selection work together, e.g. *Filter = Food* + *search = "Rice"* shows only Food transactions containing "Rice"
- **Automatically refresh results** — clearing the search restores the current category results; selecting "All" restores every transaction
- **Empty search state** — "No matching transactions found. Try a different keyword." when nothing matches

### 🎨 UI & UX

- **Material Design 3** theming with proper color roles and a deep teal/emerald palette
- **Smooth screen transitions** — slide animations between every screen (shared `BaseActivity`)
- **RecyclerView animations** — subtle fade-in entrance animations for list items
- **Snackbar feedback** — for saved/updated/deleted expenses, profile updates and category actions
- **Responsive layouts** — scrollable, constraint-based layouts that adapt to screen sizes
- **Empty state screens** — friendly placeholders on History, Categories, Analytics and search
- **Confirmation dialogs** — before deleting expenses or categories
- **Splash screen** — branded launch experience via the AndroidX SplashScreen API
- **Press feedback** — gentle scale-down on buttons and FABs

---

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/firstapp/myapplication/
│   │   │   ├── BaseActivity.kt                 # Shared slide-transition base activity
│   │   │   ├── SetupActivity.kt                # First-time setup (name + income)
│   │   │   ├── MainActivity.kt                 # Home Dashboard
│   │   │   ├── AddExpenseActivity.kt           # Add / Edit expense form
│   │   │   ├── ExpenseHistoryActivity.kt       # History + search + filter chips
│   │   │   ├── ExpenseDetailActivity.kt        # Single expense details
│   │   │   ├── CategoryManagerActivity.kt      # Category CRUD + search + sort
│   │   │   ├── AnalyticsActivity.kt            # Spending analytics
│   │   │   ├── ProfileSettingsActivity.kt      # Profile & settings
│   │   │   ├── Transaction.kt / CategoryItem.kt    # UI data models
│   │   │   ├── TransactionAdapter.kt           # Dashboard list adapter
│   │   │   ├── ExpenseHistoryAdapter.kt        # History list adapter
│   │   │   ├── CategoryAdapter.kt              # Category list adapter
│   │   │   ├── database/                       # Room layer
│   │   │   │   ├── AppDatabase.kt              # Singleton + migration + seeding
│   │   │   │   ├── dao/                        # ExpenseDao, CategoryDao, UserProfileDao
│   │   │   │   ├── entity/                     # Expense, Category, UserProfile
│   │   │   │   └── relation/                   # ExpenseWithCategory, CategoryStats
│   │   │   ├── repository/                     # Expense, Category, UserProfile repositories
│   │   │   ├── viewmodel/                      # Expense, Category, Analytics, UserProfile ViewModels
│   │   │   └── utils/                          # CurrencyUtils, DateUtils, CategoryVisuals, Mapper, UiAnimations
│   │   ├── res/
│   │   │   ├── layout/                         # XML screen & item layouts
│   │   │   ├── drawable/                       # Icons and drawable resources
│   │   │   ├── values/                         # Colors, themes, strings, dimens
│   │   │   ├── color/                          # Color state lists
│   │   │   ├── menu/                           # Bottom nav & toolbar menus
│   │   │   ├── anim/                           # Screen transition animations
│   │   │   └── mipmap/                         # App launcher icons
│   │   └── AndroidManifest.xml
│   ├── test/                                   # Unit tests
│   └── androidTest/                            # Instrumented tests
├── schemas/                                    # Room schema exports
├── gradle/                                     # Gradle wrapper & configuration
├── build.gradle.kts                            # App-level build config
└── settings.gradle.kts                         # Project-level build config
```

---

## Technologies Used

| Technology | Usage |
|---|---|
| **Kotlin** | Primary language |
| **Android Studio** | Development environment |
| **XML Layouts + ViewBinding** | UI definition and type-safe view access |
| **Material Design 3** | Theming, components and design system |
| **Room Database / SQLite** | Local persistent storage |
| **MVVM** | Repository + ViewModel + LiveData architecture |
| **LiveData / ViewModel** | Reactive UI state |
| **Kotlin Coroutines & Flow** | Asynchronous data layer |
| **RecyclerView + DiffUtil** | Efficient, animated lists |
| **SplashScreen API** | Branded launch screen |
| **KSP** | Room annotation processing |

---

## Database Design

The database (`spendwise_database`, version 2) contains three entities:

### Expense
Stores a single transaction:

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto) | Primary key |
| `title` | String | Short description, e.g. "Lunch" |
| `amount` | Double | Monetary value |
| `categoryId` | Long (FK) | References `Category.id` (`RESTRICT` — a category with expenses cannot be deleted) |
| `notes` | String | Optional user note |
| `paymentMethod` | String | e.g. "Cash" |
| `transactionDate` | Long | Epoch millis of the expense date |
| `createdAt` | Long | Epoch millis when the record was added |

Indexed on `categoryId` for fast lookups.

### Category
Represents a spending category:

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto) | Primary key |
| `name` | String | e.g. "Food" |
| `icon` | String | Drawable *resource name* (e.g. `ic_food`) |
| `color` | String | Color *resource name* for the indicator |
| `createdAt` | Long | Creation timestamp |

Icons and colors are stored as stable resource names rather than resource IDs.

### UserProfile
A single, app-wide profile:

| Column | Type | Notes |
|---|---|---|
| `id` | Long (PK, auto) | Primary key |
| `fullName` | String | Display name |
| `monthlyIncome` | Double | Used by Dashboard balance and Analytics |
| `currency` | String | ISO-style code, e.g. "LKR" |
| `createdAt` | Long | Profile creation date ("Member since") |

### Relationships
- **Category 1 → N Expense** — one category can have many expenses. Modeled with Room's `@Relation` (`ExpenseWithCategory`), so an expense always loads together with its category in a single reactive query.
- **CategoryStats** — an SQL aggregation (`COUNT`, `SUM`) grouped by category, powering the Category Manager statistics and Analytics without loading raw rows.
- The nine default categories are **seeded only on first launch**; the v1 → v2 migration adds the `user_profiles` table and any missing default categories without touching existing user data.

---

## Screens

| Screen | Description |
|---|---|
| **First-Time Setup** | Collects full name and monthly income on the very first launch, then creates the profile and opens the Dashboard |
| **Dashboard** | Greeting, balance card (income − expenses), recent transactions, bottom navigation and the Add Expense FAB |
| **Add Expense** | Full expense entry form with dropdowns, date picker, notes and validation — also used for editing |
| **Expense Details** | View every field of a transaction; edit or delete it |
| **Expense History** | Complete, searchable expense list with category filter chips, summary card and empty state |
| **Category Manager** | Create, edit, search, sort and delete categories with live statistics |
| **Analytics** | Period-based spending insights: summaries, category breakdowns, monthly trends and top categories |
| **Profile & Settings** | Profile header, financial summary, edit profile (name/income/currency) and app information dialogs |

---

## Installation

### Requirements

- **Android Studio** (latest stable release recommended)
- **JDK 11+** (AGP 9.2.1 with built-in Kotlin support)
- **Android SDK 37** (the project compiles against API 37 and supports devices from API 26)

### Steps

1. **Clone the project:**

   ```bash
   git clone <repository-url>
   cd My-first-mobile-App
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select **File → Open** and choose the cloned project folder
   - Wait for the Gradle sync to finish (the first sync downloads dependencies)

3. **Build the project:**
   - Click **Build → Make Project** (or press `Ctrl+F9`)
   - Optionally run `./gradlew assembleDebug` from the terminal

4. **Run the app:**
   - Select a physical device or emulator from the toolbar
   - Click **Run → Run 'app'** (or press `Shift+F10`)
   - The app launches with the SpendWise splash screen

5. **Explore:**
   - On first launch, enter your name and monthly income on the **Get Started** screen
   - Tap the **+** FAB to add your first expense
   - Use the search bar and category chips in **Expense History**, view insights in **Analytics**, and manage categories in **Categories**
   - All data is stored on-device and persists across restarts — no account or internet connection needed

---

## Future Improvements

- **Cloud backup** — sync data across devices via cloud storage
- **User authentication** — optional account-based sign-in
- **Export to PDF/CSV** — generate shareable financial reports
- **Budget planning** — monthly budgets per category with progress tracking
- **Recurring expenses** — automate repeated bills and subscriptions
- **Notifications** — reminders for due bills and spending limits
- **Dark mode** — full dark theme support using Material 3 dynamic colors
- **Multi-language support** — localize the app beyond English

---

## License

This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for the full license text.

---

<p align="center">Built with ❤️ using Kotlin &amp; Android</p>
