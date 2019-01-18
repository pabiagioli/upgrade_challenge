package org.pampanet.upgrade.challenge.service;

import org.pampanet.upgrade.challenge.model.Reservation;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IBookingService {

    boolean isAvailable(@NonNull LocalDateTime date);
    UUID makeReservation(@NonNull Reservation reservation);
    UUID updateReservation(@NonNull Reservation reservation);
    void cancelReservation(String id);
    void cancelAll();
    boolean validateRange(@NonNull LocalDateTime start, @NonNull LocalDateTime end);
    List<Reservation> getReservations();
}
