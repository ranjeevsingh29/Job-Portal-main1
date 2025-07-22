package com.jobportal.controllers;

import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.Users;
import com.jobportal.repository.UsersRepository;
import com.jobportal.services.JobSeekerProfileService;
import com.jobportal.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {
    private final UsersRepository usersRepository;
    private final JobSeekerProfileService jobSeekerProfileService;

    public JobSeekerProfileController(UsersRepository usersRepository, JobSeekerProfileService jobSeekerProfileService) {
        this.usersRepository = usersRepository;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    @GetMapping("/")
    public String jobSeekerProfile(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(users.getUserId());

            if (jobSeekerProfile.isPresent())
                model.addAttribute("profile", jobSeekerProfile.get());
        }

        return "job_seeker_profile";
    }

    @PostMapping("addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            jobSeekerProfile.setUserId(users);
            jobSeekerProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", jobSeekerProfile);
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(fileName);
        }
        JobSeekerProfile savedUser = jobSeekerProfileService.addNew(jobSeekerProfile);

        String uploadDir = "photos/jobseeker/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
}
