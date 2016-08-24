package com.udacity.gradle.builditbigger;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Class to test {@link MainActivity} functionality
 *
 * @author Thomas Kioko
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void buttonShouldFetchString() {
        onView(withText(R.string.button_text)).perform(click());
        onView(withId(R.id.tv_joke)).check(matches(withText(new StringRegexMatcher())));
    }

    class StringRegexMatcher extends BaseMatcher<String> {

        @Override
        public boolean matches(Object item) {
            return ((String) item).matches("^(?=\\s*\\S).*$");
        }

        @Override
        public void describeTo(Description description) {

        }
    }

}
