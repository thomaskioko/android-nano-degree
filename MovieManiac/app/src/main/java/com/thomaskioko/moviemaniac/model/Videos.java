package com.thomaskioko.moviemaniac.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Kioko
 */
public class Videos {

    private Integer id;
    private List<VideoResults> results = new ArrayList<>();

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The results
     */
    public List<VideoResults> getVideoResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setVideoResults(List<VideoResults> results) {
        this.results = results;
    }
}
