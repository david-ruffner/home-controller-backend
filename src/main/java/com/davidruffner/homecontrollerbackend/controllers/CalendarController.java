package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.dtos.ICalEvent;
import com.davidruffner.homecontrollerbackend.services.CalendarService;
import com.davidruffner.homecontrollerbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @GetMapping("/getEvents")
    public ResponseEntity<List<ICalEvent>> getEvents(@RequestParam String startTime, @RequestParam String endTime) {
        if (Utils.strEmpty(startTime) || Utils.strEmpty(endTime)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(this.calendarService.getCalendarEvents(startTime, endTime));
    }
}
