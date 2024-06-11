package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Principal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Usuarios.RecuperarContrasena;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Usuarios.RegistrarUsuarios;
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

public class Login extends AppCompatActivity {

    TextInputEditText campoCedula, campoContraseña;
    TextView recuperarContraseña, acercaDe;
    private ProgressDialog progressDialog;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        campoCedula = findViewById(R.id.campoCedulaLogin);
        campoContraseña = findViewById(R.id.campoContraseñaLogin);
        recuperarContraseña = findViewById(R.id.recuperarContraseña);
        Button registrarse = findViewById(R.id.BotonRegistrarse);
        Button BotonLogin = findViewById(R.id.BotonLogin);
        acercaDe = findViewById(R.id.acercaApp);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);
        client = new OkHttpClient();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegistrarUsuarios.class);
                startActivity(intent);
            }
        });

        acercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, AcercaDe.class);
                startActivity(intent);
            }
        });

        BotonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCamposLogin();
            }
        });

        recuperarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RecuperarContrasena.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void validarCamposLogin() {
        String cedula = campoCedula.getText().toString().trim();
        String password = campoContraseña.getText().toString().trim();

        if (cedula.isEmpty()) {
            campoCedula.setError("Ingrese una cédula válida");
            return;
        } else {
            campoCedula.setError(null);
        }

        if (password.isEmpty() || password.length() < 9) {
            campoContraseña.setError("La contraseña debe contener al menos 12 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            campoContraseña.setError("La contraseña debe contener al menos un número");
            return;
        } else {
            campoContraseña.setError(null);
        }

        LogicaInciarSesion(cedula, password);
    }

    public void LogicaInciarSesion(String cedula, String contraseña) {
        progressDialog.show();

        String url = "http://192.168.1.4:5000/api/usuarios/login";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String json = "{\"cedula\":\"" + cedula + "\", \"contrasena\":\"" + contraseña + "\"}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                });
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String message = jsonResponse.getString("message");
                        String primerNombre = jsonResponse.getString("primer_nombre");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CEDULA", cedula);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MenuPrincipal.class);
                            intent.putExtra("PRIMER_NOMBRE", primerNombre);
                            startActivity(intent);
                            finish();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
}
