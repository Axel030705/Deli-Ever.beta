package deli_ever.app.Todos.Ubicacion_Conexion;

import android.location.Location;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UbicacionUsuarios {

    private DatabaseReference databaseReference;

    public UbicacionUsuarios() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    public void actualizarUbicacion(String userId, Location location) {
        HashMap<String, Object> ubicacionMap = new HashMap<>();
        ubicacionMap.put("ubicacion_actual/latitud", location.getLatitude());
        ubicacionMap.put("ubicacion_actual/longitud", location.getLongitude());

        databaseReference.child(userId).updateChildren(ubicacionMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Manejar éxito
                    } else {
                        // Manejar error
                    }
                });
    }

    public void obtenerUbicacion(String userId, UbicacionCallback callback) {
        databaseReference.child(userId).child("ubicacion_actual").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double latitud = snapshot.child("latitud").getValue(Double.class);
                    Double longitud = snapshot.child("longitud").getValue(Double.class);
                    if (latitud != null && longitud != null) {
                        Location location = new Location("");
                        location.setLatitude(latitud);
                        location.setLongitude(longitud);
                        callback.onUbicacionObtenida(location);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar errores
            }
        });
    }

    public interface UbicacionCallback {
        void onUbicacionObtenida(Location location);
    }
}
