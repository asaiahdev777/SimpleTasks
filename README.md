SimpleTasks is a barebones todo-list app. It uses Room and RecyclerView under the hood to provide a simple, yet functional user experience. SimpleTasks allows you to separate your tasks into multiple sheets and categorize tasks within a sheet.

Key Android SDK and AndroidX API usages:
- Use of Room ORM and SQLite 3 to handle data persistence
- Use of ViewModels and LiveData for an MVC app architecture
- Use of built-in night mode supports
- Use of Toolbar, RecyclerView et all (LayoutManager, Adapter, ViewHolder, DiffUtil, DiffUtil.Callback, ItemTouchHelper, etc)
- Use of Activities and Fragments, Context, LayoutInflater
- Use of Kotlin Android Extensions
- Use of custom style attributes, Spans, SpannableStrings, TextWatcher, Menus
- Use of ConstraintLayiut

Key Kotlin API and language feature usages:
- Use of Kotlin's null safety paradigms
- Use of data classes, enum classes, interfaces, sealed classes, inheritance, polymorphism
- Use of lazy-initialization, lateinit, visibility modifiers, custom getters/setter, extension functions
- Use of annotations, coroutines, lambdas

Other API uses:
- Use of the GreenBot Event Bus

Upcoming in next release:
- Complete changeover to Jetpack Compose
- More advanced insertion features
- Styled text support