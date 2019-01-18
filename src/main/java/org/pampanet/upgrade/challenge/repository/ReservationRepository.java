package org.pampanet.upgrade.challenge.repository;

import org.pampanet.upgrade.challenge.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Modifying
    @Query(value = "insert into Reservations (id,name,during) VALUES (:id,:name,cast(:during as tsrange))", nativeQuery = true)
    @Transactional
    int save(@Param("id") UUID id, @Param("name") String name, @Param("during") String during);
}
