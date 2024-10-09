package deli_ever.app.Vendedor;

public class ClaseVendedor {
    private String nombre;
    private String correo;
    private String estado;
    private String uid; // Agregamos un campo UID para identificar el cliente en Firebase
    private String Vendera;

    public ClaseVendedor() {
        // Constructor vacío requerido para Firebase Realtime Database
    }

    public ClaseVendedor(String nombre, String correo, String estado, String Vendera) {
        this.nombre = nombre;
        this.correo = correo;
        this.estado = estado;
        this.Vendera = Vendera;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getEstado() {
        return estado;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVendera() {
        return Vendera;
    }

}
