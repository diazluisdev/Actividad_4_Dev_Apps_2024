package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Principal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Fuentes.GestionarFuentes;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Gastos.GestionarGastos;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Ingresos.GestionarIngresos;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Usuarios.DatosUsuarios;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuPrincipal extends AppCompatActivity {

    TextView vistaNombreUsuario;
    Button datosUsuarios, gestionarIngresos, gestionarFuentes, gestionarGastos, Logout;
    ProgressDialog progressDialog;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);

        vistaNombreUsuario = findViewById(R.id.nombreUsuario);
        Logout = findViewById(R.id.botonCerrarSesion);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cerrando sesión...");
        progressDialog.setCancelable(false);
        datosUsuarios = findViewById(R.id.datosPersonales);
        gestionarIngresos = findViewById(R.id.gestionarIngreso);
        gestionarFuentes = findViewById(R.id.GestionarFuentes);
        gestionarGastos = findViewById(R.id.GestionarGastos);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cedula = sharedPreferences.getString("CEDULA", "");

        if (cedula != null) {
            obtenerDatosUsuario(cedula);
        }

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        datosUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, DatosUsuarios.class);
            startActivity(intent);
        });

        gestionarIngresos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, GestionarIngresos.class);
            startActivity(intent);
        });

        gestionarFuentes.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, GestionarFuentes.class);
            startActivity(intent);
        });

        gestionarGastos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, GestionarGastos.class);
            startActivity(intent);
        });
    }

    private void obtenerDatosUsuario(String cedula) {
        String url = "http://192.168.1.4:5000/api/usuarios/" + cedula;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MenuPrincipal.this, "Error en la solicitud", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        JSONObject usuario = jsonResponse.getJSONObject("usuario");
                        String primerNombre = usuario.getString("primer_nombre");
                        String primerApellido = usuario.getString("primer_apellido");

                        runOnUiThread(() -> vistaNombreUsuario.setText(primerNombre + " " + primerApellido));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MenuPrincipal.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void cerrarSesion() {
        progressDialog.show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MenuPrincipal.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Retrasar el inicio de la siguiente actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(MenuPrincipal.this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MenuPrincipal.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // 2 segundos de retraso
    }
}
