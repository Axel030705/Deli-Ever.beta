package deli_ever.app.Cliente.Pedidos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import deli_ever.app.R;
import deli_ever.app.Todos.Pedidos.PedidoAdapter;
import deli_ever.app.Todos.Pedidos.PedidoClase;

public class pedidos_finalizados extends AppCompatActivity {

    //XML
    private LinearLayout sinPedidos, conPedidos;
    //Firebase Usuario
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String userId;
    private DatabaseReference userRef;
    //Variables usuario
    public String nombreUsr;
    // Crear lista de pedidos
    private ArrayList<PedidoClase> listaPedidos = new ArrayList<>();
    private PedidoAdapter pedidoAdapter;
    public RecyclerView recyclerViewPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_finalizados);

        //Iniciar Firebase
        FirebaseApp.initializeApp(this);
        //XML
        sinPedidos = findViewById(R.id.sinPedidos);
        conPedidos = findViewById(R.id.conPedidos);

        //Firebase Usuario
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        userId = firebaseAuth.getCurrentUser().getUid();
        userRef = usersRef.child(userId);

        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //Pedidos cliente
        pedidoAdapter = new PedidoAdapter(listaPedidos);

        pedidoAdapter.setOnItemClickListener(new PedidoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Accede al pedido seleccionado usando el adaptador
                PedidoClase pedidoSeleccionado = pedidoAdapter.getPedidoAt(position);

                // Ahora puedes hacer lo que necesitas con el pedido seleccionado
                // Por ejemplo, puedes abrir una nueva actividad y pasar el pedido como extra
                Intent intent = new Intent(getApplicationContext(), detalles_pedido.class);
                intent.putExtra("pedido", pedidoSeleccionado);
                startActivity(intent);
            }
        });


        ValidarPedidosCliente(); //Validar si el usuario tiene pedidos realizados
    }

    private void ValidarPedidosCliente() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombreUsr = dataSnapshot.child("nombre").getValue(String.class);
                    DataSnapshot pedidosSnapshot = dataSnapshot.child("Pedidos");

                    if (pedidosSnapshot.exists()) {
                        sinPedidos.setVisibility(View.GONE);
                        conPedidos.setVisibility(View.VISIBLE);
                        recyclerViewPedidos.setAdapter(pedidoAdapter);
                        // Limpiar la lista antes de agregar nuevos pedidos
                        listaPedidos.clear();
                        // Obtener información de los pedidos
                        boolean hayPedidosConEstadosValidos = false;
                        for (DataSnapshot pedidoDataSnapshot : pedidosSnapshot.getChildren()) {
                            String idPedido = pedidoDataSnapshot.child("idPedido").getValue(String.class);
                            String idCliente = pedidoDataSnapshot.child("idCliente").getValue(String.class);
                            String idTienda = pedidoDataSnapshot.child("idTienda").getValue(String.class);
                            String Producto = pedidoDataSnapshot.child("producto").getValue(String.class);
                            String cantidad = pedidoDataSnapshot.child("cantidad").getValue(String.class);
                            String direccion = pedidoDataSnapshot.child("direccion").getValue(String.class);
                            String montoSinDescuento = pedidoDataSnapshot.child("montoSinDescuento").getValue(String.class);
                            String estado = pedidoDataSnapshot.child("estado").getValue(String.class);
                            String fecha_hora = pedidoDataSnapshot.child("fecha_hora").getValue(String.class);
                            String imgProducto = pedidoDataSnapshot.child("imgProducto").getValue(String.class);
                            String nombre_Cliente = pedidoDataSnapshot.child("nombre_Cliente").getValue(String.class);
                            String descuento = pedidoDataSnapshot.child("descuento").getValue(String.class);
                            String montoConDescuento = pedidoDataSnapshot.child("montoConDescuento").getValue(String.class);
                            String Telefono_Cliente = pedidoDataSnapshot.child("telefono_Cliente").getValue(String.class);

                            // Agregar condición para filtrar por estado
                            if ("Finalizado".equals(estado)) {
                                // Crear objeto Pedido y agregar a la lista
                                PedidoClase pedido = new PedidoClase();
                                pedido.setProducto(Producto);
                                pedido.setCantidad(cantidad);
                                pedido.setMontoSinDescuento(montoSinDescuento);
                                pedido.setEstado(estado);
                                pedido.setImgProducto(imgProducto);
                                pedido.setIdPedido(idPedido);
                                pedido.setIdCliente(idCliente);
                                pedido.setIdTienda(idTienda);
                                pedido.setDireccion(direccion);
                                pedido.setFecha_Hora(fecha_hora);
                                pedido.setNombre_Cliente(nombre_Cliente);
                                pedido.setDescuento(descuento);
                                pedido.setMontoConDescuento(montoConDescuento);
                                pedido.setTelefono_Cliente(Telefono_Cliente);
                                listaPedidos.add(pedido);

                                // Marcamos que hay pedidos con estados válidos
                                hayPedidosConEstadosValidos = true;



                            }
                        }

                        // Verificar si no hay pedidos con estados válidos
                        if (!hayPedidosConEstadosValidos) {
                            sinPedidos.setVisibility(View.VISIBLE);
                            conPedidos.setVisibility(View.GONE);
                        }

                        // Notificar al adaptador que los datos han cambiado en el hilo principal de la interfaz de usuario
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pedidoAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error de base de datos, si es necesario
            }
        });
    }
}