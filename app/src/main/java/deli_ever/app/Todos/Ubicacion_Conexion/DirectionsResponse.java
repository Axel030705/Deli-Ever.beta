package deli_ever.app.Todos.Ubicacion_Conexion;

import java.util.List;

public class DirectionsResponse {
    public List<Route> routes;

    public static class Route {
        public OverviewPolyline overview_polyline;

        @Override
        public String toString() {
            return "Route{" +
                    "overview_polyline=" + overview_polyline +
                    '}';
        }
    }

    public static class OverviewPolyline {
        public String points;

        @Override
        public String toString() {
            return "OverviewPolyline{" +
                    "points='" + points + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DirectionsResponse{" +
                "routes=" + routes +
                '}';
    }
}


