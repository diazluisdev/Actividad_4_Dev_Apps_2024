package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Fuentes;

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

public class GestionarFuentes extends AppCompatActivity {

    private static final String TAG = "GestionarFuentes";

    private EditText idFuente, fechaIngreso, nombreFuente, descripcionFuente;
    private Button botonGuardar, botonBuscar, botonEditar, botonEliminar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestionar_fuentes);

        // Inicialización de FirebaseAuth y FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicialización de vistas
        idFuente = findViewById(R.id.idFuente);
        fechaIngreso = findViewById(R.id.fechaIngreso);
        nombreFuente = findViewById(R.id.nombreFuente);
        descripcionFuente = findViewById(R.id.descripcionFuente);
        botonGuardar = findViewById(R.id.botonGuardar);
        botonBuscar = findViewById(R.id.botonBuscar);
        botonEditar = findViewById(R.id.botonEditar);
        botonEliminar = findViewById(R.id.botonEliminar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar DatePicker para fechaIngreso
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
                buscarFuente();
            }
        });

        // Acción botón Editar
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarFuente();
            }
        });

        // Acción botón Eliminar
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFuente();
            }
        });

        // Acción botón Guardar
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFuente();
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

    private void buscarFuente() {
        String id = idFuente.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el ID de la fuente", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("usuarios").document(userId).collection("fuentes").document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    fechaIngreso.setText(document.getString("fechaIngreso"));
                                    nombreFuente.setText(document.getString("nombreFuente"));
                                    descripcionFuente.setText(document.getString("descripcionFuente"));
                                } else {
                                    Toast.makeText(GestionarFuentes.this, "No se encontró la fuente", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Error al obtener el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void editarFuente() {
        String id = idFuente.getText().toString().trim();
        String fecha = fechaIngreso.getText().toString().trim();
        String nombre = nombreFuente.getText().toString().trim();
        String descripcion = descripcionFuente.getText().toString().trim();

        if (id.isEmpty() || fecha.isEmpty() || nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> fuente = new HashMap<>();
            fuente.put("fechaIngreso", fecha);
            fuente.put("nombreFuente", nombre);
            fuente.put("descripcionFuente", descripcion);

            db.collection("usuarios").document(userId).collection("fuentes").document(id)
                    .update(fuente)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GestionarFuentes.this, "Fuente actualizada correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Error al actualizar el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void eliminarFuente() {
        String id = idFuente.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el ID de la fuente", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("usuarios").document(userId).collection("fuentes").document(id)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GestionarFuentes.this, "Fuente eliminada correctamente", Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            } else {
                                Log.d(TAG, "Error al eliminar el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void guardarFuente() {
        String id = idFuente.getText().toString().trim();
        String fecha = fechaIngreso.getText().toString().trim();
        String nombre = nombreFuente.getText().toString().trim();
        String descripcion = descripcionFuente.getText().toString().trim();

        if (id.isEmpty() || fecha.isEmpty() || nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> fuente = new HashMap<>();
            fuente.put("idFuente", id);
            fuente.put("fechaIngreso", fecha);
            fuente.put("nombreFuente", nombre);
            fuente.put("descripcionFuente", descripcion);

            db.collection("usuarios").document(userId).collection("fuentes").document(id)
                    .set(fuente)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GestionarFuentes.this, "Fuente guardada correctamente", Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            } else {
                                Log.d(TAG, "Error al guardar el documento: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void limpiarCampos() {
        idFuente.setText("");
        fechaIngreso.setText("");
        nombreFuente.setText("");
        descripcionFuente.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }
}
