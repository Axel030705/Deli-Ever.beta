package deli_ever.app.Vendedor.Comentarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import deli_ever.app.R;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder> {
    private List<Comentario> listaComentarios;

    public ComentariosAdapter(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    @Override
    public ComentarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout para cada comentario
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);

        // Asignar los valores del comentario a los elementos del layout
        holder.comentarioText.setText(comentario.getComentario());

        // Formatear la fecha
        String fechaFormateada = formatDate(comentario.getFecha());
        holder.fechaText.setText(fechaFormateada);  // Mostrar la fecha formateada

        holder.likesText.setText("Likes: " + comentario.getLikes());
        holder.dislikesText.setText("Dislikes: " + comentario.getDislikes());

        Glide.with(holder.itemView.getContext())
                .load(comentario.getImagenProducto())
                .into(holder.imagenProducto);
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    // Método para formatear la fecha
    private String formatDate(String fecha) {
        // Definir el formato original de la fecha que recibes
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Definir el formato final que deseas mostrar
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy");

        try {
            // Convertir la fecha de String a Date
            Date date = originalFormat.parse(fecha);
            // Devolver la fecha formateada
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return fecha;  // En caso de error, devolver la fecha original
        }
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView comentarioText, fechaText, likesText, dislikesText;
        ImageView imagenProducto;

        public ComentarioViewHolder(View itemView) {
            super(itemView);
            comentarioText = itemView.findViewById(R.id.comentarioText);
            fechaText = itemView.findViewById(R.id.fechaText);
            likesText = itemView.findViewById(R.id.likesText);
            dislikesText = itemView.findViewById(R.id.dislikesText);
            imagenProducto = itemView.findViewById(R.id.imagenProducto);
        }
    }
}
