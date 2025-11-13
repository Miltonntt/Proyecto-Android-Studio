package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper db;
    ArrayList<String> usuarios;
    ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewUsers);

        cargarUsuarios();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int userId = ids.get(position);
            mostrarOpciones(userId);
        });
    }

    private void cargarUsuarios() {
        usuarios = new ArrayList<>();
        ids = new ArrayList<>();

        Cursor cursor = db.obtenerUsuarios();

        if (cursor.getCount() == 0) {
            usuarios.add("No hay usuarios registrados");
        } else {
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(0)); // ID
                String email = cursor.getString(1); // username
                String pass = cursor.getString(2);  // password
                usuarios.add(email + " - " + pass);
            }
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                usuarios
        );

        listView.setAdapter(adapter);
    }

    private void mostrarOpciones(int userId) {
        new AlertDialog.Builder(this)
                .setTitle("Opciones")
                .setItems(new CharSequence[]{"Editar", "Eliminar"}, (dialog, which) -> {
                    if (which == 0) editarUsuario(userId);
                    else eliminarUsuario(userId);
                })
                .show();
    }

    private void editarUsuario(int userId) {
        Toast.makeText(this, "Función Editar (podés agregar un popup después)", Toast.LENGTH_SHORT).show();
    }

    private void eliminarUsuario(int userId) {
        db.eliminarUsuario(userId);
        Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
        cargarUsuarios();
    }
}
