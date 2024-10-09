package deli_ever.app.Todos.Ubicacion_Conexion;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

public class VerificarConexionInternet extends Application {

    private static VerificarConexionInternet instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerConnectivityMonitor();
    }

    public static VerificarConexionInternet getInstance() {
        return instance;
    }

    private void registerConnectivityMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Para versiones Lollipop (API 21) y superiores, usa NetworkCallback
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.d("MyApplication", "Conexión a internet disponible");
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    Log.d("MyApplication", "Conexión a internet perdida");
                }
            });
        } else {
            // Para versiones anteriores a Lollipop, usa BroadcastReceiver
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

                    if (isConnected) {
                        Log.d("MyApplication", "Conexión a internet disponible");
                    } else {
                        Log.d("MyApplication", "Conexión a internet perdida");
                    }
                }
            }, filter);
        }
    }
}

