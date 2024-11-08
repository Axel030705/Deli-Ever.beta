package deli_ever.app.Vendedor.Pedidos;

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

public class pedidos_finalizadosV extends AppCompatActivity {

    // XML
    private LinearLayout sinPedidos, conPedidos;
    // Firebase Usuario
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String userId;
    private DatabaseReference userRef;
    // Variables usuario
    public String nombreUsr;
    public String tiendaId;
    // Crear lista de pedidos
    private ArrayList<PedidoClase> listaPedidos = new ArrayList<>();
    private PedidoAdapter pedidoAdapter;
    public RecyclerView recyclerViewPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_finalizados);

        // Iniciar Firebase
        FirebaseApp.initializeApp(this);
        // XML
        sinPedidos = findViewById(R.id.sinPedidos);
        conPedidos = findViewById(R.id.conPedidos);

        // Firebase Usuario
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        userId = firebaseAuth.getCurrentUser().getUid();
        userRef = usersRef.child(userId);

        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // Pedidos vendedor
        pedidoAdapter = new PedidoAdapter(listaPedidos);

        pedidoAdapter.setOnItemClickListener(new PedidoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Accede al pedido seleccionado usando el adaptador
                PedidoClase pedidoSeleccionado = pedidoAdapter.getPedidoAt(position);

                // Abrir nueva actividad y pasar el pedido como extra
                Intent intent = new Intent(getApplicationContext(), detalles_pedido_vendedor.class);
                intent.putExtra("pedido", pedidoSeleccionado);
                startActivity(intent);
            }
        });

        ValidarPedidosVendedor(); // Validar si el vendedor tiene pedidos finalizados
    }

    private void ValidarPedidosVendedor() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tiendaId = dataSnapshot.child("tiendaId").getValue(String.class);
                    nombreUsr = dataSnapshot.child("nombre").getValue(String.class);

                    if (tiendaId != null) {
                        // Limpiar la lista antes de agregar nuevos pedidos
                        listaPedidos.clear();

                        DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda").child(tiendaId).child("Pedidos");

                        tiendaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    sinPedidos.setVisibility(View.GONE);
                                    conPedidos.setVisibility(View.VISIBLE);
                                    recyclerViewPedidos.setAdapter(pedidoAdapter);

                                    for (DataSnapshot pedidoSnapshot : snapshot.getChildren()) {
                                        String estado = pedidoSnapshot.child("estado").getValue(String.class);
                                        if ("Finalizado".equals(estado)) {
                                            PedidoClase pedido = obtenerPedidoDesdeDataSnapshot(pedidoSnapshot);
                                            listaPedidos.add(pedido);
                                        }
                                    }

                                    // Verificar si no hay pedidos con estados válidos
                                    if (listaPedidos.isEmpty()) {
                                        sinPedidos.setVisibility(View.VISIBLE);
                                        conPedidos.setVisibility(View.GONE);
                                    } else {
                                        runOnUiThread(() -> pedidoAdapter.notifyDataSetChanged());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Manejar error si es necesario
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

    // Método auxiliar para crear un objeto PedidoClase desde un DataSnapshot
    private PedidoClase obtenerPedidoDesdeDataSnapshot(DataSnapshot dataSnapshot) {
        PedidoClase pedido = new PedidoClase();
        pedido.setProducto(dataSnapshot.child("producto").getValue(String.class));
        pedido.setCantidad(dataSnapshot.child("cantidad").getValue(String.class));
        pedido.setMontoSinDescuento(dataSnapshot.child("montoSinDescuento").getValue(String.class));
        pedido.setMontoConDescuento(dataSnapshot.child("montoConDescuento").getValue(String.class));
        pedido.setEstado(dataSnapshot.child("estado").getValue(String.class));
        pedido.setImgProducto(dataSnapshot.child("imgProducto").getValue(String.class));
        pedido.setIdPedido(dataSnapshot.child("idPedido").getValue(String.class));
        pedido.setIdCliente(dataSnapshot.child("idCliente").getValue(String.class));
        pedido.setIdTienda(dataSnapshot.child("idTienda").getValue(String.class));
        pedido.setDireccion(dataSnapshot.child("direccion").getValue(String.class));
        pedido.setFecha_Hora(dataSnapshot.child("fecha_hora").getValue(String.class));
        pedido.setNombre_Cliente(dataSnapshot.child("nombre_Cliente").getValue(String.class));
        pedido.setDescuento(dataSnapshot.child("descuento").getValue(String.class));
        pedido.setTelefono_Cliente(dataSnapshot.child("telefono").getValue(String.class));
        return pedido;
    }
}
