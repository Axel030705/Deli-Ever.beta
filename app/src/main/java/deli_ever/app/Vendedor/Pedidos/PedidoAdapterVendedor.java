package deli_ever.app.Vendedor.Pedidos;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import deli_ever.app.R;
import deli_ever.app.Todos.Pedidos.PedidoClase;

public class PedidoAdapterVendedor extends RecyclerView.Adapter<PedidoAdapterVendedor.PedidoViewHolderV> {

    private ArrayList<PedidoClase> listaPedidosVendedor;
    private OnItemClickListener listenerV;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listenerV) {
        this.listenerV = listenerV;
    }

    public PedidoClase getPedidoAt(int position) {
        return listaPedidosVendedor.get(position);
    }

    public PedidoAdapterVendedor(ArrayList<PedidoClase> listaPedidosVendedor) {
        this.listaPedidosVendedor = listaPedidosVendedor;
    }

    @NonNull
    @Override
    public PedidoViewHolderV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_vendedor, parent, false);
        return new PedidoViewHolderV(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolderV holder, int position) {
        PedidoClase pedido = listaPedidosVendedor.get(position);
        holder.bind(pedido, position);

        // Configurar el click listener
        holder.itemView.setOnClickListener(view -> {
            if (listenerV != null) {
                listenerV.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaPedidosVendedor.size();
    }

    public class PedidoViewHolderV extends RecyclerView.ViewHolder {
        private final TextView nombreProductoTextView;
        private final TextView cantidadTextView;
        private final TextView precioTextView;
        private final TextView estadoTextView;
        private final ImageView ImgProductoPedido;
        private final Button Btn_preparacion, Btn_camino, Btn_finalizarPedido;
        private final LinearLayout BotonesVendedor, LayoutFinalizar;
        private RecyclerView recyclerViewPedidos;

        public PedidoViewHolderV(@NonNull View itemView) {
            super(itemView);
            // Inicialización de vistas
            nombreProductoTextView = itemView.findViewById(R.id.NombreProductoPedido);
            cantidadTextView = itemView.findViewById(R.id.CantidadProductoPedido);
            precioTextView = itemView.findViewById(R.id.PrecioProductoPedido);
            estadoTextView = itemView.findViewById(R.id.EstadoPedido);
            ImgProductoPedido = itemView.findViewById(R.id.ImgProductoPedido);
            Btn_preparacion = itemView.findViewById(R.id.Btn_preparacion);
            Btn_camino = itemView.findViewById(R.id.Btn_camino);
            Btn_finalizarPedido = itemView.findViewById(R.id.Btn_finalizarPedido);
            BotonesVendedor = itemView.findViewById(R.id.BotonesVendedor);
            LayoutFinalizar = itemView.findViewById(R.id.LayoutFinalizar);
            recyclerViewPedidos = itemView.findViewById(R.id.recyclerViewPedidos);

            Btn_preparacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerV != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            PedidoClase pedido = listaPedidosVendedor.get(position);
                            String idPedido = pedido.getIdPedido();
                            String idTienda = pedido.getIdTienda();

                            DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference("Tienda")
                                    .child(idTienda)
                                    .child("Pedidos")
                                    .child(idPedido);
                            pedidoRef.child("estado").setValue("Preparación");

                            //Actualizar pedido cliente
                            String idCliente = pedido.getIdCliente();
                            String idPedidoC = pedido.getIdPedido();

                            DatabaseReference clientePedidoRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                                    .child(idCliente)
                                    .child("Pedidos")
                                    .child(idPedidoC);

                            clientePedidoRef.child("estado").setValue("Preparando");

                            // Notificar cambios en el conjunto de datos
                            notifyItemChanged(position);
                        }
                    }
                }
            });

            Btn_camino.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerV != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            PedidoClase pedido = listaPedidosVendedor.get(position);
                            String idPedido = pedido.getIdPedido();
                            String idTienda = pedido.getIdTienda();

                            DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference("Tienda")
                                    .child(idTienda)
                                    .child("Pedidos")
                                    .child(idPedido);
                            pedidoRef.child("estado").setValue("Camino");

                            //Actualizar pedido cliente
                            String idCliente = pedido.getIdCliente();
                            String idPedidoC = pedido.getIdPedido();

                            DatabaseReference clientePedidoRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                                    .child(idCliente)
                                    .child("Pedidos")
                                    .child(idPedidoC);

                            clientePedidoRef.child("estado").setValue("Camino");

                            // Notificar cambios en el conjunto de datos
                            notifyItemChanged(position);
                        }
                    }
                }
            });

            Btn_finalizarPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Finalizar Pedido");
                    builder.setMessage("¿Estás seguro de que quieres finalizar este pedido?");

                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                PedidoClase pedido = listaPedidosVendedor.get(position);
                                String idPedido = pedido.getIdPedido();
                                String idTienda = pedido.getIdTienda();

                                // Actualizar el estado del pedido a "Finalizado" en Firebase
                                DatabaseReference pedidoRef = FirebaseDatabase.getInstance().getReference("Tienda")
                                        .child(idTienda)
                                        .child("Pedidos")
                                        .child(idPedido);
                                pedidoRef.child("estado").setValue("Finalizado")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Actualizar pedido cliente
                                                String idCliente = pedido.getIdCliente();
                                                String idPedidoC = pedido.getIdPedido();

                                                DatabaseReference clientePedidoRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                                                        .child(idCliente)
                                                        .child("Pedidos")
                                                        .child(idPedidoC);

                                                clientePedidoRef.child("estado").setValue("Finalizado")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                // Notificar cambios en el conjunto de datos
                                                                notifyItemRemoved(position);
                                                                notifyItemChanged(position);
                                                                // Mostrar Toast
                                                                Toast.makeText(itemView.getContext(), "Pedido finalizado", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Acciones cuando el usuario hace clic en "No"
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(PedidoClase pedido, int position) {
            // Mostrar u ocultar botones y layout según el estado del pedido
            if ("Camino".equals(pedido.getEstado())) {
                BotonesVendedor.setVisibility(View.GONE);
                LayoutFinalizar.setVisibility(View.VISIBLE);
            } else {
                BotonesVendedor.setVisibility(View.VISIBLE);
                LayoutFinalizar.setVisibility(View.GONE);
            }

            nombreProductoTextView.setText(pedido.getProducto());
            cantidadTextView.setText("Cantidad comprada: " + pedido.getCantidad());

            if (pedido.getDescuento().equals("Ninguno")) {
                precioTextView.setText("Monto: $" + pedido.getMontoSinDescuento());
            } else {
                precioTextView.setText("Monto: $" + pedido.getMontoConDescuento());
            }

            estadoTextView.setText("Estado: " + pedido.getEstado());
            Glide.with(ImgProductoPedido.getContext())
                    .load(pedido.getImgProducto())
                    .into(ImgProductoPedido);
        }
    }
}


