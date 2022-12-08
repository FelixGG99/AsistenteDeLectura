package com.felix.asistentelectura;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Wishlist extends AppCompatActivity {

    private DataBaseHelper db;
    private ListView listWishlist;
    private Cursor cursorWishlist;
    private FloatingActionButton btnAddTerminados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        listWishlist = findViewById(R.id.listWishlist);

        btnAddTerminados = findViewById(R.id.btnAddTerminados);
        btnAddTerminados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        db = new DataBaseHelper(this);
        populateListWishlist(db);
    }

    private void populateListWishlist(DataBaseHelper db) {
        cursorWishlist = db.getReadableDatabase().rawQuery(
                "SELECT wishlist._id, titulo, autor, ('$' || precio) as precio  " +
                        "FROM wishlist INNER JOIN libros " +
                        "ON wishlist._id = libros._id;",
                null);
        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row, cursorWishlist, new String[] {"titulo", "autor", "precio"},
                new int[] {R.id.txtTitle, R.id.txtAutor, R.id.txtPorcentaje});
        listWishlist.setAdapter(adapter);
        registerForContextMenu(listWishlist);
    }

    public static class DialogWrapper {
        EditText edtTitulo;
        EditText edtAutor;
        EditText edtTotalPaginas;
        EditText edtPrecio;
        View base;

        public DialogWrapper(View base) {
            this.base = base;
            edtTitulo       = base.findViewById(R.id.edtTitulo);
            edtAutor        = base.findViewById(R.id.edtAutor);
            edtTotalPaginas = base.findViewById(R.id.edtTotalPaginas);
            edtPrecio       = base.findViewById(R.id.edtPrecio);
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
        public EditText getEdtPrecio() {
            if(edtPrecio == null)
                edtPrecio = base.findViewById(R.id.edtPrecio);
            return edtPrecio;
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

        public float getPrecio() {
            return (Float.parseFloat(getEdtPrecio().getText().toString()));
        }

    }

    private void add() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View addView = inflater.inflate(R.layout.add_libro, null);
        final DialogWrapper dialogWrapper = new DialogWrapper(addView);

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

    public void processAdd(DialogWrapper dialogWrapper) {

        String titulo = dialogWrapper.getTitulo().toUpperCase();
        if(titulo == null || titulo.length() == 0){
            Toast.makeText(Wishlist.this, "El titulo no puede estar vacio.", Toast.LENGTH_LONG).show();
            return;
        }
        String autor = dialogWrapper.getAutor().toUpperCase();
        if(autor == null || autor.length() == 0){
            Toast.makeText(Wishlist.this, "El autor no puede estar vacio.", Toast.LENGTH_LONG).show();
            return;
        }

        int totalPaginas = 0;
        float precio = 0;

        try {
            totalPaginas = dialogWrapper.getTotalPaginas();
        } catch (Exception e){
            Toast.makeText(Wishlist.this, "Cantidad ingresada como total de paginas invalida.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            precio = dialogWrapper.getPrecio();
        } catch (Exception e){
            Toast.makeText(Wishlist.this, "Cantidad ingresada como precio invalida.", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues cv = new ContentValues();
        Cursor cursorLibros = db.getReadableDatabase().rawQuery(
                "SELECT _id FROM libros WHERE titulo = '" + titulo + "' AND autor = '" + autor + "';", null);
        if(cursorLibros.getCount() == 0) {
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

        Cursor cursorWishlistTemp = db.getReadableDatabase().rawQuery(
                "SELECT _id FROM wishlist WHERE _id = " + libroIndex + ";", null);
        if(cursorWishlistTemp.getCount() == 0) {
            cv.put("_id", libroIndex);
            cv.put("precio", precio);
            db.getWritableDatabase().insert("wishlist", null, cv);
            cv.clear();
            cursorWishlistTemp.requery();
        }

        cursorWishlist.requery();
    }
}
