package deli_ever.app.Todos.Inicio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import deli_ever.app.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @SuppressLint("AppCompatMethod")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ocultar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Establecer en pantalla completa
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Cambiar el color de la barra de estado
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.azulCielo));
        setContentView(R.layout.activity_splas_screen);
        firebaseAuth = FirebaseAuth.getInstance();

        GifImageView gifView = findViewById(R.id.LogoAnimation);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.logo);
            gifView.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int tiempo = 3900;
        new Handler().postDelayed(this::checkInternetConnection, tiempo);
    }

    private void checkInternetConnection() {
        if (isNetworkAvailable()) {
            VerificarUsuario();
        } else {
            startActivity(new Intent(SplashScreen.this, Sin_Conexion_Splash.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void VerificarUsuario() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        if (firebaseUser == null) {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        } else {
            ValidadorSesion validadorSesion = new ValidadorSesion(firebaseUser, usuariosRef, getApplicationContext());
            validadorSesion.validarInicioSesion();
        }
    }
}
