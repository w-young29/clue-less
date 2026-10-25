package jhu.clueless.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClueLessController {

  @GetMapping("/")
  public String test() {
    return "Service running successfully!";
  }
}
