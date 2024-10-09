package deli_ever.app.Todos.Inicio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import deli_ever.app.R;

public class Sin_Conexion extends AppCompatActivity {

    Button btn_salir, btn_reintentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_conexion);

        btn_salir = findViewById(R.id.btn_salir);
        btn_reintentar = findViewById(R.id.btn_reintentar);

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cierra todas las actividades y la aplicación
                finishAffinity();
            }
        });

        btn_reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    // Si hay conexión, cierra esta actividad
                    finish();
                } else {
                    // Muestra un Toast si no hay conexión
                    Toast.makeText(Sin_Conexion.this, "Aún no hay conexión", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Método para verificar la conexión a Internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
