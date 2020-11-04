package com.statuscasellc.statuscase.ui.util.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color.red
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.statuscasellc.statuscase.model.coroutine.onMain
import com.statuscasellc.statuscase.ui.util.activity.toastError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import velord.university.R

fun View.gone() { this.visibility = View.GONE }

fun View.visible() { this.visibility = View.VISIBLE }

fun View.deactivate() = this.setOnClickListener {  }

inline fun View.doOnGlobalLayout(crossinline action: (view: View) -> Unit) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @SuppressLint("ObsoleteSdkInt")
        @Suppress("DEPRECATION")
        override fun onGlobalLayout() {
            action(this@doOnGlobalLayout)
            when {
                vto.isAlive -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        vto.removeOnGlobalLayoutListener(this)
                    } else {
                        vto.removeGlobalOnLayoutListener(this)
                    }
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                }
            }
        }
    })
}

inline fun Context.sendThenGoBack(view: Pair<ProgressBar, TextView>,
                                  scopeGoBack: CoroutineScope,
                                  crossinline viewModelSend: suspend () -> Pair<String,Int>,
                                  textAnswer: Int,
                                  crossinline callbacksGoBack: () -> Unit,
                                  timer: Int = 5, ) {
    view.first.visibility = View.VISIBLE
    scopeGoBack.launch {
        //invoke function
        val (newFeedbackMessage, newColor) =
            try { viewModelSend() }
            catch (e: Exception) {
                e.message to R.color.redA700
            }
        //change view
        onMain {
            view.first.visibility = View.GONE
            view.second.setTextColor(
                ContextCompat.getColor(this@sendThenGoBack, newColor)
            )
            view.second.text = newFeedbackMessage
        }
        //if success no need wait to long
        var successTimer = 0
        if (newFeedbackMessage == "Report send success") successTimer = 4
        //launch timer
        var timer = timer - successTimer
        repeat(timer) {
            val newText = this@sendThenGoBack.getString(
                textAnswer,
                newFeedbackMessage, timer.toString()
            )
            onMain {
                view.second.apply { text = newText }
            }
            --timer
            delay(1000)
        }
        //go back
        onMain {
            callbacksGoBack()
        }
    }
}

suspend inline fun SwipeRefreshLayout.between(activity: Activity,
                                              tag: String,
                                              f: suspend () -> Unit) {
    onMain {
        this.isRefreshing = true
    }
    try { f() }
    catch (e: Exception) {
        Log.d(tag, e.message.toString())
        onMain {
            activity.toastError(e.message.toString())
        }
    }
    onMain {
        this.isRefreshing = false
    }
}

suspend inline fun <T> ProgressBar.between(activity: Activity,
                                       tag: String,
                                       f: suspend () -> T): T? {
    var entity: T? = null
    onMain {
        this.visibility = View.VISIBLE
    }
    try { entity = f() }
    catch (e: Exception) {
        Log.d(tag, e.message.toString())
        onMain {
            activity.toastError(e.message.toString())
        }
    }
    onMain {
        this.visibility = View.GONE
    }

    return entity
}