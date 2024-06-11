package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Principal.Login;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrarUsuarios extends AppCompatActivity {

    TextInputEditText contraseñaRegistro, correoElectronico, primerNombre, segundoNombre, primerApellido, segundoApellido, preguntaContraseña, respuestaContraseña, cedula, telefono, pais;
    Button registrarse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener los datos ingresados en los text input edit text
        // y demas elementos del formulario
        correoElectronico = findViewById(R.id.correoElectronico);
        contraseñaRegistro = findViewById(R.id.contraseñaRegistro);

        primerNombre = findViewById(R.id.primerNombre);
        segundoNombre = findViewById(R.id.segundoNombre);
        primerApellido = findViewById(R.id.primerApellido);
        segundoApellido = findViewById(R.id.segundoApellido);
        cedula = findViewById(R.id.registroCedula);
        preguntaContraseña = findViewById(R.id.preguntaContra);
        respuestaContraseña = findViewById(R.id.respuestaContra);
        registrarse = findViewById(R.id.registrarse);
        telefono = findViewById(R.id.telefonoUsuario);
        pais = findViewById(R.id.paisUsuario);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validarCampos();

            }
        });

    }

    //Este codigo es para validar los datos ingresados en el formulario
    public void validarCampos() {

        String email = correoElectronico.getText().toString().trim();
        String password = contraseñaRegistro.getText().toString().trim();

        String primerNombreText = primerNombre.getText().toString().trim();
        String segundoNombreText = segundoNombre.getText().toString().trim();
        String primerApellidoText = primerApellido.getText().toString().trim();
        String segundoApellidoText = segundoApellido.getText().toString().trim();
        String preguntaContraseñaText = preguntaContraseña.getText().toString().trim();
        String respuestaContraseñaText = respuestaContraseña.getText().toString().trim();
        String cedulaText = cedula.getText().toString().trim();
        String telefonoText = telefono.getText().toString().trim();
        String paisText = pais.getText().toString().trim();

        if (primerNombreText.isEmpty()) {
            primerNombre.setError("Ingrese su primer nombre");
            return;
        } else {
            primerNombre.setError(null);
        }

        if (segundoNombreText.isEmpty()) {
            segundoNombre.setError("Ingrese su segundo nombre");
            return;
        } else {
            segundoNombre.setError(null);
        }

        if (primerApellidoText.isEmpty()) {
            primerApellido.setError("Ingrese su primer apellido");
            return;
        } else {
            primerApellido.setError(null);
        }
        if (segundoApellidoText.isEmpty()) {
            segundoApellido.setError("Ingrese su segundo apellido");
            return;
        } else {
            segundoApellido.setError(null);
        }

        if (cedulaText.isEmpty()) {
            cedula.setError("Ingrese su número de cédula");
            return;
        } else {
            cedula.setError(null);
        }
        if (preguntaContraseñaText.isEmpty()) {
            preguntaContraseña.setError("Ingrese su pregunta de seguridad");
            return;
        } else {
            preguntaContraseña.setError(null);
        }
        if (respuestaContraseñaText.isEmpty()) {
            respuestaContraseña.setError("Ingrese su respuesta de seguridad");
            return;
        } else {
            respuestaContraseña.setError(null);
        }

        if (telefonoText.isEmpty()) {
            telefono.setError("Ingrese su número de teléfono");
            return;
        } else {
            telefono.setError(null);

        }
        if (paisText.isEmpty()) {
            pais.setError("Ingrese su país");
            return;

        } else {
            pais.setError(null);
        }


        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            correoElectronico.setError("Ingrese un correo electrónico válido");
            return;
        } else {

            correoElectronico.setError(null);

        }

        if (password.isEmpty() || password.length() < 9) {
            contraseñaRegistro.setError("La contraseña debe contener al menos 12 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            contraseñaRegistro.setError("La contraseña debe contener al menos un número");
            return;
        } else {

            contraseñaRegistro.setError(null);


        }
        registrarUsuario();

    }

    private final OkHttpClient client = new OkHttpClient();

    public void registrarUsuario() {
        String email = correoElectronico.getText().toString().trim();
        String password = contraseñaRegistro.getText().toString().trim();

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("cedula", cedula.getText().toString().trim());
            userJson.put("primer_nombre", primerNombre.getText().toString().trim());
            userJson.put("segundo_nombre", segundoNombre.getText().toString().trim());
            userJson.put("primer_apellido", primerApellido.getText().toString().trim());
            userJson.put("segundo_apellido", segundoApellido.getText().toString().trim());
            userJson.put("correo", email);
            userJson.put("contrasena", password);
            userJson.put("pregunta_contra", preguntaContraseña.getText().toString().trim());
            userJson.put("respuesta_contra", respuestaContraseña.getText().toString().trim());
            userJson.put("numero_tel", telefono.getText().toString().trim());
            userJson.put("pais", pais.getText().toString().trim());

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "http://192.168.1.4:5000/api/usuarios/registro";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(userJson.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(RegistrarUsuarios.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistrarUsuarios.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrarUsuarios.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistrarUsuarios.this, "Error al registrar el usuario.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
