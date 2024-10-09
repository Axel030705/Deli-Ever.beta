package deli_ever.app.Vendedor.Productos;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import deli_ever.app.Cliente.Tiendas_Activity;
import deli_ever.app.R;
import deli_ever.app.Todos.Pedidos.PedidoClase;

public class vista_producto extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String userId, vendedorId;
    private DatabaseReference userRef;
    public TextView textNombreProducto, textDescripcionProducto, textPrecioProducto, textExtraProducto, textCantidadProducto;
    public ImageView imgProducto;
    public String productoImg, productoNombre, productoDescripcion, productoPrecio, productoExtra, idTienda, productoCantidad, productoId;
    public Button Btn_comprarProducto, Btn_EditarProducto, Btn_EliminarProducto;
    //Usuario
    public String nombreUsr;
    //Variables pedido
    public double precioTotal;
    private String ubicacionV;
    public String propina;
    EditText txt_ubicacion, txt_referencias, txt_propina;

    //Ubicaciones
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_producto);

        //Ubicaciones
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseApp.initializeApp(this);
        imgProducto = findViewById(R.id.imgProducto);
        textNombreProducto = findViewById(R.id.textNombreProducto);
        textDescripcionProducto = findViewById(R.id.textDescripcionProducto);
        textPrecioProducto = findViewById(R.id.textPrecioProducto);
        textExtraProducto = findViewById(R.id.textExtraProducto);
        textCantidadProducto = findViewById(R.id.textCantidadProducto);
        Btn_comprarProducto = findViewById(R.id.Btn_comprarProducto);
        Btn_EditarProducto = findViewById(R.id.Btn_EditarProducto);
        Btn_EliminarProducto = findViewById(R.id.Btn_EliminarProducto);
        productoId = getIntent().getStringExtra("productoId");
        productoImg = getIntent().getStringExtra("productoImg");
        productoNombre = getIntent().getStringExtra("productoNombre");
        productoDescripcion = getIntent().getStringExtra("productoDescripcion");
        productoPrecio = getIntent().getStringExtra("productoPrecio");
        productoExtra = getIntent().getStringExtra("productoExtra");
        idTienda = getIntent().getStringExtra("tiendaId");
        productoCantidad = getIntent().getStringExtra("productoCantidad");
        CargarProducto();
        Logicas();
        ///////////////////////////////////////////////////////////////////////////////
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        userRef = usersRef.child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombreUsr = dataSnapshot.child("nombre").getValue(String.class);
                    String tipo = dataSnapshot.child("Tipo de usuario").getValue(String.class);

                    assert tipo != null;
                    if (tipo.equals("Vendedor")) {
                        Btn_EditarProducto.setVisibility(View.VISIBLE);
                        Btn_EliminarProducto.setVisibility(View.VISIBLE);
                        Btn_comprarProducto.setVisibility(View.GONE);
                    } else if (tipo.equals("Cliente")) {
                        Btn_EditarProducto.setVisibility(View.GONE);
                        Btn_EliminarProducto.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja cualquier error en la lectura de datos
            }
        });

        // Obtén una referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // Ubica la entrada del producto en la base de datos utilizando su tiendaId y productoId
        DatabaseReference VendedorRef = databaseRef.child("Tienda").child(idTienda).child("usuarioAsociado");

        // Lee el valor de la referencia
        VendedorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifica si la entrada existe
                if (dataSnapshot.exists()) {
                    // Obtén el valor del vendedor asociado
                    vendedorId = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void CargarProducto() {

        textNombreProducto.setText(productoNombre);
        textDescripcionProducto.setText(productoDescripcion);
        textPrecioProducto.setText("MX $" + productoPrecio);
        textExtraProducto.setText(productoExtra);
        textCantidadProducto.setText("Cantidad disponible: " + productoCantidad);
        Glide.with(imgProducto.getContext()).load(productoImg).into(imgProducto);
    }

    public void EditarProductoActivity(View view) {
        Intent i = new Intent(this, editar_producto_form.class);
        i.putExtra("productoId", productoId);
        i.putExtra("tiendaId", idTienda);
        startActivity(i);
    }

    public void EliminarProducto(View view) {
        // Obtén una referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Ubica la entrada del producto en la base de datos utilizando su tiendaId y productoId
        DatabaseReference productoRef = databaseRef.child("Tienda").child(idTienda).child("productos").child(productoId);

        // Elimina el producto
        productoRef.removeValue().addOnSuccessListener(aVoid -> {
            // Producto eliminado con éxito de la base de datos
            mostrarMensaje("El producto se ha eliminado con éxito.");
        }).addOnFailureListener(e -> {
            // Manejar errores si no se pudo eliminar el producto de la base de datos
            mostrarMensaje("Error al eliminar el producto. Inténtalo de nuevo.");
        });
    }

    // Método para mostrar el BottomSheetDialog
    @SuppressLint("SetTextI18n")
    public void mostrarDetallesDelProducto(View view) {
        // Crea una instancia del BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // Infla el diseño de tu BottomSheet personalizado
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Configura los elementos del BottomSheet según sus IDs
        ImageView imgProducto2 = bottomSheetView.findViewById(R.id.imgProducto2);
        TextView textNombreProducto2 = bottomSheetView.findViewById(R.id.textNombreProducto2);
        TextView textPrecioProducto2 = bottomSheetView.findViewById(R.id.textPrecioProducto2);
        AutoCompleteTextView cantidad = bottomSheetView.findViewById(R.id.cantidad2);
        txt_ubicacion = bottomSheetView.findViewById(R.id.txt_ubicacion);
        txt_propina = bottomSheetView.findViewById(R.id.txt_propina);
        txt_referencias = bottomSheetView.findViewById(R.id.txt_referencias);
        Button Btn_finalizarProducto2 = bottomSheetView.findViewById(R.id.Btn_finalizarProducto2);
        fetchLocationAndSet();

        //Escuchar si da propina para cambiar el precio total
        txt_propina.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método se llama justo antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se llama mientras el texto está cambiando.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método se llama después de que el texto haya cambiado.
                // Convertir la propina ingresada a Double, o asignar 0 si está vacío
                double propinaIngresada = 0.0;
                if (!s.toString().isEmpty()) {
                    try {
                        propinaIngresada = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        propinaIngresada = 0.0; // En caso de error de formato, usar 0 como predeterminado
                    }
                }

                // Recalcular el precio total
                double precioUnitario = Double.parseDouble(productoPrecio);
                int cantidadSeleccionada = 0;
                if (!cantidad.getText().toString().isEmpty()) {
                    cantidadSeleccionada = Integer.parseInt(cantidad.getText().toString());
                }

                precioTotal = (precioUnitario * cantidadSeleccionada) + propinaIngresada;

                // Actualizar el texto del botón
                Btn_finalizarProducto2.setText("Comprar MX $" + precioTotal);
            }
        });


        cantidad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Obtiene la cantidad seleccionada del AutoCompleteTextView
                String cantidadSeleccionada = cantidad.getText().toString();

                // Verifica si la cantidad seleccionada es un número válido
                int cantidad = 0;
                try {
                    cantidad = Integer.parseInt(cantidadSeleccionada);
                } catch (NumberFormatException ignored) {

                }

                // Calcula el precio total multiplicando la cantidad por el precio unitario
                double precioUnitario = Double.parseDouble(productoPrecio);
                precioTotal = cantidad * precioUnitario;
                Btn_finalizarProducto2.setText("Comprar MX $" + precioTotal);
            }
        });


        //Aquí puedes cargar la imagen del producto si está disponible:
        Glide.with(imgProducto2.getContext()).load(productoImg).into(imgProducto2);

        // Asigna valores a los elementos según los datos del producto
        textNombreProducto2.setText(productoNombre);
        textPrecioProducto2.setText("MX $" + productoPrecio);

        //Spinner con la cantidad de productos disponibles
        int cantidadInt = Integer.parseInt(productoCantidad);
        List<String> opcionesCantidad = new ArrayList<>();
        for (int i = 1; i <= cantidadInt; i++) {
            opcionesCantidad.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opcionesCantidad);
        cantidad.setAdapter(adapter);

        bottomSheetDialog.setContentView(bottomSheetView);
        // Muestra el BottomSheet
        bottomSheetDialog.show();

        Btn_finalizarProducto2.setOnClickListener(view1 -> {
            // Obtener la cantidad seleccionada del AutoCompleteTextView
            String cantidadSeleccionada = cantidad.getText().toString();
            if (cantidadSeleccionada.isEmpty()) {
                Toast.makeText(vista_producto.this, "Selecciona una cantidad", Toast.LENGTH_SHORT).show();
            } else if (txt_ubicacion.getText().toString().isEmpty()) {
                Toast.makeText(vista_producto.this, "Indica una dirección", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                usersRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                userRef = usersRef.child(userId);

                // Obtener la fecha y hora actual
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String fechaHoraActual = sdf.format(new Date());

                // Obtén el ID único para el nuevo pedido
                String nuevoPedidoId = userRef.child("Pedidos").push().getKey();

                // Convertir de Double a String
                String precioTotalString = String.valueOf(precioTotal);

                //Obtener la propina
                propina = txt_propina.getText().toString();

                //Obtener las referencias
                String referencias = txt_referencias.getText().toString();

                // Crea una instancia del modelo de PedidoClase con datos reales
                PedidoClase nuevoPedido = new PedidoClase(nuevoPedidoId, fechaHoraActual, nombreUsr, txt_ubicacion.getText().toString(), productoNombre, precioTotalString, "Pendiente", "Ninguno", idTienda, productoImg, cantidadSeleccionada, userId, productoId,"No", vendedorId,propina, referencias, "0");

                // Guarda el nuevo pedido en la base de datos bajo el nodo del usuario
                assert nuevoPedidoId != null;
                userRef.child("Pedidos").child(nuevoPedidoId).setValue(nuevoPedido).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Actualiza correctamente el pedido del usuario

                        // Actualiza la información del pedido en la tienda
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference storeOrdersRef = databaseRef.child("Tienda").child(idTienda).child("Pedidos");
                        storeOrdersRef.child(nuevoPedidoId).setValue(nuevoPedido).addOnCompleteListener(storeTask -> {
                            if (storeTask.isSuccessful()) {
                                // Éxito al actualizar la información del pedido en la tienda

                                // Actualiza la cantidad disponible en la base de datos
                                int cantidadComprada = Integer.parseInt(cantidadSeleccionada);
                                DatabaseReference productRef = databaseRef.child("Tienda").child(idTienda).child("productos").child(productoId);
                                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            int cantidadDisponible = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("cantidad").getValue(String.class)));
                                            int nuevaCantidadDisponible = cantidadDisponible - cantidadComprada;
                                            String nuevaCantidadDisponibleString = String.valueOf(nuevaCantidadDisponible);
                                            productRef.child("cantidad").setValue(nuevaCantidadDisponibleString);
                                            // Aquí puedes hacer algo más si es necesario

                                            textCantidadProducto.setText("Cantidad disponible: " + nuevaCantidadDisponibleString);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Manejar errores de la consulta
                                    }
                                });

                                Toast.makeText(vista_producto.this, "Pedido realizado con éxito", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String tipo = dataSnapshot.child("Tipo de usuario").getValue(String.class);

                                            assert tipo != null;
                                            if (tipo.equals("Vendedor")) {
                                                DatabaseReference storeRef = databaseRef.child("Tienda").child(idTienda);

                                                // Leer el usuario asociado (ID del vendedor)
                                                storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            // Obtener el ID del vendedor (usuario asociado)
                                                            String usuarioAsociado = dataSnapshot.child("usuarioAsociado").getValue(String.class);

                                                            if (usuarioAsociado != null) {
                                                                // Ahora leer el token del usuario asociado
                                                                DatabaseReference userTokenRef = databaseRef.child("Usuarios").child(usuarioAsociado).child("tokenFCM");
                                                                userTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot tokenSnapshot) {
                                                                        if (tokenSnapshot.exists()) {
                                                                            String userToken = tokenSnapshot.getValue(String.class);
                                                                            // Aquí puedes usar el token del usuario como necesites
                                                                            enviarNotificacion(userToken, "Nuevo pedido");
                                                                        } else {
                                                                            Toast.makeText(vista_producto.this, "Token no encontrado", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                        // Manejar posibles errores de la base de datos
                                                                        Log.e("UsuarioToken", "Error al leer token");
                                                                    }
                                                                });
                                                            } else {
                                                                Log.d("UsuarioAsociado", "ID del vendedor es null");
                                                            }
                                                        } else {
                                                            // Manejar el caso en que no existe el nodo Tienda
                                                            Log.d("UsuarioAsociado", "Tienda no encontrada");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Manejar posibles errores de la base de datos
                                                        Log.e("UsuarioAsociado", "Error al leer datos: " + databaseError.getMessage());
                                                    }
                                                });


                                            } else if (tipo.equals("Cliente")) {
                                                Intent i = new Intent(vista_producto.this, Tiendas_Activity.class);
                                                startActivity(i);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Maneja cualquier error en la lectura de datos
                                    }
                                });

                            } else {
                                Toast.makeText(vista_producto.this, "Error al actualizar la información del pedido en la tienda", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(vista_producto.this, "Error al realizar el pedido", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @SuppressLint("SetTextI18n")
    public void Logicas() {

        // Verificar si el textView de extra esta vacio
        if (textExtraProducto.getText().toString().isEmpty()) {
            textExtraProducto.setVisibility(View.GONE);
        }
        // Verificar si el textView de cantidad esta vacio
        if (textCantidadProducto.getText().toString().equals("Cantidad: 0")) {
            textCantidadProducto.setText("Sin stock");
        }
    }

    private void mostrarMensaje(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mensaje").setMessage(mensaje).setPositiveButton("Aceptar", (dialog, which) -> startActivity(new Intent(vista_producto.this, Tiendas_Activity.class))).show();
    }

    public static void enviarNotificacion(String userToken, String mensaje) {

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
                            // Get the street address, number, and locality if available
                            String streetAddress = address.getThoroughfare();
                            String streetNumber = address.getSubThoroughfare();
                            String locality = address.getLocality();
                            String shortAddress = (streetAddress!= null ? streetAddress + " " : "") + (streetNumber != null ? streetNumber : "") + (locality != null ? ", " + locality : "");
                            txt_ubicacion.setText(shortAddress); // Set the short address with locality in txt_ubicacion
                        } else {
                            txt_ubicacion.setText("No se puede determinar la dirección");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        txt_ubicacion.setText("No se puede determinar la dirección");
                    }
                } else {
                    Toast.makeText(vista_producto.this, "No se puede determinar la dirección", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}
