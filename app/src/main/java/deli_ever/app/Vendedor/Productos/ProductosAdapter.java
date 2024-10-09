package deli_ever.app.Vendedor.Productos;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import deli_ever.app.R;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosViewHolder> {

    private final List<Producto> productos;
    private final String tiendaId;
    public ProductosAdapter(List<Producto> productos, String tiendaId) {
        this.productos = productos;
        this.tiendaId = tiendaId;
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

        holder.itemView.setOnClickListener(v -> {
            // Acción a realizar al hacer clic en el CardView
            // Redirigir a otra actividad
            Intent intent = new Intent(v.getContext(), vista_producto.class);
            // Pasar datos adicionales a la otra actividad utilizando putExtra()
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

    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
}
