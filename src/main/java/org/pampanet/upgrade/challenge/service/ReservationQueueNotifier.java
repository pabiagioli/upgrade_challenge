package org.pampanet.upgrade.challenge.service;

import org.pampanet.upgrade.challenge.model.ReservationMadeEvent;
import org.pampanet.upgrade.challenge.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//@Component //if using @Asing and EventListener in BookingService
public class ReservationQueueNotifier {

    private static final Logger logger = LoggerFactory.getLogger(ReservationQueueNotifier.class);
    private final ReservationRepository reservationRepository;

    public ReservationQueueNotifier(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationMade(ReservationMadeEvent event){
        //Reservation reservation = reservationRepository.findById(event.uuid).get();
        logger.debug("Reservation Made - " + event.uuid.toString());
    }

}
