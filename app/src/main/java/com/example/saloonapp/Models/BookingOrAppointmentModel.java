package com.example.saloonapp.Models;

public class BookingOrAppointmentModel {
    private String bookingOrAppointmentId, parlourOrUserId, parlourOrUserName, date, status;

    public BookingOrAppointmentModel(String bookingOrAppointmentId, String parlourOrUserId, String parlourOrUserName, String date, String status) {
        this.bookingOrAppointmentId = bookingOrAppointmentId;
        this.parlourOrUserId = parlourOrUserId;
        this.parlourOrUserName = parlourOrUserName;
        this.date = date;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getBookingOrAppointmentId() {
        return bookingOrAppointmentId;
    }

    public String getParlourOrUserId() {
        return parlourOrUserId;
    }

    public String getParlourOrUserName() {
        return parlourOrUserName;
    }

    public String getDate() {
        return date;
    }
}
