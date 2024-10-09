package deli_ever.app.Vendedor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import deli_ever.app.R;

public class VendedorAdapter extends BaseAdapter {
    private Context context;
    private List<ClaseVendedor> vendedorList;

    public VendedorAdapter(Context context, List<ClaseVendedor> vendedorList) {
        this.context = context;
        this.vendedorList = vendedorList;
    }

    @Override
    public int getCount() {
        return vendedorList.size();
    }

    @Override
    public Object getItem(int position) {
        return vendedorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vendedor, parent, false);
        }

        ClaseVendedor vendedor = vendedorList.get(position);

        // Personaliza la vista del elemento aquí según tus necesidades
        TextView nombreTextView = convertView.findViewById(R.id.nombreTextView);
        TextView correoTextView = convertView.findViewById(R.id.correoTextView);
        TextView estadoTextView = convertView.findViewById(R.id.estadoTextView);
        TextView TipoPTextView = convertView.findViewById(R.id.TipoPTextView);
        Button buttonAceptarVendedor = convertView.findViewById(R.id.buttonAceptarVendedor);
        Button buttonRechazarVendedor = convertView.findViewById(R.id.buttonRechazarVendedor);

        buttonAceptarVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acción para aceptar al vendedor
                AceptarVendedor(vendedor);
            }
        });

        buttonRechazarVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acción para rechazar al vendedor
                RechazarVendedor(vendedor);
            }
        });

        nombreTextView.setText(vendedor.getNombre());
        correoTextView.setText("Correo: "+ vendedor.getCorreo());
        estadoTextView.setText("Estado: " + vendedor.getEstado());
        TipoPTextView.setText("Venderá: " + vendedor.getVendera());
        return convertView;
    }

    private void AceptarVendedor(ClaseVendedor vendedor) {
        DatabaseReference vendedorReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(vendedor.getUid());
        vendedorReference.child("estado").setValue("aprobado");

        // Puedes realizar otras acciones relacionadas con la aceptación aquí
        Toast.makeText(context.getApplicationContext(), "Vendedor aceptado", Toast.LENGTH_SHORT).show();
    }

    private void RechazarVendedor(ClaseVendedor vendedor) {
        DatabaseReference vendedorReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(vendedor.getUid());
        vendedorReference.child("estado").setValue("rechazado");

        // Puedes realizar otras acciones relacionadas con el rechazo aquí
        Toast.makeText(context.getApplicationContext(), "Vendedor rechazado", Toast.LENGTH_SHORT).show();
    }

}

