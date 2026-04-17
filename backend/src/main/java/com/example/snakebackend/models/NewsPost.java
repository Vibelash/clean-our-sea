package com.example.snakebackend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * A short news / facts article shown on the home page feed and the
 * dedicated News tab. These are not user-generated — they are seeded
 * on startup with SDG 14 content about ocean plastic, marine species,
 * and sustainability progress. Admins (or a future endpoint) can add
 * more at runtime.
 */
@Entity
public class NewsPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Short headline shown on cards and list items. */
    @Column(length = 200, nullable = false)
    private String title;

    /** 1–2 sentence teaser shown in preview cards. */
    @Column(length = 400, nullable = false)
    private String summary;

    /** Full article body for the News tab. Plain text / soft line breaks. */
    @Column(length = 4000, nullable = false)
    private String body;

    /** Tag: "plastic", "species", "progress", "science", "action". */
    @Column(length = 40, nullable = false)
    private String category;

    /** Emoji or short code used as a visual badge on cards. */
    @Column(length = 10)
    private String icon;

    /** Named source e.g. "UNEP" or "NOAA" — for attribution. */
    @Column(length = 120)
    private String source;

    /** Optional URL to the real-world source. */
    @Column(length = 400)
    private String sourceUrl;

    /** When the post was added to the feed. */
    @Column(nullable = false)
    private LocalDateTime postedAt;

    public NewsPost() {}

    public NewsPost(String title, String summary, String body,
                    String category, String icon,
                    String source, String sourceUrl,
                    LocalDateTime postedAt) {
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.category = category;
        this.icon = icon;
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.postedAt = postedAt;
    }

    // ---- getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
}
