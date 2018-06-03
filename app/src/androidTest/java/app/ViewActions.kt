package app

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.v7.widget.SearchView
import android.view.View
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

fun typeQuery(query: String): ViewAction = object : ViewAction {
    override fun getDescription(): String {
        return "typing query $query"
    }

    override fun getConstraints(): Matcher<View> {
        return allOf(isAssignableFrom(SearchView::class.java), isDisplayed())
    }

    override fun perform(uiController: UiController?, view: View?) {
        val searchView = view as? SearchView ?: throw IllegalAccessException("Not SearchView")
        searchView.setQuery(query, false)
    }
}