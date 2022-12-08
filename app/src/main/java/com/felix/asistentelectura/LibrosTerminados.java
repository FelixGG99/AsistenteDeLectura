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
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibrosTerminados extends AppCompatActivity {

    private DataBaseHelper db;
    private ListView listTerminados;
    private Cursor cursorTerminados;
    private SimpleCursorAdapter adapter;

    private Button btnAdd;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libros_terminados);

        listTerminados = findViewById(R.id.listWishlist);

//        btnAdd      = findViewById(R.id.btnAddTerminados);
//        btnDelete   = findViewById(R.id.btnDeleteTerminados);

        db = new DataBaseHelper(this);
        populateListTerminados(db);
    }

    private void populateListTerminados(DataBaseHelper db) {
        cursorTerminados = db.getReadableDatabase().rawQuery(
                "SELECT progreso._id, titulo, autor, ((porcentaje * 100) || '%') as porc  " +
                        "FROM progreso INNER JOIN libros " +
                        "ON progreso._id = libros._id WHERE porcentaje = 1;",
                null);
        adapter = new SimpleCursorAdapter(this, R.layout.row, cursorTerminados, new String[] {"titulo", "autor", "porc"},
                new int[] {R.id.txtTitle, R.id.txtAutor, R.id.txtPorcentaje});
        listTerminados.setAdapter((ListAdapter) adapter);
        registerForContextMenu(listTerminados);
    }

//    public static class DialogWrapperTerminados {
////
////        Spinner spnLibros;
////        View base;
////
////        public DialogWrapperTerminados(View base, Cursor cursor) {
////            this.base = base;
////            spnLibros = base.findViewById(R.id.spnLibroTerminados);
////            SimpleCursorAdapter adapter = new SimpleCursorAdapter(LibrosTerminados.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, cursor, new String[] {"titulo"}, new int[] {R.id.text1});
////        }
////
////        public Spinner getSpnLibros() {
////            if(spnLibros == null) {
////                spnLibros = base.findViewById(R.id.spnLibroTerminados);
////            }
////            return spnLibros;
////        }
////
////        public String getLibro() {
////            return (getSpnLibros().toString());
////        }
////
////        public void fillSpinnerFromCursor(Cursor cursor) {
////            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.)
////            getSpnLibros().setAdapter(adapter);
////        }
////    }
////
////    public void deleteLibrosTerminados(final int rowId) {
////
////        LayoutInflater inflater = LayoutInflater.from(this);
////        View addView = inflater.inflate(R.layout.del_terminados, null);
////        final DialogWrapperTerminados dialogWrapper = new DialogWrapperTerminados(addView);
////
////        if(rowId > 0) {
////            new AlertDialog.Builder(this)
////                .setTitle(R.string.msgDeleteLibroTerminado)
////                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        processDelete(rowId);
////                    }
////                })
////                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {}
////                })
////                .show();
////        }
////    }


}