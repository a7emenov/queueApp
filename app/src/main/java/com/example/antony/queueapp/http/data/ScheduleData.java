package com.example.antony.queueapp.http.data;

import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleData implements Serializable {

    public Period period;
    public ArrayList<Schedule> schedules;

    public ScheduleData(Period period, ArrayList<Schedule> schedules) {
        this.period = period;
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        return "ScheduleData{" +
                "period=" + period +
                ", schedules=" + schedules +
                '}';
    }
}
