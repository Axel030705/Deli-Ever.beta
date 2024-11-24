package deli_ever.app.Vendedor;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import deli_ever.app.R;
import deli_ever.app.Red_Social.Fragment_Red_Social_Main;
import deli_ever.app.Todos.Perfil.FragmentPerfil;

public class Vendedor_Main extends AppCompatActivity {

    FragmentTransaction transactionDP;
    Fragment fragmentInicioV, fragmentPedidosV, fragmentPerfilV, fragmentRedV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_main);

        //Fragments
        fragmentInicioV = new FragmentInicioV();
        fragmentPedidosV = new FragmentPedidosV();
        fragmentRedV = new Fragment_Red_Social_Main();
        fragmentPerfilV = new FragmentPerfil();

        //Fragment Inicio
        getSupportFragmentManager().beginTransaction().add(R.id.contentFragments_tiendas, fragmentInicioV).commit();

        //Botones de navegación
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_vendedor);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transactionDP = getSupportFragmentManager().beginTransaction();

                // Obtener el id de cada botón
                int itemId = item.getItemId();

                // Realizar las acciones correspondientes al elemento seleccionado
                if (itemId == R.id.menu_inicio) {
                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentInicioV).commit();
                    return true;
                } else if (itemId == R.id.menu_pedidos) {
                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentPedidosV).commit();
                    return true;
//                } else if (itemId == R.id.menu_red) {
//                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentRedV).commit();
//                    return true;
                } else if (itemId == R.id.menu_perfil) {
                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentPerfilV).commit();
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

}

