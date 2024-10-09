package deli_ever.app.Vendedor.Productos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;
import deli_ever.app.Vendedor.Vendedor_Main;

public class agregar_producto extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private DatabaseReference tiendaRef;
    private DatabaseReference productosRef;
    private EditText txtNombreProducto, txtDescripcionProducto, txtPrecioProducto, txtExtraProducto, txtCantidadProducto;
    private Button btnGuardarProducto, btnSalir;
    private CircleImageView imgProducto;
    private String usuarioId;
    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri imagenSeleccionada;
    private String tiendaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        // Obtener referencia a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tiendaRef = database.getReference("Tienda");

        // Obtener el usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            usuarioId = currentUser.getUid();
            cargarTienda();
        }

        // Inicializar vistas
        txtNombreProducto = findViewById(R.id.txt_NombreProducto);
        txtDescripcionProducto = findViewById(R.id.txt_DescripcionProducto);
        txtPrecioProducto = findViewById(R.id.txt_PrecioProducto);
        txtExtraProducto = findViewById(R.id.txt_ExtraProducto);
        txtCantidadProducto = findViewById(R.id.txt_CantidadProducto);
        btnGuardarProducto = findViewById(R.id.Btn_GuardarProducto);
        btnSalir = findViewById(R.id.Btn_Salir);
        imgProducto = findViewById(R.id.ImagenProducto);
        imgProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(agregar_producto.this, Vendedor_Main.class);
                startActivity(intent);
                finish();
            }
        });

        configurarGuardarProducto();
    }

    private void cargarTienda() {
        tiendaRef.orderByChild("usuarioAsociado").equalTo(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot tiendaSnapshot = dataSnapshot.getChildren().iterator().next();
                    tiendaId = tiendaSnapshot.getKey();
                    if (tiendaId != null) {
                        DatabaseReference tiendaUsuarioRef = tiendaRef.child(tiendaId);
                        productosRef = tiendaUsuarioRef.child("productos");
                    } else {
                        // Manejar el caso en el que no se encuentre la tienda del usuario
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error de la consulta
            }
        });
    }

    private void configurarGuardarProducto() {
        btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el vendedor
                String nombreProducto = txtNombreProducto.getText().toString();
                String descripcionProducto = txtDescripcionProducto.getText().toString();
                String precioProducto = txtPrecioProducto.getText().toString();
                String cantidadProducto = txtCantidadProducto.getText().toString();

                // Validar que se hayan ingresado valores para los campos obligatorios
                if (TextUtils.isEmpty(nombreProducto) || TextUtils.isEmpty(descripcionProducto) || TextUtils.isEmpty(precioProducto) || TextUtils.isEmpty(cantidadProducto)){
                    Toast.makeText(agregar_producto.this, "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que el precio sea un número
                if (!esNumero(precioProducto)) {
                    Toast.makeText(agregar_producto.this, "El precio debe ser un número válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que la cantidad del producto sea un número
                if (!esNumero(cantidadProducto)) {
                    Toast.makeText(agregar_producto.this, "La cantidad del producto debe ser un número válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar si se ha seleccionado una imagen
                if (imagenSeleccionada == null) {
                    Toast.makeText(agregar_producto.this, "Por favor, selecciona una imagen del producto", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(agregar_producto.this);
                progressDialog.setMessage("Cargando producto...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                guardarProducto();
            }
        });
    }

    private void guardarProducto() {
        // Obtener los valores ingresados por el vendedor
        String nombreProducto = txtNombreProducto.getText().toString();
        String descripcionProducto = txtDescripcionProducto.getText().toString();
        String precioProducto = txtPrecioProducto.getText().toString();
        String extraProducto = txtExtraProducto.getText().toString();
        String cantidadProducto = txtCantidadProducto.getText().toString();

        // Generar un nuevo identificador único para el producto
        String idProducto = productosRef.push().getKey();

        // Crear un objeto Producto con los valores ingresados
        String tiendaString = tiendaId.toString();
        Producto producto = new Producto(idProducto, nombreProducto, descripcionProducto, precioProducto, extraProducto, cantidadProducto, "0", tiendaString);

        // Subir la imagen a Firebase Storage
        assert idProducto != null;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("productos").child(idProducto);
        storageRef.putFile(imagenSeleccionada)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtener la URL de la imagen subida
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imagenUrl = uri.toString();
                                // Agregar la URL de la imagen al objeto Producto
                                producto.setImagenUrl(imagenUrl);
                                // Guardar el producto en la base de datos bajo el identificador generado
                                productosRef.child(idProducto).setValue(producto)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(agregar_producto.this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
                                                limpiarCampos();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(agregar_producto.this);
                                                builder.setTitle("Deseas agregar otro producto?")
                                                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                startActivity(new Intent(agregar_producto.this, Vendedor_Main.class));
                                                                finish();
                                                            }
                                                        })
                                                        .show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(agregar_producto.this, "Error al agregar el producto", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(agregar_producto.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void limpiarCampos() {
        int colorAzulCielo = R.color.azulCielo;
        txtNombreProducto.setText("");
        txtDescripcionProducto.setText("");
        txtPrecioProducto.setText("");
        txtExtraProducto.setText("");
        txtCantidadProducto.setText("");
        imgProducto.setImageResource(colorAzulCielo);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imagenSeleccionada = data.getData();
            imgProducto.setImageURI(imagenSeleccionada);
        }
    }

    private boolean esNumero(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
