package org.pampanet.upgrade.challenge;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pampanet.upgrade.challenge.config.ManagerConfig;
import org.pampanet.upgrade.challenge.config.PersistenceConfig;
import org.pampanet.upgrade.challenge.model.Reservation;
import org.pampanet.upgrade.challenge.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = {ManagerConfig.class, PersistenceConfig.class}, loader = AnnotationConfigContextLoader.class)
@SpringBootTest
public class CampsiteTests {

    LocalDate today = LocalDate.now();
    LocalDate monthFromNow = today.plusMonths(1);

    @Autowired
    private IBookingService bookingManager;

    UUID pablo;

    @Before
    public void setUp(){
        pablo = null;
        bookingManager.cancelAll();
        pablo = bookingManager.makeReservation(new Reservation("Pablo", today.plusDays(2), today.plusDays(4)));
        //UUID invalid = bookingManager.makeReservation(new Reservation("NoWay", today.plusDays(3), today.plusDays(6)));
        UUID fede = bookingManager.makeReservation(new Reservation("Fede", today.plusDays(4), today.plusDays(6)));
        bookingManager.cancelReservation(pablo.toString());
    }

    @After
    public void tearDown(){
        bookingManager.cancelAll();
    }

    @Test
    public void testDateValidations(){
        //max of 3 days
        //lower bound is tomorrow and upper bound is 1 month after tomorrow
        Assert.assertFalse(bookingManager.validateRange(today.atStartOfDay(), today.atStartOfDay().plusWeeks(1)));
        Assert.assertFalse(bookingManager.validateRange(monthFromNow.atStartOfDay(), monthFromNow.atStartOfDay().plusDays(2)));

        Assert.assertTrue(bookingManager.validateRange(
                monthFromNow.atStartOfDay().plusDays(1).minusDays(3),
                monthFromNow.atStartOfDay().plusDays(1)));
    }

    @Test
    public void testAlreadyBooked(){
        Assert.assertTrue(bookingManager.isAvailable(today.atStartOfDay().plusDays(2)));
        Assert.assertTrue(bookingManager.isAvailable(today.atStartOfDay().plusDays(3)));
        Assert.assertFalse(bookingManager.isAvailable(today.atStartOfDay().plusDays(4)));
        Assert.assertNull(bookingManager.makeReservation(new Reservation("Mom", today.plusDays(2), today.plusDays(5))));
    }

    @Test
    public void testUpdate(){
        Reservation pabloDTO = new Reservation(pablo, "Pablo", today.atStartOfDay().plusDays(10), today.atStartOfDay().plusDays(12));
        Assert.assertNotNull(bookingManager.makeReservation(pabloDTO));
    }

    /**
     * This function will test the concurrency and DB synchronization for trying to book the place at the same date range 3 times.
     * This is based on POSTGRESQL EXCLUDE Constraints for overlapping ranges.
     */
    @Test
    public void testConcurrency() {
        Reservation pabloDTO = new Reservation("Pablo", today.plusDays(10), today.plusDays(12));
        Reservation pepeDTO = new Reservation("Pepe", today.plusDays(10), today.plusDays(12));
        Reservation fedeDTO = new Reservation("Fede", today.plusDays(10), today.plusDays(12));
        List<UUID> ids = Stream.of(pabloDTO,pepeDTO,fedeDTO)
                .parallel()
                .map(dto-> bookingManager.makeReservation(dto))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Assert.assertTrue(!ids.isEmpty());
        Assert.assertEquals(1, ids.size());
    }

}
