package deli_ever.app.Todos.Inicio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import deli_ever.app.Administrador.SolicitudesVendedores;
import deli_ever.app.Cliente.Tiendas_Activity;
import deli_ever.app.R;
import deli_ever.app.Vendedor.MainActivityEspera;

public class Registro extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private EditText txt_Nombre, txt_Correo, txt_Password, txt_ConfirmarPassword, txt_vendera, txt_telefono;
    private Button Btn_RegistrarUsuario;
    private TextView TengoCuentaTXT;
    private AutoCompleteTextView spinner;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient fusedLocationClient;
    private String nombre = "", correo = "", password = "", confirmarpassword = "", tipoUsuario, txt_vendera2, direccionUser, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar los componentes de la UI
        spinner = findViewById(R.id.spinner);
        String[] OpcionesUsuario = {"Cliente", "Vendedor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, OpcionesUsuario);
        spinner.setAdapter(adapter);

        TextInputLayout TextInput = findViewById(R.id.TextInput);
        txt_Nombre = findViewById(R.id.txt_Nombres);
        txt_telefono = findViewById(R.id.txt_telefono);
        txt_Correo = findViewById(R.id.txt_Correo);
        txt_Password = findViewById(R.id.txt_Password);
        txt_ConfirmarPassword = findViewById(R.id.txt_ConfirmarPassword);
        Btn_RegistrarUsuario = findViewById(R.id.Btn_RegistrarUsuario);
        TengoCuentaTXT = findViewById(R.id.TengoCuentaTXT);
        txt_vendera = findViewById(R.id.txt_vendera);
        txt_vendera.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(Registro.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        spinner.setOnItemClickListener((adapterView, view, i, l) -> {
            String tipoSeleccionado = (String) spinner.getAdapter().getItem(i);
            txt_vendera.setVisibility("Vendedor".equals(tipoSeleccionado) ? View.VISIBLE : View.GONE);
        });

        Btn_RegistrarUsuario.setOnClickListener(view -> ValidarDatos());
        TengoCuentaTXT.setOnClickListener(view -> startActivity(new Intent(Registro.this, Login.class)));

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        txt_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método se llama justo antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se llama mientras el texto está cambiando.
                // Puedes realizar las acciones que necesites en tiempo real aquí.
                // Por ejemplo, puedes filtrar una lista en base a lo que el usuario escriba.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método se llama justo después de que el texto ha cambiado.
                if(txt_Password.getText().toString().equals("Administrador")){
                    TextInput.setVisibility(View.GONE);
                } else {
                    TextInput.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
            Btn_RegistrarUsuario.setVisibility(View.VISIBLE); // Mostrar botón si el permiso ya está concedido
        } else {
            Btn_RegistrarUsuario.setVisibility(View.GONE); // Ocultar botón si el permiso no está concedido
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String streetAddress = address.getThoroughfare();
                        String streetNumber = address.getSubThoroughfare();
                        String locality = address.getLocality();
                        direccionUser = (streetAddress != null ? streetAddress + " " : "") + (streetNumber != null ? streetNumber : "") + (locality != null ? ", " + locality : "");
                    }
                } catch (IOException e) {
                    Toast.makeText(Registro.this, "Error al obtener la dirección", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Registro.this, "No se puede determinar la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
                Btn_RegistrarUsuario.setVisibility(View.VISIBLE); // Mostrar botón si el permiso es concedido
            } else {
                mostrarSnackbarPermisoDenegado();
            }
        }
    }

    private void mostrarSnackbarPermisoDenegado() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Permiso de ubicación denegado (Es necesario para el uso correcto de la aplicación)",
                        Snackbar.LENGTH_INDEFINITE)
                .setAction("Configurar", view -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                });

        snackbar.show();
    }

    private void ValidarDatos() {
        nombre = txt_Nombre.getText().toString();
        correo = txt_Correo.getText().toString();
        password = txt_Password.getText().toString();
        confirmarpassword = txt_ConfirmarPassword.getText().toString();
        tipoUsuario = spinner.getText().toString().trim();
        txt_vendera2 = txt_vendera.getText().toString();
        telefono = txt_telefono.getText().toString();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmarpassword)) {
            Toast.makeText(this, "Confirme su contraseña", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmarpassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else if ("Vendedor".equals(tipoUsuario) && TextUtils.isEmpty(txt_vendera2)) {
            Toast.makeText(this, "Ingrese lo que vendera en su tienda", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(telefono)) {
            Toast.makeText(this, "Ingrese su numero de telefono", Toast.LENGTH_SHORT).show();
        } else if (!telefono.matches("\\d{10}")) {
            Toast.makeText(this, "El número de teléfono debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
        } else {
            RegistrarUsuario();
        }
    }

    private void RegistrarUsuario() {
        progressDialog.setMessage("Registrando usuario...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            GuardarInformacion(token);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Registro.this, "Error al obtener token de FCM", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void GuardarInformacion(String token) {
        progressDialog.setMessage("Guardando su información");

        String uid = firebaseAuth.getUid();
        HashMap<String, Object> Datos = new HashMap<>();
        Datos.put("uid", uid);
        Datos.put("correo", correo);
        Datos.put("nombre", nombre);
        Datos.put("password", password);
        Datos.put("Tipo de usuario", tipoUsuario);
        Datos.put("Vendera", txt_vendera2);
        Datos.put("direccion", direccionUser);
        Datos.put("telefono", telefono);

        if (token != null) {
            Datos.put("tokenFCM", token);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid).setValue(Datos)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                    iniciarActividadSegunTipoUsuario(uid);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, "Error al guardar la información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void iniciarActividadSegunTipoUsuario(String uid) {
        if ("Cliente".equals(tipoUsuario)) {
            startActivity(new Intent(Registro.this, Tiendas_Activity.class));
        } else if ("Vendedor".equals(tipoUsuario)) {
            DatabaseReference vendedorReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
            vendedorReference.child("estado").setValue("pendiente");
            Intent intent = new Intent(this, MainActivityEspera.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        } else if("Administrador".equals(password)){
            Intent intent = new Intent(this, SolicitudesVendedores.class);
            startActivity(intent);
        } else {
            Toast.makeText(Registro.this, "Tipo de usuario no válido", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
