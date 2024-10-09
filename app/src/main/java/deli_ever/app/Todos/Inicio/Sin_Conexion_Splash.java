package deli_ever.app.Todos.Inicio;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import deli_ever.app.R;


public class Sin_Conexion_Splash extends AppCompatActivity {

    Button btn_reintentar, btn_salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_conexion_splash);

        btn_reintentar = findViewById(R.id.btn_reintentar);
        btn_salir = findViewById(R.id.btn_salir);

        btn_reintentar.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(Sin_Conexion_Splash.this, SplashScreen.class));
                finish();
            } else {
                Toast.makeText(Sin_Conexion_Splash.this, "Aún no hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_salir.setOnClickListener(view -> {
            finishAffinity(); // Cierra la aplicación por completo
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
