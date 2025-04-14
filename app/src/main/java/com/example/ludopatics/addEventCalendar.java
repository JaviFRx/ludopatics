package com.example.ludopatics;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

public class addEventCalendar {

    public static void insertarVictoria(final Context context) {
        Thread hilo = new Thread(() -> {
            long calendarId = 1; // ID por defecto

            // Obtener el tiempo actual
            Calendar cal = Calendar.getInstance();
            long startMillis = cal.getTimeInMillis();
            long endMillis = startMillis + (10 * 60 * 1000); // Evento de 10 minutos

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "¡Victoria en la ruleta!");
            values.put(CalendarContract.Events.DESCRIPTION, "Has ganado una partida el " + cal.getTime());
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            try {
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                if (uri != null) {
                    System.out.println("Evento insertado: " + uri.toString());
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        });

        hilo.start();
    }
}