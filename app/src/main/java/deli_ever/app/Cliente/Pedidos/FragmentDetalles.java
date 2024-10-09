package deli_ever.app.Cliente.Pedidos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import deli_ever.app.Cliente.Tiendas_Activity;
import deli_ever.app.R;
import deli_ever.app.Todos.Chat.MainActivityChat;
import deli_ever.app.Todos.Pedidos.PedidoClase;

public class FragmentDetalles extends Fragment {

    //Pedido//
    private PedidoClase pedido;
    //Variables pedido finalizado
    private Float puntajeDado = 0.0f;
    //XML//
    private ImageView ImgEstado;
    private TextView TextoEstado, txt_productos, txt_precio, txt_descuento, txt_envio, txt_precioTotal, txt_direccion, TextoInfo;
    private LinearLayout LayoutMsj, LayoutUbicacion;
    private Button btn_cancelarPedido;
    //Variables
    private String idUsr;
    //Firebase
    private DatabaseReference usuarioRef;
    private DatabaseReference pedidoRef;
    //Chat
    private DatabaseReference databaseReference;

    public FragmentDetalles() {
        //Requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Obtén el pedido de los extras || Variables
        pedido = (PedidoClase) requireActivity().getIntent().getSerializableExtra("pedido");
        assert pedido != null;
        idUsr = pedido.getIdCliente();

        //XML
        ImgEstado = view.findViewById(R.id.ImgEstado);
        TextoEstado = view.findViewById(R.id.TextoEstado);
        txt_productos = view.findViewById(R.id.txt_productos);
        txt_precio = view.findViewById(R.id.txt_precio);
        txt_descuento = view.findViewById(R.id.txt_descuento);
        txt_envio = view.findViewById(R.id.txt_envio);
        txt_precioTotal = view.findViewById(R.id.txt_precioTotal);
        txt_direccion = view.findViewById(R.id.txt_direccion);
        LayoutMsj = view.findViewById(R.id.LayoutMsj);
        LayoutUbicacion = view.findViewById(R.id.LayoutUbicacion);
        TextoInfo = view.findViewById(R.id.TextoInfo);
        btn_cancelarPedido = view.findViewById(R.id.btn_cancelarPedido);

        //Firebase
        // Referencia al nodo del usuario
        usuarioRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(idUsr);
        // Referencia al nodo de Pedidos dentro del nodo del usuario
        pedidoRef = usuarioRef.child("Pedidos").child(pedido.getIdPedido());

        //Escuchar cambios en el estado del pedido
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Actualiza el objeto PedidoClase con los nuevos datos
                    PedidoClase nuevoPedido = dataSnapshot.getValue(PedidoClase.class);

                    // Realiza la actualización de la interfaz de usuario con el nuevo estado
                    if (nuevoPedido != null) {
                        pedido = nuevoPedido;
                        ValidarEstadoPedido();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja errores si es necesario
            }
        });

        //Ingresar al chat con el cliente
        LayoutMsj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la referencia a la base de datos
                databaseReference = FirebaseDatabase.getInstance().getReference("chat");
                //Pasar id del pedido
                String idPedido = pedido.getIdPedido();

                String idSala = pedido.getIdTienda() + "_" + pedido.getIdPedido();

                //Obtener la referencia a la ubicación del pedido
                DatabaseReference pedidoRef = databaseReference.child(idSala);

                pedidoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Verificar si existen mensajes para este pedido
                        if (dataSnapshot.exists()) {

                            // Obtener el ID del usuario actual
                            /*String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();*/

                            // Navegar a la actividad de chat
                            Intent intent = new Intent(requireActivity(), MainActivityChat.class);
                            intent.putExtra("salaId", idSala);
                            /*intent.putExtra("idUsuario1", idUsuarioActual);
                            intent.putExtra("idUsuario2", idUsuario2);*/
                            intent.putExtra("idPedido", pedido.getIdPedido());
                            startActivity(intent);

                        } else {

                            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chat");
                            String idSala = pedido.getIdTienda() + "_" + pedido.getIdPedido();

                            // Crear la sala de chat
                            /*chatRef.child(idSala).child("usuario1").setValue(idUsuarioActual);
                            chatRef.child(idSala).child("usuario2").setValue(idUsuario2);*/
                            chatRef.child(idSala).child("idPedido").setValue(pedido.getIdPedido());

                            // Navegar a la actividad de chat
                            Intent intent = new Intent(requireActivity(), MainActivityChat.class);
                            intent.putExtra("salaId", idSala);
                            /*intent.putExtra("idUsuario1", idUsuarioActual);
                            intent.putExtra("idUsuario2", idUsuario2);*/
                            intent.putExtra("idPedido", pedido.getIdPedido());
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores de Firebase, si es necesario
                    }
                });
            }
        });

        //Ingresar a la ubicacion
        LayoutUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        InformacionPedido();


    }

    @SuppressLint("SetTextI18n")
    private void ValidarEstadoPedido() {
        if (pedido != null) {
            if (pedido.getEstado().equals("Pendiente")) {
                TextoEstado.setText("Tu pedido está esperando a ser aceptado por la tienda");
                ImgEstado.setImageResource(R.drawable.svg1);
                btn_cancelarPedido.setVisibility(View.VISIBLE);

                // 1 minuto de cancelación
                new CountDownTimer(60000, 1000) {
                    @SuppressLint("SetTextI18n")
                    public void onTick(long millisUntilFinished) {
                        long secondsRemaining = millisUntilFinished / 1000;
                        btn_cancelarPedido.setText("Cancelar pedido (" + secondsRemaining + "s)");
                    }

                    public void onFinish() {
                        btn_cancelarPedido.setText("Tiempo de cancelación expirado");
                        btn_cancelarPedido.setEnabled(false);
                        btn_cancelarPedido.setVisibility(View.GONE);
                    }
                }.start();

                // Handle the cancel button click
                btn_cancelarPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Referencias necesarias
                        DatabaseReference pedidoRef = usuarioRef.child("Pedidos").child(pedido.getIdPedido());
                        DatabaseReference tiendaPedidoRef = FirebaseDatabase.getInstance()
                                .getReference("Tienda")
                                .child(pedido.getIdTienda())  // Nodo de la tienda
                                .child("Pedidos")
                                .child(pedido.getIdPedido()); // Nodo del pedido dentro de la tienda

                        // Eliminar el pedido del nodo principal 'Pedidos'
                        pedidoRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Eliminar el pedido del nodo 'Tienda'
                                tiendaPedidoRef.removeValue().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Pedido cancelado con éxito", Toast.LENGTH_SHORT).show();
                                        // Redirigir a Tiendas_Activity.java
                                        Intent intent = new Intent(requireActivity(), Tiendas_Activity.class);
                                        startActivity(intent);
                                        requireActivity().finish();
                                    } else {
                                        Toast.makeText(requireContext(), "Error al cancelar el pedido en la tienda", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(requireContext(), "Error al cancelar el pedido principal", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else if (pedido.getEstado().equals("Preparando")) {
                TextoEstado.setText("Tu pedido está en preparación");
                ImgEstado.setImageResource(R.drawable.svg2);
            } else if (pedido.getEstado().equals("Camino")) {
                TextoEstado.setText("Tu pedido está en camino");
                ImgEstado.setImageResource(R.drawable.svg3);
            } else if (pedido.getEstado().equals("Finalizado")) {
                TextoEstado.setText("Tu pedido fue entregado disfrutalo!");
                ImgEstado.setImageResource(R.drawable.svg4);
                TextoInfo.setText("Para cualquier duda o aclaración no ovides ponerte en contacto con el vendedor");

                //Validar si el producto ya se califico
                if (pedido.getCalificado().equals("No") && pedido.getEstado().equals("Finalizado")) {
                    mostrarFinalizadoDialog(getView(), pedido.getIdPedido());
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void InformacionPedido() {

        //Setear la cantidad de productos y el precio de ellos
        txt_productos.setText("Productos (" + pedido.getCantidad() + ")");
        txt_precio.setText("$ " + pedido.getMontoSinDescuento());
        //Validar si tiene descuento
        if (pedido.getDescuento().equals("Ninguno")) {
            int colorRojo = getResources().getColor(R.color.red);
            txt_descuento.setTextColor(colorRojo);
            txt_descuento.setText("Ninguno");
        } else {
            int colorVerde = getResources().getColor(R.color.green);
            txt_descuento.setTextColor(colorVerde);
            txt_descuento.setText("- $ " + pedido.getDescuento());
        }
        //Validar si tiene envio gratis
        /*if(pedido.getEnvio.equals("Gratis")){
            int colorVerde = getResources().getColor(R.color.green);
            txt_envio.setTextColor(colorVerde);
            txt_envio.setText(pedido.getEnvio);
        }else{

        }*/
        //Precio total - Descuento
        if (pedido.getDescuento().equals("Ninguno")) {
            txt_precioTotal.setText("$ " + pedido.getMontoSinDescuento());
        } else {
            double monto = Double.parseDouble(pedido.getMontoSinDescuento());
            double descuento = Double.parseDouble(pedido.getDescuento());
            double precioTotal = monto - descuento;
            @SuppressLint("DefaultLocale") String precioTotalString = String.format("$ %.2f", precioTotal);
            txt_precioTotal.setText(precioTotalString);
        }
        //Setear la ubicación
        txt_direccion.setText(pedido.getDireccion());
    }

    private void mostrarFinalizadoDialog(View parentView, String id_pedido) {
        ConstraintLayout finalizado_constraint = parentView.findViewById(R.id.finalizado_constraint);
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.finalizado_dialog, finalizado_constraint);

        //XML
        TextView txt_nombre_producto = view.findViewById(R.id.txt_nameProducto);
        ImageView estrella1 = view.findViewById(R.id.estrella1);
        ImageView estrella2 = view.findViewById(R.id.estrella2);
        ImageView estrella3 = view.findViewById(R.id.estrella3);
        ImageView estrella4 = view.findViewById(R.id.estrella4);
        ImageView estrella5 = view.findViewById(R.id.estrella5);
        LinearLayout layout_btn_hecho = view.findViewById(R.id.layout_btn_hecho);
        Button btn_hecho = view.findViewById(R.id.btn_hecho);

        //Configuracion del AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        txt_nombre_producto.setText(pedido.getProducto());

        estrella1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Poner la estrella seleccionada azul
                estrella1.setImageResource(R.drawable.estrella_2dialog);
                layout_btn_hecho.setVisibility(View.VISIBLE);

                //Poner las demas estrellas grises
                estrella2.setImageResource(R.drawable.estrella_dialog);
                estrella3.setImageResource(R.drawable.estrella_dialog);
                estrella4.setImageResource(R.drawable.estrella_dialog);
                estrella5.setImageResource(R.drawable.estrella_dialog);

                puntajeDado = 0.2f;

            }
        });

        estrella2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estrella1.setImageResource(R.drawable.estrella_2dialog);
                estrella2.setImageResource(R.drawable.estrella_2dialog);
                layout_btn_hecho.setVisibility(View.VISIBLE);

                //Poner las demas estrellas grises
                estrella3.setImageResource(R.drawable.estrella_dialog);
                estrella4.setImageResource(R.drawable.estrella_dialog);
                estrella5.setImageResource(R.drawable.estrella_dialog);

                puntajeDado = 0.4f;
            }
        });

        estrella3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estrella1.setImageResource(R.drawable.estrella_2dialog);
                estrella2.setImageResource(R.drawable.estrella_2dialog);
                estrella3.setImageResource(R.drawable.estrella_2dialog);
                layout_btn_hecho.setVisibility(View.VISIBLE);

                //Poner las demas estrellas grises
                estrella4.setImageResource(R.drawable.estrella_dialog);
                estrella5.setImageResource(R.drawable.estrella_dialog);

                puntajeDado = 0.6f;
            }
        });

        estrella4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estrella1.setImageResource(R.drawable.estrella_2dialog);
                estrella2.setImageResource(R.drawable.estrella_2dialog);
                estrella3.setImageResource(R.drawable.estrella_2dialog);
                estrella4.setImageResource(R.drawable.estrella_2dialog);
                layout_btn_hecho.setVisibility(View.VISIBLE);

                //Poner las demas estrellas grises
                estrella5.setImageResource(R.drawable.estrella_dialog);

                puntajeDado = 0.8f;
            }
        });

        estrella5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estrella1.setImageResource(R.drawable.estrella_2dialog);
                estrella2.setImageResource(R.drawable.estrella_2dialog);
                estrella3.setImageResource(R.drawable.estrella_2dialog);
                estrella4.setImageResource(R.drawable.estrella_2dialog);
                estrella5.setImageResource(R.drawable.estrella_2dialog);
                layout_btn_hecho.setVisibility(View.VISIBLE);

                puntajeDado = 1.0f;
            }
        });

        btn_hecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(requireActivity(), "Agracedemos tu opinión", Toast.LENGTH_SHORT).show();

                //Actualiza el pedido del cliente a calificado ("Si")
                DatabaseReference clientePedidoRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                        .child(idUsr)
                        .child("Pedidos")
                        .child(id_pedido);

                clientePedidoRef.child("calificado").setValue("Si");

                //Actualiza el pedido de la tienda a calificado ("Si")
                DatabaseReference tiendaPedidoRef = FirebaseDatabase.getInstance().getReference("Tienda")
                        .child(pedido.getIdTienda())
                        .child("Pedidos")
                        .child(id_pedido);

                tiendaPedidoRef.child("calificado").setValue("Si");

                //Obtener el puntaje del producto
                DatabaseReference tiendaProductoRef = FirebaseDatabase.getInstance().getReference("Tienda")
                        .child(pedido.getIdTienda())
                        .child("productos")
                        .child(pedido.getIdProducto());

                tiendaProductoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("puntaje").exists()) {
                            String puntajeActualString = snapshot.child("puntaje").getValue(String.class);

                            if (puntajeActualString != null && !puntajeActualString.isEmpty()) {
                                Float puntajeActualFloat = Float.valueOf(puntajeActualString);
                                setPuntaje(puntajeActualFloat, puntajeDado);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();


    }

    private void setPuntaje(Float puntajeActual, Float puntajeAsignado) {

        float puntajeTotal = puntajeAsignado + puntajeActual;
        String puntajeTotalString = Float.toString(puntajeTotal);

        // Actualiza el puntaje del producto en la base de datos
        DatabaseReference tiendaProductoRef = FirebaseDatabase.getInstance().getReference("Tienda")
                .child(pedido.getIdTienda())
                .child("productos")
                .child(pedido.getIdProducto());

        tiendaProductoRef.child("puntaje").setValue(puntajeTotalString);

    }
}