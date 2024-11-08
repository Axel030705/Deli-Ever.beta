package deli_ever.app.Vendedor.Comentarios;

public class Comentario {
    private String comentario;
    private String fecha;
    private String idCliente;
    private String idProducto;
    private String idTienda;
    private String Likes;
    private String Dislikes;
    private String imagenProducto;

    public Comentario() {
        // Constructor vacío necesario para Firebase
    }

    public Comentario(String comentario, String fecha, String idCliente, String idProducto, String idTienda, String Likes, String Dislikes, String imagenProducto) {
        this.comentario = comentario;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.idTienda = idTienda;
        this.Likes = Likes;
        this.Dislikes = Dislikes;
        this.imagenProducto = imagenProducto;
    }

    // Getters y setters
    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdTienda() {
        return idTienda;
    }

    public void setIdTienda(String idTienda) {
        this.idTienda = idTienda;
    }

    public String getLikes() {
        return Likes;
    }

    public void setLikes(String likes) {
        this.Likes = likes;
    }

    public String getDislikes() {
        return Dislikes;
    }

    public void setDislikes(String Dislikes) {
        this.Dislikes = Dislikes;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
}
