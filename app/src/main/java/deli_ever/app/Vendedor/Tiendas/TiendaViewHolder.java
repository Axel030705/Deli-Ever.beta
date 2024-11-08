package deli_ever.app.Vendedor.Tiendas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import deli_ever.app.R;

public class TiendaViewHolder extends RecyclerView.ViewHolder {

    final TextView txtNombreTienda;
    final TextView txtDescripcionTienda;
    final TextView txtDireccionTienda;
    final TextView txtExtraTienda;
    final LinearLayout txtEstadoTienda;
    final ImageView imagenTienda;

    @SuppressLint("NewApi")
    public TiendaViewHolder(View itemView) {
        super(itemView);

        txtNombreTienda = itemView.findViewById(R.id.TXTView_NombreTienda);
        txtDescripcionTienda = itemView.findViewById(R.id.TXTView_DescripcionTienda);
        txtDireccionTienda = itemView.findViewById(R.id.TXTView_DireccionTienda);
        txtExtraTienda = itemView.findViewById(R.id.TXTView_ExtraTienda);
        txtEstadoTienda = itemView.findViewById(R.id.TXTView_EstadoTienda);
        imagenTienda = itemView.findViewById(R.id.ImagenTienda);

        itemView.setOnLongClickListener(v -> {
            TiendaClase tienda = (TiendaClase) v.getTag();
            // Mostrar detalles solo si la tienda no está cerrada
            /*if (!"Cerrado".equals(tienda.getEstado())) {
                mostrarDetallesTienda(v.getContext(), tienda);
            }*/

            //Mostrar detalles de la tienda aunque esté cerrada
            mostrarDetallesTienda(v.getContext(), tienda);
            return true;
        });
    }

    public void bind(TiendaClase tienda) {
        itemView.setTag(tienda);
        txtNombreTienda.setText(tienda.getNombre());
        txtDescripcionTienda.setText(tienda.getDescripcion());
        txtDireccionTienda.setText(tienda.getDireccion());
        txtExtraTienda.setText(tienda.getExtra());

        // Mostrar el estado de la tienda
        String estado = tienda.getEstado();
        if ("Cerrado".equals(estado)) {
            txtEstadoTienda.setVisibility(View.VISIBLE);
            itemView.setAlpha(0.5f); // Opcional: hacer que la card sea transparente
        } else {
            txtEstadoTienda.setVisibility(View.GONE);
            itemView.setAlpha(1.0f); // Restaurar opacidad
        }

        Glide.with(imagenTienda.getContext())
                .load(tienda.getImageUrl())
                .into(imagenTienda);
    }

    @SuppressLint("SetTextI18n")
    public void mostrarDetallesTienda(Context context, TiendaClase tienda) {
        // Inflar el diseño del diálogo
        View dialogView = LayoutInflater.from(context).inflate(R.layout.detalles_tienda_dialog, null);

        // Inicializar vistas del diálogo
        ImageView imgTienda = dialogView.findViewById(R.id.imgTienda);
        TextView nombreTienda = dialogView.findViewById(R.id.nombreTienda);
        TextView descripcionTienda = dialogView.findViewById(R.id.descripcionTienda);
        TextView ubicacionTienda = dialogView.findViewById(R.id.ubicacionTienda);
        TextView extraTienda = dialogView.findViewById(R.id.extraTienda);

        // Establecer valores de las vistas con los detalles de la tienda
        nombreTienda.setText(tienda.getNombre());
        descripcionTienda.setText("Descripcion: " + tienda.getDescripcion());
        ubicacionTienda.setText("Ubicacion: " + tienda.getDireccion());
        extraTienda.setText(tienda.getExtra());

        // Cargar imagen de la tienda utilizando Glide
        Glide.with(context)
                .load(tienda.getImageUrl())
                .into(imgTienda);

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
}
