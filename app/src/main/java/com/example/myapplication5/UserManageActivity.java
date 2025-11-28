package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserManageActivity extends AppCompatActivity {

    private TextView tvUserCount;
    private Button btnRefresh, btnDeleteAll;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);

        db = new DatabaseHelper(this);

        // Estos IDs tienen que existir en activity_user_manage.xml
        tvUserCount   = findViewById(R.id.tvUserCount);   // TextView: "Usuarios registrados: X"
        btnRefresh    = findViewById(R.id.btnRefresh);    // Botón: ACTUALIZAR DATOS
        btnDeleteAll  = findViewById(R.id.btnDeleteAll);  // Botón: ELIMINAR TODOS LOS USUARIOS

        // Mostrar cantidad apenas entro
        updateUserCount();

        // Botón ACTUALIZAR DATOS: vuelve a leer de la BD y muestra un Toast
        btnRefresh.setOnClickListener(v -> {
            updateUserCount();
            Toast.makeText(this, "Datos actualizados desde la base", Toast.LENGTH_SHORT).show();
        });

        // Botón ELIMINAR TODOS LOS USUARIOS
        btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar")
                    .setMessage("¿Eliminar todos los usuarios de la base?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        db.deleteAllUsers();      // borra en SQLite
                        updateUserCount();        // refresca el contador
                        Toast.makeText(this, "Todos los usuarios fueron eliminados", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    // Lee la cantidad real de usuarios en la base y actualiza el TextView
    private void updateUserCount() {
        int count = db.getUserCount();
        tvUserCount.setText("Usuarios registrados: " + count);
    }
}
