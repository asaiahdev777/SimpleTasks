package com.ajt.simpletasks

import android.content.Context
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEach
import androidx.core.view.postDelayed

//Extension functions copied from RapidLearning

val View.inputMethodManager get() = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun View.showKB() {
    requestFocus()
    postDelayed(16) { inputMethodManager.showSoftInput(this, 0) }
}

//Universal function for hiding keyboard
fun View.hideKB() {
    clearFocus()
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.forceShowKB(onVisible: (() -> Unit)? = null) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            onVisible?.invoke()
            postDelayed(25L) { showKB() }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    })
    showKB()
}

//Used to load some text into an EditText and position the cursor at the end of its contents
fun TextView.loadText(s: Any) {
    text = if (s !is CharSequence) s.toString() else s
    if (this is EditText) setSelection(text.length, text.length)
    post { scrollTo(0, 0) }
}

fun View.onClickMenu(
    overrideAnchor: View? = null,
    useContext: Context? = null,
    onDismiss: PopupMenu.OnDismissListener? = null,
    onClick: Menu.(Menu) -> Unit
) {
    setOnClickListener {
        PopupMenu(useContext ?: context, overrideAnchor
            ?: this).apply {
            setOnDismissListener(onDismiss)
            MenuCompat.setGroupDividerEnabled(menu, true)
            menu.onClick(menu)
            menu.clearHeaders()
            show()
        }
    }
}

fun Menu.add(
    title: CharSequence,
    itemId: Int = 0,
    group: Int = 0,
    onClick: (MenuItem) -> Unit
): MenuItem = add(group, itemId, 0, title).apply {
    clearHeaders()
    setOnMenuItemClickListener {
        onClick(this)
        false
    }
}

fun Menu.add(title: Int, itemId: Int = 0, group: Int = 0, onClick: ((MenuItem) -> Unit)? = null): MenuItem =
    add(group, itemId, 0, title).apply {
        clearHeaders()
        setOnMenuItemClickListener {
            onClick?.invoke(this)
            false
        }
    }

fun Menu.clearHeaders() {
    forEach {
        it.subMenu?.apply {
            clearHeader()
            item?.subMenu?.clearHeaders()
        }
    }
}