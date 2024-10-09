package deli_ever.app.Cliente;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;
import deli_ever.app.Vendedor.Tiendas.TiendaAdapter;
import deli_ever.app.Vendedor.Tiendas.TiendaClase;

public class FragmentInicio extends Fragment {

    private TiendaAdapter adapter;
    private final List<TiendaClase> tiendas = new ArrayList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference tiendaRef = database.getReference("Tienda");
    private TextView TXTNombreUsuario, textView, textViewNoTiendas;
    private CircleImageView ImagenUsuario;
    private LottieAnimationView ViewEmpty;

    public FragmentInicio() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TiendaAdapter(tiendas);
        recyclerView.setAdapter(adapter);

        TXTNombreUsuario = view.findViewById(R.id.TXTNombreUsuario);
        ImagenUsuario = view.findViewById(R.id.ImagenUsuario);
        ViewEmpty = view.findViewById(R.id.NoTiendas);
        textView = view.findViewById(R.id.textView);
        textViewNoTiendas = view.findViewById(R.id.textViewNoTiendas);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();

        tiendaRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tiendas.clear(); // Limpiar la lista antes de agregar nuevos datos

                // Recorrer los nodos hijos de dataSnapshot para obtener los datos de cada tienda
                for (DataSnapshot tiendaSnapshot : dataSnapshot.getChildren()) {
                    // Obtener los valores de cada atributo de la tienda
                    String nombre = tiendaSnapshot.child("nombre").getValue(String.class);
                    String descripcion = tiendaSnapshot.child("descripcion").getValue(String.class);
                    String direccion = tiendaSnapshot.child("direccion").getValue(String.class);
                    String extra = tiendaSnapshot.child("extra").getValue(String.class);
                    String usuarioAsociado = tiendaSnapshot.child("usuarioAsociado").getValue(String.class);
                    String estado = tiendaSnapshot.child("estado").getValue(String.class);
                    String imageUrl = tiendaSnapshot.child("imageUrl").getValue(String.class);
                    String tiendaId = tiendaSnapshot.child("id").getValue(String.class);

                    TiendaClase tienda = new TiendaClase(tiendaId, nombre, descripcion, direccion, extra, usuarioAsociado, imageUrl, estado);
                    tiendas.add(tienda);
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();

                // Mostrar u ocultar la imagen basada en si la lista está vacía
                if (tiendas.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    ViewEmpty.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    textViewNoTiendas.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    ViewEmpty.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textViewNoTiendas.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
            }
        });

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId);
        usuariosRef.child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombreUsuario = dataSnapshot.getValue(String.class);
                    TXTNombreUsuario.setText("Bienvenido(a): " + " " + nombreUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
            }
        });

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("imagenPerfil").child("url").getValue(String.class);
                if (imageUrl != null) {
                    Picasso.get().load(imageUrl).into(ImagenUsuario);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar cualquier error en la lectura de datos
            }
        });
    }
}