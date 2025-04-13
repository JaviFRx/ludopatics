package com.example.ludopatics;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ludopatics.db";
    private static final int DATABASE_VERSION = 2;  // Aumentado por la nueva tabla 'partidas' y campo en 'historico_tiradas'

    // Definición de las tablas
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";

    private static final String TABLE_PARTIDAS = "partidas";
    private static final String COLUMN_PARTIDA_ID = "id";
    private static final String COLUMN_PARTIDA_JUGADOR_ID = "jugador_id";  // Nueva columna para vincular con 'usuarios'
    private static final String COLUMN_SALDO = "saldofinal";

    private static final String COLUMN_PARTIDA_FECHA = "fecha";
    private static final String TABLE_HISTORICO = "historico_tiradas";
    private static final String COLUMN_HIST_ID = "id";
    private static final String COLUMN_HIST_USUARIO_ID = "usuario_id";
    private static final String COLUMN_HIST_PARTIDA_ID = "partida_id";  // Vincula la tirada con una partida
    private static final String COLUMN_HIST_APUESTA = "apuesta";
    private static final String COLUMN_HIST_RESULTADO = "resultado";
    private static final String COLUMN_HIST_SALDO = "saldo";
    private static final String COLUMN_HIST_GANO = "gano";
    private static final String COLUMN_HIST_FECHA = "fecha";
    private static final String COLUMN_USUARIO_LATITUD = "latitud";
    private static final String COLUMN_USUARIO_LONGITUD = "longitud";
    private Context context;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla de usuarios
        String createUsersTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT UNIQUE, " +
                COLUMN_USUARIO_LATITUD + " REAL, " +  // Nueva columna latitud
                COLUMN_USUARIO_LONGITUD + " REAL)";   // Nueva columna longitud
        db.execSQL(createUsersTable);
        String createPartidasTable = "CREATE TABLE " + TABLE_PARTIDAS + " (" +
                COLUMN_PARTIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PARTIDA_JUGADOR_ID + " INTEGER, " +
                COLUMN_PARTIDA_FECHA + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_SALDO + " INTEGER , " +  // Permite valores NULL
                "FOREIGN KEY(" + COLUMN_PARTIDA_JUGADOR_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(createPartidasTable);

        // Crear la tabla de histórico de tiradas (con partida_id)
        String createHistoricoTable = "CREATE TABLE " + TABLE_HISTORICO + " (" +
                COLUMN_HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HIST_USUARIO_ID + " INTEGER, " +
                COLUMN_HIST_PARTIDA_ID + " INTEGER, " +  // Nuevo campo para vincular tirada con partida
                COLUMN_HIST_APUESTA + " INTEGER, " +
                COLUMN_HIST_RESULTADO + " REAL, " +
                COLUMN_HIST_SALDO + " REAL, " +
                COLUMN_HIST_GANO + " INTEGER, " +
                COLUMN_HIST_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_HIST_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_HIST_PARTIDA_ID + ") REFERENCES " + TABLE_PARTIDAS + "(" + COLUMN_PARTIDA_ID + "))";
        db.execSQL(createHistoricoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                // Verificar si las columnas latitud y longitud ya existen en la tabla usuarios.
                db.execSQL("ALTER TABLE " + TABLE_USUARIOS + " ADD COLUMN " + COLUMN_USUARIO_LATITUD + " REAL");
            } catch (Exception e) {
                // Si la columna ya existe, no hacer nada.
                Log.d("DBHelper", "Columna 'latitud' ya existe o no se necesita agregar");
            }

            try {
                db.execSQL("ALTER TABLE " + TABLE_USUARIOS + " ADD COLUMN " + COLUMN_USUARIO_LONGITUD + " REAL");
            } catch (Exception e) {
                // Si la columna ya existe, no hacer nada.
                Log.d("DBHelper", "Columna 'longitud' ya existe o no se necesita agregar");
            }
        }
    }



    // Método para verificar si una columna existe en una tabla
    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ");", null);
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex != -1) {
                    String existingColumn = cursor.getString(columnIndex);
                    if (existingColumn.equalsIgnoreCase(columnName)) {
                        exists = true;
                        break;
                    }
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return exists;
    }


    public void imprimirPartidas() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PARTIDAS, null, null, null, null, null, null);

        Log.d("PARTIDAS", "Total de partidas: " + cursor.getCount());

        // Registra las columnas disponibles para depuración
        String[] columnNames = cursor.getColumnNames();
        Log.d("PARTIDAS", "Columnas disponibles: " + Arrays.toString(columnNames));

        // Obtener los índices de las columnas
        int idIndex = cursor.getColumnIndex(COLUMN_PARTIDA_ID);
        int jugadorIdIndex = cursor.getColumnIndex(COLUMN_PARTIDA_JUGADOR_ID);
        int fechaIndex = cursor.getColumnIndex(COLUMN_PARTIDA_FECHA);
        int saldoIndex = cursor.getColumnIndex(COLUMN_SALDO);

        // Verificar que los índices sean válidos
        if (idIndex == -1 || jugadorIdIndex == -1 || fechaIndex == -1 || saldoIndex == -1) {
            Log.e("PARTIDAS", "Una o más columnas no existen en la tabla.");
            cursor.close();
            db.close();
            return;
        }

        // Recorrer el cursor e imprimir cada registro
        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            int jugadorId = cursor.getInt(jugadorIdIndex);
            String fecha = cursor.getString(fechaIndex);
            int saldo = cursor.getInt(saldoIndex);

            Log.d("PARTIDAS", "Partida - ID: " + id +
                    ", Jugador ID: " + jugadorId +
                    ", Fecha: " + fecha +
                    ", Saldo: " + saldo);
        }

        cursor.close();
        db.close();
    }

    public void eliminarPartidas() {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasEliminadas = db.delete(TABLE_PARTIDAS, null, null);
        Log.d("PARTIDAS", "Número de registros eliminados: " + filasEliminadas);
        db.close();
    }


    // Método para guardar el nombre de un usuario
    public boolean guardarNombre(final String nombre) {
        final SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si el nombre ya existe
        if (existeNombre(nombre)) {
            Log.i("Database", "El nombre ya existe, no se inserta.");
            return false; // No insertar si ya está en la base de datos, devolver false
        }

        // Ejecutar la tarea de obtener la ubicación en un hilo separado
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // Obtener la ubicación y guardar en la base de datos
                obtenerUbicacionYGuardar(db, nombre); // Cambiar obtenerUbicacionYGuardar a que devuelva un booleano

            }
        });

        return true; // Si no hubo error al enviar la tarea, devolvemos true
    }


    private void obtenerUbicacionYGuardar(final SQLiteDatabase db, final String nombre) {
        // Obtener el cliente de ubicación
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Verificar si el permiso de ubicación está concedido
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Insertar en la base de datos con la ubicación obtenida
                                ContentValues values = new ContentValues();
                                values.put(COLUMN_NOMBRE, nombre); // Asegúrate de que esta constante existe
                                values.put("latitud", latitude);
                                values.put("longitud", longitude);

                                long result = db.insert(TABLE_USUARIOS, null, values); // Asegúrate de que esta constante también existe
                                if (result == -1) {
                                    Log.e("Database", "Error al guardar el nombre y la ubicación");
                                } else {
                                    Log.i("Database", "Nombre y ubicación guardados correctamente");
                                }
                            } else {
                                Log.e("Location", "No se pudo obtener la ubicación");
                            }
                        }
                    });
        } else {
            // Solicitar permisos si no están otorgados
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Log.e("Permiso", "El contexto no es una Activity. No se puede solicitar el permiso.");
            }
        }
    }


    public boolean existeNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});

        boolean existe = cursor.moveToFirst(); // Si hay resultados, el nombre ya existe
        cursor.close();

        return existe;
    }



    // Método para obtener el nombre de un usuario
    public String obtenerNombre() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NOMBRE + " FROM " + TABLE_USUARIOS + " LIMIT 1", null);

        String nombre = "";
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return nombre;
    }

    public int obtenerIdJugador(String nombre) {
        if (nombre == null) {
            Log.e("DatabaseHelper", "Nombre de usuario es nulo");
            return -1;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int jugadorId = -1;

        try {
            Log.d("DatabaseHelper", "Buscando usuario: '" + nombre + "'");

            cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});

            Log.d("DatabaseHelper", "Número de resultados: " + (cursor != null ? cursor.getCount() : "cursor nulo"));

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_ID);
                if (columnIndex >= 0) {
                    jugadorId = cursor.getInt(columnIndex);
                    Log.d("DatabaseHelper", "ID encontrado: " + jugadorId);
                } else {
                    Log.e("DatabaseHelper", "Índice de columna no válido");
                }
            } else {
                Log.e("DatabaseHelper", "No se encontró ningún usuario con ese nombre");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error al obtener ID de jugador", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return jugadorId;
    }

    public float[] obtenerCoordenadas(String nombre) {
        if (nombre == null) {
            Log.e("DatabaseHelper", "Nombre de usuario es nulo");
            return null; // Si el nombre es nulo, devuelve null
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        float[] coordenadas = null;

        try {
            Log.d("DatabaseHelper", "Buscando coordenadas para el usuario: '" + nombre + "'");

            cursor = db.rawQuery("SELECT latitud, longitud FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});

            Log.d("DatabaseHelper", "Número de resultados: " + (cursor != null ? cursor.getCount() : "cursor nulo"));

            if (cursor != null && cursor.moveToFirst()) {
                // Obtener las coordenadas
                int indexLatitud = cursor.getColumnIndex("latitud");
                int indexLongitud = cursor.getColumnIndex("longitud");

                if (indexLatitud >= 0 && indexLongitud >= 0) {
                    float latitud = cursor.getFloat(indexLatitud);
                    float longitud = cursor.getFloat(indexLongitud);

                    coordenadas = new float[]{latitud, longitud}; // Guardamos las coordenadas en el array
                    Log.d("DatabaseHelper", "Coordenadas encontradas: Latitud = " + latitud + ", Longitud = " + longitud);
                } else {
                    Log.e("DatabaseHelper", "Índices de columna no válidos para las coordenadas");
                }
            } else {
                Log.e("DatabaseHelper", "No se encontró ningún usuario con ese nombre");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error al obtener coordenadas del usuario", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return coordenadas; // Devuelve el array de coordenadas o null si no se encontraron
    }




    // Método para crear una nueva partida para un jugador
    public long crearPartida(int usuarioId, int saldo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTIDA_JUGADOR_ID, usuarioId);  // Asociar la partida al jugador
        values.put(COLUMN_SALDO, saldo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid")); // Ajusta la zona horaria según tu ubicación
        String fecha = dateFormat.format(new Date());

        values.put(COLUMN_PARTIDA_FECHA, fecha); // Guardar la fecha actual

        long resultado = db.insert("partidas", null, values);

        if (resultado == -1) {
            Log.e("DatabaseHelper", "Error al insertar partida con usuarioId: " + usuarioId);
        } else {
            Log.d("DatabaseHelper", "Partida creada con éxito, ID: " + resultado);
        }

        db.close();  // Cierra la BD para liberar recursos

        return resultado; // Devuelve el ID de la partida creada o -1 si hubo error
    }


    // Método para obtener la fecha actual en formato adecuado (Texto)
    private String getFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Método para guardar el histórico de una tirada (con partida_id)
    public boolean guardarTirada(int usuarioId, int partidaId, int apuesta, double resultado, double saldo, boolean gano) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIST_USUARIO_ID, usuarioId);
        values.put(COLUMN_HIST_PARTIDA_ID, partidaId);  // Relaciona la tirada con la partida
        values.put(COLUMN_HIST_APUESTA, apuesta);
        values.put(COLUMN_HIST_RESULTADO, resultado);
        values.put(COLUMN_HIST_SALDO, saldo);
        values.put(COLUMN_HIST_GANO, gano ? 1 : 0);

        long result = db.insert(TABLE_HISTORICO, null, values);
        db.close();
        return result != -1;
    }

    // Método para obtener el histórico de tiradas de un usuario
    public Cursor obtenerHistorico(int usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_HIST_PARTIDA_ID + ", " +  // Se obtiene el ID de la partida
                        COLUMN_HIST_APUESTA + ", " +
                        COLUMN_HIST_RESULTADO + ", " +
                        COLUMN_HIST_SALDO + ", " +
                        COLUMN_HIST_GANO + ", " +
                        COLUMN_HIST_FECHA + " FROM " + TABLE_HISTORICO +
                        " WHERE " + COLUMN_HIST_USUARIO_ID + " = ? ORDER BY " + COLUMN_HIST_FECHA + " DESC",
                new String[]{String.valueOf(usuarioId)});
    }

    public List<Partida> obtenerPartidas(int usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Partida> partidasList = new ArrayList<>();

        // Consulta para obtener las partidas incluyendo saldofinal
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PARTIDA_ID + ", " +
                        COLUMN_PARTIDA_JUGADOR_ID + ", " +
                        COLUMN_PARTIDA_FECHA + ", " +
                        COLUMN_SALDO +  // Agregado a la consulta
                        " FROM " + TABLE_PARTIDAS +
                        " WHERE " + COLUMN_PARTIDA_JUGADOR_ID + " = ? ORDER BY " + COLUMN_PARTIDA_ID + " DESC",
                new String[]{String.valueOf(usuarioId)});

        // Procesar el cursor
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        // Obtener los índices de las columnas
                        int idIndex = cursor.getColumnIndex(COLUMN_PARTIDA_ID);
                        int jugadorIdIndex = cursor.getColumnIndex(COLUMN_PARTIDA_JUGADOR_ID);
                        int fechaIndex = cursor.getColumnIndex(COLUMN_PARTIDA_FECHA);
                        int saldoIndex = cursor.getColumnIndex(COLUMN_SALDO); // Agregado

                        // Validar que las columnas existen
                        if (idIndex != -1 && jugadorIdIndex != -1 && fechaIndex != -1 && saldoIndex != -1) {
                            int idPartida = cursor.getInt(idIndex);
                            int idJugador = cursor.getInt(jugadorIdIndex);
                            String fecha = cursor.getString(fechaIndex);
                            int saldoFinal = cursor.getInt(saldoIndex); // Obtener el valor real

                            partidasList.add(new Partida(idPartida, idJugador, fecha, saldoFinal));
                        } else {
                            Log.e("DatabaseHelper", "Error: columna no encontrada en el cursor");
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close(); // Asegurarse de cerrar el cursor
            }
        }

        return partidasList.isEmpty() ? new ArrayList<>() : partidasList;
    }


}
