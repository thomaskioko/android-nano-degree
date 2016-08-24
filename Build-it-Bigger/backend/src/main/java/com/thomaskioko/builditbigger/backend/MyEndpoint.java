package com.thomaskioko.builditbigger.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.thomaskioko.jokeprovider.JokeProvider;

import javax.inject.Named;

/**
 * An endpoint class we are exposing.
 *
 * @author Thomas Kioko
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.builditbigger.thomaskioko.com",
                ownerName = "backend.builditbigger.thomaskioko.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public JokesBean sayHi(@Named("name") String name) {
        JokesBean response = new JokesBean();
        response.setJoke("Hi, " + name);

        return response;
    }

    /**
     * A simple endpoint to return a random joke
     */
    @ApiMethod(name = "getJoke")
    public JokesBean getJoke() {
        JokesBean jokesBean = new JokesBean();
        jokesBean.setJoke(JokeProvider.getRandomJoke());

        return jokesBean;
    }
}
