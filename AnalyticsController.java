package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analytics;
    public AnalyticsController(AnalyticsService analytics){this.analytics=analytics;}
    @GetMapping("/dashboard") @PreAuthorize("hasRole('ADMIN')") public ApiDtos.AnalyticsResponse dashboard(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){LocalDate end=to==null?LocalDate.now():to; LocalDate start=from==null?end.withDayOfMonth(1):from; if(end.isBefore(start)) throw new IllegalArgumentException("to must not be before from"); return analytics.dashboard(start,end);}
}
