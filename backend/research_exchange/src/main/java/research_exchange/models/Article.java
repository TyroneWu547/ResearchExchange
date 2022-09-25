package research_exchange.models;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Transient;
import research_exchange.dto.CommenterDTO;

@MappedEntity
public class Article {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int score;

    private int approved;

    @DateCreated
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "EST")
    private Instant datePosted;

    private String articleAbstract;

    private String mainField;

    private String subField;

    @Nullable
    private String pdfUrl;

    @Nullable
    private String repoUrl;

    @Nullable
    private String dataUrl;

    @Transient
    List<CommenterDTO> authors;

    @Transient
    List<String> expertReviews;

    @Nullable
    @Transient
    List<String> tags;

    @Nullable
    @Transient
    List<String> links;

    @Creator
    public Article(String name, String articleAbstract, String mainField, String subField, @Nullable String pdfUrl,
            @Nullable String repoUrl, @Nullable String dataUrl) {
        this.name = name;
        this.articleAbstract = articleAbstract;
        this.mainField = mainField;
        this.subField = subField;
        this.pdfUrl = pdfUrl;
        this.repoUrl = repoUrl;
        this.dataUrl = dataUrl;
    }

    public Article(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public Instant getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Instant datePosted) {
        this.datePosted = datePosted;
    }

    public String getArticleAbstract() {
        return articleAbstract;
    }

    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    public String getMainField() {
        return mainField;
    }

    public void setMainfield(String mainField) {
        this.mainField = mainField;
    }

    public String getSubField() {
        return subField;
    }

    public void setSubfield(String subField) {
        this.subField = subField;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public List<CommenterDTO> getAuthors() {
        return this.authors;
    }

    public void setAuthors(List<CommenterDTO> authors) {
        this.authors = authors;
    }

    public List<String> getExpertReviews() {
        return this.expertReviews;
    }

    public void setExpertReviews(List<String> expertReviews) {
        this.expertReviews = expertReviews;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getLinks() {
        return this.links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

}
