package com.example.ludopatics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "usuarios.db";
    private static final int DATABASE_VERSION = 2;  // Aumentado porque agregamos una nueva tabla

    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";

    private static final String TABLE_HISTORICO = "historico_tiradas";
    private static final String COLUMN_HIST_ID = "id";
    private static final String COLUMN_HIST_USUARIO_ID = "usuario_id";
    private static final String COLUMN_HIST_APUESTA = "apuesta";
    private static final String COLUMN_HIST_RESULTADO = "resultado";
    private static final String COLUMN_HIST_SALDO = "saldo";
    private static final String COLUMN_HIST_GANO = "gano";
    private static final String COLUMN_HIST_FECHA = "fecha";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String createUsersTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT UNIQUE)";
        db.execSQL(createUsersTable);

        // Crear tabla de histórico de tiradas
        String createHistoricoTable = "CREATE TABLE " + TABLE_HISTORICO + " (" +
                COLUMN_HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HIST_USUARIO_ID + " INTEGER, " +
                COLUMN_HIST_APUESTA + " REAL, " +
                COLUMN_HIST_RESULTADO + " REAL, " +
                COLUMN_HIST_SALDO + " REAL, " +
                COLUMN_HIST_GANO + " INTEGER, " +
                COLUMN_HIST_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_HIST_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(createHistoricoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Si venimos de la versión 1, agregamos la nueva tabla
            db.execSQL("CREATE TABLE " + TABLE_HISTORICO + " (" +
                    COLUMN_HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HIST_USUARIO_ID + " INTEGER, " +
                    COLUMN_HIST_APUESTA + " REAL, " +
                    COLUMN_HIST_RESULTADO + " REAL, " +
                    COLUMN_HIST_SALDO + " REAL, " +
                    COLUMN_HIST_GANO + " INTEGER, " +
                    COLUMN_HIST_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_HIST_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))");
        }
    }

    public boolean guardarNombre(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);

        long result = db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
    }

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

    public boolean guardarTirada(int usuarioId, double apuesta, double resultado, double saldo, boolean gano) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIST_USUARIO_ID, usuarioId);
        values.put(COLUMN_HIST_APUESTA, apuesta);
        values.put(COLUMN_HIST_RESULTADO, resultado);
        values.put(COLUMN_HIST_SALDO, saldo);
        values.put(COLUMN_HIST_GANO, gano ? 1 : 0);

        long result = db.insert(TABLE_HISTORICO, null, values);
        db.close();
        return result != -1;
    }

    public Cursor obtenerHistorico(int usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_HIST_APUESTA + ", " +
                        COLUMN_HIST_RESULTADO + ", " +
                        COLUMN_HIST_SALDO + ", " +
                        COLUMN_HIST_GANO + ", " +
                        COLUMN_HIST_FECHA + " FROM " + TABLE_HISTORICO +
                        " WHERE " + COLUMN_HIST_USUARIO_ID + " = ? ORDER BY " + COLUMN_HIST_FECHA + " DESC",
                new String[]{String.valueOf(usuarioId)});
    }
}
