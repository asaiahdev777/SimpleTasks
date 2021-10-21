package com.ajt.simpletasks

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.main_activity_layout.*
import java.util.*

//This activity inherits from a base/parent class called AppCompatActivity
class MainActivity : AppCompatActivity() {

    private var oldTasks = mutableListOf<Task>()

    //ViewModel = special class for holding data for a UI controller (fragment, activity, etc)
    //"By lazy" means to initialize the variable once only when it is first accessed
    private val taskViewModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(TaskViewModel::class.java) }

    //1 source of truth. This adapter uses a LayoutInflater to create the rows and reads the Tasks objects directly from the TaskViewModel
    private val adapter by lazy { TaskAdapter(layoutInflater, taskViewModel) }

    //Called when Activity is 1st created
    override fun onCreate(savedInstanceState: Bundle?) {
        //Call the super-classes onCreate method
        super.onCreate(savedInstanceState)
        //Set the layout file
        setContentView(R.layout.main_activity_layout)
        //Remove system bars
        makeFullscreen()
        //Set up the ViewModel code
        setupViewModel()
        //Set up the UI widgets (add listeners, gesturedetector, etc)
        configureWidgets()
    }

    private fun setupViewModel() {
        taskViewModel.forceShowNewSheetLiveData.observe(this, Observer { showNewSheetDialog(false) })
        //Once the current sheet loads for the first time...
        taskViewModel.currentSheetLiveData.observe(this, object : Observer<Sheet> {
            override fun onChanged(value: Sheet?) {
                //Load the tasks adapter
                tasksView.adapter = adapter

                //Remove this specific observer (only wanted this to load adapter once the sheets were available)
                taskViewModel.currentSheetLiveData.removeObserver(this)

                //Create a new observer that will get the tasks (from once the current sheet changes
                taskViewModel.currentSheetLiveData.observe(this@MainActivity, Observer { taskViewModel.getTasks() })

                //Once the tasks change, update the
                taskViewModel.tasksLiveData.observe(this@MainActivity, Observer {
                    adapter.update(oldTasks, it)
                    oldTasks = it.toMutableList()
                    val sheetName = taskViewModel.currentSheetName
                    countView.text = getString(R.string.currentSheetNTodos, sheetName, it.size)
                })
            }
        })

        //When the available sheets change, update the sheets shown when the countView button is pressed
        taskViewModel.availableSheets.observe(this@MainActivity, Observer { sheets ->
            //onClickMenu = extension function (my own) that shows a menu on click
            countView.onClickMenu {
                add(R.string.newString, group = 1) { showNewSheetDialog(true) }
                add(R.string.delete, group = 1) { showDeleteSheetDialog() }
                sheets.forEach { sheet ->
                    add(sheet.name) {
                        currentFocus?.hideKB()
                        taskViewModel.currentSheetLiveData.value = sheet
                    }
                }
            }
        })
    }

    private fun configureWidgets() {
        newButton.setOnClickListener { taskViewModel.addTask() }
        //Create a GestureDetector
        val swipeDownDetector = object : GesturesDetectingTouchListener(this) {

            //Override what is done when a down swipe is detected
            override fun onSwipeDown() {
                //currentFocus?.hideKB()
            }

            //Override what is done when a double tap is detected
            override fun onDoubleTap() = taskViewModel.addTask()
        }

        leftSwipeView.setOnTouchListener(swipeDownDetector)
        rightSwipeView.setOnTouchListener(swipeDownDetector)

        //Add a global listener for touches on the recyclerview
        /*tasksView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                swipeDownDetector.onTouch(null, e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
        })*/
        //Add an ItemDecoration (a row divider)
        tasksView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        /*//When adapter is updated, don't try to refocus previously focused views
        tasksView.preserveFocusAfterLayout = false*/
    }

    private fun makeFullscreen() {
        //Run the code between { } on the window
        with(window) {
            //If the app runs a device running Android Pie (when notches were introduced), force the app to extend into the notch aree
            if (Build.VERSION.SDK_INT >= 28) attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            //Make the window fullscreen
            addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //Set the system bar visibility
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    //Used to make Lint (a code analyzer) to stop complaining about the specified issue
    @SuppressLint("InflateParams")
    private fun showNewSheetDialog(cancelable: Boolean) {

        //Create an EditText
        val view = EditText(this).apply {
            setHint(R.string.sheetName)
            setSingleLine()
            inputType = EditorInfo.TYPE_CLASS_TEXT
            //Force show the KB (my function)
            forceShowKB()
        }

        //Create an AlertDialog builder and run methods on that objects
        AlertDialog.Builder(this).apply {
            setCancelable(cancelable)
            //Set the dialog content view
            setView(view)
            //Set the positive button to Save and create a new sheet if the name entered is not blank
            setPositiveButton(R.string.save) { _, _ ->
                val sheet = "${view.text}"
                if (sheet.isNotBlank()) taskViewModel.addSheet(sheet)
            }
        }.show() //Show the dialog
    }

    private fun showDeleteSheetDialog() {
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setTitle(getString(R.string.deleteCurrentSheet, taskViewModel.currentSheetName))
            setPositiveButton(R.string.delete) { _, _ -> taskViewModel.deleteSheet(taskViewModel.currentSheetLiveData.value!!) }
        }.show()
    }

}