# SpendWise – Personal Finance Tracker

SpendWise is a personal finance tracker designed to help you manage your money effectively. Built with **Kotlin**, **Material Design 3** and **Room Database**, SpendWise provides a clean and intuitive interface for tracking daily expenses.

> **Note:** The app now uses a **Room Database** for persistent local storage (MVVM architecture). All screens load real data — no more sample/hardcoded data.

---

## Current Features

### ✅ First-Time Setup
- Shown automatically on the very first launch when no user profile exists
- Collects the user's **Full Name** and **Monthly Income** with validation
- Saves the profile into Room and navigates to the Dashboard
- Never shown again unless the app data is cleared

### ✅ User Profile (Room)
- `UserProfile` entity with full name, monthly income, currency and creation date
- Only one profile is ever stored; created on first launch, editable afterwards
- Every screen observes the same profile, so changes propagate instantly

### ✅ Home Dashboard
- Time-aware greeting (**Good Morning / Afternoon / Evening**) with the user's real name
- Balance card calculated from Room: **Monthly Income − Total Expenses = Remaining Balance**
- Recent Transactions list rendered via `RecyclerView`
- Bottom Navigation bar with four tabs (Home, Categories, Analytics, Profile)
- Floating Action Button to navigate to the Add Expense screen

### ✅ Add Expense
- Full-screen form with the following fields:
  - Expense Title (text input with clear button)
  - Amount (numeric input with Rs. prefix)
  - Category (dropdown loaded from the Room database)
  - Date (Material DatePicker dialog, triggered on tap or calendar icon)
  - Payment Method (dropdown with 5 options)
  - Notes (multi-line text area, optional)
- **Saves expenses to the Room database** with real validation
- **Edit mode** — the same screen pre-fills and updates an existing expense
- Snackbar feedback after saving, returns to the previous screen
- Toolbar with back navigation and save action

### ✅ Expense History
- Full list of all expenses loaded from Room via `RecyclerView`
- Summary card (transaction count + total expenses) computed from real data
- Empty state shown when the database has no expenses
- Tap an item to open its Expense Detail screen

### ✅ Expense Details
- Loads the expense from Room by id
- Displays title, amount, category, date, time, payment method and notes
- **Edit** opens the Add Expense screen in edit mode
- **Delete** with a confirmation dialog removes the expense from Room

### ✅ Category Manager
- Categories + live expense counts loaded from Room
- **Add** category via FAB / empty-state button
- **Edit** category via the pencil icon on each row
- **Delete** category via long-press, with a friendly warning when the category still has expenses
- Custom color picker (8 colors) and icon dropdown for each category
- Empty state shown when there are no categories

### ✅ Analytics
- All numbers calculated from the Room database
- Period selector (This Week / This Month / This Year) that recomputes the aggregates
- Four summary cards: **Total Expenses, Monthly Income, Remaining Balance, Total Transactions**
- Spending by Category — dynamic list grouped by category with colored indicators
- Monthly Spending — per-month totals with progress bars for the whole year
- Top Categories ranking and Insights card (most spent on, remaining balance, avg daily, largest expense)
- Friendly empty state: *"No expense data available yet. Start by adding your first expense."*

### ✅ Material Design 3 Interface
- Deep teal / emerald color palette
- Material 3 theming with proper color roles (Primary, Secondary, Tertiary, Surface, Error)
- Rounded card components (`MaterialCardView`)
- Outlined text fields with custom corner radius
- Splash screen integration

### ✅ Room Database (persistence)
- `Expense` entity with auto-generated primary key, category foreign key (RESTRICT), and index on `categoryId`
- `Category` entity with name, icon and color stored as resource names
- One-to-many relation modeled with Room's `@Relation` (`ExpenseWithCategory`)
- Reactive `Flow` queries so the UI updates automatically when data changes
- **Nine default categories are seeded only on the first launch** (Food, Transport, Shopping, Bills, Entertainment, Health, Education, Salary, Other) — no duplicates on later launches
- `UserProfile` entity for persistent profile management (name, monthly income, currency)
- Database version 2 with a data-preserving migration (adds `user_profiles` + new default categories)
- Schema exported to `app/schemas/`

### ✅ MVVM Architecture
- **Data layer:** `database/` (entities, DAOs, `AppDatabase` singleton), `repository/`
- **UI layer:** Activities observe `LiveData` exposed by ViewModels
- **Utils layer:** currency formatting, date helpers, category visuals mapping

---

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/firstapp/myapplication/
│   │   │   ├── SetupActivity.kt             # First-time setup (name + income)
│   │   │   ├── MainActivity.kt              # Home Dashboard
│   │   │   ├── AddExpenseActivity.kt        # Add / Edit Expense form
│   │   │   ├── ExpenseHistoryActivity.kt    # Full expense list
│   │   │   ├── ExpenseDetailActivity.kt     # Single expense detail
│   │   │   ├── CategoryManagerActivity.kt   # Category CRUD
│   │   │   ├── AnalyticsActivity.kt         # Spending insights
│   │   │   ├── ProfileSettingsActivity.kt   # Profile & settings
│   │   │   ├── Transaction.kt               # UI data model
│   │   │   ├── CategoryItem.kt              # UI data model
│   │   │   ├── TransactionAdapter.kt        # Dashboard list adapter
│   │   │   ├── ExpenseHistoryAdapter.kt     # History list adapter
│   │   │   ├── CategoryAdapter.kt           # Category list adapter
│   │   │   ├── database/                    # Room: entities, DAOs, AppDatabase
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── dao/ExpenseDao.kt
│   │   │   │   ├── dao/CategoryDao.kt
│   │   │   │   ├── dao/UserProfileDao.kt
│   │   │   │   ├── entity/Expense.kt
│   │   │   │   ├── entity/Category.kt
│   │   │   │   ├── entity/UserProfile.kt
│   │   │   │   └── relation/ExpenseWithCategory.kt
│   │   │   ├── repository/                  # Expense, Category, UserProfile repositories
│   │   │   ├── viewmodel/                   # Expense, Category, Analytics, UserProfile ViewModels
│   │   │   └── utils/                       # CurrencyUtils, DateUtils, CategoryVisuals, Mapper
│   │   ├── res/
│   │   │   ├── layout/                      # XML layout files
│   │   │   ├── drawable/                    # Icons and drawable resources
│   │   │   ├── values/                      # Colors, themes, strings, dimens
│   │   │   ├── menu/                        # Bottom nav & toolbar menus
│   │   │   ├── color/                       # Color state lists
│   │   │   └── mipmap/                      # App launcher icons
│   │   └── AndroidManifest.xml
│   └── test/                                # Unit tests
│   └── androidTest/                         # Instrumented tests
├── schemas/                                 # Room schema exports
├── build.gradle.kts                         # App-level build config
├── gradle/                                  # Gradle wrapper & configuration
└── build.gradle.kts                         # Root-level build config
```

---

## Technologies

| Technology | Status |
|---|---|
| **Kotlin** | ✅ In Use |
| **Android Studio** | ✅ Development Environment |
| **XML Layouts** | ✅ In Use |
| **Material Design 3** | ✅ In Use |
| **RecyclerView** | ✅ In Use |
| **SplashScreen API** | ✅ In Use |
| **Room Database** | ✅ In Use |
| **SQLite** (via Room) | ✅ In Use |
| **LiveData / ViewModel** | ✅ In Use |
| **Kotlin Coroutines** | ✅ In Use |
| **KSP** (Room annotation processing) | ✅ In Use |

---

## Screens

| Screen | Description |
|---|---|
| **First-Time Setup** | Collects full name and monthly income on the very first launch |
| **Home Dashboard** | Balance overview, recent transactions, bottom navigation, and FAB |
| **Add Expense** | Full expense entry form with dropdowns, date picker, and validation (also used for editing) |
| **Expense History** | Full list of expenses with summary and empty state |
| **Expense Details** | View/edit/delete an individual transaction |
| **Category Manager** | Add, edit, and delete custom spending categories |
| **Analytics** | Real spending insights computed from the database |
| **Profile / Settings** | Real profile data, edit profile, About / Privacy / Help, disabled logout |

---

## Development Status

- ✅ Home Dashboard (real data)
- ✅ Add / Edit Expense (saves to Room)
- ✅ Expense History (real data)
- ✅ Expense Details (load, update, delete)
- ✅ Category Manager (full CRUD)
- ✅ Analytics (real calculations)
- ✅ First-time setup (persistent profile)
- ✅ Profile & Settings (edit name, income, currency)
- ✅ Room Database + seeding (9 default categories)
- ✅ MVVM (Repository + ViewModel + LiveData)
- ✅ Material Design 3 Theming
- ⏳ Search / Filter logic
- ⏳ Real charts (pie/bar) — currently summarized in lists
- ⏳ Data export, budget tracking, dark mode

---

## How to Run

1. **Open in Android Studio:**
   - Launch Android Studio
   - Select **File → Open** and navigate to the project directory
   - Wait for Gradle sync to complete (this may download dependencies on first run)

2. **Build the project:**
   - Click **Build → Make Project** (or press `Ctrl+F9`)

3. **Run the app:**
   - Select a device/emulator from the toolbar
   - Click **Run → Run 'app'** (or press `Shift+F10`)
   - The app will launch with the SpendWise splash screen followed by the Home Dashboard

4. **Explore the app:**
   - On first launch, the **Get Started** screen asks for your name and monthly income
   - Tap the **+** (FAB) button to add your first expense
   - Edit your profile in **Profile**, view insights in **Analytics**
   - All data is stored on-device and persists across app restarts

> **Requirements:** Android Studio, Android SDK 37, JDK 11+ (AGP 9.2.1 with built-in Kotlin support).

---

## Future Improvements

- **Search & Filter** — filter expenses by title or category
- **Real Charts** — replace summarized lists with pie/bar chart visuals
- **Dark Mode** — implement dark theme support using Material 3
- **Budget Tracking** — set monthly budgets per category and track progress
- **Monthly Reports** — generate detailed monthly financial summaries
- **Data Export** — export transactions to CSV or PDF
- **Notifications** — remind users about due bills and spending limits
- **Cloud Backup** — sync data across devices via cloud storage
- **Multi-Currency Support** — handle different currencies with live exchange rates

---

<p align="center">Built with ❤️ using Kotlin & Android</p>
