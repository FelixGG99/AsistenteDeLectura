package com.felix.asistentelectura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HomePage extends AppCompatActivity {

    private DataBaseHelper db;
    private Cursor cursorProgreso;
    private ListView listProgreso;

    private Button btnTerminados;
    private Button btnWishlist;
    private Button btnColeccion;
    private Button btnProgreso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Lists
        listProgreso    = findViewById(R.id.listProgresoPortada);

        // Buttons
        btnTerminados   = findViewById(R.id.btnTerminados);
        btnWishlist     = findViewById(R.id.btnWishlist);
        btnColeccion    = findViewById(R.id.btnColeccion);
        btnProgreso     = findViewById(R.id.btnProgresoPortada);

        // Button Listeners
        btnTerminados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, LibrosTerminados.class));
            }
        });

        btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, Wishlist.class));
            }
        });

        btnColeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, Coleccion.class));
            }
        });

        btnProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, ProgresoScreen.class));
            }
        });

        db = new DataBaseHelper(this);
        populateListProgreso(db);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursorProgreso.requery();
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
