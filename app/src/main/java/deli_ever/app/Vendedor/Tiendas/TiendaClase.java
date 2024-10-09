package deli_ever.app.Vendedor.Tiendas;

public class TiendaClase {
    private String id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String extra;
    private String usuarioAsociado;
    private String imageUrl;
    private String estado;

    public TiendaClase() {
        // Constructor sin parámetros
    }

    public TiendaClase(String id, String nombre, String descripcion, String direccion, String extra, String usuarioAsociado, String imageUrl,String estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.extra = extra;
        this.usuarioAsociado = usuarioAsociado;
        this.imageUrl = imageUrl;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Resto de los métodos getter y setter para los demás atributos

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getUsuarioAsociado() {
        return usuarioAsociado;
    }

    public void setUsuarioAsociado(String usuarioAsociado) {
        this.usuarioAsociado = usuarioAsociado;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}