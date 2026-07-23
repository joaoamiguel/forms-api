package com.example.iaassessment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "scores")
public class ScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private SubmissionEntity submission;

    private Integer readinessScore;
    private Integer literacyScore;
    private Integer opportunityScore;
    private String quadrant;
    private Integer technicalScore;
    private String technicalBand;
    @Column(columnDefinition = "TEXT")
    private String resultJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubmissionEntity getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionEntity submission) {
        this.submission = submission;
    }

    public Integer getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(Integer readinessScore) {
        this.readinessScore = readinessScore;
    }

    public Integer getLiteracyScore() {
        return literacyScore;
    }

    public void setLiteracyScore(Integer literacyScore) {
        this.literacyScore = literacyScore;
    }

    public Integer getOpportunityScore() {
        return opportunityScore;
    }

    public void setOpportunityScore(Integer opportunityScore) {
        this.opportunityScore = opportunityScore;
    }

    public String getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
    }

    public Integer getTechnicalScore() {
        return technicalScore;
    }

    public void setTechnicalScore(Integer technicalScore) {
        this.technicalScore = technicalScore;
    }

    public String getTechnicalBand() {
        return technicalBand;
    }

    public void setTechnicalBand(String technicalBand) {
        this.technicalBand = technicalBand;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }
}
