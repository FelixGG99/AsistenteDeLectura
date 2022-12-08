package com.felix.asistentelectura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;

public class HomePage extends AppCompatActivity implements LocationListener {

    private DataBaseHelper db;
    private Cursor cursorProgreso;
    private ListView listProgreso;

    private Button btnTerminados;
    private Button btnWishlist;
    private Button btnColeccion;
    private Button btnProgreso;
    private Button btnBiblioteca;

    private LocationManager locationManager;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Lists
        listProgreso = findViewById(R.id.listProgresoPortada);

        // Buttons
        btnTerminados = findViewById(R.id.btnTerminados);
        btnWishlist = findViewById(R.id.btnWishlist);
        btnColeccion = findViewById(R.id.btnColeccion);
        btnProgreso = findViewById(R.id.btnProgresoPortada);
        btnBiblioteca = findViewById(R.id.btnBibliotecas);

        // LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

        btnBiblioteca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?z=13&q=libraries");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        db = new DataBaseHelper(this);
        populateListProgreso(db);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursorProgreso.requery();

        Criteria criteria = new Criteria();
        criteria.setAccuracy((Criteria.ACCURACY_FINE));
        List<String> enabledProviders = locationManager.getProviders(criteria, true);

        String stringBuffer = "";
        for (String provider : enabledProviders) {
            stringBuffer += provider + " ";
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1000);
            }
            locationManager.requestSingleUpdate(provider, this, null);
        }
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle b) {}

}
