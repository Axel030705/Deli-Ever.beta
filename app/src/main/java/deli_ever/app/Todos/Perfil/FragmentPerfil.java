package deli_ever.app.Todos.Perfil;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;
import deli_ever.app.Todos.Inicio.MainActivity;

public class FragmentPerfil extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String userId;
    private DatabaseReference userRef;
    private CircleImageView ImagenUsuario;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Button btnCerrarSesion, Btn_Update_Password;
    private TextView txtNombreUsuarioPerfil;
    private TextView txtCorreoPerfil;
    private TextView txtTipoPerfil;
    private EditText txt_update_password;

    public FragmentPerfil() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        userId = firebaseAuth.getCurrentUser().getUid();
        userRef = usersRef.child(userId);
        ImagenUsuario = view.findViewById(R.id.ImagenUsuario);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("imagenes_perfil").child(UUID.randomUUID().toString());

        txtNombreUsuarioPerfil = view.findViewById(R.id.TXTNombreUsuarioPerfil);
        txtCorreoPerfil = view.findViewById(R.id.TXTCorreoPerfil);
        txtTipoPerfil = view.findViewById(R.id.TXTTipoPerfil);
        txt_update_password = view.findViewById(R.id.txt_update_password);
        Btn_Update_Password = view.findViewById(R.id.Btn_Update_Password);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);
                    String correo = dataSnapshot.child("correo").getValue(String.class);
                    String tipo = dataSnapshot.child("Tipo de usuario").getValue(String.class);

                    if (tipo.equals("") && password.equals("Administrador")) { // Valida si es Administrador
                        txtNombreUsuarioPerfil.setText(nombre);
                        txtTipoPerfil.setText("Administrador");
                        txtCorreoPerfil.setText(correo);
                    } else {
                        txtNombreUsuarioPerfil.setText(nombre);
                        txtCorreoPerfil.setText(correo);
                        txtTipoPerfil.setText(tipo);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja cualquier error en la lectura de datos
            }
        });

        //Escuchar si el usuario pone algo dentro del campo
        txt_update_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario usar este método en este caso
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Verifica si hay texto en el campo
                if (s.length() > 0) {
                    // Muestra el botón
                    Btn_Update_Password.setVisibility(View.VISIBLE);
                    btnCerrarSesion.setVisibility(View.GONE);
                } else {
                    // Oculta el botón
                    Btn_Update_Password.setVisibility(View.GONE);
                    btnCerrarSesion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No es necesario usar este método en este caso
            }
        });

        // Actualizar la contraseña en la base de datos
        Btn_Update_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = txt_update_password.getText().toString().trim();

                if (!newPassword.isEmpty()) {
                    // Actualiza el campo 'password' en Firebase Realtime Database
                    userRef.child("password").setValue(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Contraseña actualizada exitosamente en la base de datos", Toast.LENGTH_SHORT).show();
                                    txt_update_password.setText(""); // Limpia el campo de texto
                                    Btn_Update_Password.setVisibility(View.GONE);
                                    btnCerrarSesion.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getActivity(), "Error al actualizar la contraseña en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Ingresa una nueva contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnCerrarSesion = view.findViewById(R.id.Btn_CerrarSesionPerfil);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                cerrarSesion();
            }
            });

            LinearLayout layoutImagePerfil = view.findViewById(R.id.LayoutImagePerfil);
        layoutImagePerfil.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View view){
                if (txtTipoPerfil.getText().equals("Administrador")) {
                    Toast.makeText(requireActivity(), "Función solo para clientes y vendedores", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            }
            });

            // Recupera la URL de la imagen almacenada en Firebase Database
        userRef.child("imagenPerfil").

            addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                    if (dataSnapshot.exists()) {
                        ImageInfo imageInfo = dataSnapshot.getValue(ImageInfo.class);
                        String imageUrl = imageInfo.getUrl();

                        // Carga la imagen almacenada en el CircleImageView
                        Picasso.get().load(imageUrl).into(ImagenUsuario);
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){
                    // Maneja cualquier error en la recuperación de la URL de la imagen
                }
            });
        }

        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                // Aquí puedes cargar la imagen seleccionada en el CircleImageView y subirla a Firebase Storage
                ImagenUsuario.setImageURI(selectedImageUri);

                // Sube el archivo al Firebase Storage
                storageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Obtiene la URL de descarga del archivo cargado
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Guarda la URL en Firebase Database o realiza cualquier otra operación necesaria
                                        String downloadUrl = uri.toString();
                                        // Crea un objeto para guardar la información de la imagen
                                        ImageInfo imageInfo = new ImageInfo(downloadUrl);

                                        // Guarda la información de la imagen en Firebase Database bajo el perfil del usuario
                                        userRef.child("imagenPerfil").setValue(imageInfo)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // La URL de la imagen se guardó exitosamente en Firebase Database bajo el perfil del usuario
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Maneja cualquier error al guardar la URL de la imagen en Firebase Database
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Maneja cualquier error en la obtención de la URL
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Maneja cualquier error en la carga del archivo
                            }
                        });
            }
        }

        private void cerrarSesion () {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            // Limpiar la pila de actividades
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Finalizar la actividad actual
            requireActivity().finish();
        }


    }