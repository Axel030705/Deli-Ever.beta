package deli_ever.app.Vendedor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;
import deli_ever.app.Vendedor.Productos.agregar_producto;
import deli_ever.app.Vendedor.Tiendas.EditarTiendaForm;
import deli_ever.app.Vendedor.Tiendas.TiendaAdapter;
import deli_ever.app.Vendedor.Tiendas.TiendaClase;

public class FragmentInicioV extends Fragment {

    private TextView TXTNombreUsuarioVendedor;
    private CircleImageView ImagenUsuarioVendedor;
    private RecyclerView recyclerView;
    private TiendaAdapter adapter;
    private List<TiendaClase> tiendas = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tiendaRef = database.getReference("Tienda");
    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    private String tiendaId;

    public FragmentInicioV() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio_v, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImagenUsuarioVendedor = view.findViewById(R.id.ImagenUsuarioVendedor);
        TXTNombreUsuarioVendedor = view.findViewById(R.id.TXTNombreUsuarioVendedor);
        recyclerView = view.findViewById(R.id.recyclerViewVendedor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TiendaAdapter(tiendas);
        recyclerView.setAdapter(adapter);

        SwitchCompat switchButton = view.findViewById(R.id.btn_estado);
        TextView textViewCerrado = view.findViewById(R.id.textViewCerrado);
        TextView textViewAbierto = view.findViewById(R.id.textViewAbierto);
        Button Btn_EditarTienda = view.findViewById(R.id.Btn_EditarTienda);
        Button Btn_AgregarProducto = view.findViewById(R.id.Btn_AgregarProducto);
        Button Btn_EliminarTienda = view.findViewById(R.id.Btn_EliminarTienda);
        tiendaId = requireActivity().getIntent().getStringExtra("tiendaId");

        String userId = currentUser.getUid();

        // Obtén el estado de la tienda y configura el SwitchCompat
        tiendaRef.orderByChild("usuarioAsociado").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tiendas.clear();
                for (DataSnapshot tiendaSnapshot : dataSnapshot.getChildren()) {
                    String nombre = tiendaSnapshot.child("nombre").getValue(String.class);
                    String estado = tiendaSnapshot.child("estado").getValue(String.class);

                    TiendaClase tienda = new TiendaClase(
                            tiendaSnapshot.child("id").getValue(String.class),
                            nombre,
                            tiendaSnapshot.child("descripcion").getValue(String.class),
                            tiendaSnapshot.child("direccion").getValue(String.class),
                            tiendaSnapshot.child("extra").getValue(String.class),
                            tiendaSnapshot.child("usuarioAsociado").getValue(String.class),
                            tiendaSnapshot.child("imageUrl").getValue(String.class),
                            estado
                    );
                    tiendas.add(tienda);

                    // Configura el SwitchCompat según el estado de la tienda
                    if (estado != null) {
                        switchButton.setChecked(estado.equals("Abierto"));
                        textViewCerrado.setAlpha(estado.equals("Cerrado") ? 1f : 0.5f);
                        textViewAbierto.setAlpha(estado.equals("Abierto") ? 1f : 0.5f);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewCerrado.setAlpha(0.5f);  // Reduce la opacidad de "Cerrado"
                    textViewAbierto.setAlpha(1f);    // Restaura la opacidad de "Abierto"
                    cambiarEstadoTienda("Abierto");
                } else {
                    textViewCerrado.setAlpha(1f);    // Restaura la opacidad de "Cerrado"
                    textViewAbierto.setAlpha(0.5f);  // Reduce la opacidad de "Abierto"
                    cambiarEstadoTienda("Cerrado");
                }
            }
        });

        Btn_EditarTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), EditarTiendaForm.class);
                startActivity(intent);
            }
        });

        Btn_AgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), agregar_producto.class);
                intent.putExtra("tiendaId", tiendaId);
                startActivity(intent);
            }
        });

        Btn_EliminarTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Advertencia!")
                        .setMessage("Deseas eliminar la tienda y todo lo relacionado con esta?")
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            // Busca la tienda asociada al usuario vendedor
                            tiendaRef.orderByChild("usuarioAsociado").equalTo(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot tiendaSnapshot : dataSnapshot.getChildren()) {
                                                String tiendaId = tiendaSnapshot.getKey(); // Obtiene el ID de la tienda
                                                String imageUrl = tiendaSnapshot.child("imageUrl").getValue(String.class); // Obtiene la URL de la imagen de la tienda
                                                imageUrl = imageUrl.trim();

                                                // Ahora que tienes el ID de la tienda y la URL de la imagen, puedes eliminar la tienda
                                                EliminarTienda(tiendaId, imageUrl);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Manejar errores si la búsqueda se cancela
                                        }
                                    });
                        })
                        .show();
            }
        });

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId);
        usuariosRef.child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombreUsuario = dataSnapshot.getValue(String.class);
                    TXTNombreUsuarioVendedor.setText("Bienvenido(a): " + " " + nombreUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("imagenPerfil").child("url").getValue(String.class);
                if (imageUrl != null) {
                    Picasso.get().load(imageUrl).into(ImagenUsuarioVendedor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });
    }

    private void cambiarEstadoTienda(String nuevoEstado) {
        String userId = currentUser.getUid();

                    tiendaRef.orderByChild("usuarioAsociado").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot tiendaSnapshot : dataSnapshot.getChildren()) {
                                String tiendaId = tiendaSnapshot.child("id").getValue(String.class);
                                if (tiendaId != null) {
                                    DatabaseReference tiendaRef = database.getReference("Tienda").child(tiendaId);
                                    tiendaRef.child("estado").setValue(nuevoEstado)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Actualización exitosa
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Manejar errores
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejar errores
                        }
                    });
    }

    private void EliminarTienda(String tiendaId, String imageUrl) {
        DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda").child(tiendaId);

        tiendaRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storageRef.delete().addOnSuccessListener(aVoid1 -> {
                        Intent intent = new Intent(requireActivity(), Activity_Vendedor.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish(); // Finaliza la Activity actual para que no pueda volver a ella
                    }).addOnFailureListener(exception -> {
                        // Error al eliminar la imagen
                    });
                })
                .addOnFailureListener(exception -> {
                    // Error al eliminar la tienda
                });
    }
}
