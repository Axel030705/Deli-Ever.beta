package deli_ever.app.Todos.Perfil;

public class ImageInfo {
    private String url;

    public ImageInfo() {
        // Constructor vacío requerido para Firebase Database
    }

    public ImageInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
