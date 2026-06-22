package com.backend.latihan.job.service.Impl;


import com.backend.latihan.Util.ApplicationUtility;
import com.backend.latihan.dto.JobDto;
import com.backend.latihan.entity.Job;
import com.backend.latihan.entity.JobPortalUser;
import com.backend.latihan.job.service.IJobService;
import com.backend.latihan.repository.JobPortalUserRepository;
import com.backend.latihan.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobServiceImpl implements IJobService {

    private final JobPortalUserRepository userRepository;

    private final JobRepository jobRepository;

    @Override
    public List<JobDto> getEmployerJobs(String employerEmail) {
        JobPortalUser employer = userRepository.findJobPortalUserByEmail(employerEmail).orElseThrow(() -> new RuntimeException("Employer not found"));

        List<Job> jobs = employer.getCompany().getJobs();
        return  jobs.stream().map(job -> ApplicationUtility.transformJobToDto(job)).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public JobDto updateJobStatus(Long jobId, String status, String employerEmail) {
        //Validate status
        if (!status.equals("ACTIVE") && !status.equals("CLOSED") && !status.equals("DRAFT")){
            throw new RuntimeException("Invalid status. Must be ACTIVE, CLOSED, DRAFT");
        }

        JobPortalUser employer = userRepository.findJobPortalUserByEmail(employerEmail).orElseThrow(() -> new RuntimeException("Job not found"));

        if (employer.getCompany() == null){
            throw new RuntimeException("Employer does not have a company assigned");
        }

        Job job = employer.getCompany().getJobs().stream().filter(j -> j.getId().equals(jobId)).findFirst().orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(status);

        return ApplicationUtility.transformJobToDto(job);
    }


    @Override
    @Transactional
    public JobDto createJob(JobDto jobDto, String employerEmail) {
        //Validate employer and get their company
        JobPortalUser employer = userRepository.findJobPortalUserByEmail(employerEmail).orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null){
            throw new RuntimeException("Employer does not have a company assigned. Please contact admin.");
        }

            Job job = transformDtoToEntity(jobDto);

            job.setPostedDate(Instant.now());

            job.setApplicationsCount(0);

            job.setStatus("DRAFT");

            job.setCompany(employer.getCompany());

            Job savedJob = jobRepository.save(job);

            return ApplicationUtility.transformJobToDto(savedJob);

    }


    private Job transformDtoToEntity(JobDto jobDto){
        Job job = new Job();

        BeanUtils.copyProperties(jobDto, job);

        return job;
    }



}
