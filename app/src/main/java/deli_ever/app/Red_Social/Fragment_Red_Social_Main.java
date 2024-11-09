package deli_ever.app.Red_Social;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;

public class Fragment_Red_Social_Main extends Fragment {

    private EditText btn_publicar_todo;
    private ImageView btn_publicar, btn_publicar_imagen;
    private CircleImageView ImagenUsuario;
    
    private String nombre, correo, uid, tipoUsuario, imageUrl;

    public Fragment_Red_Social_Main() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_red_social_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //btn_publicar = view.findViewById(R.id.btn_publicar);
        btn_publicar_imagen = view.findViewById(R.id.btn_publicar_imagen);
        btn_publicar_todo = view.findViewById(R.id.btn_publicar_todo);
        ImagenUsuario = view.findViewById(R.id.ImagenUsuario);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();

        //Obtener datos del usuario
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId);
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombre = dataSnapshot.child("nombre").getValue(String.class);
                    correo = dataSnapshot.child("correo").getValue(String.class);
                    tipoUsuario = dataSnapshot.child("Tipo de usuario").getValue(String.class);
                    uid = dataSnapshot.child("uid").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
            }
        });

        //Obtener la imagen del usuario
        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageUrl = dataSnapshot.child("imagenPerfil").child("url").getValue(String.class);
                if (imageUrl != null) {
                    Picasso.get().load(imageUrl).into(ImagenUsuario);
                    ImagenUsuario.setBorderWidth(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar cualquier error en la lectura de datos
            }
        });

        btn_publicar_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para la Activity de destino
                Intent intent = new Intent(getContext(), Activity_Publicar_Todo.class);

                // Agregar los datos al Intent
                intent.putExtra("nombre", nombre);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("correo", correo);
                intent.putExtra("uid", uid);
                intent.putExtra("tipoUsuario", tipoUsuario);

                // Iniciar la Activity
                startActivity(intent);
            }
        });

    }
}