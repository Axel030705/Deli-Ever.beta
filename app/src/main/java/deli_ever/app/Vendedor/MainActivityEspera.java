package deli_ever.app.Vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import deli_ever.app.R;

public class MainActivityEspera extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_espera);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference usuarioReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
            usuarioReference.child("estado").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Verificar el estado del usuario y redirigir en consecuencia
                    String nuevoEstado = dataSnapshot.getValue(String.class);
                    if ("aprobado".equals(nuevoEstado)) {
                        // El usuario ha sido aprobado, redirigir a la actividad principal de Vendedores
                        startActivity(new Intent(MainActivityEspera.this, Activity_Vendedor.class));
                        finish(); // Cerrar la actividad de espera
                    } else if ("rechazado".equals(nuevoEstado)) {
                        mostrarMensajeDeRechazo();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores si es necesario
                }
            });
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

    }

    private void mostrarMensajeDeRechazo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Solicitud Rechazada")
                .setMessage("Lo sentimos, su solicitud de registro ha sido rechazada.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Redirigir al usuario a una actividad de inicio de sesión o página principal
                    //startActivity(new Intent(MainActivityEspera.this, MainActivity.class));
                    finish(); // Cerrar la actividad de espera
                })
                .show();
    }

    public void Salir(View view){
        finish();
        onDestroy();
    }

}