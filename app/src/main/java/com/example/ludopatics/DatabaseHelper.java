package com.example.ludopatics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ludopatics.db";
    private static final int DATABASE_VERSION = 3;  // Aumentado por la nueva tabla 'partidas' y campo en 'historico_tiradas'

    // Definición de las tablas
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";

    private static final String TABLE_PARTIDAS = "partidas";
    private static final String COLUMN_PARTIDA_ID = "id";
    private static final String COLUMN_PARTIDA_JUGADOR_ID = "jugador_id";  // Nueva columna para vincular con 'usuarios'

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
                COLUMN_PARTIDA_FECHA + " TEXT, " +  // Columna de fecha y hora
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
            // Añadir la tabla 'partidas' y la relación con 'usuarios'
            db.execSQL("CREATE TABLE " + TABLE_PARTIDAS + " (" +
                    COLUMN_PARTIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PARTIDA_JUGADOR_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_PARTIDA_JUGADOR_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))");
        }
        if (oldVersion < 3) {
            // Añadir la relación entre 'partidas' y 'historico_tiradas'
            db.execSQL("ALTER TABLE " + TABLE_HISTORICO + " ADD COLUMN " + COLUMN_HIST_PARTIDA_ID + " INTEGER");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_partida_id ON " + TABLE_HISTORICO + " (" + COLUMN_HIST_PARTIDA_ID + ")");
        }
    }

    // Método para guardar el nombre de un usuario
    public boolean guardarNombre(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);

        long result = db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});

        int jugadorId = -1;  // Valor por defecto en caso de no encontrar al jugador

            if (cursor.moveToFirst()) {
                // Verificar que el índice de la columna es válido
                int columnIndex = cursor.getColumnIndex(COLUMN_ID);
                if (columnIndex >= 0) {
                    jugadorId = cursor.getInt(columnIndex);
                }

            cursor.close();
        }
        db.close();
        return jugadorId;
    }


    // Método para crear una nueva partida para un jugador
    public boolean crearPartida(int jugadorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARTIDA_JUGADOR_ID, jugadorId);

        long result = db.insert(TABLE_PARTIDAS, null, values);
        db.close();
        return result != -1;
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
}
