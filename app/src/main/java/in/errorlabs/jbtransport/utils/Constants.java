package in.errorlabs.jbtransport.utils;

/**
 * Created by root on 6/29/17.
 */

public class Constants {
    public static final String AppKey="TransportAppKey";
    public static final String RouteNumber="RouteNumber";
    public static final String HomeRouteObjectName="Routes";
    public static final String HomeRouteCoordinatesObjectName="Coordinates";
    private static final String BaseURL="https://jbgroup.org.in/sync/sync_mapp/transport_php/";
    public static final String RouteSelectDataUrl=BaseURL+"routeregister.php";
    public static final String RouteGetDetailsById=BaseURL+"getDetailsByRoute.php";
    public static final String RouteAllDetails=BaseURL+"getAllRoutes.php";
}
