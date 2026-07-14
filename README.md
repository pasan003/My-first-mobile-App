# SpendWise – Personal Finance Tracker

SpendWise is a personal finance tracker designed to help you manage your money effectively. Built with **Kotlin** and **Material Design 3**, SpendWise provides a clean and intuitive interface for tracking daily expenses.

> **Note:** This project is currently in **UI development phase**. All data displayed is sample/hardcoded. Database integration is planned for a future release.

---

## Current Features

### ✅ Home Dashboard UI
- Greeting header with user name and profile avatar
- Balance card showing total balance, income, and expenses breakdown
- Recent Transactions list rendered via `RecyclerView`
- Bottom Navigation bar with four tabs (Home, Categories, Analytics, Profile)
- Floating Action Button to navigate to the Add Expense screen

### ✅ Add Expense UI
- Full-screen form with the following fields:
  - Expense Title (text input with clear button)
  - Amount (numeric input with Rs. prefix)
  - Category (dropdown with 8 predefined categories)
  - Date (Material DatePicker dialog, triggered on tap or calendar icon)
  - Payment Method (dropdown with 5 options)
  - Notes (multi-line text area, optional)
- Save button with placeholder snackbar feedback (no persistence yet)
- Cancel button to return to dashboard
- Basic validation UI (error text visibility toggling)
- Toolbar with back navigation and save action

### ✅ Material Design 3 Interface
- Deep teal / emerald color palette
- Material 3 theming with proper color roles (Primary, Secondary, Tertiary, Surface, Error)
- Rounded card components (`MaterialCardView`)
- Outlined text fields with custom corner radius
- Splash screen integration

### ✅ Responsive Layouts
- Scrollable content using `NestedScrollView`
- ConstraintLayout-based adaptive layouts
- Proper spacing and padding using dimens resources

### ✅ RecyclerView with Sample Transaction Data
- `TransactionAdapter` with `ListAdapter` and `DiffUtil` for efficient updates
- Five hardcoded sample transactions (Lunch, Groceries, Bus Fare, Netflix, Electricity Bill)
- Each transaction displays: category icon, title, category name, date, and formatted amount
- Expense amounts shown in red, with a minus prefix
- Subtle dividers between items

### ✅ Bottom Navigation (UI only)
- Four navigation destinations: Home, Categories, Analytics, Profile
- Icon tinting with active/inactive states
- Currently no fragment-based navigation — static UI placeholder

### ✅ Floating Action Button (UI only)
- Positioned above the bottom navigation bar
- Navigates to the Add Expense screen via `Intent`

---

## Planned Features

| Feature | Description |
|---|---|
| Room Database Integration | Persistent local storage with SQLite via Room ORM |
| Expense CRUD Operations | Full create, read, update, and delete for expenses |
| Category Management | Add, edit, and delete custom categories |
| Analytics Dashboard | Monthly spending breakdown with visual summaries |
| User Profile | Avatar selection, user name editing, preferences |
| Data Validation | Real-time validation with error messages |
| Charts and Reports | Graphical spending visualization |
| Search and Filter | Search by title, filter by category or date range |
| Settings | Currency selection, theme toggle, data management |

---

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/firstapp/myapplication/
│   │   │   ├── MainActivity.kt              # Home Dashboard
│   │   │   ├── AddExpenseActivity.kt        # Add Expense form
│   │   │   ├── TransactionAdapter.kt        # RecyclerView adapter
│   │   │   ├── Transaction.kt               # Transaction data class
│   │   │   └── database/ (planned)          # Room Database entities & DAOs
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
| **Room Database** | ⏳ Planned |
| **SQLite** (via Room) | ⏳ Planned |

---

## Screens

### Current

| Screen | Description |
|---|---|
| **Home Dashboard** | Balance overview, recent transactions, bottom navigation, and FAB |
| **Add Expense** | Full expense entry form with dropdowns, date picker, and validation |

### Planned

| Screen | Description |
|---|---|
| **Expense Details** | View/edit individual transaction details |
| **Category Manager** | Manage custom spending categories |
| **Analytics** | Monthly breakdown charts and spending insights |
| **Profile / Settings** | User profile and app preferences |

---

## Development Status

- ✅ Home Dashboard UI
- ✅ Add Expense UI
- ✅ Material Design 3 Theming
- ✅ RecyclerView with Sample Data
- ✅ Splash Screen
- ⏳ Room Database Integration
- ⏳ Expense CRUD Operations
- ⏳ Categories Management
- ⏳ Analytics Dashboard
- ⏳ User Profile / Settings
- ⏳ Data Validation (real-time)
- ⏳ Charts and Reports
- ⏳ Search and Filter

---

## How to Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/SpendWise.git
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select **File → Open** and navigate to the project directory
   - Wait for Gradle sync to complete

3. **Build the project:**
   - Click **Build → Make Project** (or press `Ctrl+F9`)
   - Resolve any dependency issues if prompted

4. **Run the app:**
   - Select a device/emulator from the toolbar
   - Click **Run → Run 'app'** (or press `Shift+F10`)
   - The app will launch with the SpendWise splash screen followed by the Home Dashboard

5. **Explore the UI:**
   - Tap the **+** (FAB) button to navigate to the Add Expense screen
   - Fill in the form fields and tap **Save Expense** to see placeholder feedback
   - Bottom navigation tabs are visible but not yet functional

> **Requirements:** Android Studio Hedgehog (2023.1.1) or later, Android SDK 33+, Kotlin plugin

---

## Future Improvements

- **Dark Mode** — Implement dark theme support using Material 3 dynamic theming
- **Data Export** — Export transactions to CSV or PDF
- **Budget Tracking** — Set monthly budgets per category and track progress
- **Monthly Reports** — Generate detailed monthly financial summaries
- **Notifications** — Remind users about due bills and spending limits
- **Cloud Backup** — Sync data across devices via cloud storage
- **Biometric Lock** — Secure the app with fingerprint or face unlock
- **Multi-Currency Support** — Handle different currencies with live exchange rates

---

<p align="center">Built with ❤️ using Kotlin & Android</p>