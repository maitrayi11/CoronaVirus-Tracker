package com.example.coronavirustracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.coronavirustracker.services.CoronaVirusDataService;


@Controller
public class VirusController {

    @Autowired CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String trackerHome(Model model){
        model.addAttribute("location" , coronaVirusDataService.getAllStats());
        model.addAttribute("totalReportedCases",coronaVirusDataService.getTotalReportedCases());
        return "trackerHome";
    }

}
