package com.felix.asistentelectura;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.SensorManager;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "asistenteLectura";

    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE generos(_id integer primary key autoincrement, nombre text not null);");
        db.execSQL("CREATE TABLE libros(_id integer primary key autoincrement, titulo text not null, autor text not null, " +
                        "genero_id integer not null, total_paginas integer not null);");
        db.execSQL("CREATE TABLE wishlist(_id integer primary key, precio float);");
        db.execSQL("CREATE TABLE progreso(_id integer primary key," +
                        "porcentaje float not null, paginas_leidas integer not null);");
        db.execSQL("CREATE TABLE coleccion(_id integer primary key, fecha_adquisicion text);");

        ContentValues cv = new ContentValues();

        cv.put("nombre", "MISTERIO");
        db.insert("generos", null, cv);

        cv.put("nombre", "CIENCIA FICCION");
        db.insert("generos", null, cv);

        cv.put("nombre", "FANTASIA");
        db.insert("generos", null, cv);

        cv.put("nombre", "NO FICCION");
        db.insert("generos", null, cv);
        cv.clear();

        cv.put("titulo", "EL CANDOR DEL PADRE BROWN");
        cv.put("autor", "G. K. CHESTERTON");
        cv.put("genero_id", 1);
        cv.put("total_paginas", 300);
        db.insert("libros", null, cv);

        cv.put("titulo", "SHERLOCK HOLMES");
        cv.put("autor", "ARTHUR CONAN DOYLE");
        cv.put("genero_id", 1);
        cv.put("total_paginas", 400);
        db.insert("libros", null, cv);

        cv.put("titulo", "DUNE");
        cv.put("autor", "FRANK HERBERT");
        cv.put("genero_id", 2);
        cv.put("total_paginas", 600);
        db.insert("libros", null, cv);

        cv.put("titulo", "FUNDACION");
        cv.put("autor", "ISAAC ASIMOV");
        cv.put("genero_id", 2);
        cv.put("total_paginas", 300);
        db.insert("libros", null, cv);

        cv.put("titulo", "EL CAMINO DE LOS REYES");
        cv.put("autor", "BRANDON SANDERSON");
        cv.put("genero_id", 3);
        cv.put("total_paginas", 800);
        db.insert("libros", null, cv);

        cv.put("titulo", "EL SEÑOR DE LOS ANILLOS: LA COMUNIDAD DEL ANILLO");
        cv.put("autor", "J.R.R. TOLKIEN");
        cv.put("genero_id", 3);
        cv.put("total_paginas", 300);
        db.insert("libros", null, cv);

        cv.put("titulo", "EL ARTE DE LA GUERRA");
        cv.put("autor", "SUN TZU");
        cv.put("genero_id", 4);
        cv.put("total_paginas", 200);
        db.insert("libros", null, cv);
        cv.clear();

        cv.put("_id", 3);
        cv.put("porcentaje", 0.5);
        cv.put("paginas_leidas", 300);
        db.insert("progreso", null, cv);

        cv.put("_id", 5);
        cv.put("porcentaje", 1);
        cv.put("paginas_leidas", 800);
        db.insert("progreso", null, cv);
        cv.clear();

        cv.put("_id", 6);
        cv.put("precio", 399.99);
        db.insert("wishlist", null, cv);

        cv.put("_id", 1);
        cv.put("precio", 99.99);
        db.insert("wishlist", null, cv);
        cv.clear();

        cv.put("_id", 4);
        cv.put("fecha_adquisicion", "06/12/2022");
        db.insert("coleccion", null, cv);
        cv.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        android.util.Log.w("constants", "Actualizando Base de Datos. Se destruirá la información anterior.");
    }
}
