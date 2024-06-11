package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Usuarios;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Principal.Login;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import com.google.android.material.textfield.TextInputEditText;

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

public class RecuperarContrasena extends AppCompatActivity {

    TextInputEditText campoCedula, campoRespuesta;
    Button botonBuscarPregunta, botonValidarRespuesta;
    TextView preguntaRecuperacion;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        campoCedula = findViewById(R.id.campoCedula);
        campoRespuesta = findViewById(R.id.respuestaRecuperacion);
        botonBuscarPregunta = findViewById(R.id.BotonBuscarPregunta);
        botonValidarRespuesta = findViewById(R.id.botonValidarRespuesta);
        preguntaRecuperacion = findViewById(R.id.preguntaRecuperacion);

        botonBuscarPregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarPreguntaRecuperacion();
            }
        });

        botonValidarRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarRespuesta();
            }
        });
    }

    private void buscarPreguntaRecuperacion() {
        String cedula = campoCedula.getText().toString().trim();

        if (cedula.isEmpty()) {
            campoCedula.setError("Ingrese su cédula");
            return;
        }

        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/usuarios/recuperar/" + cedula)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RecuperarContrasena.this, "Error en la solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String pregunta = jsonResponse.getString("pregunta");

                        runOnUiThread(() -> {
                            preguntaRecuperacion.setText(pregunta);
                            preguntaRecuperacion.setVisibility(View.VISIBLE);
                            campoRespuesta.setVisibility(View.VISIBLE);
                            botonValidarRespuesta.setVisibility(View.VISIBLE);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(RecuperarContrasena.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void validarRespuesta() {
        String cedula = campoCedula.getText().toString().trim();
        String respuesta = campoRespuesta.getText().toString().trim();

        if (respuesta.isEmpty()) {
            campoRespuesta.setError("Ingrese su respuesta");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("respuesta", respuesta);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/usuarios/recuperar/validar/" + cedula)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RecuperarContrasena.this, "Error en la solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String mensaje = jsonResponse.getString("message");

                        runOnUiThread(() -> mostrarContrasena(mensaje));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(RecuperarContrasena.this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void mostrarContrasena(String contrasena) {
        // Mostrar un diálogo con la contraseña
        AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarContrasena.this);
        builder.setTitle("Contraseña recuperada");
        builder.setMessage(contrasena);
        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent intent = new Intent(RecuperarContrasena.this, Login.class);
            startActivity(intent);
            finish();
        });
        builder.show();
    }


    //codigo para regresar al login con el boton de atras del movil
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
