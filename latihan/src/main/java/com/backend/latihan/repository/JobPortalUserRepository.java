package com.backend.latihan.repository;
import com.backend.latihan.entity.JobPortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPortalUserRepository extends JpaRepository<JobPortalUser, Long> {
    Optional<JobPortalUser> readJobPortalUserByNameOrMobileNumber(String email, String mobileNumber);

    Optional<JobPortalUser> findJobPortalUserByEmail(String email);
}
