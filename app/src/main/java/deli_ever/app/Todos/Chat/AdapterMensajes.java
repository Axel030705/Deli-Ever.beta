package deli_ever.app.Todos.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import deli_ever.app.R;

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    public static final int Mensaje_izquierda = 0;
    public static final int Mensaje_derecha = 1;
    private final List<Mensaje> listMensaje = new ArrayList<>();
    private final Context c;
    private final String currentUserUid;

    public AdapterMensajes(Context c, String currentUserUid) {
        this.c = c;
        this.currentUserUid = currentUserUid;
    }

    public void addMensaje(Mensaje m) {
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    @NonNull
    @Override
    public HolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Mensaje_derecha) {
            view = LayoutInflater.from(c).inflate(R.layout.item_mensaje_derecha, parent, false);
        } else {
            view = LayoutInflater.from(c).inflate(R.layout.item_mensaje_izquierda, parent, false);
        }
        return new HolderMensaje(view);
    }

    @Override
    public void onBindViewHolder(HolderMensaje holder, int position) {
        Mensaje mensaje = listMensaje.get(position);

        holder.getMensaje().setText(mensaje.getMensaje());

        if (mensaje.getType_mensaje().equals("2")) {
            holder.getMandarFoto().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(mensaje.getUrlFoto()).into(holder.getMandarFoto());
        } else if (mensaje.getType_mensaje().equals("1")) {
            holder.getMandarFoto().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = listMensaje.get(position);
        if (mensaje != null && mensaje.getSender() != null && mensaje.getSender().equals(currentUserUid)) {
            return Mensaje_derecha;
        } else {
            return Mensaje_izquierda;
        }
    }

    public void clear() {
        listMensaje.clear();
    }
}


