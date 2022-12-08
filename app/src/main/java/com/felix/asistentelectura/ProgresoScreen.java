package com.felix.asistentelectura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ProgresoScreen extends AppCompatActivity {

    private DataBaseHelper db;
    private Cursor cursorProgreso;
    private ListView listProgreso;

    private static final int UPDATE_ID = Menu.FIRST + 6;
    private static final int DELETE_ID = Menu.FIRST + 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso_screen);

        // Lists
        listProgreso    = findViewById(R.id.listProgreso);
        db = new DataBaseHelper(this);
        populateListProgreso(db);
    }

    private void populateListProgreso(DataBaseHelper db){
        cursorProgreso = db.getReadableDatabase().rawQuery(
                "SELECT progreso._id, titulo, autor, ((porcentaje * 100) || '%') as porc  " +
                        "FROM progreso INNER JOIN libros " +
                        "ON progreso._id = libros._id WHERE porcentaje < 1;",
                null);
        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row, cursorProgreso, new String[] {"titulo", "autor", "porc"},
                new int[] {R.id.txtTitle, R.id.txtAutor, R.id.txtPorcentaje});
        listProgreso.setAdapter(adapter);
        registerForContextMenu(listProgreso);
    }

    public static class DialogWrapper {
        EditText edtPaginasLeidas;
        View base;

        public DialogWrapper(View base) {
            this.base = base;
            edtPaginasLeidas = base.findViewById(R.id.edtPaginasLeidas);
        }

        public EditText getEdtPaginasLeidas() {
            if(edtPaginasLeidas == null)
                edtPaginasLeidas = base.findViewById(R.id.edtPaginasLeidas);
            return edtPaginasLeidas;
        }

        public Integer getPaginasLeidas() {
            return (Integer.parseInt(edtPaginasLeidas.getText().toString()));
        }

    }

    private void update(long rowId) {

        Cursor cursorLibros = db.getReadableDatabase().rawQuery(
                "SELECT _id, total_paginas FROM libros WHERE _id = " + rowId + ";", null);
        cursorLibros.moveToFirst();
        int totalPaginas = cursorLibros.getInt(1);

        LayoutInflater inflater = LayoutInflater.from(this);
        View addView = inflater.inflate(R.layout.update_progreso, null);
        TextView total = addView.findViewById(R.id.txtTotalPaginas);
        total.setText("/"+String.valueOf(totalPaginas));

        final ProgresoScreen.DialogWrapper dialogWrapper = new ProgresoScreen.DialogWrapper(addView);

        new AlertDialog.Builder(this)
                .setTitle(R.string.msgActualizarProgreso)
                .setView(addView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        processUpdate(dialogWrapper, rowId);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    public void processUpdate(ProgresoScreen.DialogWrapper dialogWrapper, long rowId) {

        int     paginasLeidas = 0;
        float   porcentajeLeido = 0.f;

        try {
            paginasLeidas = dialogWrapper.getPaginasLeidas();
        } catch (Exception e){
            Toast.makeText(ProgresoScreen.this, "Cantidad ingresada de paginas leidas invalida.", Toast.LENGTH_LONG).show();
            return;
        }

        if(paginasLeidas < 0){
            Toast.makeText(ProgresoScreen.this, "Cantidad ingresada de paginas leidas invalida.", Toast.LENGTH_LONG).show();
            return;
        }


        Cursor cursorLibros = db.getReadableDatabase().rawQuery(
                "SELECT _id, total_paginas FROM libros WHERE _id = " + rowId + ";", null);

        cursorLibros.moveToFirst();
        int totalPaginas = cursorLibros.getInt(1);
        if(totalPaginas < paginasLeidas){
            Toast.makeText(ProgresoScreen.this, "Cantidad ingresada de paginas leidas no puede ser mayor a la total.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            porcentajeLeido = (float) paginasLeidas / (float) totalPaginas;
        } catch (Exception e){
            Toast.makeText(ProgresoScreen.this, "No es posible realizar una division entre 0.", Toast.LENGTH_LONG).show();
            return;
        }

        if(porcentajeLeido == 1.f)
            Toast.makeText(ProgresoScreen.this, "Felicidades, terminaste el libro.", Toast.LENGTH_LONG).show();

        db.getWritableDatabase().execSQL("UPDATE progreso SET paginas_leidas = " + paginasLeidas + ", " +
                "porcentaje = " + porcentajeLeido + " WHERE _id = " + rowId);
        cursorProgreso.requery();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, UPDATE_ID, Menu.NONE, "Actualizar progreso...").setAlphabeticShortcut('a');
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Borrar...").setAlphabeticShortcut('b');
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case UPDATE_ID:
                update(info.id);
                break;
            case DELETE_ID:
                delete(info.id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void delete(long rowId) {
        if (rowId > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            processDelete(rowId);
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
    }

    public void processDelete(long rowId) {
        String[] args = { String.valueOf(rowId) };
        db.getReadableDatabase().delete("progreso", "_id=?", args);
        cursorProgreso.requery();
    }
}