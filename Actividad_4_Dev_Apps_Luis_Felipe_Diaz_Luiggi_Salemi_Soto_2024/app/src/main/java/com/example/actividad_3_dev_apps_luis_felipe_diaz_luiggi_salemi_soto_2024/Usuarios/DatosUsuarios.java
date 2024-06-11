package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Usuarios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import okhttp3.Response;
import okhttp3.RequestBody;

public class DatosUsuarios extends AppCompatActivity {

    TextInputEditText password, email, pregunta, respuesta, telefono, pais, currentPasswordInput;
    Button guardar, eliminarUser;
    String cedulaUsuario;

    private OkHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_usuarios);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cedulaUsuario = sharedPreferences.getString("CEDULA", "");

        // Set up the system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = new OkHttpClient();

        password = findViewById(R.id.updateContraseña);
        email = findViewById(R.id.updateCorreo);
        pregunta = findViewById(R.id.updatePregunta);
        respuesta = findViewById(R.id.updateRespuesta);
        telefono = findViewById(R.id.updateTelefono);
        pais = findViewById(R.id.updatePais);
        guardar = findViewById(R.id.actualizarDatos);
        eliminarUser = findViewById(R.id.eliminarUsuario);


        cargarDatosUsuario();

        guardar.setOnClickListener(v -> {
            validarCampos();
        });

        eliminarUser.setOnClickListener(v -> {
            eliminarCuentaUsuario();
        });
    }

    private void cargarDatosUsuario() {

        String url = "http://192.168.1.4:5000/api/usuarios/" + cedulaUsuario;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (jsonResponse.has("usuario")) {
                            JSONObject usuarioData = jsonResponse.getJSONObject("usuario");
                            runOnUiThread(() -> {
                                try {
                                    telefono.setText(usuarioData.getString("numero_tel"));
                                    email.setText(usuarioData.getString("correo"));
                                    pregunta.setText(usuarioData.getString("pregunta_contra"));
                                    respuesta.setText(usuarioData.getString("respuesta_contra"));
                                    pais.setText(usuarioData.getString("pais"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(DatosUsuarios.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    public void validarCampos() {
        String contraseña = password.getText().toString().trim();
        String correo = email.getText().toString().trim();
        String preguntaContraseña = pregunta.getText().toString().trim();
        String respuestaContraseña = respuesta.getText().toString().trim();
        String telefonoUsuario = telefono.getText().toString().trim();
        String paisUsuario = pais.getText().toString().trim();


        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            email.setError("Ingrese un correo electrónico válido");
            return;
        } else {
            email.setError(null);
        }

        if (preguntaContraseña.isEmpty()) {
            pregunta.setError("Ingrese su pregunta de seguridad");
            return;
        } else {
            pregunta.setError(null);
        }

        if (respuestaContraseña.isEmpty()) {
            respuesta.setError("Ingrese su respuesta de seguridad");
            return;
        } else {
            respuesta.setError(null);
        }

        if (telefonoUsuario.isEmpty()) {
            telefono.setError("Ingrese su teléfono");
            return;
        } else {
            telefono.setError(null);
        }

        if (paisUsuario.isEmpty()) {
            pais.setError("Ingrese su país");
            return;
        } else {
            pais.setError(null);
        }

        actualizarDatosUsuario(telefonoUsuario, correo, preguntaContraseña, respuestaContraseña, paisUsuario, contraseña);
    }

    private void actualizarDatosUsuario(String telefono, String correo, String pregunta, String respuesta, String pais, String contraseña) {

        // Construir el cuerpo de la solicitud JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("telefono", telefono);
            jsonBody.put("correo", correo);
            jsonBody.put("pregunta_contra", pregunta);
            jsonBody.put("respuesta_contra", respuesta);
            jsonBody.put("pais", pais);
            jsonBody.put("contraseña", contraseña);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al construir la solicitud JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear una solicitud HTTP utilizando OkHttp
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
        Request request = new Request.Builder()
                .url("http://192.168.1.4:5000/api/usuarios/actualizar/" + cedulaUsuario)
                .put(requestBody)
                .build();

        // Realizar la solicitud HTTP de forma asíncrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Manejar la respuesta exitosa de la API
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, message, Toast.LENGTH_SHORT).show());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Manejar errores de la solicitud HTTP
                    runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    private void eliminarCuentaUsuario() {

        // Construir la URL para la solicitud HTTP DELETE
        String url = "http://192.168.1.4:5000/api/usuarios/" + cedulaUsuario;

        // Crear una solicitud HTTP utilizando OkHttp
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        // Realizar la solicitud HTTP de forma asíncrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al eliminar la cuenta del usuario", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Manejar la respuesta exitosa de la API
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> {
                            Toast.makeText(DatosUsuarios.this, message, Toast.LENGTH_SHORT).show();
                            // Redirigir a la pantalla de inicio de sesión
                            startActivity(new Intent(DatosUsuarios.this, Login.class));
                            finish();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Manejar errores de la solicitud HTTP
                    runOnUiThread(() -> Toast.makeText(DatosUsuarios.this, "Error al eliminar la cuenta del usuario", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}





