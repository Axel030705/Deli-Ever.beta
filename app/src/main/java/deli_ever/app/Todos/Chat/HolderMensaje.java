package deli_ever.app.Todos.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import deli_ever.app.R;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView mensaje;
    ImageView MandarFoto;

    public HolderMensaje(View itemView){
        super(itemView);

        mensaje = itemView.findViewById(R.id.mensajeMensaje);
        MandarFoto = itemView.findViewById(R.id.msj_foto);
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public ImageView getMandarFoto() {
        return MandarFoto;
    }
}
