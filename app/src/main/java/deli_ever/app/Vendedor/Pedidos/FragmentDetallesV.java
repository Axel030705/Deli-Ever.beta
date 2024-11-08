package deli_ever.app.Vendedor.Pedidos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import deli_ever.app.R;
import deli_ever.app.Todos.Chat.MainActivityChat;
import deli_ever.app.Todos.Pedidos.PedidoClase;

public class FragmentDetallesV extends Fragment {

    // XML Views
    private EditText txt_descuento;
    private TextView txt_productosV, txt_precio, txt_envio, txt_precioTotal, txt_direccion;
    private LinearLayout layout_btn_descuento, LayoutMsjV;
    private Button btn_descuento;
    ImageView llamar;

    // Variables
    private double precioTotal;
    private String precioTotalString, precioTotal2;

    // Chat
    private DatabaseReference databaseReference;

    // Pedido
    private PedidoClase pedidoV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pedidoV = (PedidoClase) requireActivity().getIntent().getSerializableExtra("pedido");
        assert pedidoV != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalles_v, container, false);

        // Initialize XML views
        txt_descuento = view.findViewById(R.id.txt_descuentoV);
        layout_btn_descuento = view.findViewById(R.id.layout_btn_descuento);
        txt_productosV = view.findViewById(R.id.txt_productosV);
        txt_precio = view.findViewById(R.id.txt_precioV);
        txt_envio = view.findViewById(R.id.txt_envioV);
        txt_precioTotal = view.findViewById(R.id.txt_precioTotalV);
        txt_direccion = view.findViewById(R.id.txt_direccionV);
        btn_descuento = view.findViewById(R.id.btn_descuento);
        LayoutMsjV = view.findViewById(R.id.LayoutMsjV);
        llamar = view.findViewById(R.id.llamar);
        layout_btn_descuento.setVisibility(View.GONE);

        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telefonoCliente = "tel:" + pedidoV.getTelefono_Cliente(); // Asegúrate de obtener el número de teléfono del cliente

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(telefonoCliente));

                // Verifica si se tiene el permiso de CALL_PHONE
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent); // Inicia la llamada
                } else {
                    // Si no se tiene el permiso, solicitarlo
                    ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
            }
        });

        // Escuchar cambios en el campo de descuento
        txt_descuento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_descuento.getText().length() > 0) {
                    layout_btn_descuento.setVisibility(View.VISIBLE);
                } else {
                    layout_btn_descuento.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up discount button click listener
        btn_descuento.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Mensaje")
                    .setMessage("Deseas aprobar el descuento? Este ya no podrá ser editado ni cancelado")
                    .setPositiveButton("Si", (dialog, id) -> {
                        dialog.dismiss();
                        aprobar_descuento();
                        layout_btn_descuento.setVisibility(View.GONE);
                    })
                    .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        // Set up chat click listener
        LayoutMsjV.setOnClickListener(view12 -> {
            databaseReference = FirebaseDatabase.getInstance().getReference("chat");
            String idPedido = pedidoV.getIdPedido();
            String idSala = pedidoV.getIdTienda() + "_" + idPedido;
            DatabaseReference pedidoRef = databaseReference.child(idSala);

            pedidoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(requireActivity(), MainActivityChat.class);
                    intent.putExtra("salaId", idSala);
                    intent.putExtra("idPedido", idPedido);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        });

        // Populate order information
        InformacionPedido();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void InformacionPedido() {
        if (pedidoV != null) {
            txt_productosV.setText("Productos (" + pedidoV.getCantidad() + ")");
            txt_precio.setText("$ " + pedidoV.getMontoSinDescuento());

            if (!pedidoV.getDescuento().equals("Ninguno")) {
                int colorVerde = getResources().getColor(R.color.green);
                txt_descuento.setTextColor(colorVerde);
                txt_descuento.setText("- $ " + pedidoV.getDescuento());
                txt_descuento.setEnabled(false);
                layout_btn_descuento.setVisibility(View.GONE);
            } else {
                int colorRojo = getResources().getColor(R.color.red);
                txt_descuento.setTextColor(colorRojo);
                txt_descuento.setHint("Ninguno");
                txt_descuento.setEnabled(true);
            }

            if (pedidoV.getDescuento().equals("Ninguno")) {
                txt_precioTotal.setText("$ " + pedidoV.getMontoSinDescuento());
            } else {
                double monto = Double.parseDouble(pedidoV.getMontoSinDescuento());
                double descuentoD = Double.parseDouble(pedidoV.getDescuento());
                precioTotal = monto - descuentoD;
                precioTotalString = String.format("$ %.2f", precioTotal);
                txt_precioTotal.setText(precioTotalString);
            }

            txt_direccion.setText(pedidoV.getDireccion());
        }
    }

    @SuppressLint("SetTextI18n")
    public void aprobar_descuento() {
        if (pedidoV != null) {
            try {
                double monto = Double.parseDouble(pedidoV.getMontoSinDescuento());
                double descuentoD = Double.parseDouble(txt_descuento.getText().toString());
                precioTotal = monto - descuentoD;
                precioTotal2 = String.valueOf(precioTotal);
                precioTotalString = String.format("$ %.2f", precioTotal);
                txt_precioTotal.setText(precioTotalString);
                txt_descuento.setText("- $" + descuentoD);
                txt_descuento.setTextColor(getResources().getColor(R.color.green));
                txt_descuento.setEnabled(false);
                layout_btn_descuento.setVisibility(View.GONE);
                actualizar_pedido(String.valueOf(descuentoD));
            } catch (NumberFormatException e) {
                txt_descuento.setError("Ingrese un descuento válido");
            }
        } else {
            // Manejar el caso cuando pedidoV es null
            Log.e("FragmentDetallesV", "Error: pedidoV es null");
            txt_precioTotal.setText("Error al obtener los datos del pedido");
            txt_descuento.setEnabled(false);
            layout_btn_descuento.setVisibility(View.GONE);
        }
    }


    public void actualizar_pedido(String descuento) {
        if (pedidoV != null) {
            String idPedido = pedidoV.getIdPedido();
            String idTienda = pedidoV.getIdTienda();
            DatabaseReference tiendaRef = FirebaseDatabase.getInstance().getReference("Tienda")
                    .child(idTienda)
                    .child("Pedidos")
                    .child(idPedido);
            tiendaRef.child("descuento").setValue(descuento);
            tiendaRef.child("montoConDescuento").setValue(precioTotal2);

            String idCliente = pedidoV.getIdCliente();
            DatabaseReference clientePedidoRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                    .child(idCliente)
                    .child("Pedidos")
                    .child(idPedido);
            clientePedidoRef.child("descuento").setValue(descuento);
            clientePedidoRef.child("montoConDescuento").setValue(precioTotal2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realiza la llamada
                String telefonoCliente = "tel:" + pedidoV.getTelefono_Cliente();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(telefonoCliente));
                startActivity(intent);
            } else {
                // Permiso denegado, puedes mostrar un mensaje al usuario
                Toast.makeText(requireActivity(), "Permiso de llamada no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
