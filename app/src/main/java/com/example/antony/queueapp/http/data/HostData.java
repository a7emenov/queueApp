package com.example.antony.queueapp.http.data;

import java.io.Serializable;

public class HostData implements Serializable {

    public int id;
    public String firstName;
    public String surname;
    public String patronymic;

    public String fullName() {
        return String.format("%s %s. %s.", surname, firstName.charAt(0), patronymic.charAt(0));
    }

    //spinner display
    @Override
    public String toString() {
        return fullName();
    }
}
