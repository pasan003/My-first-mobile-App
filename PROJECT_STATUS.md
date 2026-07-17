# PROJECT_STATUS.md

> **Generated:** July 17, 2026  
> **Purpose:** This file provides AI assistants with enough context to understand the project without reading every file. Update after every major feature.

---

## 1. Project Information

| Field | Value |
|-------|-------|
| **Project Name** | SpendWise |
| **Application Purpose** | Personal expense tracking app — users can log, view, and manage their daily expenses. |
| **Current Development Stage** | UI Prototype / Sample Data Phase (all screens are built with hardcoded sample data; no database or persistence yet) |
| **Technology Stack** | Kotlin, Android SDK, Material Design 3 |
| **Target Android SDK** | 37 (Android 14 / UpsideDownCake) |
| **Minimum SDK** | 26 (Android 8.0 Oreo) |
| **Kotlin Version** | Kotlin (managed via AGP 9.2.1; no explicit Kotlin plugin version in build files) |
| **Build System** | Gradle (Kotlin DSL) with Version Catalog (`libs.versions.toml`) |
| **Material Design Version** | Material 3 (via `com.google.android.material:material:1.14.0`) |
| **View Binding** | Enabled |
| **AGP Version** | 9.2.1 |

---

## 2. Project Structure

```
My-first-mobile-App/
├── build.gradle.kts                  # Root build file (applies android application plugin)
├── settings.gradle.kts               # Module includes, repositories
├── gradle.properties                 # JVM args, config cache, Kotlin code style
├── gradle/
│   ├── libs.versions.toml            # Version catalog (all dependency versions)
│   └── wrapper/                      # Gradle wrapper files
├── app/
│   ├── build.gradle.kts              # Module build config (SDK versions, deps, viewBinding)
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml           # 4 activities declared
│           ├── java/com/firstapp/myapplication/
│           │   ├── MainActivity.kt           # Dashboard screen (home)
│           │   ├── AddExpenseActivity.kt     # Add expense form
│           │   ├── ExpenseDetailActivity.kt  # Single expense detail view
│           │   ├── ExpenseHistoryActivity.kt # Full expense history list
│           │   ├── Transaction.kt            # Data model (data class)
│           │   ├── TransactionAdapter.kt     # RecyclerView adapter (dashboard list)
│           │   └── ExpenseHistoryAdapter.kt  # RecyclerView adapter (history list)
│           ├── res/
│           │   ├── color/                    # Color state selectors (bottom nav, text fields)
│           │   ├── drawable/                 # 30+ vector drawables (icons, dividers, gradients)
│           │   ├── layout/                   # 6 layout XML files (see Section 3)
│           │   ├── menu/                     # 4 menu XML files (toolbars + bottom nav)
│           │   ├── mipmap-anydpi/             # Launcher icons
│           │   ├── values/
│           │   │   ├── colors.xml            # Full MD3 color palette + finance-specific colors
│           │   │   ├── dimens.xml            # Spacing, corner radii, text sizes
│           │   │   ├── strings.xml           # All user-facing strings (en)
│           │   │   └── themes.xml            # MD3 light theme + splash screen theme
│           │   └── xml/                      # Backup & data extraction rules
│           ├── test/                         # Unit test placeholder
│           └── androidTest/                  # Instrumented test placeholder
└── PROJECT_STATUS.md                   # THIS FILE
```

### Purpose of Key Directories

| Directory | Purpose |
|-----------|---------|
| `java/.../myapplication/` | All Kotlin source files (activities, data models, adapters) |
| `res/layout/` | XML layout files for all screens and list items |
| `res/drawable/` | Vector drawables (category icons, UI icons, dividers, splash logo) |
| `res/color/` | Color state list selectors (e.g., checked/unchecked bottom nav tint) |
| `res/menu/` | Toolbar menus and bottom navigation definition |
| `res/values/` | Colors, dimensions, strings, and theme/style definitions |

---

## 3. Current Screens

### 3.1 Dashboard (Home)

| Field | Value |
|-------|-------|
| **Screen Name** | Dashboard / Home |
| **Purpose** | Shows greeting, balance card (total balance, income, expenses), recent transactions list, bottom navigation, and FAB |
| **Layout File** | `activity_main.xml` |
| **Activity** | `MainActivity` |
| **Current Status** | **Completed** (UI + navigation to other screens) |

Features on this screen:
- Top app bar with greeting text and user avatar
- Balance card (gradient background) with total balance, income, and expenses
- Recent transactions section with "View All" link
- FAB to add new expense
- Bottom navigation bar (4 tabs: Home, Categories, Analytics, Profile)

### 3.2 Add Expense

| Field | Value |
|-------|-------|
| **Screen Name** | Add Expense |
| **Purpose** | Form to input a new expense (title, amount, category, date, payment method, notes) |
| **Layout File** | `activity_add_expense.xml` |
| **Activity** | `AddExpenseActivity` |
| **Current Status** | **Completed** (UI + form validation placeholders + date picker) |

Features on this screen:
- MaterialToolbar with back arrow and save icon
- Outlined text fields with clear text and dropdown end icons
- Category dropdown (8 categories via string array)
- Payment method dropdown (5 methods via string array)
- MaterialDatePicker integration for date selection
- Save (filled) and Cancel (outlined) buttons
- Error text views for validation (toggle visibility, no real validation logic yet)
- Snackbar placeholder on save (no database persistence)

### 3.3 Expense Detail

| Field | Value |
|-------|-------|
| **Screen Name** | Expense Detail |
| **Purpose** | Displays full details of a single expense |
| **Layout File** | `activity_expense_detail.xml` |
| **Activity** | `ExpenseDetailActivity` |
| **Current Status** | **Completed** (UI passes via Intent extras; sample data fallback) |

Features on this screen:
- Toolbar with overflow menu (Edit, Delete — both show placeholder toasts)
- Card 1: Expense summary (title, amount, category chip, date)
- Card 2: Expense information (category, payment method, date, time, notes with icons)
- Card 3: Actions card (Edit filled button + Delete outlined button — placeholder toasts)
- Data passed via Intent extras (EXTRA_TITLE, EXTRA_AMOUNT, EXTRA_CATEGORY, EXTRA_DATE, EXTRA_ICON_RES_ID)
- Falls back to sample detail strings when extras not provided

### 3.4 Expense History

| Field | Value |
|-------|-------|
| **Screen Name** | Expense History |
| **Purpose** | Full list of all expenses with summary, search bar, filter chips, and empty state |
| **Layout File** | `activity_expense_history.xml` |
| **Activity** | `ExpenseHistoryActivity` |
| **Current Status** | **Completed** (UI with sample data; search/filter are visual only) |

Features on this screen:
- Toolbar with back arrow and filter menu icon (placeholder toast)
- Summary card (total expenses label, transaction count, amount)
- Search bar card (visual only — EditText is non-clickable/non-focusable)
- Horizontally scrollable filter chip group (8 category chips, single-selection, "All" checked by default)
- RecyclerView with 12 sample transactions
- Empty state layout (hidden by default — icon, title, description, "Add Expense" button)

---

## 4. Navigation Flow

```
MainActivity (Dashboard)
    │
    ├──→ AddExpenseActivity          (via FAB click)
    │
    ├──→ ExpenseDetailActivity        (via transaction item tap)
    │
    └──→ ExpenseHistoryActivity       (via "View All" link)
```

**Implementation details:**
- All navigation uses explicit `Intent` with `startActivity()` — no NavComponent / Jetpack Navigation yet.
- Activity parent relationships are declared in AndroidManifest (parentActivityName=".MainActivity").
- `ExpenseDetailActivity` receives data via Intent extras.
- Add Expense screen has a toolbar save action (menu item) but it does not pass data back yet.
- Bottom navigation items exist visually but none are clickable — no navigation logic is wired.

**Planned navigation:**
- Categories → Categories screen (not implemented)
- Analytics → Analytics screen (not implemented)
- Profile → Profile screen (not implemented)
- Edit/Delete from Expense Detail → placeholder toasts (not implemented)
- Filter from Expense History → placeholder toast (not implemented)

---

## 5. Current Features

- ✅ **Dashboard UI** — greeting, user name, balance card, recent transactions
- ✅ **Material Design 3 Theme** — full MD3 color scheme with custom palette
- ✅ **Splash Screen** — using AndroidX Core SplashScreen API (v1.0.1)
- ✅ **Add Expense UI** — form with all fields, dropdowns, date picker
- ✅ **Expense Detail UI** — 3-card layout showing full expense info
- ✅ **Expense History UI** — summary card, search bar, filter chips, list, empty state
- ✅ **RecyclerView (Dashboard)** — 5 recent transactions with DiffUtil
- ✅ **RecyclerView (History)** — 12 transactions with DiffUtil
- ✅ **View Binding** — enabled across all activities
- ✅ **Sample Data** — hardcoded sample transactions for UI demonstration
- ✅ **Category-specific icons** — 8 unique vector drawables (food, transport, shopping, bills, entertainment, health, education, other)
- ✅ **Form validation placeholders** — error text views toggle on save (no real validation logic)
- ✅ **MaterialDatePicker** — integrated in Add Expense screen
- ✅ **Color state selectors** — bottom nav tint, text field hint/stroke colors
- ✅ **Dashboard → Detail navigation** — transaction item tap opens detail with data
- ✅ **Dashboard → History navigation** — "View All" link opens full history

---

## 6. Planned Features

- 🔲 **Room Database** — persistence layer (not implemented)
- 🔲 **CRUD Operations** — create, read, update, delete expenses (not implemented)
- 🔲 **Categories Screen** — manage expense categories (not implemented)
- 🔲 **Analytics Screen** — charts/graphs for spending (not implemented)
- 🔲 **Profile Screen** — user settings (not implemented)
- 🔲 **Search** — filter expenses by text (UI only, no logic)
- 🔲 **Filtering** — filter by category chips (UI only, no logic)
- 🔲 **Income Tracking** — differentiate income vs expense (isExpense field exists but no UI to add income)
- 🔲 **Edit Expense** — update an existing expense
- 🔲 **Delete Expense** — remove an expense (with confirmation)
- 🔲 **Bottom Navigation** — wire up Categories, Analytics, Profile tabs
- 🔲 **Real-time Greeting** — dynamic greeting based on time of day
- 🔲 **Data Validation** — proper form validation (amount format, required fields, etc.)

---

## 7. Data Models

### Transaction

| Field | Type | Default | Purpose |
|-------|------|---------|---------|
| `title` | `String` | — | Short description (e.g., "Lunch", "Bus Fare") |
| `category` | `String` | — | Category name (e.g., "Food", "Transport") |
| `amount` | `Double` | — | Monetary value |
| `date` | `String` | — | Date string (e.g., "Today", "15 July 2026") |
| `iconResId` | `Int` | — | Drawable resource ID for the category icon |
| `isExpense` | `Boolean` | `true` | Whether it's an expense (true) or income (false) |

**Note:** This is the **only** data model. There is no `id` field yet (needed for Room). The model is a Kotlin `data class` with no database annotations. All data is currently hardcoded sample data.

---

## 8. RecyclerViews

### 8.1 Dashboard Recent Transactions

| Aspect | Details |
|--------|---------|
| **Adapter** | `TransactionAdapter` |
| **ViewHolder** | `TransactionAdapter.ViewHolder` |
| **Item Layout** | `item_transaction.xml` |
| **Data Source** | Hardcoded sample list (5 items) in `MainActivity.getSampleTransactions()` |
| **DiffUtil** | `TransactionAdapter.DiffCallback` — matches on title + date |
| **Click Handler** | Lambda `onItemClick: ((Transaction) -> Unit)?` passed via constructor |
| **Item Decoration** | `DividerItemDecoration` with custom `divider_transaction` drawable |

### 8.2 Expense History List

| Aspect | Details |
|--------|---------|
| **Adapter** | `ExpenseHistoryAdapter` |
| **ViewHolder** | `ExpenseHistoryAdapter.ViewHolder` |
| **Item Layout** | `item_expense_history.xml` |
| **Data Source** | Hardcoded sample list (12 items) in `ExpenseHistoryActivity.getSampleHistoryData()` |
| **DiffUtil** | `ExpenseHistoryAdapter.DiffCallback` — matches on title + date |
| **Click Handler** | Lambda `onItemClick: ((Transaction) -> Unit)?` passed via constructor (currently unused) |
| **Item Decoration** | `DividerItemDecoration` with custom `divider_transaction` drawable |

---

## 9. Resources

### Colors (`colors.xml`)

Well-organized into sections:
- **MD3 Theme Colors** — Primary (deep teal `#0F766E`), Secondary (teal `#14B8A6`), Tertiary (amber `#F59E0B`), surface, background, error, outline
- **Finance Theme Colors** — dedicated `finance_*` color set for the Add Expense screen
- **Card & Balance Colors** — income card, expense card, balance card gradient endpoints
- **Text Colors** — primary, secondary, hint, income (green), expense (red), white
- **Legacy Colors** — black, white (kept for compatibility)

### Typography

Uses Material 3 text appearances (`TextAppearance.Material3.*`) throughout — no custom typeface or font files. Text sizes are defined in `dimens.xml` (body: 16sp, small: 14sp, error: 12sp).

### Themes (`themes.xml`)

- **`Theme.MyApplication`** — extends `Theme.Material3.Light.NoActionBar`, customizes all MD3 color attributes, sets status bar color to balance card start color
- **`Theme.App.Starting`** — extends `Theme.SplashScreen`, splash screen with icon wrapper

### Icons & Drawables

30+ vector drawables in `res/drawable/`:
- **Category icons:** `ic_food`, `ic_transport`, `ic_shopping`, `ic_bills`, `ic_entertainment`, `ic_health`, `ic_education`, `ic_category_outline`
- **Navigation icons:** `ic_home`, `ic_categories`, `ic_analytics`, `ic_profile`
- **Action icons:** `ic_add`, `ic_add_expense`, `ic_arrow_back`, `ic_filter`, `ic_search`, `ic_save`, `ic_edit`, `ic_delete`, `ic_calendar`, `ic_time`, `ic_payment`, `ic_notes`
- **Other:** `ic_launcher_background`, `ic_launcher_foreground`, `ic_logo.png`, `splash_logo_wrapper`, `bg_balance_card` (gradient), `divider_transaction`

**Consistency:** Colors are reused consistently via `@color/` references. The `finance_*` color set mirrors the MD3 colors for the Add/Detail screens but is a separate namespace. Category icons are 24-28dp with consistent styling.

---

## 10. Dependencies

All dependencies are managed via the version catalog (`gradle/libs.versions.toml`):

| Dependency | Version | Purpose |
|------------|---------|---------|
| `androidx.appcompat:appcompat` | 1.7.1 | AppCompat base library |
| `androidx.core:core-ktx` | 1.19.0 | Kotlin extensions for core Android |
| `androidx.core:core-splashscreen` | 1.0.1 | Splash Screen API |
| `com.google.android.material:material` | 1.14.0 | Material Design 3 components |
| `androidx.recyclerview:recyclerview` | 1.4.0 | RecyclerView + ListAdapter |
| `androidx.constraintlayout:constraintlayout` | 2.2.1 | ConstraintLayout |
| `junit:junit` | 4.13.2 | Unit testing (test) |
| `androidx.test.ext:junit` | 1.3.0 | Instrumented test JUnit extension |
| `androidx.test.espresso:espresso-core` | 3.7.0 | UI testing |

**Notable omissions** (not yet added):
- Room Database (no persistence)
- Lifecycle ViewModel / LiveData / Flow (no architecture components)
- Navigation Component (manual Intent-based navigation)
- Any chart/graph library (MPAndroidChart, etc.)

---

## 11. Current UI Style

### Design Language

Material Design 3 (Material You) with a **finance/expense tracking aesthetic**.

### Color Palette
- **Primary:** Deep teal (`#0F766E`) — used for buttons, active bottom nav items, toolbar save icons, date picker icon
- **Secondary:** Teal (`#14B8A6`) — secondary elements
- **Tertiary (Accent):** Warm amber (`#F59E0B`) — highlights
- **Surface:** Light gray (`#F8FAFC`) — background for all screens
- **Cards:** White with 2dp elevation, 16-20dp corner radius, subtle stroke
- **Balance Card:** Gradient from deep teal to darker teal, white text
- **Expense text:** Red (`#B3261E`)
- **Income text:** Green (`#16A34A`)

### Card Style
- Rounded corners (16-24dp), 2dp elevation, white background
- Subtle stroke (`#1A000000`, 0.5dp) on list items
- Content padding of 14-20dp

### Button Style
- **Filled buttons:** Primary color background, white text, 16dp corner radius, 56dp height, icon + text
- **Outlined buttons:** White background, primary/error stroke (1.5dp), 16dp corner radius
- **FAB:** Primary color, white "+" icon, positioned above bottom navigation

### Text Field Style
- Outlined box style (`Widget.Material3.TextInputLayout.OutlinedBox`)
- 16dp corner radius, 1.5dp default stroke, 2dp focused stroke
- Custom color state selectors for hint text and stroke (primary when focused)

### Navigation Style
- **Toolbars:** White background, 2dp elevation, custom back arrow icon, title text
- **Bottom Navigation:** White background, labeled mode, 4 tabs, custom tint selector (primary when checked, hint when unchecked)
- **Overflow menus:** In Expense Detail (Edit, Delete) and Expense History (Filter)

### Overall UI Consistency
High — all screens share the same color palette, card styles, button styles, and spacing conventions (`dimens.xml`). The Add Expense and Expense Detail screens use the `finance_*` color namespace which mirrors the MD3 palette. Formatting is consistent across all XML layouts.

---

## 12. Known Issues / TODOs

1. **No database (Room)** — all data is hardcoded sample data; app loses all state on restart
2. **No ID on Transaction** — data class has no unique identifier, making database integration harder
3. **Form validation is visual only** — error text views toggle visibility but no actual validation logic
4. **Search is cosmetic** — EditText is non-clickable/non-focusable
5. **Filter chips are cosmetic** — no filtering logic is wired
6. **Bottom navigation is cosmetic** — clicking Categories, Analytics, or Profile does nothing
7. **Edit/Delete are placeholder toasts** — no actual functionality
8. **No ViewModel/LiveData** — no architecture components; activities hold all logic
9. **No dependency injection** — no Hilt/Dagger/Koin
10. **Hardcoded greeting** — "Good Morning" is hardcoded regardless of time of day
11. **No theme switching** — light theme only; no dark mode support
12. **Similar add icon drawables** — `ic_add` and `ic_add_expense` both exist in drawable but only `ic_add` is used by the FAB
13. **Divider drawable uses `android-color`** — `divider_transaction.xml` uses `android-color` instead of `android:color` (potential lint warning)
14. **Time field gap** — `ExpenseDetailActivity` populates a `tvInfoTime` view but the `Transaction` data model has no `time` field, nor is time passed via Intent extras
15. **ExpenseHistoryAdapter onClick unused** — the adapter accepts an `onItemClick` callback but `ExpenseHistoryActivity` does not pass one, so tapping history items does nothing
14. **ExpenseHistoryActivity FAB** — the layout includes a FAB reference but it is not wired in the Activity

---

## 13. Next Recommended Tasks

1. **Add Room Database** — create Entity, DAO, and Database classes; add `id: Long` field to Transaction with `@PrimaryKey(autoGenerate = true)`
2. **Replace sample data with database queries** — update all adapters/activities to observe Room data
3. **Add ViewModel + LiveData/Flow** — move data logic out of Activities for proper lifecycle management
4. **Wire Add Expense save** — persist to Room database instead of showing snackbar
5. **Wire Edit/Delete** — implement actual update and delete operations from Expense Detail
6. **Wire Filter chips + Search** — implement query-based filtering
7. **Add dark theme** — create `values-night/themes.xml` with dark color palette
8. **Implement Categories screen** — show category-wise breakdown
9. **Implement Analytics screen** — add charts (consider MPAndroidChart or similar)
10. **Wire bottom navigation** — connect remaining tabs to their Activities
11. **Add proper form validation** — validate amount format, required fields, etc.
12. **Add navigation safeguards** — confirm dialog before discard/deletion

---

## 14. Build Status

| Aspect | Status |
|--------|--------|
| **Build Status** | ✅ Builds successfully (no reported errors) |
| **AGP Version** | 9.2.1 (latest) |
| **Gradle JDK** | Daemon JVM configured in `gradle-daemon-jvm.properties` |
| **Configuration Cache** | Enabled (`org.gradle.configuration-cache=true`) |
| **Known Build Warnings** | None documented (no CI/CD pipeline configured) |
| **Unit Tests** | Placeholder test files only (`ExampleUnitTest.kt`, `ExampleInstrumentedTest.kt`) |
| **CI/CD** | Not configured |

The project uses the latest Android Gradle Plugin (9.2.1) with compileSdk 37 and targets SDK 37. View Binding is enabled. No build variants beyond standard debug/release.
