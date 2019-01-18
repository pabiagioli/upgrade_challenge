package org.pampanet.upgrade.challenge.service;

import org.pampanet.upgrade.challenge.model.Reservation;
import org.pampanet.upgrade.challenge.model.ReservationMadeEvent;
import org.pampanet.upgrade.challenge.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.NEVER) //don't propagate the transactional exceptions
public class BookingService implements IBookingService {

    private final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public BookingService(ReservationRepository reservationRepository, ApplicationEventPublisher eventPublisher){
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean validateRange(@NonNull LocalDateTime start, @NonNull LocalDateTime end){
        long days = Duration.between(start, end).toDays();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime upperBound = today.plusDays(2).plusMonths(1);
        return days <= 3L && start.isAfter(today) && end.isBefore(upperBound);
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    @Override
    public boolean isAvailable(LocalDateTime date) {
        Optional<Reservation> result = getReservationRepository().findAll().stream().filter(reservation ->
                reservation.getCheckin().isEqual(date) ||
                        reservation.getCheckout().minusDays(1).isEqual(date) ||
                        (date.isAfter(reservation.getCheckin()) &&
                                date.isBefore(reservation.getCheckout()))).findFirst();
        return !result.isPresent();
    }

    //@Async
    //@TransactionalEventListener
    @Override
    public UUID makeReservation(@NonNull Reservation reservation) {
        UUID result = null;
        try {
            if (validateRange(reservation.getCheckin(), reservation.getCheckout())) {
                String dateRange = String.format("[%s,%s]", reservation.getCheckin().toString(), reservation.getCheckout().toString());
                getReservationRepository().save(reservation.getId(), reservation.getName(), dateRange);
                result = reservation.getId();
                //eventPublisher.publishEvent(new ReservationMadeEvent(result));
            }
        }catch (Exception ex){
            logger.error("error making reservation");
        }
        return result;
    }

    public UUID updateReservation(@NonNull Reservation reservation){
        return makeReservation(reservation);
    }

    @Override
    public void cancelReservation(@NonNull String id) {
        UUID uuid = UUID.fromString(id);
        if(getReservationRepository().findById(uuid).isPresent())
            getReservationRepository().deleteById(uuid);
    }
    public List<Reservation> getReservations(){
        return getReservationRepository().findAll();
    }
    public void cancelAll(){
        getReservationRepository().deleteAll();
    }
}

