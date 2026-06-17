package com.eazybytes.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // FK ke tabel users
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JobPortalUser user;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Size(max = 255)
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Size(max = 50)
    @NotNull
    @Column(name = "experience_level", nullable = false, length = 50)
    private String experienceLevel;

    @NotNull
    @Lob
    @Column(name = "professional_bio", nullable = false)
    private String professionalBio;

    @Size(max = 500)
    @Column(name = "portfolio_website", length = 500)
    private String portfolioWebsite;


    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Size(max = 255)
    @Column(name = "profile_picture_name", length = 255)
    private String profilePictureName;

    @Size(max = 100)
    @Column(name = "profile_picture_type", length = 100)
    private String profilePictureType;


    @Column(name = "resume")
    private byte[] resume;

    @Size(max = 255)
    @Column(name = "resume_name", length = 255)
    private String resumeName;

    @Size(max = 100)
    @Column(name = "resume_type", length = 100)
    private String resumeType;
}
