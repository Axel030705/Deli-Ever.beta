package deli_ever.app.Todos.Ubicacion_Conexion;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import deli_ever.app.Todos.Inicio.Sin_Conexion;

public class UbicacionConexionAplicacion extends Application {

    // Variables para la ubicación
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private UbicacionUsuarios ubicacionUsuarios;
    private String userId;

    // Variable para la verificación de conexión a Internet
    private static UbicacionConexionAplicacion instance;
    private BroadcastReceiver connectivityReceiver;
    private boolean isSinConexionActivityStarted = false; // Para evitar múltiples inicios

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        // Inicializar cliente de ubicación y FirebaseAuth
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ubicacionUsuarios = new UbicacionUsuarios();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // Iniciar actualizaciones de ubicación
        setupLocationUpdates();

        // Inicializar la instancia para verificación de conexión
        instance = this;

        // Registrar monitor de conexión a internet
        registerConnectivityMonitor();
    }

    public static UbicacionConexionAplicacion getInstance() {
        return instance;
    }

    @SuppressLint("MissingPermission")
    private void setupLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000); // Cada 15 segundos
        locationRequest.setFastestInterval(5000); // El intervalo más rápido es cada 5 segundos
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null && userId != null) {
                        ubicacionUsuarios.actualizarUbicacion(userId, location);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void registerConnectivityMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.d("UbicacionConexionAplicacion", "Conexión a internet disponible");
                    isSinConexionActivityStarted = false; // Restablecer el estado
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    startSinConexionActivity();
                }
            });
        } else {
            // Para versiones anteriores a Lollipop, usar BroadcastReceiver
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            connectivityReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

                    if (isConnected) {
                        Log.d("UbicacionConexionAplicacion", "Conexión a internet disponible");
                        isSinConexionActivityStarted = false;
                    } else {
                        startSinConexionActivity();
                    }
                }
            };
            registerReceiver(connectivityReceiver, filter);
        }
    }

    private void startSinConexionActivity() {
        if (!isSinConexionActivityStarted) {
            Intent i = new Intent(UbicacionConexionAplicacion.this, Sin_Conexion.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            isSinConexionActivityStarted = true; // Asegurar que solo se inicie una vez
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver); // Desregistrar BroadcastReceiver
        }
    }
}
