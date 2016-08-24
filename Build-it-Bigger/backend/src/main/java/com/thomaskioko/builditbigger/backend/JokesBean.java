package com.thomaskioko.builditbigger.backend;

/**
 * The object model for the data we are sending through endpoints
 *
 * @author Thomas Kioko
 */
public class JokesBean {

    private String joke;

    /**
     * Get the joke
     *
     * @return {@link String}
     */
    public String getData() {
        return joke;
    }

    /**
     * Set the joke
     *
     * @param joke {@link String}
     */
    public void setJoke(String joke) {
        this.joke = joke;
    }

}