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
    public static final String Coordinates=BaseURL+"getCoordinatesById.php";
    public static final String RouteAllDetails=BaseURL+"getAllRoutes.php";
    public static final String GmapsDirectionsBaseURL="https://maps.googleapis.com/maps/api/directions/";
    public static final String GmapsResultType="json?";
    public static final String GmapsOrigin="origin=";
    public static final String GmapsDestination="destination=";
    public static final String Gmapswaypoints="waypoints=";
    public static final String GmapswaypointsOptimize="optimize:";
    public static final String GmapsAnd="&";
    public static final String GmapswaypointsSeperator="|";
    public static final String GmapsSensor="sensor=";
    public static final String GmapsMode="mode=";
    public static final String GmapsModeStyle="driving";
    public static final String GmapsAlternative="alternatives=";
    public static final String Gmapskey="key";
}
