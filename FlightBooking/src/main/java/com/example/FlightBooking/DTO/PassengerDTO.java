package com.example.FlightBooking.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PassengerDTO {

    private Long id;
    private Long bookingId;

    @NotBlank
    private String name;

    @Min(1)
    private int age;

    @NotBlank
    private String seatNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
