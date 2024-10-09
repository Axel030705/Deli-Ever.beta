package deli_ever.app.Vendedor.Productos;


import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import deli_ever.app.R;

public class productos_tienda extends AppCompatActivity {

    private ProductosAdapter adapter;
    private final List<Producto> productosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_tienda);

        // Obtén el ID de la tienda desde los extras del Intent
        String tiendaId = getIntent().getStringExtra("tiendaId");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductosAdapter(productosList, tiendaId);
        recyclerView.setAdapter(adapter);

        if (tiendaId != null) {
            DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda").child(tiendaId);

            tiendaRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot productosSnapshot = dataSnapshot.child("productos");

                        productosList.clear();

                        for (DataSnapshot productoSnapshot : productosSnapshot.getChildren()) {
                            String id = productoSnapshot.getKey();
                            String nombre = productoSnapshot.child("nombre").getValue(String.class);
                            String descripcion = productoSnapshot.child("descripcion").getValue(String.class);
                            String precio = productoSnapshot.child("precio").getValue(String.class);
                            String extra = productoSnapshot.child("extra").getValue(String.class);
                            String imagenUrl = productoSnapshot.child("imagenUrl").getValue(String.class);
                            String cantidad = productoSnapshot.child("cantidad").getValue(String.class);
                            String puntaje = productoSnapshot.child("puntaje").getValue(String.class);
                            String idTienda = productoSnapshot.child("idTienda").getValue(String.class);
                            Producto producto = new Producto(id, nombre, descripcion, precio, extra, cantidad, puntaje, idTienda);
                            producto.setImagenUrl(imagenUrl); // Agregar la URL de la imagen al producto
                            productosList.add(producto);

                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se encontraron productos en esta tienda", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}