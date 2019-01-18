package org.pampanet.upgrade.challenge.model;

import java.util.UUID;

public class ReservationMadeEvent {
    public UUID uuid;

    public ReservationMadeEvent(UUID uuid){
        this.uuid = uuid;
    }
}
