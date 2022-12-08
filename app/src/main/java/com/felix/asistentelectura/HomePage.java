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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Lists
        listProgreso    = findViewById(R.id.listWishlist);

        // Buttons
        btnTerminados   = findViewById(R.id.btnTerminados);
        btnWishlist     = findViewById(R.id.btnWishlist);
        btnColeccion    = findViewById(R.id.btnColeccion);

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

//public class HomePage extends ListActivity {
//
//    private DataBaseHelper db;
//    private Cursor cursorProgreso;
//    private ListView listProgreso;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        db = new DataBaseHelper(this);
//        populateListProgreso(db);
//    }
//
//    private void populateListProgreso(DataBaseHelper db){
////        cursorProgreso = db.getReadableDatabase().rawQuery(
////                "SELECT progreso._id, titulo, autor, porcentaje " +
////                        "FROM progreso INNER JOIN libros " +
////                        "ON progreso._id = libros._id WHERE porcentaje < 1;",
////            null);
//        cursorProgreso = db.getReadableDatabase().rawQuery("SELECT _id, titulo, autor FROM libros;", null);
//        //cursorProgreso.moveToFirst();
//        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row, cursorProgreso, new String[] {"titulo", "autor"},
//                new int[] {R.id.txtTitle, R.id.txtAutor});
//        setListAdapter(adapter);
//        registerForContextMenu(getListView());
//    }
//}
