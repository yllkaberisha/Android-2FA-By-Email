package com.example.a2fa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.mindrot.jbcrypt.BCrypt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DB extends SQLiteOpenHelper {

    public DB(@Nullable Context context) {
        super(context, "2fa.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(firstName TEXT, lastName TEXT, email TEXT PRIMARY KEY, password TEXT)");
        db.execSQL("CREATE TABLE codeConfirmation(email TEXT, code TEXT, date DATE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS codeConfirmation");
        onCreate(db);
    }

    public boolean insertUser(String firstName, String lastName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        contentValues.put("firstName", firstName);
        contentValues.put("lastName", lastName);
        contentValues.put("email", email);
        contentValues.put("password", hashPassword);

        long result = db.insert("users", null, contentValues);
        return result != -1;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE email=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE email=?", new String[]{email});

        if (cursor.moveToFirst()) {
            String hashedPassword = cursor.getString(0);
            cursor.close();
            return BCrypt.checkpw(password, hashedPassword);
        }
        cursor.close();
        return false;
    }

    public boolean insertCodeConfirmation(String email, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Format the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        contentValues.put("email", email);
        contentValues.put("code", code);
        contentValues.put("date", currentDate);

        long result = db.insert("codeConfirmation", null, contentValues);
        return result != -1;
    }

    public String getCodeConfirmation(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Get the most recent code by date
        Cursor cursor = db.rawQuery("SELECT code FROM codeConfirmation WHERE email=? ORDER BY date DESC LIMIT 1", new String[]{email});

        String code = null;
        if (cursor.moveToFirst()) {
            code = cursor.getString(0);
        }
        cursor.close();
        return code;
    }
}
