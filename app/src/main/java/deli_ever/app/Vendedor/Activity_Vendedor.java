package deli_ever.app.Vendedor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;
import deli_ever.app.Vendedor.Tiendas.TiendaClase;

public class Activity_Vendedor extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String usuarioId;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    boolean ImagenCargada = false;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText txtDireccionTienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        assert user != null;
        usuarioId = user.getUid();

        //Ubicaciones
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        EditText txtNombreTienda = findViewById(R.id.txt_NombreTienda);
        EditText txtDescripcionTienda = findViewById(R.id.txt_DescripcionTienda);
        txtDireccionTienda = findViewById(R.id.txt_DireccionTienda);
        EditText txtExtraTienda = findViewById(R.id.txt_ExtraTienda);
        Button btnRegistrarTienda = findViewById(R.id.Btn_RegistrarTienda);
        CircleImageView imagenTienda = findViewById(R.id.ImagenTienda);
        fetchLocationAndSet();

        imagenTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnRegistrarTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNombreTienda.getText().toString().isEmpty() ||
                        txtDescripcionTienda.getText().toString().isEmpty() ||
                        !ImagenCargada) {

                    Toast.makeText(Activity_Vendedor.this, "Rellena todos los campos y carga la imagen", Toast.LENGTH_SHORT).show();

                } else {
                    ProgressDialog progressDialog = new ProgressDialog(Activity_Vendedor.this);
                    progressDialog.setTitle("Registrando tienda");
                    progressDialog.setMessage("Por favor, espera...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String nombreTienda = txtNombreTienda.getText().toString();
                    String descripcionTienda = txtDescripcionTienda.getText().toString();
                    String direccionTienda = txtDireccionTienda.getText().toString();
                    String extraTienda = txtExtraTienda.getText().toString();

                    // Obtener la referencia al Storage de Firebase
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                    // Crea una referencia al archivo en el Storage con un nombre único
                    StorageReference imageRef = storageRef.child("imagenes_tiendas/" + UUID.randomUUID().toString());

                    // Carga la imagen al Storage
                    uploadImageToFirebaseStorage(imageRef, progressDialog, nombreTienda, descripcionTienda, direccionTienda, extraTienda, "Cerrado");
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Asigna la imagenUri a la variable de instancia imageUri
            ImagenCargada = true;
            CircleImageView imagenTienda = findViewById(R.id.ImagenTienda);
            imagenTienda.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(StorageReference imageRef, ProgressDialog progressDialog, String nombreTienda, String descripcionTienda, String direccionTienda, String extraTienda, String estadoTienda) {
        progressDialog.setTitle("Cargando imagen");
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);

        imageRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progress = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress(progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        TiendaClase tienda = new TiendaClase(null, nombreTienda, descripcionTienda, direccionTienda, extraTienda, usuarioId, imageUrl, estadoTienda);

                        CircleImageView imagenTienda = findViewById(R.id.ImagenTienda);
                        Glide.with(Activity_Vendedor.this)
                                .load(imageUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imagenTienda);

                        ImagenCargada = true;

                        DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda");
                        String tiendaId = tiendaRef.push().getKey();
                        tienda.setId(tiendaId);

                        // Asociar la tienda con el usuario en la base de datos
                        DatabaseReference usuarioTiendaRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioId).child("tiendaId");
                        usuarioTiendaRef.setValue(tiendaId);

                        tiendaRef.child(tiendaId).setValue(tienda).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(Activity_Vendedor.this, Vendedor_Main.class);
                                intent.putExtra("tiendaId", tiendaId);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void fetchLocationAndSet() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    // Use the location object to get the latitude and longitude
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Convert the latitude and longitude into a user-friendly address using Geocoder
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String fullAddress = address.getAddressLine(0);
                            txtDireccionTienda.setText(fullAddress);
                        } else {
                            txtDireccionTienda.setText("No se puede determinar la dirección");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        txtDireccionTienda.setText("No se puede determinar la dirección");
                    }
                } else {
                    Toast.makeText(Activity_Vendedor.this, "No se puede determinar la dirección", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
