package jhu.clueless.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClueLessController {

  @Operation(summary = "Base endpoint to verify that the service is running.")
  @GetMapping("/")
  public String test() {
    return "Service running successfully!";
  }
}
