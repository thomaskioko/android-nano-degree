package com.thomaskioko.moviemaniac.interfaces;

import com.thomaskioko.moviemaniac.model.Result;

import java.util.ArrayList;

/**
 * Movie callback interface methods
 *
 * @author Thomad kioko
 */
public interface MovieCallback {

    /**
     * @param requestType Request Type.
     * @param bundleData  Data passed through a  {@link android.os.Bundle}
     */
    void CallbackRequest(String requestType, String bundleData);

    /**
     * @param requestType     Movie Request type
     * @param resultArrayList {@link Result} A list of movie objects
     */
    void CallbackRequest(String requestType, ArrayList<Result> resultArrayList);
}



