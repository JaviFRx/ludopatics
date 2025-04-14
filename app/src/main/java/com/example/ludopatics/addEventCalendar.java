package com.example.ludopatics;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class addEventCalendar {
    private static final String TAG = "CalendarEvents";

    private static long obtenerPrimerCalendarioId(ContentResolver cr) {
        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Cursor cursor = cr.query(uri, projection, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    long calendarId = cursor.getLong(0);
                    Log.d("CalendarDebug", "ID de calendario encontrado: " + calendarId);
                    return calendarId; // Primer ID encontrado
                }
            } finally {
                cursor.close();
            }
        }
        Log.e("CalendarDebug", "No se encontró ningún calendario disponible.");
        return -1;
    }

    public static void insertarVictoria(final Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Thread hilo = new Thread(() -> {
            ContentResolver cr = context.getContentResolver();
            long calendarId = obtenerPrimerCalendarioId(cr);

            if (calendarId == -1) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "No se encontró ningún calendario activo en este dispositivo", Toast.LENGTH_LONG).show()
                );
                return;
            }

            // Obtener el tiempo actual
            Calendar cal = Calendar.getInstance();
            long startMillis = cal.getTimeInMillis();
            long endMillis = startMillis + (10 * 60 * 1000); // Evento de 10 minutos

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "¡Victoria en la ruleta!");
            values.put(CalendarContract.Events.DESCRIPTION, "Has ganado una partida el " + cal.getTime());
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            try {
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                ((Activity) context).runOnUiThread(() -> {
                    if (uri != null) {
                        Toast.makeText(context, "Evento añadido al calendario", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al guardar el evento", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SecurityException e){
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Permisos de calendario requeridos", Toast.LENGTH_LONG).show()
                    );
                }
        });

        hilo.start();
    }
}