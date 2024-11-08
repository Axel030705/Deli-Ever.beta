package deli_ever.app.Cliente.Pedidos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

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
import deli_ever.app.Vendedor.Pedidos.PedidoAdapterVendedor;
import deli_ever.app.Vendedor.Pedidos.detalles_pedido_vendedor;

public class pedidos extends AppCompatActivity {
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
    private ArrayList<PedidoClase> listaPedidosVendedor = new ArrayList<>();
    private PedidoAdapter pedidoAdapter;
    private PedidoAdapterVendedor pedidoAdapterVendedor;
    public RecyclerView recyclerViewPedidos;
    private Button Btn_menu_pedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        //Iniciar Firebase
        FirebaseApp.initializeApp(this);
        //XML
        sinPedidos = findViewById(R.id.sinPedidos);
        conPedidos = findViewById(R.id.conPedidos);
        Btn_menu_pedidos = findViewById(R.id.Btn_menu_pedidos);

        //Firebase Usuario
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        userId = firebaseAuth.getCurrentUser().getUid();
        userRef = usersRef.child(userId);

        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //Pedidos cliente
        pedidoAdapter = new PedidoAdapter(listaPedidos);
        //Pedidos Vendedor
        pedidoAdapterVendedor = new PedidoAdapterVendedor(listaPedidosVendedor);
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

        pedidoAdapterVendedor.setOnItemClickListener(new PedidoAdapterVendedor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Accede al pedido seleccionado usando el adaptador
                PedidoClase pedidoSeleccionado = pedidoAdapterVendedor.getPedidoAt(position);

                // Ahora puedes hacer lo que necesitas con el pedido seleccionado
                // Por ejemplo, puedes abrir una nueva actividad y pasar el pedido como extra
                Intent intent = new Intent(getApplicationContext(), detalles_pedido_vendedor.class);
                intent.putExtra("pedido", pedidoSeleccionado);
                startActivity(intent);
            }
        });

        Btn_menu_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creamos el objeto PupupMenu
                PopupMenu popupMenu = new PopupMenu(pedidos.this, view);
                //Infla el menu desde el archivo XML
                popupMenu.getMenuInflater().inflate(R.menu.menu_opt_pedidos, popupMenu.getMenu());
                //Configura el listener para manejar las opciones del menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        int itemId = menuItem.getItemId();

                        if (itemId == R.id.opcion1_pedidos) {
                            // Acción para la opción 1
                            Intent i = new Intent(pedidos.this, pedidos_finalizados.class);
                            startActivity(i);
                            return true;
                        } else {
                            // Otros casos si es necesario
                            return false;
                        }
                    }
                });
                //Muestra el PupupMenu
                popupMenu.show();
            }
        });


        ValidarPedidosCliente(); //Validar si el usuario tiene pedidos realizados
        ValidarPedidosTienda(); //Validar si la tienda tiene pedidos realizados
    }

    private void ValidarPedidosCliente() {
        userRef.addValueEventListener(new ValueEventListener() {
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
                        boolean hayPedidosConEstadosValidos = false, hayPedidosFinalizados = false;
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
                            String idVendedor = pedidoDataSnapshot.child("idVendedor").getValue(String.class);
                            String Telefono_Cliente = pedidoDataSnapshot.child("telefono_Cliente").getValue(String.class);

                            // Agregar condición para filtrar por estado
                            if ("Pendiente".equals(estado) || "Camino".equals(estado) || "Preparando".equals(estado)) {
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
                                pedido.setIdVendedor(idVendedor);
                                pedido.setTelefono_Cliente(Telefono_Cliente);
                                listaPedidos.add(pedido);

                                // Marcamos que hay pedidos con estados válidos
                                hayPedidosConEstadosValidos = true;

                            } else if ("Finalizado".equals(estado)) {
                                Btn_menu_pedidos.setVisibility(View.VISIBLE);
                                hayPedidosFinalizados = true;
                            }

                        }

                        // Verificar si no hay pedidos con estados válidos
                        if (!hayPedidosConEstadosValidos) {
                            sinPedidos.setVisibility(View.VISIBLE);
                            conPedidos.setVisibility(View.GONE);
                        }else if (!hayPedidosFinalizados){
                            Btn_menu_pedidos.setVisibility(View.GONE);
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


    public void ValidarPedidosTienda() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String tipoU = snapshot.child("Tipo de usuario").getValue(String.class);
                    String idTienda = snapshot.child("tiendaId").getValue(String.class);

                    assert tipoU != null;
                    if (tipoU.equals("Vendedor")) {
                        // Crear una referencia a la tienda en la base de datos
                        assert idTienda != null;
                        DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda").child(idTienda).child("Pedidos");

                        tiendaRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    recyclerViewPedidos.setAdapter(pedidoAdapterVendedor);
                                    // Limpiar la lista antes de agregar nuevos pedidos
                                    listaPedidosVendedor.clear();
                                    for (DataSnapshot pedidoSnapshot : snapshot.getChildren()) {
                                        String estado = pedidoSnapshot.child("estado").getValue(String.class);

                                        // Verificar si el estado no es "Finalizado"
                                        if (!"Finalizado".equals(estado)) {
                                            sinPedidos.setVisibility(View.GONE);
                                            conPedidos.setVisibility(View.VISIBLE);
                                            String idPedido = pedidoSnapshot.child("idPedido").getValue(String.class);
                                            String idCliente = pedidoSnapshot.child("idCliente").getValue(String.class);
                                            String idTienda = pedidoSnapshot.child("idTienda").getValue(String.class);
                                            String Producto = pedidoSnapshot.child("producto").getValue(String.class);
                                            String cantidad = pedidoSnapshot.child("cantidad").getValue(String.class);
                                            String direccion = pedidoSnapshot.child("direccion").getValue(String.class);
                                            String montoSinDescuento = pedidoSnapshot.child("montoSinDescuento").getValue(String.class);
                                            String fecha_hora = pedidoSnapshot.child("fecha_hora").getValue(String.class);
                                            String imgProducto = pedidoSnapshot.child("imgProducto").getValue(String.class);
                                            String nombre_Cliente = pedidoSnapshot.child("nombre_Cliente").getValue(String.class);
                                            String descuento = pedidoSnapshot.child("descuento").getValue(String.class);
                                            String montoConDescuento = pedidoSnapshot.child("montoConDescuento").getValue(String.class);
                                            String idVendedor = pedidoSnapshot.child("idVendedor").getValue(String.class);
                                            String Telefono_Cliente = pedidoSnapshot.child("telefono_Cliente").getValue(String.class);

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
                                            pedido.setIdVendedor(idVendedor);
                                            pedido.setTelefono_Cliente(Telefono_Cliente);
                                            listaPedidosVendedor.add(pedido);
                                        }
                                    }
                                }
                                // Notificar al adaptador que los datos han cambiado en el hilo principal de la interfaz de usuario
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pedidoAdapterVendedor.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}

