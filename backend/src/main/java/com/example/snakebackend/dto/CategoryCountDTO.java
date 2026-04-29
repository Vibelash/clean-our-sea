package com.example.snakebackend.dto;

/**
 * One row of the GET /news/categories response — the news feed grouped
 * by tag, with how many posts are in each group. Drives the count
 * pills next to the filter chips on news.html.
 */
public class CategoryCountDTO {

    private String category;
    private long count;

    public CategoryCountDTO() {}

    public CategoryCountDTO(String category, long count) {
        this.category = category;
        this.count = count;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
