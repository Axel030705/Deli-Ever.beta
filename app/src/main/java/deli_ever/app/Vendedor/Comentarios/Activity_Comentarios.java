package deli_ever.app.Vendedor.Comentarios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import deli_ever.app.Cliente.Tiendas_Activity;
import deli_ever.app.R;

public class Activity_Comentarios extends AppCompatActivity {

    private final DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda");
    ImageView atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        atras = findViewById(R.id.atras);

        atras.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Comentarios.this, Tiendas_Activity.class);
            startActivity(intent);
            finish();
        });

        // Obtener el ID_Tienda desde el Intent
        String idTienda = getIntent().getStringExtra("ID_Tienda");

        // Usar el ID_Tienda en tu actividad
        if (idTienda != null) {
            obtenerComentariosDeTienda(idTienda);
        } else {
            Toast.makeText(this, "No se encontró el ID de la tienda", Toast.LENGTH_SHORT).show();
        }
    }



    public void obtenerComentariosDeTienda(String idTienda) {
        // Buscar la tienda por idTienda
        tiendaRef.child(idTienda).child("Comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Comentario> listaComentarios = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Asegúrate de que los valores sean Strings
                        Comentario comentario = snapshot.getValue(Comentario.class);

                        if (comentario != null) {
                            listaComentarios.add(comentario);
                        }
                    }

                    actualizarRecyclerView(listaComentarios);
                } else {
                    Toast.makeText(Activity_Comentarios.this, "Aun no hay comentarios para esta Tienda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al obtener comentarios: " + databaseError.getMessage());
            }
        });
    }

    public void actualizarRecyclerView(List<Comentario> listaComentarios) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewComentarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ComentariosAdapter adapter = new ComentariosAdapter(listaComentarios);
        recyclerView.setAdapter(adapter);
    }
}
