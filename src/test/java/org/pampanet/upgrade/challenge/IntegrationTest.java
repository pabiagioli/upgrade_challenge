package org.pampanet.upgrade.challenge;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pampanet.upgrade.challenge.model.Reservation;
import org.pampanet.upgrade.challenge.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest(classes = MainApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    LocalDate today = LocalDate.now();
    LocalDate monthFromNow = today.plusMonths(1);

    @LocalServerPort
    int port;
    @Autowired
    TestRestTemplate restTemplate;
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    IBookingService bookingService;

    @Before
    public void setup(){
        bookingService.cancelAll();
    }

    @After
    public void tearDown(){
        bookingService.cancelAll();
    }

    /**
     * Integration test that does the same but hitting the Controller
     */
    @Test
    public void integrationTestConcurrency() {
        Reservation pabloDTO = new Reservation("Pablo", today.plusDays(13), today.plusDays(16));
        Reservation noWayDTO = new Reservation("NoWay", today.plusDays(13), today.plusDays(16));
        Reservation fedeDTO = new Reservation("Fede", today.plusDays(13), today.plusDays(16));

        List<UUID> ids = Stream.of(new HttpEntity<>(pabloDTO, headers), new HttpEntity<>(noWayDTO, headers), new HttpEntity<>(fedeDTO, headers))
                .parallel()
                .map(entity-> {
                    ResponseEntity<UUID> result = restTemplate.postForEntity("/campsite/book", entity, UUID.class);
                    return result.getBody();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Assert.assertTrue(!ids.isEmpty());
        Assert.assertEquals(1, ids.size());
    }
}
