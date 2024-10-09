package deli_ever.app.Vendedor.Pedidos;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import deli_ever.app.R;

public class detalles_pedido_vendedor extends AppCompatActivity {

    FragmentTransaction transactionDP;
    Fragment fragmentDetallesV, fragmentUbicacionV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);

        //Fragments
        fragmentDetallesV = new FragmentDetallesV();
        fragmentUbicacionV = new FragmentUbicacionV();

        //Fragment Inicio
        getSupportFragmentManager().beginTransaction().add(R.id.contentFragments_detalles_pedido, fragmentDetallesV).commit();

        //Botones de navegación
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_detalles_pedido);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transactionDP = getSupportFragmentManager().beginTransaction();

                // Obtener el id de cada botón
                int itemId = item.getItemId();

                // Realizar las acciones correspondientes al elemento seleccionado
                if (itemId == R.id.menu_detalles) {
                    transactionDP.replace(R.id.contentFragments_detalles_pedido, fragmentDetallesV).commit();
                    // Cambia el ícono y el color del elemento del menú seleccionado
                    /*item.setIcon(R.drawable.info_2);
                    Objects.requireNonNull(item.getIcon()).setColorFilter(getResources().getColor(R.color.azulCielo), PorterDuff.Mode.SRC_IN);*/
                    return true;
                } else if (itemId == R.id.menu_ubicacion) {
                    transactionDP.replace(R.id.contentFragments_detalles_pedido, fragmentUbicacionV).commit();
                    // Cambia el ícono y el color del elemento del menú seleccionado
                    /*item.setIcon(R.drawable.ubi_2);
                    Objects.requireNonNull(item.getIcon()).setColorFilter(getResources().getColor(R.color.azulCielo), PorterDuff.Mode.SRC_IN);*/


                    return true;
                } else {
                    return false;
                }
            }
        });
    }

}