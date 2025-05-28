package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.DashboardSummaryDTO;
import com.seroter.unknownPaw.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class DashboardController {

  private final DashboardService dashboardService;


  @GetMapping("/{mid}/summary")
  public DashboardSummaryDTO getSummary(@PathVariable Long mid) {
    return dashboardService.getDashboardSummary(mid);
  }
}
