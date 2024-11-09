package deli_ever.app.Red_Social;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import deli_ever.app.R;

public class Activity_Publicar_Todo extends AppCompatActivity {

    private String nombre, correo, uid, tipoUsuario, imageUrl;
    ImageView atras;
    Button publicar;
    CircleImageView ImagenUsuario;
    TextView nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_todo);

        //XML
        atras = findViewById(R.id.atras);
        publicar = findViewById(R.id.publicar);
        ImagenUsuario = findViewById(R.id.ImagenUsuario);
        nombreUsuario = findViewById(R.id.usuario);

        // Obtener el Intent y los datos
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        imageUrl = intent.getStringExtra("imageUrl");
        correo = intent.getStringExtra("correo");
        uid = intent.getStringExtra("uid");
        tipoUsuario = intent.getStringExtra("tipoUsuario");

        //Setear los datos del usuario//
        //Imagen de usuario
        Picasso.get().load(imageUrl).into(ImagenUsuario);
        ImagenUsuario.setBorderWidth(0);
        //Nombre de usuario
        nombreUsuario.setText(nombre);

        atras.setOnClickListener(v -> {
            finish();
        });



        }

    }
