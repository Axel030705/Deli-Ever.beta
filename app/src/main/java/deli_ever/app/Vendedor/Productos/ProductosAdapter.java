package deli_ever.app.Vendedor.Productos;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

import deli_ever.app.R;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosViewHolder> {

    private final List<Producto> productos;
    private final String tiendaId;
    private String estadoTienda = "Cerrado";  // Estado predeterminado
    private DatabaseReference tiendaRef;

    public ProductosAdapter(List<Producto> productos, String tiendaId) {
        this.productos = productos;
        this.tiendaId = tiendaId;
        escucharEstadoTienda();  // Configura el listener para el estado en tiempo real
    }

    private void escucharEstadoTienda() {
        tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda").child(tiendaId).child("estado");
        tiendaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    estadoTienda = snapshot.getValue(String.class);
                    notifyDataSetChanged();  // Notifica cambios para actualizar la vista en tiempo real
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(null, "Error al obtener el estado de la tienda: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_nvo, parent, false);
        return new ProductosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.bind(producto);

        // Ajustar la opacidad y la acción de clic según el estado de la tienda
        if ("Abierto".equals(estadoTienda)) {
            holder.itemView.setAlpha(1.0f);  // Totalmente opaco
            holder.itemView.setOnClickListener(v -> {
                // Redirigir a otra actividad con los datos del producto
                Intent intent = new Intent(v.getContext(), vista_producto.class);
                intent.putExtra("productoId", producto.getId());
                intent.putExtra("productoNombre", producto.getNombre());
                intent.putExtra("productoDescripcion", producto.getDescripcion());
                intent.putExtra("productoPrecio", producto.getPrecio());
                intent.putExtra("productoExtra", producto.getExtra());
                intent.putExtra("productoImg", producto.getImagenUrl());
                intent.putExtra("tiendaId", tiendaId);
                intent.putExtra("productoCantidad", producto.getCantidad());
                v.getContext().startActivity(intent);
            });
        } else {
            // Si la tienda está "Cerrado", reducir opacidad y deshabilitar clic
            holder.itemView.setAlpha(0.5f);  // Opacidad reducida
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "La tienda está cerrada. No se puede acceder al producto.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
}
