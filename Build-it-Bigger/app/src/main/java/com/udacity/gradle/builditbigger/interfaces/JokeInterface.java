package com.udacity.gradle.builditbigger.interfaces;

/**
 * @author Thomas Kioko
 */
public interface JokeInterface {

    void onJokeFetched(String joke, Exception exception);
}
