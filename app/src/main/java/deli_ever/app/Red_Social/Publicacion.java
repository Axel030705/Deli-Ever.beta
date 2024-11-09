package deli_ever.app.Red_Social;

public class Publicacion {
    private String idTienda;
    private String idUsuario;
    private String texto;
    private String imagenUrl;
    private long timestamp;
    private int likes;
    private int dislikes;

    // Constructor vacío requerido para Firebase
    public Publicacion() {}

    // Constructor con parámetros
    public Publicacion(String idTienda, String idUsuario, String texto, String imagenUrl, long timestamp, int likes, int dislikes) {
        this.idTienda = idTienda;
        this.idUsuario = idUsuario;
        this.texto = texto;
        this.imagenUrl = imagenUrl;
        this.timestamp = timestamp;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters y Setters
    public String getIdTienda() {
        return idTienda;
    }

    public void setIdTienda(String idTienda) {
        this.idTienda = idTienda;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
