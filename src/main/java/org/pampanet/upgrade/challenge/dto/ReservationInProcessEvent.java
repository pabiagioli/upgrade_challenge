package org.pampanet.upgrade.challenge.dto;

import java.time.LocalDate;

public class ReservationInProcessEvent extends ReservationDTO{
    public ReservationInProcessEvent(String name, LocalDate arriveDate, LocalDate departureDate) {
        super(name, arriveDate, departureDate);
    }
}
