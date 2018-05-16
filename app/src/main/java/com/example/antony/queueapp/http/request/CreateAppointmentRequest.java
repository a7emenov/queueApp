package com.example.antony.queueapp.http.request;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class CreateAppointmentRequest {

    public int hostId;
    public int visitorId;
    public LocalDate date;
    public LocalTime start;
    public LocalTime end;

    public CreateAppointmentRequest(int hostId, int visitorId, LocalDate date, LocalTime start, LocalTime end) {
        this.hostId = hostId;
        this.visitorId = visitorId;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "CreateAppointmentRequest{" +
                "hostId=" + hostId +
                ", visitorId=" + visitorId +
                ", date=" + date +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
