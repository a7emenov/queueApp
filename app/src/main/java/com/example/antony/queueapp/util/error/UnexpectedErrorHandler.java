package com.example.antony.queueapp.util.error;

import android.util.Log;
import android.view.View;

public final class UnexpectedErrorHandler {

    private UnexpectedErrorHandler() {}

    public static void handle(Throwable e) {
        Log.e("UNEXPECTED_ERROR", "", e);
    }

    public static void handle(Throwable e, String msg) {
        Log.e("UNEXPECTED_ERROR", msg, e);
    }
}