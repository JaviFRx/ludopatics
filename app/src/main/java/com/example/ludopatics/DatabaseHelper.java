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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ludopatics.db";
    private static final int DATABASE_VERSION = 7;  // Aumentado por la nueva tabla 'partidas' y campo en 'historico_tiradas'

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
        // En este ejemplo, asumimos que estamos migrando desde la versión 6 a la 7.
        if (oldVersion <= 6) {
            // 1. Crear la nueva tabla con la estructura deseada (incluye la columna 'saldo')
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PARTIDAS + "_new (" +
                    COLUMN_PARTIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PARTIDA_JUGADOR_ID + " INTEGER, " +
                    COLUMN_PARTIDA_FECHA + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_SALDO + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(" + COLUMN_PARTIDA_JUGADOR_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))");

            // 2. Migrar los datos de la tabla antigua a la nueva.
            // Como la tabla antigua solo tiene 3 columnas, asignamos un 0 de forma predeterminada a la columna 'saldo'
            db.execSQL("INSERT INTO " + TABLE_PARTIDAS + "_new (" +
                    COLUMN_PARTIDA_ID + ", " +
                    COLUMN_PARTIDA_JUGADOR_ID + ", " +
                    COLUMN_PARTIDA_FECHA + ", " +
                    COLUMN_SALDO + ") " +
                    "SELECT " +
                    COLUMN_PARTIDA_ID + ", " +
                    COLUMN_PARTIDA_JUGADOR_ID + ", " +
                    COLUMN_PARTIDA_FECHA + ", " +
                    "0 FROM " + TABLE_PARTIDAS);

            // 3. Eliminar la tabla antigua
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTIDAS);

            // 4. Renombrar la nueva tabla para que adopte el nombre original
            db.execSQL("ALTER TABLE " + TABLE_PARTIDAS + "_new RENAME TO " + TABLE_PARTIDAS);
        }
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


    // Método para crear una nueva partida para un jugador
    public long crearPartida(int usuarioId, int saldo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTIDA_JUGADOR_ID, usuarioId);  // Asociar la partida al jugador
        values.put(COLUMN_SALDO, saldo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fecha = dateFormat.format(new Date());
        values.put(COLUMN_PARTIDA_FECHA, fecha); // Guardar la fecha actual en milisegundos

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

        // Consulta para obtener las partidas
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PARTIDA_ID + ", " +
                        COLUMN_PARTIDA_JUGADOR_ID + ", " +
                        COLUMN_PARTIDA_FECHA + ", " +
                        COLUMN_SALDO + " FROM " + TABLE_PARTIDAS +
                        " WHERE " + COLUMN_PARTIDA_JUGADOR_ID + " = ? ORDER BY " + COLUMN_PARTIDA_FECHA + " DESC",
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
                        int saldoIndex = cursor.getColumnIndex(COLUMN_SALDO);

                        // Validar que las columnas existen y no son -1
                        if (idIndex != -1 && jugadorIdIndex != -1 && fechaIndex != -1 && saldoIndex != -1) {
                            int idPartida = cursor.getInt(idIndex);
                            int idJugador = cursor.getInt(jugadorIdIndex);
                            String fecha = cursor.getString(fechaIndex);
                            int saldoFinal = cursor.getInt(saldoIndex);

                            // Agregar la partida a la lista
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
