package com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.Principal;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.actividad_3_dev_apps_luis_felipe_diaz_luiggi_salemi_soto_2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AnimacionApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        ImageView logo_cipa = findViewById(R.id.logo_cipa);

        logo_cipa.setAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Si el usuario ya está autenticado, redirigir a la actividad o pantalla de usuario
                    Intent intent = new Intent(AnimacionApp.this, MenuPrincipal.class);
                    startActivity(intent);
                    finish();
                    //si no esta autenticado, redirigir a la actividad de login
                } else {

                    Intent intent = new Intent(AnimacionApp.this, Login.class);
                    startActivity(intent);
                    finish();

                }
            }
        }, 3000);


    }


}