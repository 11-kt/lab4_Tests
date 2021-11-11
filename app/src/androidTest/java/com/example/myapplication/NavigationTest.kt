package com.example.myapplication

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    private val listOfBut = mutableListOf(
                                            R.id.bnToSecond,
                                            R.id.bnToFirst,
                                            R.id.bnToThird,
                                            R.id.bnToFirst,
                                            R.id.bnToSecond
                                          )

    private val listOfFrag = mutableListOf(
                                            R.id.fragment1,
                                            R.id.fragment2,
                                            R.id.fragment3,
                                          )

    private fun openAboutAndPressBack(i: Int) {

        openAbout()
        onView(withId(R.id.activity_about)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(listOfFrag[i])).check(matches(isDisplayed()))

    }

    private fun fromTo(from: Int, to: Int) {

        if (from == 1 && to == 2) onView(withId(listOfBut[0])).perform(click())

        if (from == 1 && to == 3) {
            onView(withId(listOfBut[0])).perform(click())
            onView(withId(listOfBut[2])).perform(click())
        }

        if (from == 2 && to == 1) onView(withId(listOfBut[1])).perform(click())

        if (from == 2 && to == 3) onView(withId(listOfBut[2])).perform(click())

        if (from == 3 && to == 1) onView(withId(listOfBut[3])).perform(click())

        if (from == 3 && to == 2) onView(withId(listOfBut[4])).perform(click())

    }

    @Test
    fun testButtonNav() {
        launchActivity<MainActivity>()
        // Проверяем, что находимся в первом фр->переходим в About->возвращаемся->проверяем фр
        onView(withId(listOfFrag[0])).check(matches(isDisplayed()))
        openAboutAndPressBack(0)

        // Нажимаем на кнопку bnToSecond
        // Проверяем, что находимся в фр2->переходим в About->возвращаемся->проверяем фр2
        fromTo(1, 2)
        onView(withId(listOfFrag[1])).check(matches(isDisplayed()))
        openAboutAndPressBack(1)

        // Нажимаем на кнопку bnToThird
        // Проверяем, что находимся в фр3->переходим в About->возвращаемся->проверяем фр3
        fromTo(2, 3)
        onView(withId(listOfFrag[2])).check(matches(isDisplayed()))
        openAboutAndPressBack(2)

        // Нажимаем на кнопку bnToFirst->Проверяем, что находимся в фр1
        fromTo(3, 1)
        onView(withId(listOfFrag[0])).check(matches(isDisplayed()))

        // Нажимаем на кнопку bnToSecond->bnToThird->Проверяем, что находимся в фр3
        fromTo(1, 3)
        onView(withId(listOfFrag[2])).check(matches(isDisplayed()))

        // Нажимаем на кнопку bnToSecond-->Проверяем, что находимся в фр2
        fromTo(3, 2)
        onView(withId(listOfFrag[1])).check(matches(isDisplayed()))

        // Нажимаем на кнопку bnToFirst-->Проверяем, что находимся в фр1
        fromTo(2, 1)
        onView(withId(listOfFrag[0])).check(matches(isDisplayed()))

    }

    @Test
    fun testStackState() {

        var act = launchActivity<MainActivity>()

        // Переходим в About, backStack.size = 2
        openAbout()
        repeat(2) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр2, backStack.size = 2
        act = launchActivity<MainActivity>()
        fromTo(1, 2)
        repeat(2) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр2->about, backStack.size = 3
        act = launchActivity()
        fromTo(1, 2)
        openAbout()
        repeat(3) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр3, backStack.size = 3
        act = launchActivity<MainActivity>()
        fromTo(1, 3)
        repeat(3) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр3->about, backStack.size = 4
        act = launchActivity()
        fromTo(1, 3)
        openAbout()
        repeat(4) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр3->фр2, backStack.size = 2
        act = launchActivity()
        fromTo(1, 3)
        fromTo(3, 2)
        repeat(2) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр3->фр2->about, backStack.size = 3
        act = launchActivity()
        fromTo(1, 3)
        fromTo(3, 2)
        openAbout()
        repeat(3) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр3->фр2->фр1, backStack.size = 1
        act = launchActivity()
        fromTo(1, 3)
        fromTo(3, 1)
        repeat(1) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

        // Переходим в фр3->фр2->фр1->about, backStack.size = 2
        act = launchActivity()
        fromTo(1, 3)
        fromTo(3, 1)
        openAbout()
        repeat(2) {
            pressBackUnconditionally()
        }
        assertEquals(act.state, Lifecycle.State.DESTROYED)

    }

    @Test
    fun testRecreate() {
        val act = launchActivity<MainActivity>()

        // Проверка фр1, вложеннх вью: 1
        act.recreate()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(listOfFrag[0])).check(matches(isDisplayed()))
        onView(withId(listOfFrag[0])).check(matches(hasChildCount(1)))
        onView(withId(listOfBut[0])).check(matches(isDisplayed()))

        // Проверка фр2, вложеннх вью: 2
        fromTo(1, 2)
        act.recreate()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(listOfFrag[1])).check(matches(isDisplayed()))
        onView(withId(listOfFrag[1])).check(matches(hasChildCount(2)))
        onView(withId(listOfBut[1])).check(matches(isDisplayed()))
        onView(withId(listOfBut[2])).check(matches(isDisplayed()))

        // Проверка фр3, вложеннх вью: 2
        fromTo(2, 3)
        act.recreate()
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()))
        onView(withId(listOfFrag[2])).check(matches(isDisplayed()))
        onView(withId(listOfFrag[2])).check(matches(hasChildCount(2)))
        onView(withId(listOfBut[3])).check(matches(isDisplayed()))
        onView(withId(listOfBut[4])).check(matches(isDisplayed()))

    }

}