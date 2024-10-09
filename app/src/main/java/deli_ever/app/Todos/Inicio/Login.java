package deli_ever.app.Todos.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import deli_ever.app.R;

public class Login extends AppCompatActivity {

    EditText txt_CorreoLogin, txt_PasswordLogin;
    Button Btn_Login;
    TextView UsuarioNuevoTXT;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    String correo = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_CorreoLogin = findViewById(R.id.txt_CorreoLogin);
        txt_PasswordLogin = findViewById(R.id.txt_PasswordLogin);
        Btn_Login = findViewById(R.id.Btn_Login);
        UsuarioNuevoTXT = findViewById(R.id.UsuarioNuevoTXT);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarDatos();
            }
        });

        UsuarioNuevoTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Registro.class));
            }
        });

    }

    private void ValidarDatos() {

        correo = txt_CorreoLogin.getText().toString();
        password = txt_PasswordLogin.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo invalido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
        } else {
            LoginDeUsuario();
        }

    }

    private void LoginDeUsuario() {

        progressDialog.setMessage("Iniciando Sesión...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    ValidadorSesion validadorSesion = new ValidadorSesion(user, usuariosRef, getApplicationContext());
                    validadorSesion.validarInicioSesion();
                    Toast.makeText(Login.this, "Bienvenido(a): " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Verifique si el correo y contraseña son los correctos", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}