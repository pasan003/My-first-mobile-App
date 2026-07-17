# PROJECT_STATUS.md

> **Generated:** July 17, 2026  
> **Last Audit:** July 17, 2026 — Full source audit completed  
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
| **Kotlin Version** | Bundled via AGP 9.2.1 (no explicit Kotlin plugin version in build files) |
| **Build System** | Gradle (Kotlin DSL) with Version Catalog (`libs.versions.toml`) |
| **Material Design Version** | Material 3 (via `com.google.android.material:material:1.14.0`) |
| **View Binding** | Enabled |
| **AGP Version** | 9.2.1 |

---

## 2. Project Structure

```
My-first-mobile-App/
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts               # Module includes, repositories
├── gradle.properties                 # JVM args, config cache, Kotlin code style
├── gradle/
│   ├── libs.versions.toml            # Version catalog (all dependency versions)
│   └── wrapper/                      # Gradle wrapper files
├── app/
│   ├── build.gradle.kts              # Module build config (SDK versions, deps, viewBinding)
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml           # 7 activities declared
│           ├── java/com/firstapp/myapplication/
│           │   ├── MainActivity.kt           # Dashboard screen (home)
│           │   ├── AddExpenseActivity.kt     # Add expense form
│           │   ├── ExpenseDetailActivity.kt  # Single expense detail view
│           │   ├── ExpenseHistoryActivity.kt # Full expense history list
│           │   ├── CategoryManagerActivity.kt # Category management screen
│           │   ├── AnalyticsActivity.kt      # Analytics screen with spending insights
│           │   ├── ProfileSettingsActivity.kt # Profile & Settings screen
│           │   ├── Transaction.kt            # Data model (data class)
│           │   ├── CategoryItem.kt           # Category data model (data class)
│           │   ├── TransactionAdapter.kt     # RecyclerView adapter (dashboard list)
│           │   ├── ExpenseHistoryAdapter.kt  # RecyclerView adapter (history list)
│           │   └── CategoryAdapter.kt        # RecyclerView adapter (category list)
│           ├── res/
│           │   ├── color/                    # Color state selectors (bottom nav, text fields)
│           │   ├── drawable/                 # 38 drawable resources (see Section 11)
│           │   ├── layout/                   # 15 layout/XML files (see Section 9)
│           │   ├── menu/                     # 7 menu XML files (see Section 8)
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
| `java/.../myapplication/` | All Kotlin source files (7 activities, 2 data models, 3 adapters) |
| `res/layout/` | XML layout files for screens, list items, dialogs, and empty states |
| `res/drawable/` | Vector drawables (category icons, UI icons, dividers, splash logo) |
| `res/color/` | Color state list selectors (e.g., checked/unchecked bottom nav tint) |
| `res/menu/` | Toolbar menus, bottom navigation, and overflow menu definitions |
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
- **Bottom navigation wired**: Home stays active, Categories opens `CategoryManagerActivity`, Analytics opens `AnalyticsActivity`, Profile shows placeholder toast

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
- FAB (UI only — not wired in code)

### 3.5 Category Manager

| Field | Value |
|-------|-------|
| **Screen Name** | Category Manager |
| **Purpose** | Displays and manages expense categories (add, edit, delete) with UI dialogs ready for future database integration |
| **Layout File** | `activity_category_manager.xml` |
| **Activity** | `CategoryManagerActivity` |
| **Current Status** | **Completed** (UI with sample data; add/edit/delete dialogs are visual only; FAB not wired) |

Features on this screen:
- **MaterialToolbar** with back arrow and more options overflow icon (placeholder toast)
- **Summary card**: Label ("Categories"), description ("Total categories"), count badge (8) with `primary_container` background
- **Search bar card**: Visual only — search icon + EditText (non-clickable/non-focusable)
- **RecyclerView** with 8 sample categories, each displaying:
  - Left color indicator (4dp wide vertical bar)
  - Category icon in a rounded card with tinted background
  - Category name (bold)
  - Expense count with plural support (e.g., "42 Expenses", "1 Expense")
  - Edit icon on the right (shows placeholder toast on tap)
- **Empty state layout** (hidden by default, `visibility="gone"`):
  - Rounded icon container with category outline icon
  - Title: "No Categories Found"
  - Description: "Create your first category to organize your expenses."
  - "Add Category" button (UI only)
- **Floating Action Button** (FAB) — positioned bottom-end, uses `ic_add` icon, primary background tint (not wired in code)

**Planned enhancements (dialogs exist but not launched):**
- **Add/Edit dialog** (`dialog_add_edit_category.xml`): Category name input, icon dropdown (`ExposedDropdownMenu` with `AppCompatAutoCompleteTextView`), horizontal color picker with 8 color circles (primary, secondary, tertiary, primary_container, secondary_container, error_container, income green, surface_variant), Cancel + Save buttons
- **Delete dialog** (`dialog_delete_category.xml`): Warning icon in error container, title, message explaining expenses won't be removed, Cancel + Delete buttons (delete button has error background and delete icon)

### 3.6 Analytics

| Field | Value |
|-------|-------|
| **Screen Name** | Analytics |
| **Purpose** | Displays spending insights with financial summary cards, category breakdown, monthly trends, and top categories ranking |
| **Layout File** | `activity_analytics.xml` |
| **Activity** | `AnalyticsActivity` |
| **Current Status** | **Completed** (UI with sample data; charts are visual placeholders for future real chart library) |

Features on this screen:
- MaterialToolbar with back arrow and calendar icon (placeholder toast)
- Horizontal period selector chips (This Week, This Month, This Year — UI only)
- Three financial summary cards (Total Expenses, Total Transactions, Average Expense)
- Spending by Category section with pie chart placeholder (FrameLayout container) and 5-item legend (Food, Transport, Shopping, Bills, Entertainment) with colored dots and amounts
- Monthly Spending section with bar chart placeholder (FrameLayout container) and 6 month labels (Jan-Jun)
- Top Categories ranking card (🥇 Food, 🥈 Bills, 🥉 Shopping, 4️⃣ Transport, 5️⃣ Entertainment) with emoji rankings
- Insights card with 4 bullet-point insights (highest category, avg daily, highest day, largest expense)
- Empty state layout (hidden by default — icon, title, description, "Add Expense" button with placeholder toast)

### 3.7 Profile & Settings

| Field | Value |
|-------|-------|
| **Screen Name** | Profile & Settings |
| **Purpose** | Displays user profile information and application settings (financial preferences, app settings, help & support, about, logout) |
| **Layout File** | `activity_profile_settings.xml` |
| **Activity** | `ProfileSettingsActivity` |
| **Current Status** | **Completed** (UI with sample data; all interactive elements show placeholder toasts) |

Features on this screen:
- **MaterialToolbar** with back arrow and more options overflow menu icon (placeholder toast)
- **Profile Header** card with:
  - Circular avatar placeholder (profile icon on `primary_container` background)
  - User name: Pasan
  - Email: pasan@example.com
  - Member since: July 2026
  - Small edit icon (top-right) — placeholder toast
- **Financial Preferences** card with:
  - Preferred Currency: Sri Lankan Rupee (LKR)
  - Monthly Budget: Rs. 75,000
  - Budget Reminder: Material M3 switch (UI only, shows toast on toggle)
- **Application Settings** card with 4 rows (icon + title + description + chevron):
  - Notifications (bell icon) — "Manage your notification preferences"
  - Language (globe icon) — "Select your preferred language"
  - Date Format (calendar icon) — "Choose how dates are displayed"
  - Currency Format (payment icon) — "Customise currency display"
- **Help & Support** card with 4 rows (icon + title + chevron):
  - Help Center, Contact Support, Privacy Policy, Terms & Conditions
- **About SpendWise** card with:
  - Application Version: Version 1.0.0
  - Developer: Student Project
  - Technology: Kotlin + XML + Material Design 3
- **Logout button** card with outlined error-styled button (logout icon, placeholder toast)
- **Reusable row layout** `item_setting_row.xml` used by all 8 setting rows in App Settings and Help & Support
- **New drawables created:** `ic_notifications.xml` (bell), `ic_language.xml` (globe), `ic_logout.xml` (power-off)
- All sections scroll smoothly via `NestedScrollView > ConstraintLayout`
- All interactive elements have descriptive IDs for future data binding

---

## 4. Navigation Flow

```
MainActivity (Dashboard)
    │
    ├──→ AddExpenseActivity          (via FAB click)
    │
    ├──→ ExpenseDetailActivity        (via transaction item tap)
    │
    ├──→ ExpenseHistoryActivity       (via "View All" link)
    │
    ├──→ CategoryManagerActivity      (via bottom navigation "Categories" tab)
    │
    ├──→ AnalyticsActivity            (via bottom navigation "Analytics" tab)
    │
    └──→ ProfileSettingsActivity      (via bottom navigation "Profile" tab)
```

**Implementation details:**
- All navigation uses explicit `Intent` with `startActivity()` — no NavComponent / Jetpack Navigation yet.
- Activity parent relationships are declared in AndroidManifest (parentActivityName=".MainActivity").
- `ExpenseDetailActivity` receives data via Intent extras.
- Add Expense screen has a toolbar save action (menu item) but does not pass data back yet.
- Bottom navigation items are wired:
  - **Home** → stays on current screen (already on dashboard)
  - **Categories** → opens `CategoryManagerActivity` via explicit Intent
  - **Analytics** → opens `AnalyticsActivity` via explicit Intent
  - **Profile** → shows placeholder toast ("Profile — coming soon in a future update")
  - Home tab is preselected on activity start

**Planned navigation:**
- ProfileSettingsActivity created but bottom nav "Profile" tab not yet wired to launch it
- Edit/Delete from Expense Detail → placeholder toasts (not implemented)
- Filter from Expense History → placeholder toast (not implemented)
- ExpenseHistoryAdapter onClick → adapter accepts callback but not passed from activity
- Newly added "Add Expense" from Analytics empty state → placeholder toast (not navigated)

---

## 5. Current Features

- ✅ **Dashboard UI** — greeting, user name, balance card, recent transactions
- ✅ **Material Design 3 Theme** — full MD3 color scheme with custom palette
- ✅ **Splash Screen** — using AndroidX Core SplashScreen API (v1.0.1)
- ✅ **Add Expense UI** — form with all fields, dropdowns, date picker
- ✅ **Expense Detail UI** — 3-card layout showing full expense info
- ✅ **Expense History UI** — summary card, search bar, filter chips, list, empty state, FAB
- ✅ **Category Manager UI** — full screen with summary card, search bar, category list, empty state, FAB
- ✅ **Add/Edit Category Dialog** — name input, icon dropdown, color picker, save/cancel buttons
- ✅ **Delete Category Dialog** — warning icon, explanation, confirm/cancel buttons
- ✅ **Analytics UI** — full screen with summary cards, category breakdown, monthly trends, top rankings, insights, empty state
- ✅ **RecyclerView (Dashboard)** — 5 recent transactions with DiffUtil
- ✅ **RecyclerView (History)** — 12 transactions with DiffUtil
- ✅ **RecyclerView (Categories)** — 8 categories with DiffUtil, color indicators, expense count plurals
- ✅ **View Binding** — enabled across all activities
- ✅ **Sample Data** — hardcoded sample transactions for UI demonstration
- ✅ **Category-specific icons** — 8 unique vector drawables (food, transport, shopping, bills, entertainment, health, education, other)
- ✅ **Form validation placeholders** — error text views toggle on save (no real validation logic)
- ✅ **MaterialDatePicker** — integrated in Add Expense screen
- ✅ **Color state selectors** — bottom nav tint, text field hint/stroke colors
- ✅ **Dashboard → Detail navigation** — transaction item tap opens detail with data
- ✅ **Dashboard → History navigation** — "View All" link opens full history
- ✅ **Dashboard → Category Manager** — bottom navigation "Categories" tab opens Category Manager
- ✅ **Dashboard → Analytics** — bottom navigation "Analytics" tab opens Analytics screen
- ✅ **Profile & Settings UI** — full screen with profile header, financial preferences, app settings, help & support, about section, and logout button
- ✅ **Reusable row layout** — `item_setting_row.xml` with icon, title, optional description, and chevron arrow for consistent settings rows
- ✅ **Drawable icons** — `ic_notifications.xml`, `ic_language.xml`, `ic_logout.xml` for settings and logout
- ✅ **Bottom Navigation** — partially wired (Home + Categories + Analytics functional; Profile shows placeholder)

---

## 6. Planned Features

- 🔲 **Room Database** — persistence layer (not implemented)
- 🔲 **CRUD Operations** — create, read, update, delete expenses (not implemented)
- 🔲 **Wire Profile bottom nav** — ProfileSettingsActivity exists but bottom nav tab still shows placeholder toast
- 🔲 **Search** — filter expenses/categories by text (UI only, no logic)
- 🔲 **Filtering** — filter by category chips (UI only, no logic)
- 🔲 **Income Tracking** — differentiate income vs expense (isExpense field exists but no UI to add income)
- 🔲 **Edit Expense** — update an existing expense
- 🔲 **Delete Expense** — remove an expense (with confirmation)
- 🔲 **FAB wiring** — Category Manager FAB, Expense History FAB, and empty state "Add Category" button are UI only
- 🔲 **Category CRUD** — add/edit/delete category dialogs exist but are not launched or wired
- 🔲 **Category Manager toolbar** — more options menu shows placeholder toast
- 🔲 **Real-time Greeting** — dynamic greeting based on time of day
- 🔲 **Data Validation** — proper form validation (amount format, required fields, etc.)
- 🔲 **Real Charts** — pie chart and bar chart placeholders need a chart library (e.g., MPAndroidChart)
- 🔲 **Period Chip Filtering** — analytics period selector chips have no filtering logic
- 🔲 **ExpenseHistoryAdapter onClick** — adapter accepts callback but not wired from activity
- 🔲 **ExpenseDetail buttons** — Edit and Delete buttons show placeholder toasts, no actual CRUD

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

**Note:** This model has no `id` field yet (needed for Room). All data is currently hardcoded sample data.

### CategoryItem

| Field | Type | Default | Purpose |
|-------|------|---------|---------|
| `id` | `Int` | — | Unique identifier (designed for future Room `@PrimaryKey` with `autoGenerate = true`) |
| `name` | `String` | — | Display name of the category (e.g., "Food", "Transport") |
| `iconResId` | `Int` | — | Drawable resource ID for the category icon |
| `expenseCount` | `Int` | — | Number of expenses in this category (sample data for now) |
| `colorIndicatorResId` | `Int` | — | Color resource ID for the category color indicator (e.g., `primary_container`, `secondary_container`) |

**Note:** Designed for future Room Database integration. The `id` field will serve as the `@PrimaryKey`.

---

## 8. Menu Files (6 total)

| Menu File | Used By | Items |
|-----------|---------|-------|
| `bottom_nav_menu.xml` | MainActivity (Dashboard) | Home, Categories, Analytics, Profile |
| `menu_add_expense.xml` | AddExpenseActivity | Save icon |
| `menu_analytics.xml` | AnalyticsActivity | Calendar icon |
| `menu_category_manager.xml` | CategoryManagerActivity | More options (three-dot) |
| `menu_expense_detail.xml` | ExpenseDetailActivity | Edit, Delete (overflow) |
| `menu_expense_history.xml` | ExpenseHistoryActivity | Filter icon |
| `menu_profile_settings.xml` | ProfileSettingsActivity | More options (three-dot) |

---

## 9. Layout Files (15 total)

| Layout File | Type | Used By |
|-------------|------|---------|
| `activity_main.xml` | Screen | MainActivity (Dashboard) |
| `activity_add_expense.xml` | Screen | AddExpenseActivity |
| `activity_expense_detail.xml` | Screen | ExpenseDetailActivity |
| `activity_expense_history.xml` | Screen | ExpenseHistoryActivity |
| `activity_category_manager.xml` | Screen | CategoryManagerActivity |
| `activity_analytics.xml` | Screen | AnalyticsActivity |
| `activity_profile_settings.xml` | Screen | ProfileSettingsActivity |
| `dialog_add_edit_category.xml` | Dialog | CategoryManagerActivity (not yet launched) |
| `dialog_delete_category.xml` | Dialog | CategoryManagerActivity (not yet launched) |
| `item_transaction.xml` | List Item | TransactionAdapter (Dashboard) |
| `item_expense_history.xml` | List Item | ExpenseHistoryAdapter |
| `item_category.xml` | List Item | CategoryAdapter |
| `item_setting_row.xml` | List Item | ProfileSettingsActivity (reusable row) |
| `layout_empty_categories.xml` | Include | CategoryManagerActivity empty state |
| `splash_logo_wrapper.xml` | Splash | Splash screen icon wrapper |

---

## 10. RecyclerViews

### 10.1 Dashboard Recent Transactions

| Aspect | Details |
|--------|---------|
| **Adapter** | `TransactionAdapter` |
| **ViewHolder** | `TransactionAdapter.ViewHolder` |
| **Item Layout** | `item_transaction.xml` |
| **Data Source** | Hardcoded sample list (5 items) in `MainActivity.getSampleTransactions()` |
| **DiffUtil** | `TransactionAdapter.DiffCallback` — matches on title + date |
| **Click Handler** | Lambda `onItemClick: ((Transaction) -> Unit)?` passed via constructor |
| **Item Decoration** | `DividerItemDecoration` with custom `divider_transaction` drawable |

### 10.2 Expense History List

| Aspect | Details |
|--------|---------|
| **Adapter** | `ExpenseHistoryAdapter` |
| **ViewHolder** | `ExpenseHistoryAdapter.ViewHolder` |
| **Item Layout** | `item_expense_history.xml` |
| **Data Source** | Hardcoded sample list (12 items) in `ExpenseHistoryActivity.getSampleHistoryData()` |
| **DiffUtil** | `ExpenseHistoryAdapter.DiffCallback` — matches on title + date |
| **Click Handler** | Lambda `onItemClick: ((Transaction) -> Unit)?` passed via constructor (currently unused — not passed from activity) |
| **Item Decoration** | `DividerItemDecoration` with custom `divider_transaction` drawable |

### 10.3 Category Manager List

| Aspect | Details |
|--------|---------|
| **Adapter** | `CategoryAdapter` |
| **ViewHolder** | `CategoryAdapter.ViewHolder` |
| **Item Layout** | `item_category.xml` |
| **Data Source** | Hardcoded sample list (8 items) in `CategoryManagerActivity.getSampleCategories()` |
| **DiffUtil** | `CategoryAdapter.DiffCallback` — matches on `id` for item identity, full data class equality for content comparison |
| **Click Handler** | Lambda `onEditClick: ((CategoryItem) -> Unit)?` passed via constructor (currently shows placeholder toast) |
| **Item Decoration** | None (items have built-in `layout_marginBottom` for spacing) |

---

## 11. Drawable Resources (38 total)

**Category icons (8):** `ic_food`, `ic_transport`, `ic_shopping`, `ic_bills`, `ic_entertainment`, `ic_health`, `ic_education`, `ic_category_outline`  
**Navigation icons (4):** `ic_home`, `ic_categories`, `ic_analytics`, `ic_profile`  
**Action icons (13):** `ic_add`, `ic_add_expense`, `ic_arrow_back`, `ic_filter`, `ic_search`, `ic_save`, `ic_edit`, `ic_delete`, `ic_calendar`, `ic_time`, `ic_payment`, `ic_notes`, `ic_more_vert`  
**Settings icons (3, new):** `ic_notifications` (bell), `ic_language` (globe), `ic_logout` (power-off)  
**Backgrounds & Dividers (4):** `bg_balance_card` (gradient), `divider_transaction`, `ic_launcher_background`, `ic_launcher_foreground`  
**Legend dots (4):** `bg_legend_dot_food`, `bg_legend_dot_transport`, `bg_legend_dot_shopping`, `bg_legend_dot_bills`  
**Splash (1):** `splash_logo_wrapper`  
**Logo (1):** `ic_logo.png` (raster)

**Consistency:** Colors are reused consistently via `@color/` references. The `finance_*` color set mirrors the MD3 colors but is a separate namespace. Category icons are 24-28dp with consistent styling. The `ic_more_vert` icon uses `@color/finance_text_primary` tint. New settings icons (`ic_notifications`, `ic_language`, `ic_logout`) follow the same 24dp viewport, white fill, and programmatic tint pattern.

---

## 12. Dependencies

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
- Kotlin Coroutines / RxJava

---

## 13. Current UI Style

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
- **Filled buttons:** Primary color background, white text, 16dp corner radius, 56dp height (standard), 48dp (dialog), icon + text
- **Outlined buttons:** White background, primary/error stroke (1.5dp), 16dp corner radius
- **FAB:** Primary color, white "+" icon, positioned above bottom navigation or bottom-end

### Text Field Style
- Outlined box style (`Widget.Material3.TextInputLayout.OutlinedBox`)
- 16dp corner radius, 1.5dp default stroke, 2dp focused stroke
- Custom color state selectors for hint text and stroke (primary when focused)

### Navigation Style
- **Toolbars:** White background, 2dp elevation, custom back arrow icon, title text
- **Bottom Navigation:** White background, labeled mode, 4 tabs, custom tint selector (primary when checked, hint when unchecked)
- **Overflow menus:** In Expense Detail (Edit, Delete), Expense History (Filter), and Category Manager (More Options)

### Overall UI Consistency
High — all 7 screens share the same color palette, card styles, button styles, and spacing conventions (`dimens.xml`). The Category Manager uses the same `finance_*` color namespace as the Add Expense, Expense Detail, Analytics, and Profile & Settings screens. Formatting is consistent across all XML layouts.

---

## 14. Known Issues / TODOs

1. **No database (Room)** — all data is hardcoded sample data; app loses all state on restart
2. **No ID on Transaction** — data class has no unique identifier, making database integration harder
3. **Form validation is visual only** — error text views toggle visibility but no actual validation logic
4. **Search is cosmetic** — EditText is non-clickable/non-focusable (both categories and history)
5. **Filter chips are cosmetic** — no filtering logic is wired
6. **Edit/Delete are placeholder toasts** — no actual functionality (both toolbar menu and action card buttons)
7. **No ViewModel/LiveData** — no architecture components; activities hold all logic
8. **No dependency injection** — no Hilt/Dagger/Koin
9. **Hardcoded greeting** — "Good Morning" is hardcoded regardless of time of day
10. **No theme switching** — light theme only; no dark mode support
11. **Similar add icon drawables** — `ic_add` and `ic_add_expense` both exist but only `ic_add` is used
12. **Divider drawable uses `android-color`** — `divider_transaction.xml` uses `android-color` instead of `android:color` (potential lint warning)
13. **Time field gap** — `ExpenseDetailActivity` populates a `tvInfoTime` view but the `Transaction` data model has no `time` field
14. **ExpenseHistoryAdapter onClick unused** — the adapter accepts an `onItemClick` callback but `ExpenseHistoryActivity` does not pass one
15. **ExpenseHistoryActivity FAB** — the layout includes a FAB reference but it is not wired in the Activity
16. **Category Manager FAB not wired** — FAB is UI only; does not launch the add dialog
17. **Category Manager toolbar overflow** — "More options" shows a placeholder toast, no real menu
18. **Category dialogs not launched** — `dialog_add_edit_category.xml` and `dialog_delete_category.xml` are designed but never shown via DialogFragment or AlertDialog
19. **Category edit icon** — tapping edit on a category item shows a placeholder toast instead of the edit dialog
20. **Analytics charts are placeholders** — pie chart and bar chart use FrameLayout containers with text placeholders, need real chart library
21. **Analytics period chips cosmetic** — This Week / This Month / This Year chips have no filtering logic
22. **Analytics empty state button** — "Add Expense" button shows placeholder toast, does not navigate to AddExpenseActivity
23. **Profile placeholder** — Bottom navigation Profile tab shows "coming soon" toast; ProfileSettingsActivity exists but is not wired to the tab
24. **Profile & Settings all placeholder** — edit profile, preferences, settings rows, help links, and logout all show placeholder toasts; no actual functionality
25. **Kotlin coroutines/flow** — no dependency declared for async operations

---

## 15. Next Recommended Tasks

1. **Wire Profile bottom nav tab** — launch `ProfileSettingsActivity` from MainActivity bottom navigation "Profile" tab instead of showing placeholder toast
2. **Wire Category Manager dialogs** — launch `dialog_add_edit_category` and `dialog_delete_category` from FAB and edit icon clicks using `DialogFragment` or `AlertDialog`
3. **Add Room Database** — create Entity, DAO, and Database classes; add `id: Long` field to Transaction with `@PrimaryKey(autoGenerate = true)`
4. **Replace sample data with database queries** — update all adapters/activities to observe Room data
5. **Add ViewModel + LiveData/Flow** — move data logic out of Activities for proper lifecycle management
6. **Wire Add Expense save** — persist to Room database instead of showing snackbar
7. **Wire Edit/Delete** — implement actual update and delete operations from Expense Detail
8. **Wire Filter chips + Search** — implement query-based filtering for both expenses and categories
9. **Wire Analytics empty state** — navigate to AddExpenseActivity from Analytics empty state button
10. **Add dark theme** — create `values-night/themes.xml` with dark color palette
11. **Implement real charts** — integrate MPAndroidChart or similar for pie and bar charts
12. **Add proper form validation** — validate amount format, required fields, etc.
13. **Add navigation safeguards** — confirm dialog before discard/deletion
14. **Add dynamic greeting** — update "Good Morning" based on time of day

---

## 16. Build Status

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
