package org.pampanet.upgrade.challenge.model;

import org.hibernate.annotations.Formula;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;
    String name;

    @Formula("lower(during)")
    LocalDateTime checkin;
    @Formula("upper(during)")
    LocalDateTime checkout;

    public Reservation(){}

    public Reservation(UUID id, String name, LocalDateTime checkin, LocalDateTime checkout) {
        this.id = id;
        this.name = name;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public Reservation(@NonNull String name, @NonNull LocalDate checkin, @NonNull LocalDate checkout){
        this(UUID.randomUUID(), name, checkin.atStartOfDay(), checkout.atStartOfDay());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCheckin() {
        return checkin;
    }

    public LocalDateTime getCheckout() {
        return checkout;
    }

    public void setCheckin(LocalDateTime checkin) {
        this.checkin = checkin;
    }

    public void setCheckout(LocalDateTime checkout) {
        this.checkout = checkout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return getId().equals(that.getId()) &&
                getName().equals(that.getName()) &&
                getCheckin().equals(that.getCheckin()) &&
                getCheckout().equals(that.getCheckout());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCheckin(), getCheckout());
    }
}
