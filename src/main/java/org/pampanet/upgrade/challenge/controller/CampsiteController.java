package org.pampanet.upgrade.challenge.controller;

import org.pampanet.upgrade.challenge.model.Reservation;
import org.pampanet.upgrade.challenge.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/campsite")
public class CampsiteController {

    @Autowired
    IBookingService bookingManager;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public Boolean isAvailable(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        long days = 0;
        if(startDate == null || endDate == null) {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            days = Duration.between(today, today.plusMonths(1)).toDays();
        }else {
            days = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        }

        boolean isValidRange = bookingManager.validateRange(startDate.atStartOfDay(), endDate.atStartOfDay());
        if(!isValidRange)
            return false;

        for (int i = 0; i < days; i++) {
            if(!bookingManager.isAvailable(startDate.atStartOfDay().plusDays(i)))
                return false;
        }

        return true;
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public UUID makeReservation(@RequestBody @NonNull Reservation reservation){
        return bookingManager.makeReservation(reservation);
    }

    @RequestMapping(value = "/book", method = RequestMethod.PUT)
    public UUID updateReservation(@RequestBody @NonNull Reservation reservation){
        return bookingManager.updateReservation(reservation);
    }

    @RequestMapping(value = "/book/{id}", method = RequestMethod.PUT)
    public Boolean updateReservation(@PathParam("id") @NonNull String id){
        bookingManager.cancelReservation(id);
        return true;
    }

}
