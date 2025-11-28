package com.example.myapplication5;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla centralizado
    private static final String TABLE_USERS = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_USERS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT, " +
                        "password TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // REGISTRAR USUARIO
    public boolean insertarUsuario(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    // LOGIN (usuario + password)
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?",
                new String[]{email, password}
        );

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // VERIFICAR SOLO EL EMAIL (para evitar duplicados)
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{email}
        );
        boolean existe = c.getCount() > 0;
        c.close();
        return existe;
    }

    // OBTENER TODOS LOS USUARIOS (ID + USERNAME + PASS)
    public Cursor obtenerUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, username, password FROM " + TABLE_USERS,
                null
        );
    }

    // ELIMINAR POR ID
    public void eliminarUsuario(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_USERS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // CANTIDAD TOTAL DE USUARIOS
    public int getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_USERS,
                null
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ELIMINAR TODOS LOS USUARIOS
    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
    }
    // Obtener un usuario por su ID
    public Cursor obtenerUsuarioPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, username, password FROM users WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Actualizar correo y contraseña de un usuario
    public boolean actualizarUsuario(int id, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        int rows = db.update("users", cv, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

}
