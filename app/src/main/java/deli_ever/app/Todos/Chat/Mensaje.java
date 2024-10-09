package deli_ever.app.Todos.Chat;

public class Mensaje {
    private String mensaje;
    private String type_mensaje;
    private String urlFoto;
    private String sender; // Nuevo campo para almacenar el remitente

    public Mensaje() {
    }

    public Mensaje(String mensaje, String type_mensaje, String urlFoto, String sender) {
        this.mensaje = mensaje;
        this.type_mensaje = type_mensaje;
        this.urlFoto = urlFoto;
        this.sender = sender;
    }

    // Otros métodos y getters/setters...

    public String getSender() {
        return sender;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getType_mensaje() {
        return type_mensaje;
    }

    public String getUrlFoto() {
        return urlFoto;
    }
}


