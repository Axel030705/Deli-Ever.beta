package deli_ever.app.Todos.Ubicacion_Conexion;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

public class UbicacionAplicacion extends Application {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private UbicacionUsuarios ubicacionUsuarios;
    private String userId;

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ubicacionUsuarios = new UbicacionUsuarios();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void setupLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000); // 15 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        ubicacionUsuarios.actualizarUbicacion(userId, location);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
