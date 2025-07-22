package com.jobportal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.jobportal.services.SavedJobsService; // Ensure this service exists
import org.springframework.web.bind.annotation.RestController;

@Controller
public class JobSeekerController {

    private final SavedJobsService savedJobsService;

    public JobSeekerController(SavedJobsService savedJobsService) {
        this.savedJobsService = savedJobsService;
    }

    @GetMapping("/saved-jobs")
    public String savedJobs(Model model) {
        model.addAttribute("savedJobs", savedJobsService.getSavedJobsForCurrentUser());
        return "saved-jobs"; // Ensure there is a corresponding Thymeleaf template named saved-jobs.html
    }
}

