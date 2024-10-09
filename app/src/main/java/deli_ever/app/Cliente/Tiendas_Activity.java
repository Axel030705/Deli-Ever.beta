package deli_ever.app.Cliente;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import deli_ever.app.R;
import deli_ever.app.Todos.Perfil.FragmentPerfil;

public class Tiendas_Activity extends AppCompatActivity {

    FragmentTransaction transactionDP;
    Fragment fragmentInicio, fragmentPedidos, fragmentPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas);

        //Fragments
        fragmentInicio = new FragmentInicio();
        fragmentPedidos = new FragmentPedidos();
        fragmentPerfil = new FragmentPerfil();

        //Fragment Inicio
        getSupportFragmentManager().beginTransaction().add(R.id.contentFragments_tiendas, fragmentInicio).commit();

        //Botones de navegación
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_tiendas);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transactionDP = getSupportFragmentManager().beginTransaction();

                // Obtener el id de cada botón
                int itemId = item.getItemId();

                // Realizar las acciones correspondientes al elemento seleccionado
                if (itemId == R.id.menu_inicio) {
                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentInicio).commit();
                    // Cambia el ícono y el color del elemento del menú seleccionado
                    /*item.setIcon(R.drawable.info_2);
                    Objects.requireNonNull(item.getIcon()).setColorFilter(getResources().getColor(R.color.azulCielo), PorterDuff.Mode.SRC_IN);*/
                    return true;
                } else if (itemId == R.id.menu_pedidos) {
                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentPedidos).commit();
                    // Cambia el ícono y el color del elemento del menú seleccionado
                    /*item.setIcon(R.drawable.ubi_2);
                    Objects.requireNonNull(item.getIcon()).setColorFilter(getResources().getColor(R.color.azulCielo), PorterDuff.Mode.SRC_IN);*/
                    return true;
                } else if (itemId == R.id.menu_perfil) {
                    transactionDP.replace(R.id.contentFragments_tiendas, fragmentPerfil).commit();
                    return true;
                } else {
                    return false;
                }
            }
        });

        }
}