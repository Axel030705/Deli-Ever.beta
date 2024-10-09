package deli_ever.app.Todos.Ubicacion_Conexion;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static Retrofit retrofit;

    public static DirectionsApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(DirectionsApiService.class);
    }
}


