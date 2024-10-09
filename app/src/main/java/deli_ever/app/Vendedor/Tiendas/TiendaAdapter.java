package deli_ever.app.Vendedor.Tiendas;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import deli_ever.app.R;
import deli_ever.app.Vendedor.Productos.productos_tienda;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaViewHolder> {
    private final List<TiendaClase> tiendas;

    public TiendaAdapter(List<TiendaClase> tiendas) {
        this.tiendas = tiendas;
    }

    @NonNull
    @Override
    public TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tienda, parent, false);
        return new TiendaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaViewHolder holder, int position) {
        TiendaClase tienda = tiendas.get(position);
        holder.bind(tienda);

        // Mostrar el estado en la card
        if ("Cerrado".equals(tienda.getEstado())) {
            holder.txtEstadoTienda.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(null); // Deshabilitar clic
            holder.itemView.setAlpha(0.5f); // Opcional: hacer que la card sea transparente
        } else {
            holder.txtEstadoTienda.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), productos_tienda.class);
                intent.putExtra("tiendaId", tienda.getId());
                v.getContext().startActivity(intent);
            });
            holder.itemView.setAlpha(1.0f); // Restaurar opacidad
        }
    }

    @Override
    public int getItemCount() {
        return tiendas.size();
    }
}
