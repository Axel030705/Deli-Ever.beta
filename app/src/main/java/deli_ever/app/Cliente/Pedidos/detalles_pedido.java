package deli_ever.app.Cliente.Pedidos;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import deli_ever.app.R;
import deli_ever.app.Todos.Pedidos.PedidoClase;

public class detalles_pedido extends AppCompatActivity {

    FragmentTransaction transactionDP;
    Fragment fragmentDetalles, fragmentUbicacion;

    // Pedido
    private PedidoClase pedido;

    // Items
    // Obtener el id de cada botón
    public int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);

        // Obtener el pedido del intent
        pedido = (PedidoClase) getIntent().getSerializableExtra("pedido");
        assert pedido != null;

        // Fragments
        fragmentDetalles = new FragmentDetalles();
        fragmentUbicacion = new FragmentUbicacion();

        // Fragment Inicio
        getSupportFragmentManager().beginTransaction().add(R.id.contentFragments_detalles_pedido, fragmentDetalles).commit();

        // Botones de navegación
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_detalles_pedido);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transactionDP = getSupportFragmentManager().beginTransaction();

                // Verificar si el pedido está finalizado
                if (pedido.getEstado().equals("Finalizado")) {
                    // Si está finalizado, deshabilitar la opción de ver ubicación
                    if (item.getItemId() == R.id.menu_ubicacion) {
                        Toast.makeText(detalles_pedido.this, "Tu pedido fue entregado con exito!", Toast.LENGTH_SHORT).show();
                        return false;  // No hacer nada si se selecciona ubicación cuando el pedido está finalizado
                    }
                }

                // Realizar las acciones correspondientes al elemento seleccionado
                if (item.getItemId() == R.id.menu_detalles) {
                    transactionDP.replace(R.id.contentFragments_detalles_pedido, fragmentDetalles).commit();
                    return true;
                } else if (item.getItemId() == R.id.menu_ubicacion) {
                    transactionDP.replace(R.id.contentFragments_detalles_pedido, fragmentUbicacion).commit();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}