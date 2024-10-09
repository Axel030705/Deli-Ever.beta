package deli_ever.app.Administrador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;
import deli_ever.app.Todos.Perfil.Perfil_Activity;
import deli_ever.app.Vendedor.ClaseVendedor;
import deli_ever.app.Vendedor.VendedorAdapter;


public class SolicitudesVendedores extends AppCompatActivity {

    private ListView listViewVendedores;
    private List<ClaseVendedor> vendedoresList;
    private VendedorAdapter vendedorAdapter; // Puedes seguir utilizando el adaptador ClienteAdapter para vendedores si deseas
    private DatabaseReference vendedoresReference; // Cambia el nombre de la referencia
    public TextView TXTNombreAdmin;
    public CircleImageView ImagenAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes_vendedores);

        TXTNombreAdmin = findViewById(R.id.TXTNombreAdmin);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();
        ImagenAdmin = findViewById(R.id.ImagenAdmin);

        listViewVendedores = findViewById(R.id.listViewVendedores);
        vendedoresList = new ArrayList<>();
        vendedorAdapter = new VendedorAdapter(this, vendedoresList);
        listViewVendedores.setAdapter(vendedorAdapter);

        vendedoresReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        vendedoresReference.orderByChild("estado").equalTo("pendiente")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        vendedoresList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ClaseVendedor vendedor = snapshot.getValue(ClaseVendedor.class);
                            if (vendedor != null) {
                                vendedor.setUid(snapshot.getKey());
                                vendedoresList.add(vendedor);
                            }
                        }
                        vendedorAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores si es necesario
                    }
                });

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId); //Obtener el usuario registrado
        usuariosRef.child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombreUsuario = dataSnapshot.getValue(String.class);
                    TXTNombreAdmin.setText("Bienvenido(a): " + " " + nombreUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al cargar el usuario", Toast.LENGTH_LONG).show();
                finish();
                onDestroy();
            }
        });

        ImagenAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(SolicitudesVendedores.this, Perfil_Activity.class);
            startActivity(intent);
        });


        }

}

