package com.felix.asistentelectura;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Progreso extends AppCompatActivity {

    private DataBaseHelper db;
    private Cursor cursorProgreso;
    private ListView listProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso);

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
}