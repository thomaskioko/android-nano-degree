package com.thomaskioko.moviemaniac.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Kioko
 */
public class Reviews {

    private Integer id;
    private Integer page;
    @SerializedName(value = "results")
    private List<ReviewResults> reviewResultsList = new ArrayList<>();
    @SerializedName(value = "total_pages")
    private Integer totalPages;
    @SerializedName(value = "total_results")
    private Integer totalResults;

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
     * @return The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return The reviewResultsList
     */
    public List<ReviewResults> getReviewResultsList() {
        return reviewResultsList;
    }

    /**
     * @param reviewResultsList The reviewResultsList
     */
    public void setReviewResultsList(List<ReviewResults> reviewResultsList) {
        this.reviewResultsList = reviewResultsList;
    }

    /**
     * @return The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     * @param totalResults The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

}
