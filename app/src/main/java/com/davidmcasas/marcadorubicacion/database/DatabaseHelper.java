package com.davidmcasas.marcadorubicacion.database;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.davidmcasas.marcadorubicacion.MainActivity;
import com.davidmcasas.marcadorubicacion.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    //--
    /*  Para implementar el patrón de diseño SINGLETON, que asegura que solo exista una instancia
        de una misma clase durante el ciclo de vida del programa, se necesita un atributo estático
        instance y un método getInstance()
    */
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    //--

    private static final String DATABASE_NAME = "MarcadorDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MARCADOR = "MARCADOR";
    private static final String KEY_MARCADOR_ID = "id";
    private static final String KEY_MARCADOR_NOMBRE = "nombre";
    private static final String KEY_MARCADOR_ICONO = "icono";
    private static final String KEY_MARCADOR_COLOR = "color";
    private static final String KEY_MARCADOR_COORDENADAS = "coordenadas";
    private static final String KEY_MARCADOR_DESCRIPCION = "descripcion";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    /*public DatabaseHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }*/

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MARCADOR = "CREATE TABLE " + TABLE_MARCADOR +
                "(" +
                KEY_MARCADOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_MARCADOR_NOMBRE + " VARCHAR," +
                KEY_MARCADOR_ICONO + " VARCHAR," +
                KEY_MARCADOR_COLOR + " VARCHAR," +
                KEY_MARCADOR_COORDENADAS + " VARCHAR," +
                KEY_MARCADOR_DESCRIPCION + " VARCHAR" +
                ")";

        db.execSQL(CREATE_TABLE_MARCADOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARCADOR);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addMarcador(Marcador marcador) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_MARCADOR_NOMBRE, marcador.getNombre());
            values.put(KEY_MARCADOR_ICONO, marcador.getIcono());
            values.put(KEY_MARCADOR_COLOR, marcador.getColor());
            values.put(KEY_MARCADOR_COORDENADAS, marcador.getCoordenadas());
            values.put(KEY_MARCADOR_DESCRIPCION, marcador.getDescripcion());

            db.insertOrThrow(TABLE_MARCADOR, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, MainActivity.appContext.getResources().getString(R.string.database_error));
        } finally {
            db.endTransaction();
        }
    }

    public void deleteMarcador(int i) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = new String[] { String.valueOf(i) };
        db.delete(TABLE_MARCADOR, whereClause, whereArgs);
    }

    public void deleteAllMarcadores() {
        List<Marcador> marcadores = getMarcadores();
        for (Marcador m : marcadores) {
            deleteMarcador(m.getId());
        }
    }

    public ArrayList<Marcador> getMarcadores() {

        ArrayList<Marcador> marcadores = new ArrayList();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_MARCADOR;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Marcador marcador = new Marcador(
                            cursor.getInt(cursor.getColumnIndex(KEY_MARCADOR_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_NOMBRE)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_ICONO)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_COLOR)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_COORDENADAS)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_DESCRIPCION))
                    );
                    marcadores.add(marcador);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, MainActivity.appContext.getResources().getString(R.string.database_error_load));
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return marcadores;
    }

    public Marcador getMarcador(int id) {

        String SELECT_QUERY = "SELECT * FROM " + TABLE_MARCADOR;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Marcador marcador = new Marcador(
                            cursor.getInt(cursor.getColumnIndex(KEY_MARCADOR_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_NOMBRE)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_ICONO)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_COLOR)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_COORDENADAS)),
                            cursor.getString(cursor.getColumnIndex(KEY_MARCADOR_DESCRIPCION))
                    );
                    if (marcador.getId() == id) {
                        return marcador;
                    }
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, MainActivity.appContext.getResources().getString(R.string.database_error_load));
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public void updateMarcador(Marcador marcador) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_MARCADOR_NOMBRE, marcador.getNombre());
        cv.put(KEY_MARCADOR_ICONO, marcador.getIcono());
        cv.put(KEY_MARCADOR_COLOR, marcador.getColor());
        cv.put(KEY_MARCADOR_COORDENADAS, marcador.getCoordenadas());
        cv.put(KEY_MARCADOR_DESCRIPCION, marcador.getDescripcion());
        getWritableDatabase().update(TABLE_MARCADOR, cv, "ID=?", new String[] {String.valueOf(marcador.getId())});

    }
}
