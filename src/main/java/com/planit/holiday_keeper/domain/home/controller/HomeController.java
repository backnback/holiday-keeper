package com.planit.holiday_keeper.domain.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String showMain() {
    return "redirect:/swagger-ui/index.html";
  }
}
