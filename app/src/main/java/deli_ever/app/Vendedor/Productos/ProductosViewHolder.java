package deli_ever.app.Vendedor.Productos;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;

import deli_ever.app.R;

public class ProductosViewHolder extends RecyclerView.ViewHolder {

    private final TextView TXTView_NombreProducto;
    private final TextView TXTView_DescripcionProducto;
    private final TextView TXTView_PrecioProducto;
    private final TextView TXTView_ExtraProducto;
    private final ImageView ImagenProductoItem;
    private final ImageView estrella1, estrella2, estrella3, estrella4, estrella5;
    private final LinearLayout LayoutPuntaje;
    private final MaterialCardView cardViewProducto;


    public ProductosViewHolder(View itemView){
        super(itemView);

        TXTView_NombreProducto = itemView.findViewById(R.id.TXTView_NombreProducto);
        TXTView_DescripcionProducto = itemView.findViewById(R.id.TXTView_DescripcionProducto);
        TXTView_PrecioProducto = itemView.findViewById(R.id.TXTView_PrecioProducto);
        TXTView_ExtraProducto = itemView.findViewById(R.id.TXTView_ExtraProducto);
        ImagenProductoItem = itemView.findViewById(R.id.ImagenProductoItem);
        cardViewProducto = itemView.findViewById(R.id.cardViewProducto);
        LayoutPuntaje = itemView.findViewById(R.id.LayoutPuntaje);
        estrella1 = itemView.findViewById(R.id.estrella1);
        estrella2 = itemView.findViewById(R.id.estrella2);
        estrella3 = itemView.findViewById(R.id.estrella3);
        estrella4 = itemView.findViewById(R.id.estrella4);
        estrella5 = itemView.findViewById(R.id.estrella5);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Producto producto){
        if (producto != null) {
            if (producto.getNombre() != null) {
                TXTView_NombreProducto.setText(producto.getNombre());
            } else {
                TXTView_NombreProducto.setText("");
            }

            if (producto.getDescripcion() != null) {
                TXTView_DescripcionProducto.setText(producto.getDescripcion());
            } else {
                TXTView_DescripcionProducto.setText("");
            }

            if (producto.getPrecio() != null) {
                TXTView_PrecioProducto.setText("MX$ " + producto.getPrecio());
            } else {
                TXTView_PrecioProducto.setText("");
            }

            if (producto.getExtra() != null) {
                TXTView_ExtraProducto.setText(producto.getExtra());
            } else {
                TXTView_ExtraProducto.setText("");
            }

            if(producto.getPuntaje() != null){

                String puntajeString = producto.getPuntaje();
                int puntajeEntero = (int) Double.parseDouble(puntajeString);

                if(puntajeEntero == 0){
                    //Ocultar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.GONE);
                    //Cambiar el tamaño del card view
                    int nuevoAlto = 450;
                    ViewGroup.LayoutParams layoutParams = cardViewProducto.getLayoutParams();
                    layoutParams.height = nuevoAlto;
                    cardViewProducto.setLayoutParams(layoutParams);
                } else if (puntajeEntero == 1 && puntajeEntero < 2) {
                    //Mostrar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.VISIBLE);
                    estrella1.setImageResource(R.drawable.estrella_2);
                } else if (puntajeEntero == 2 && puntajeEntero < 3) {
                    //Mostrar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.VISIBLE);
                    estrella1.setImageResource(R.drawable.estrella_2);
                    estrella2.setImageResource(R.drawable.estrella_2);
                } else if (puntajeEntero == 3 && puntajeEntero < 4) {
                    //Mostrar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.VISIBLE);
                    estrella1.setImageResource(R.drawable.estrella_2);
                    estrella2.setImageResource(R.drawable.estrella_2);
                    estrella3.setImageResource(R.drawable.estrella_2);
                } else if (puntajeEntero == 4 && puntajeEntero < 5) {
                    //Mostrar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.VISIBLE);
                    estrella1.setImageResource(R.drawable.estrella_2);
                    estrella2.setImageResource(R.drawable.estrella_2);
                    estrella3.setImageResource(R.drawable.estrella_2);
                    estrella4.setImageResource(R.drawable.estrella_2);
                } else if (puntajeEntero >= 5) {
                    //Mostrar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.VISIBLE);
                    estrella1.setImageResource(R.drawable.estrella_2);
                    estrella2.setImageResource(R.drawable.estrella_2);
                    estrella3.setImageResource(R.drawable.estrella_2);
                    estrella4.setImageResource(R.drawable.estrella_2);
                    estrella5.setImageResource(R.drawable.estrella_2);
                }else{
                    //Ocultar el layout de puntaje
                    LayoutPuntaje.setVisibility(View.GONE);
                    //Cambiar el tamaño del card view
                    int nuevoAlto = 450;
                    ViewGroup.LayoutParams layoutParams = cardViewProducto.getLayoutParams();
                    layoutParams.height = nuevoAlto;
                    cardViewProducto.setLayoutParams(layoutParams);
                }
            }else{
                //Ocultar el layout de puntaje
                LayoutPuntaje.setVisibility(View.GONE);
                //Cambiar el tamaño del card view
                int nuevoAlto = 450;
                ViewGroup.LayoutParams layoutParams = cardViewProducto.getLayoutParams();
                layoutParams.height = nuevoAlto;
                cardViewProducto.setLayoutParams(layoutParams);
            }

            // Cargar la imagen con Glide y agregar un listener de carga
            Glide.with(itemView.getContext())
                    .load(producto.getImagenUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Mostrar un Toast indicando que la carga de la imagen falló
                            Toast.makeText(itemView.getContext(), "Error al cargar la imagen del producto", Toast.LENGTH_SHORT).show();
                            return true; // Devolver true para que Glide maneje la visualización de un recurso de error
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // La imagen se cargó correctamente
                            return false;
                        }
                    })
                    .into(ImagenProductoItem);

        } else {
            // Si el producto es nulo, establecer todas las vistas en blanco
            TXTView_NombreProducto.setText("");
            TXTView_DescripcionProducto.setText("");
            TXTView_PrecioProducto.setText("");
            TXTView_ExtraProducto.setText("");
        }
    }
}
