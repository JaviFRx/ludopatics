package com.example.ludopatics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ludopatics.db";
    private static final int DATABASE_VERSION = 6;  // Aumentado por la nueva tabla 'partidas' y campo en 'historico_tiradas'

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

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla de usuarios
        String createUsersTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT UNIQUE)";
        db.execSQL(createUsersTable);

        String createPartidasTable = "CREATE TABLE " + TABLE_PARTIDAS + " (" +
                COLUMN_PARTIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PARTIDA_JUGADOR_ID + " INTEGER, " +
                COLUMN_PARTIDA_FECHA + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_HIST_GANO + " INTEGER , " +  // Permite valores NULL
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
        if (oldVersion == 6) { // Nueva versión con la columna 'saldo' y la tabla 'partidas' actualizada
            // Primero creamos la nueva tabla 'partidas' con la columna 'saldo'
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PARTIDAS + "_new (" +
                    COLUMN_PARTIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PARTIDA_JUGADOR_ID + " INTEGER, " +
                    COLUMN_PARTIDA_FECHA + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_SALDO + " INTEGER DEFAULT 0, " +  // Agregar la columna saldo
                    "FOREIGN KEY(" + COLUMN_PARTIDA_JUGADOR_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))");

            // Copiar los datos de la tabla antigua a la nueva
            db.execSQL("INSERT INTO " + TABLE_PARTIDAS + "_new (" +
                    COLUMN_PARTIDA_ID + ", " + COLUMN_PARTIDA_JUGADOR_ID + ", " +
                    COLUMN_PARTIDA_FECHA + ", " + COLUMN_SALDO + ") " +
                    "SELECT " + COLUMN_PARTIDA_ID + ", " + COLUMN_PARTIDA_JUGADOR_ID + ", " +
                    COLUMN_PARTIDA_FECHA + ", " + COLUMN_SALDO + " FROM " + TABLE_PARTIDAS);

            // Eliminar la tabla antigua
            db.execSQL("DROP TABLE " + TABLE_PARTIDAS);

            // Renombrar la nueva tabla con el nombre original
            db.execSQL("ALTER TABLE " + TABLE_PARTIDAS + "_new RENAME TO " + TABLE_PARTIDAS);
        }
    }

    // Método para guardar el nombre de un usuario
    public boolean guardarNombre(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);

        long result = db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        db.close();

        if (result == -1) {
            Log.e("Database", "Error al guardar el nombre");
            return false; // No se guardó el nombre
        } else {
            Log.i("Database", "Nombre guardado correctamente");
            return true; // El nombre se guardó correctamente
        }
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
            return -1;  // Retorna -1 si el nombre es nulo
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int jugadorId = -1;  // Valor por defecto en caso de no encontrar al jugador

        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_ID);
                if (columnIndex >= 0) {
                    jugadorId = cursor.getInt(columnIndex);
                }
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


    // Método para crear una nueva partida para un jugador
    public long crearPartida(int usuarioId, int saldoFinal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTIDA_JUGADOR_ID, usuarioId);  // Asociar la partida al jugador
        values.put(COLUMN_SALDO, saldoFinal);

        values.put(COLUMN_PARTIDA_FECHA, System.currentTimeMillis()); // Guardar la fecha actual en milisegundos

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
        return db.rawQuery("SELECT " + COLUMN_HIST_PARTIDA_ID + ", " +  // Ahora también se obtiene el ID de la partida
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

        // Ejecutar la consulta, ajustando las columnas para que coincidan con los nombres de la base de datos
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_HIST_PARTIDA_ID + ", " +
                        COLUMN_HIST_USUARIO_ID + ", " +  // Asegurándote de obtener el ID del jugador también
                        COLUMN_HIST_FECHA + ", " +
                        COLUMN_HIST_SALDO + " FROM " + TABLE_HISTORICO +
                        " WHERE " + COLUMN_HIST_USUARIO_ID + " = ? ORDER BY " + COLUMN_HIST_FECHA + " DESC",
                new String[]{String.valueOf(usuarioId)});

        // Verificamos si la consulta obtuvo resultados
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Verificamos si el índice de cada columna es válido antes de acceder a su valor
                int idPartidaIndex = cursor.getColumnIndex(COLUMN_HIST_PARTIDA_ID);
                int usuarioIdIndex = cursor.getColumnIndex(COLUMN_HIST_USUARIO_ID); // Para obtener el id del jugador
                int fechaIndex = cursor.getColumnIndex(COLUMN_HIST_FECHA);
                int saldoIndex = cursor.getColumnIndex(COLUMN_HIST_SALDO);

                // Si alguno de los índices es -1, eso indica que la columna no existe
                if (idPartidaIndex != -1 && usuarioIdIndex != -1 && fechaIndex != -1 && saldoIndex != -1) {
                    // Si los índices son válidos, obtenemos los valores
                    int idPartida = cursor.getInt(idPartidaIndex);
                    int idJugador = cursor.getInt(usuarioIdIndex);  // Obtener el id del jugador (aunque no se use aquí, es útil)
                    String fecha = cursor.getString(fechaIndex);
                    double saldoFinal = cursor.getDouble(saldoIndex);  // El saldo final es un double

                    // Crear el objeto Partida y agregarlo a la lista
                    partidasList.add(new Partida(idPartida, idJugador, fecha, saldoFinal));
                } else {
                    // Si algún índice es inválido, puedes mostrar un mensaje de error o manejarlo de alguna otra forma
                    Log.e("DatabaseHelper", "Error: columna no encontrada en el cursor");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return partidasList;
    }

}
