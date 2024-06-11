package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Gastos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;;
import androidx.appcompat.app.AppCompatActivity;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GestionarGastos extends AppCompatActivity {

    private EditText idGastos, fechaIngreso, nombreGasto, valorGasto, categoriaGasto, descripcionGasto;
    private Button botonGuardar, botonBuscar, botonEditar, botonEliminar;
    private OkHttpClient client;
    private String cedulaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_gastos);

        client = new OkHttpClient();

        // Obtener cédula del usuario desde SharedPreferences
        cedulaUsuario = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("cedula", null);

        // Inicializar elementos de la interfaz
        idGastos = findViewById(R.id.idGastos);
        fechaIngreso = findViewById(R.id.fechaIngreso);
        nombreGasto = findViewById(R.id.nombreGasto);
        valorGasto = findViewById(R.id.valorGasto);
        categoriaGasto = findViewById(R.id.categoriaGasto);
        descripcionGasto = findViewById(R.id.descripcionGasto);

        botonGuardar = findViewById(R.id.botonGuardar);
        botonBuscar = findViewById(R.id.botonBuscar);
        botonEditar = findViewById(R.id.botonEditar);
        botonEliminar = findViewById(R.id.botonEliminar);

        //eventos para los botones de la interfaz de usuario
        botonBuscar.setOnClickListener(v -> buscarGasto());
        botonEditar.setOnClickListener(v -> editarGasto());
        botonEliminar.setOnClickListener(v -> eliminarGasto());
        botonGuardar.setOnClickListener(v -> guardarGasto());
    }

    private void buscarGasto() {
        String id = idGastos.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el ID del gasto", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/gastos/buscar/" + id)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al buscar el gasto", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            fechaIngreso.setText(jsonResponse.getString("fecha_gasto"));
                            nombreGasto.setText(jsonResponse.getString("nombre_gasto"));
                            valorGasto.setText(jsonResponse.getString("valor_gasto"));
                            categoriaGasto.setText(jsonResponse.getString("categoria_gasto"));
                            descripcionGasto.setText(jsonResponse.getString("descripcion_gasto"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(GestionarGastos.this, "Error al procesar los datos del gasto", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Gasto no encontrado", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void editarGasto() {
        String id = idGastos.getText().toString().trim();
        String fecha = fechaIngreso.getText().toString().trim();
        String nombre = nombreGasto.getText().toString().trim();
        String valor = valorGasto.getText().toString().trim();
        String categoria = categoriaGasto.getText().toString().trim();
        String descripcion = descripcionGasto.getText().toString().trim();

        if (id.isEmpty() || fecha.isEmpty() || nombre.isEmpty() || valor.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_usuario", cedulaUsuario);
            jsonBody.put("fecha_gasto", fecha);
            jsonBody.put("nombre_gasto", nombre);
            jsonBody.put("valor_gasto", valor);
            jsonBody.put("categoria_gasto", categoria);
            jsonBody.put("descripcion_gasto", descripcion);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al construir la solicitud JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/gastos/actualizar/" + id)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al actualizar el gasto", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Gasto actualizado correctamente", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al actualizar el gasto", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void eliminarGasto() {
        String id = idGastos.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el ID del gasto", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/gastos/eliminar/" + id)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al eliminar el gasto", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(GestionarGastos.this, "Gasto eliminado correctamente", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al eliminar el gasto", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void guardarGasto() {
        String id = idGastos.getText().toString().trim();
        String fecha = fechaIngreso.getText().toString().trim();
        String nombre = nombreGasto.getText().toString().trim();
        String valor = valorGasto.getText().toString().trim();
        String categoria = categoriaGasto.getText().toString().trim();
        String descripcion = descripcionGasto.getText().toString().trim();

        if (id.isEmpty() || fecha.isEmpty() || nombre.isEmpty() || valor.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Eliminar separadores de miles
        valor = valor.replace(".", "").replace(",", ".");

        double cantidad;
        try {
            cantidad = Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor de gasto inválido", Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_usuario", cedulaUsuario);
            jsonBody.put("fecha_gasto", fecha);
            jsonBody.put("nombre_gasto", nombre);
            jsonBody.put("valor_gasto", valor);
            jsonBody.put("categoria_gasto", categoria);
            jsonBody.put("descripcion_gasto", descripcion);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al construir la solicitud JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/gastos/registrar/")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al guardar el gasto", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(GestionarGastos.this, "Gasto guardado correctamente", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(GestionarGastos.this, "Error al guardar el gasto", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void limpiarCampos() {
        idGastos.setText("");
        fechaIngreso.setText("");
        nombreGasto.setText("");
        valorGasto.setText("");
        categoriaGasto.setText("");
        descripcionGasto.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}