package com.felix.asistentelectura;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Coleccion extends AppCompatActivity {

    private DataBaseHelper db;
    private ListView listColeccion;
    private Cursor cursorColeccion;
    private FloatingActionButton btnAddColeccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleccion);

        listColeccion   = findViewById(R.id.listColeccion);
        btnAddColeccion = findViewById(R.id.btnAddColeccion);
        btnAddColeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        db = new DataBaseHelper(this);
        populateListColeccion(db);
    }

    private void populateListColeccion(DataBaseHelper db) {
        cursorColeccion = db.getReadableDatabase().rawQuery(
                "SELECT coleccion._id, titulo, autor, fecha_adquisicion  " +
                        "FROM coleccion INNER JOIN libros " +
                        "ON coleccion._id = libros._id;",
                null);
        Log.v("cc", DatabaseUtils.dumpCursorToString(cursorColeccion));
        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row, cursorColeccion, new String[] {"titulo", "autor", "fecha_adquisicion"},
                new int[] {R.id.txtTitle, R.id.txtAutor, R.id.txtPorcentaje});
        listColeccion.setAdapter(adapter);
        registerForContextMenu(listColeccion);
    }

    public static class DialogWrapper {
        EditText edtTitulo;
        EditText edtAutor;
        EditText edtTotalPaginas;
        View base;

        public DialogWrapper(View base) {
            this.base = base;
            edtTitulo       = base.findViewById(R.id.edtTitulo);
            edtAutor        = base.findViewById(R.id.edtAutor);
            edtTotalPaginas = base.findViewById(R.id.edtTotalPaginas);
        }

        public EditText getEdtTitulo() {
            if(edtTitulo == null)
                edtTitulo = base.findViewById(R.id.edtTitulo);
            return edtTitulo;
        }

        public EditText getEdtAutor() {
            if(edtAutor == null)
                edtAutor = base.findViewById(R.id.edtAutor);
            return edtAutor;
        }
        public EditText getEdtTotalPaginas() {
            if(edtTotalPaginas == null)
                edtTotalPaginas = base.findViewById(R.id.edtTotalPaginas);
            return edtTotalPaginas;
        }

        public String getTitulo() {
            return (getEdtTitulo().getText().toString());
        }

        public String getAutor() {
            return (getEdtAutor().getText().toString());
        }

        public int getTotalPaginas() {
            return Integer.parseInt(getEdtTotalPaginas().getText().toString());
        }
    }

    private void add() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View addView = inflater.inflate(R.layout.add_libro, null);
        final Wishlist.DialogWrapper dialogWrapper = new Wishlist.DialogWrapper(addView);

        new AlertDialog.Builder(this)
                .setTitle(R.string.msgAddWishlist)
                .setView(addView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        processAdd(dialogWrapper);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    public void processAdd(Wishlist.DialogWrapper dialogWrapper) {

        String titulo = dialogWrapper.getTitulo().toUpperCase();
        if (titulo == null || titulo.length() == 0) {
            Toast.makeText(Coleccion.this, "El titulo no puede estar vacio.", Toast.LENGTH_LONG).show();
            return;
        }
        String autor = dialogWrapper.getAutor().toUpperCase();
        if (autor == null || autor.length() == 0) {
            Toast.makeText(Coleccion.this, "El autor no puede estar vacio.", Toast.LENGTH_LONG).show();
            return;
        }

        int totalPaginas = 0;

        try {
            totalPaginas = dialogWrapper.getTotalPaginas();
        } catch (Exception e) {
            Toast.makeText(Coleccion.this, "Cantidad ingresada como total de paginas invalida.", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues cv = new ContentValues();
        Cursor cursorLibros = db.getReadableDatabase().rawQuery(
                "SELECT _id FROM libros WHERE titulo = '" + titulo + "' AND autor = '" + autor + "';", null);
        if (cursorLibros.getCount() == 0) {
            cv.put("titulo", titulo);
            cv.put("autor", autor);
            cv.put("total_paginas", totalPaginas);
            cv.put("genero_id", 1);
            db.getWritableDatabase().insert("libros", null, cv);
            cv.clear();
            cursorLibros.requery();
        }
        cursorLibros.moveToFirst();
        int libroIndex = cursorLibros.getInt(0);

        Cursor cursorColeccionTemp = db.getReadableDatabase().rawQuery(
                "SELECT _id FROM coleccion WHERE _id = " + libroIndex + ";", null);
        if(cursorColeccionTemp.getCount() == 0) {
            String pattern = "dd/MM/yyyy";
            String dateInString =new SimpleDateFormat(pattern).format(new Date());
            cv.put("_id", libroIndex);
            cv.put("fecha_adquisicion", dateInString);
            db.getWritableDatabase().insert("coleccion", null, cv);
            cv.clear();
            cursorColeccionTemp.requery();
        }
        cursorColeccion.requery();
    }
}