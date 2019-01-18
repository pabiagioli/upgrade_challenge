package org.pampanet.upgrade.challenge.dto;

import java.time.LocalDate;
import java.util.Objects;

public class ReservationDTO {

    private String name;
    private LocalDate arriveDate;
    private LocalDate departureDate;

    public ReservationDTO(String name, LocalDate arriveDate, LocalDate departureDate) {
        this.name = name;
        this.arriveDate = arriveDate;
        this.departureDate = departureDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(LocalDate arriveDate) {
        this.arriveDate = arriveDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDTO that = (ReservationDTO) o;
        return getName().equals(that.getName()) &&
                getArriveDate().equals(that.getArriveDate()) &&
                getDepartureDate().equals(that.getDepartureDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getArriveDate(), getDepartureDate());
    }
}
