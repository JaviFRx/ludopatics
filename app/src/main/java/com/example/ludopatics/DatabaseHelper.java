package com.example.ludopatics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "juego_db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    private static final String TABLE_NAME = "historial";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RESULTADO = "resultado";
    private static final String COLUMN_MONEDAS_APOSTADAS = "monedasApostadas";
    private static final String COLUMN_MONEDAS_GANADAS = "monedasGanadas";
    private static final String COLUMN_FECHA = "fecha";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESULTADO + " TEXT, " +
                COLUMN_MONEDAS_APOSTADAS + " INTEGER, " +
                COLUMN_MONEDAS_GANADAS + " INTEGER, " +
                COLUMN_FECHA + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar una partida de forma asíncrona con RxJava
    public Completable insertarPartidaAsync(String resultado, int monedasApostadas, int monedasGanadas, String fecha) {
        return Completable.create(emitter -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_RESULTADO, resultado);
            values.put(COLUMN_MONEDAS_APOSTADAS, monedasApostadas);
            values.put(COLUMN_MONEDAS_GANADAS, monedasGanadas);
            values.put(COLUMN_FECHA, fecha);

            long result = db.insert(TABLE_NAME, null, values);
            db.close();

            if (result != -1) {
                emitter.onComplete();
            } else {
                emitter.onError(new Exception("Error al insertar la partida"));
            }
        });
    }

    // Método para obtener el historial de partidas de forma asíncrona con RxJava
    public Single<Cursor> obtenerHistorialAsync() {
        return Single.create(emitter -> {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(query, null);
            emitter.onSuccess(cursor);  // Enviar el cursor al observador
        });
    }

    // Método para actualizar el saldo de monedas de forma asíncrona con RxJava
    public Completable actualizarSaldoAsync(int id, int nuevasMonedas) {
        return Completable.create(emitter -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_MONEDAS_GANADAS, nuevasMonedas);
            int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            db.close();

            if (rowsAffected > 0) {
                emitter.onComplete();
            } else {
                emitter.onError(new Exception("No se encontró el registro"));
            }
        });
    }
}
