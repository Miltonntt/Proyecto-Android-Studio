package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

        // Botón para ir al panel de gestión
        Button btnOpenManage = findViewById(R.id.btnOpenManage);
        btnOpenManage.setOnClickListener(v -> {
            Intent i = new Intent(UserListActivity.this, UserManageActivity.class);
            startActivity(i);
        });

        // Primera carga
        cargarUsuarios();

        // Click en un item -> Editar / Eliminar
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (ids.size() == 0) {
                // Solo hay el texto "No hay usuarios registrados"
                return;
            }
            int userId = ids.get(position);
            mostrarOpciones(userId);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que volvés a esta pantalla, recarga desde SQLite
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        usuarios = new ArrayList<>();
        ids = new ArrayList<>();

        Cursor cursor = db.obtenerUsuarios();

        if (cursor.getCount() == 0) {
            usuarios.add("No hay usuarios registrados");
        } else {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);          // id
                String email = cursor.getString(1); // username
                String pass = cursor.getString(2);  // password

                ids.add(id);
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
        // Traer datos actuales del usuario
        Cursor c = db.obtenerUsuarioPorId(userId);
        if (!c.moveToFirst()) {
            c.close();
            Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentEmail = c.getString(1); // username
        String currentPass  = c.getString(2); // password
        c.close();

        // Inflar layout del popup
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        EditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        EditText etPass  = dialogView.findViewById(R.id.etEditPassword);

        // Setear valores actuales
        etEmail.setText(currentEmail);
        etPass.setText(currentPass);

        new AlertDialog.Builder(this)
                .setTitle("Editar usuario")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newEmail = etEmail.getText().toString().trim();
                    String newPass  = etPass.getText().toString().trim();

                    if (newEmail.isEmpty() || newPass.isEmpty()) {
                        Toast.makeText(this, "No puede haber campos vacíos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok = db.actualizarUsuario(userId, newEmail, newPass);
                    if (ok) {
                        Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                        cargarUsuarios(); // refresca lista
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(int userId) {
        db.eliminarUsuario(userId);
        Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
        cargarUsuarios();
    }
}
