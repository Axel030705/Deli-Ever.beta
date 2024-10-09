package deli_ever.app.Vendedor.Productos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class editar_producto_form extends AppCompatActivity {

    private CircleImageView ImagenProductoEdit;
    private EditText txt_NombreProductoEdit, txt_DescripcionProductoEdit, txt_PrecioProductoEdit, txt_ExtraProductoEdit, txt_CantidadProductoEdit;
    Button Btn_GuardarCambios_EP, Btn_Salir_EP;
    private DatabaseReference Tienda;
    private String productoId;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private Uri selectedImageUri;
    private ValueEventListener productoListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto_form);

        productoId = getIntent().getStringExtra("productoId");
        String tiendaId = getIntent().getStringExtra("tiendaId");

        if (productoId == null || tiendaId == null) {
            // Manejar el caso en el que productoId o tiendaId sean nulos
            Toast.makeText(this, "Producto o tienda no válidos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Tienda = FirebaseDatabase.getInstance().getReference().child("Tienda").child(tiendaId).child("productos").child(productoId);
        ImagenProductoEdit = findViewById(R.id.ImagenProductoEdit);
        txt_NombreProductoEdit = findViewById(R.id.txt_NombreProductoEdit);
        txt_DescripcionProductoEdit = findViewById(R.id.txt_DescripcionProductoEdit);
        txt_PrecioProductoEdit = findViewById(R.id.txt_PrecioProductoEdit);
        txt_ExtraProductoEdit = findViewById(R.id.txt_ExtraProductoEdit);
        txt_CantidadProductoEdit = findViewById(R.id.txt_CantidadProductoEdit);
        Btn_GuardarCambios_EP = findViewById(R.id.Btn_GuardarCambios_EP);
        Btn_Salir_EP = findViewById(R.id.Btn_Salir_EP);

        productoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Los datos se encuentran en la ruta correcta
                    Producto producto = dataSnapshot.getValue(Producto.class);

                    if (producto != null) {
                        Picasso.get().load(producto.getImagenUrl()).into(ImagenProductoEdit);
                        txt_NombreProductoEdit.setText(producto.getNombre());
                        txt_DescripcionProductoEdit.setText(producto.getDescripcion());
                        txt_PrecioProductoEdit.setText(producto.getPrecio());
                        txt_ExtraProductoEdit.setText(producto.getExtra());
                        txt_CantidadProductoEdit.setText(producto.getCantidad());
                    }
                } else {
                    // Manejar el caso en el que no se encuentra el producto
                    Toast.makeText(editar_producto_form.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    finish(); // Finalizar la actividad
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores, si es necesario
                Toast.makeText(editar_producto_form.this, "Error al cargar el producto", Toast.LENGTH_SHORT).show();
            }
        };

        Tienda.addValueEventListener(productoListener);

        ImagenProductoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void  onClick(View view) {
                // Abre un selector de imágenes
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        Btn_GuardarCambios_EP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });

        Btn_Salir_EP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ImagenProductoEdit.setImageURI(selectedImageUri);
        }
    }

    private void guardarCambios() {
        // Obtén los valores editados de los campos de texto
        String nuevoNombre = txt_NombreProductoEdit.getText().toString();
        String nuevaDescripcion = txt_DescripcionProductoEdit.getText().toString();
        String nuevoPrecio = txt_PrecioProductoEdit.getText().toString();
        String nuevaExtra = txt_ExtraProductoEdit.getText().toString();
        String nuevaCantidad = txt_CantidadProductoEdit.getText().toString();

        // Verifica si se seleccionó una nueva imagen
        if (selectedImageUri != null) {
            // Elimina la imagen anterior del producto en Firebase Storage
            eliminarImagenAnterior(productoId);

            // Sube la nueva imagen y actualiza el producto en la base de datos
            subirImagenYActualizarProducto(selectedImageUri, nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaExtra, nuevaCantidad);
        } else {
            // No se seleccionó una nueva imagen, simplemente actualiza los otros campos del producto
            actualizarProductoEnBaseDeDatos(productoId, nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaExtra, nuevaCantidad);
        }
    }

    // Método para eliminar la imagen anterior del producto en Firebase Storage
    private void eliminarImagenAnterior(String productoId) {
        // Obtiene la URL de la imagen actual del producto en Firebase Database
        Tienda.child("imagenUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imagenAnteriorUrl = dataSnapshot.getValue(String.class);
                if (imagenAnteriorUrl != null) {
                    // Parsea la URL y obtiene el nombre del archivo
                    Uri imagenAnteriorUri = Uri.parse(imagenAnteriorUrl);
                    String nombreImagen = imagenAnteriorUri.getLastPathSegment();

                    // Obtiene una referencia al archivo en Firebase Storage y lo elimina
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imagenAnteriorUrl);
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // La imagen anterior se eliminó exitosamente
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejo de error si no se puede eliminar la imagen anterior
                            Toast.makeText(editar_producto_form.this, "Error al eliminar la imagen anterior", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores al obtener la URL de la imagen
                Toast.makeText(editar_producto_form.this, "Error al obtener la URL de la imagen anterior", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para actualizar el producto en la base de datos sin cambiar la imagen
    private void actualizarProductoEnBaseDeDatos(String productoId, String nuevoNombre, String nuevaDescripcion, String nuevoPrecio, String nuevaExtra, String nuevaCantidad) {
        // Actualiza los campos del producto en la base de datos
        Tienda.child("nombre").setValue(nuevoNombre);
        Tienda.child("descripcion").setValue(nuevaDescripcion);
        Tienda.child("precio").setValue(nuevoPrecio);
        Tienda.child("extra").setValue(nuevaExtra);
        Tienda.child("cantidad").setValue(nuevaCantidad);

        // Notificar al usuario que los cambios se han guardado
        Toast.makeText(editar_producto_form.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void subirImagenYActualizarProducto(Uri imageUri, String nuevoNombre, String nuevaDescripcion, String nuevoPrecio, String nuevaExtra, String nuevaCantidad) {
        // Sube la imagen a Firebase Storage y obtén la URL de descarga
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String imagenPath = "images/" + UUID.randomUUID().toString();
        StorageReference imagenRef = storageRef.child(imagenPath);

        imagenRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // La imagen se ha subido con éxito, obtén la URL de descarga
                imagenRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Actualiza la URL de la imagen en la base de datos
                        Tienda.child("imagenUrl").setValue(downloadUri.toString());

                        // Actualiza otros campos del producto
                        actualizarProductoEnBaseDeDatos(productoId, nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaExtra, nuevaCantidad);

                        // Notificar al usuario que los cambios se han guardado
                        Toast.makeText(editar_producto_form.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar errores si no se pudo subir la imagen
                Toast.makeText(editar_producto_form.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tienda.removeEventListener(productoListener);
    }
}



