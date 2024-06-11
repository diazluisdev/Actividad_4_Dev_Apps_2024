package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Ingresos;


import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Principal.MenuPrincipal;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

import java.util.Map;

public class GestionarIngresos extends AppCompatActivity {

    private EditText idIngreso, fechaIngreso, valorIngreso, descripcionIngreso;
    private Button botonGuardar, botonBuscar, botonEditar, botonEliminar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestionar_ingresos);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firestore y Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        idIngreso = findViewById(R.id.idIngreso);
        fechaIngreso = findViewById(R.id.fechaIngreso);
        valorIngreso = findViewById(R.id.valorIngreso);
        descripcionIngreso = findViewById(R.id.descripcionIngreso);
        botonGuardar = findViewById(R.id.botonGuardar);
        botonBuscar = findViewById(R.id.botonBuscar);
        botonEditar = findViewById(R.id.botonEditar);
        botonEliminar = findViewById(R.id.botonEliminar);

        fechaIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog();

            }


        });
        // Acción botón Buscar
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarIngreso();
            }
        });

        // Acción botón Editar
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarIngreso();
            }
        });

        // Acción botón Eliminar
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarIngreso();
            }
        });

        // Acción botón Guardar
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarIngreso();
            }
        });

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Mes en DatePicker empieza en 0, por eso sumamos 1
                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                fechaIngreso.setText(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void buscarIngreso() {
        String id = idIngreso.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el ID del ingreso", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("usuarios").document(userId).collection("ingresos").document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    fechaIngreso.setText(document.getString("fechaIngreso"));
                                    valorIngreso.setText(document.getString("valorIngreso"));
                                    descripcionIngreso.setText(document.getString("descripcionIngreso"));
                                } else {
                                    Toast.makeText(GestionarIngresos.this, "No se encontró el ingreso", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Error al obtener el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void editarIngreso() {
        String id = idIngreso.getText().toString().trim();
        String fecha = fechaIngreso.getText().toString().trim();
        String valor = valorIngreso.getText().toString().trim();
        String descripcion = descripcionIngreso.getText().toString().trim();

        if (id.isEmpty() || fecha.isEmpty() || valor.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> ingreso = new HashMap<>();
            ingreso.put("fechaIngreso", fecha);
            ingreso.put("valorIngreso", valor);
            ingreso.put("descripcionIngreso", descripcion);

            db.collection("usuarios").document(userId).collection("ingresos").document(id)
                    .update(ingreso)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GestionarIngresos.this, "Ingreso actualizado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Error al actualizar el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void eliminarIngreso() {
        String id = idIngreso.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el ID del ingreso", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("usuarios").document(userId).collection("ingresos").document(id)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GestionarIngresos.this, "Ingreso eliminado correctamente", Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            } else {
                                Log.d(TAG, "Error al eliminar el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void guardarIngreso() {
        String id = idIngreso.getText().toString().trim();
        String fecha = fechaIngreso.getText().toString().trim();
        String valor = valorIngreso.getText().toString().trim();
        String descripcion = descripcionIngreso.getText().toString().trim();

        if (id.isEmpty() || fecha.isEmpty() || valor.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> ingreso = new HashMap<>();
            ingreso.put("idIngreso", id);
            ingreso.put("fechaIngreso", fecha);
            ingreso.put("valorIngreso", valor);
            ingreso.put("descripcionIngreso", descripcion);

            db.collection("usuarios").document(userId).collection("ingresos").document(id)
                    .set(ingreso)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GestionarIngresos.this, "Ingreso guardado correctamente", Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            } else {
                                Log.d(TAG, "Error al guardar el documento: ", task.getException());
                            }
                        }
                    });
        }
    }


    private void limpiarCampos() {
        idIngreso.setText("");
        fechaIngreso.setText("");
        valorIngreso.setText("");
        descripcionIngreso.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }


}
