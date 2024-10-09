package deli_ever.app.Vendedor.Pedidos;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import deli_ever.app.R;
import deli_ever.app.Todos.Pedidos.PedidoClase;
import deli_ever.app.Todos.Ubicacion_Conexion.DirectionsApiService;
import deli_ever.app.Todos.Ubicacion_Conexion.DirectionsResponse;
import deli_ever.app.Todos.Ubicacion_Conexion.RetrofitClient;
import deli_ever.app.Todos.Ubicacion_Conexion.UbicacionUsuarios;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentUbicacionV extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    private PedidoClase pedido;
    private Marker clienteMarker;
    private static final String API_KEY = "AIzaSyBThcT3OrZ1ONOofNafOMJspNn6sAeErdI";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ubicacion_v, container, false);
        mapView = view.findViewById(R.id.mapViewV);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        pedido = (PedidoClase) requireActivity().getIntent().getSerializableExtra("pedido");
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        obtenerUbicacionesYMostrarRuta();
    }

    private void obtenerUbicacionesYMostrarRuta() {
        UbicacionUsuarios ubicacionUsuarios = new UbicacionUsuarios();

        // Obtener ubicación del vendedor
        ubicacionUsuarios.obtenerUbicacion(pedido.getIdVendedor(), new UbicacionUsuarios.UbicacionCallback() {
            @Override
            public void onUbicacionObtenida(Location ubicacionVendedor) {
                LatLng vendedorLatLng = new LatLng(ubicacionVendedor.getLatitude(), ubicacionVendedor.getLongitude());
                mMap.addMarker(new MarkerOptions().position(vendedorLatLng).title("Ubicación actual del Vendedor"));

                // Obtener ubicación del cliente
                ubicacionUsuarios.obtenerUbicacion(pedido.getIdCliente(), new UbicacionUsuarios.UbicacionCallback() {
                    @Override
                    public void onUbicacionObtenida(Location ubicacionCliente) {
                        LatLng clienteLatLng = new LatLng(ubicacionCliente.getLatitude(), ubicacionCliente.getLongitude());

                        // Si el marcador del cliente ya existe, muévelo con animación
                        if (clienteMarker != null) {
                            moverMarcadorConAnimacion(clienteMarker, clienteLatLng);
                        } else {
                            // Si no existe, crea uno nuevo
                            clienteMarker = mMap.addMarker(new MarkerOptions().position(clienteLatLng).title("Ubicación del Cliente"));
                        }

                        // Trazar la ruta entre el vendedor y el cliente
                        obtenerYMostrarRuta(vendedorLatLng, clienteLatLng);
                    }
                });
            }
        });
    }

    private void obtenerYMostrarRuta(LatLng origen, LatLng destino) {
        DirectionsApiService service = RetrofitClient.getService();
        String originStr = origen.latitude + "," + origen.longitude;
        String destinationStr = destino.latitude + "," + destino.longitude;

        Log.d("FragmentUbicacionV", "Solicitando ruta de " + originStr + " a " + destinationStr);

        service.getDirections(originStr, destinationStr, API_KEY).enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful()) {
                    DirectionsResponse directionsResponse = response.body();
                    if (directionsResponse != null && directionsResponse.routes != null && !directionsResponse.routes.isEmpty()) {
                        String encodedPoints = directionsResponse.routes.get(0).overview_polyline.points;
                        List<LatLng> routePoints = decodePoly(encodedPoints);
                        mostrarRutaEnMapa(routePoints);
                    } else {
                        Log.e("FragmentUbicacionV", "No se encontraron rutas en la respuesta.");
                    }
                } else {
                    Log.e("FragmentUbicacionV", "Respuesta no exitosa.");
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e("FragmentUbicacionV", "Error obteniendo la ruta: " + t.getMessage());
            }
        });
    }

    private void mostrarRutaEnMapa(List<LatLng> points) {
        PolylineOptions polylineOptions = new PolylineOptions().addAll(points)
                .color(ContextCompat.getColor(getContext(), R.color.azulCielo)).width(50);
        mMap.addPolyline(polylineOptions);

        // Ajustar el zoom y la cámara para mostrar la ruta
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    // Método para animar el movimiento del marcador
    private void moverMarcadorConAnimacion(final Marker marcador, final LatLng nuevaUbicacion) {
        final LatLng inicioPosicion = marcador.getPosition();
        final long duracion = 2000; // Duración de la animación en milisegundos
        final long inicioTiempo = SystemClock.uptimeMillis();

        final Interpolator interpolador = new LinearInterpolator();

        // Usamos un Handler para actualizar el marcador cada 16 ms (aproximadamente 60 FPS)
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                long tiempoTranscurrido = SystemClock.uptimeMillis() - inicioTiempo;
                float fraccionProgreso = interpolador.getInterpolation((float) tiempoTranscurrido / duracion);
                double lat = (nuevaUbicacion.latitude - inicioPosicion.latitude) * fraccionProgreso + inicioPosicion.latitude;
                double lng = (nuevaUbicacion.longitude - inicioPosicion.longitude) * fraccionProgreso + inicioPosicion.longitude;
                marcador.setPosition(new LatLng(lat, lng));

                if (fraccionProgreso < 1.0) {
                    handler.postDelayed(this, 16); // Actualización cada 16ms (~60 FPS)
                } else {
                    marcador.setPosition(nuevaUbicacion); // Ajuste final de la posición
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
