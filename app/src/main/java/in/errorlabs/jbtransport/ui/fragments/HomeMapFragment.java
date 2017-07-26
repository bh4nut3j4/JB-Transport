package in.errorlabs.jbtransport.ui.fragments;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;
import okhttp3.OkHttpClient;

import static in.errorlabs.jbtransport.ui.activities.HomeActivity.COORDINATES_LOADER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<List<LatLng>>{
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    public String routeNumber;
    SharedPrefs sharedPrefs;
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    List<LatLng> list = new ArrayList<LatLng>();
    public static final int POLYLINE_LOADER_ID = 100;
    public static final String STRING_CONSTANT = "CONSTANT";
    public static final String LIST_CONSTANT = "list";
    @BindView(R.id.mapFrame)FrameLayout frameLayout;
    public HomeMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_map, container, false);
        ButterKnife.bind(this,rootView);
        sharedPrefs = new SharedPrefs(getContext());
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapfragment);
        if (savedInstanceState==null){
            mapFragment.getMapAsync(this);
        }
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> details = loaderManager.getLoader(COORDINATES_LOADER_ID);
        if (details == null) {
            loaderManager.initLoader(COORDINATES_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(COORDINATES_LOADER_ID, null, this);
        }
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    @Override
    public Loader<List<LatLng>> onCreateLoader(int id, final Bundle args) {
        if (id==COORDINATES_LOADER_ID){
            return new AsyncTaskLoader<List<LatLng>>(getContext()) {
                String bus_number;
                @Override
                protected void onStartLoading() {
                    if (routeNumber!=null && routeNumber.length()>0){
                        bus_number = routeNumber;
                        forceLoad();
                    }
                }
                @Override
                public List<LatLng> loadInBackground() {
                    list = getData(bus_number);
                    return list;
                }
            };
        } else if (id == POLYLINE_LOADER_ID) {
            return new AsyncTaskLoader<List<LatLng>>(getContext()) {
                String Url=null;
                @Override
                protected void onStartLoading() {
                    if (args!=null){
                        Url = args.getString(STRING_CONSTANT);
                        forceLoad();
                    }
                }
                @Override
                public List<LatLng> loadInBackground() {
                    getRoutePolyLine(Url);
                    return null;
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<LatLng>> loader, List<LatLng> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<LatLng>> loader) {

    }

    public List<LatLng> getData(String busNumber){
        AndroidNetworking.post(Constants.Coordinates)
                .setOkHttpClient(okHttpClient)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, getString(R.string.transportAppKey))
                .addBodyParameter(Constants.RouteNumber, busNumber)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        list= generatePath(response);
                        if (list!=null&& list.size()>0){
                            generateURL(list);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                    }
                });

        return list;
    }

    public List<LatLng> generatePath(JSONObject response){
        Log.d("LOG", response.toString());
        if (response.length() > 0) {
            if (response.has(Constants.HomeRouteCoordinatesObjectName)) {
                try {
                    JSONArray coOrdinatesArray = response.getJSONArray(Constants.HomeRouteCoordinatesObjectName);
                    if (coOrdinatesArray.length() > 0) {
                        list.clear();
                        try {
                            int array_lenght = coOrdinatesArray.length();

                            for (int i = 0; i <= coOrdinatesArray.length(); i++) {
                                int middle = coOrdinatesArray.length() / 4;
                                JSONObject ordinates = coOrdinatesArray.getJSONObject(i);
                                Double lat = Double.valueOf(ordinates.getString(HomeConstants.latitude));
                                Double lng = Double.valueOf(ordinates.getString(HomeConstants.longitude));
                                LatLng latLng = new LatLng(lat, lng);
                                String name = ordinates.getString(HomeConstants.stopName);
                                String isStop = ordinates.getString(HomeConstants.isStop);
                                if (i==0 || i>=coOrdinatesArray.length()){
                                    list.add(latLng);
                                }
                                if(isStop.equals("0")){
                                    list.add(latLng);
                                }else {
                                    Log.d("LOGG",list.toString());
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.title(name);
                                    //markerOptions.snippet(name);
                                    if (i==0){
                                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.start);
                                        Bitmap b=bitmapdraw.getBitmap();
                                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                    }else if (i==coOrdinatesArray.length()){
                                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.end);
                                        Bitmap b=bitmapdraw.getBitmap();
                                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                    }else {
                                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.updown);
                                        Bitmap b=bitmapdraw.getBitmap();
                                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                    }
                                    mMap.addMarker(markerOptions);
                                    if (i == 0) {
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(latLng)
                                                .zoom(13)
                                                .bearing(180)
                                                .tilt(30)
                                                .build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    }
                                }

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.d("LIST", list.toString());
        }
        return list;
    }

    public void getRoutePolyLine(String Url) {
        AndroidNetworking.post(Url)
                .setOkHttpClient(okHttpClient)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            drawPath(response.toString());
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void generateURL(List<LatLng> list){
        LatLng latLng;
        String BaseUrl = Constants.GmapsDirectionsBaseURL;
        String ReslutType =Constants.GmapsResultType;
        String OriginName =Constants.GmapsOrigin;
        latLng=list.get(0);
        String Origin = latLng.latitude+","+latLng.longitude;
        String DestinationName =Constants.GmapsDestination;
        //int last= list.size();
        //Toast.makeText(getContext(),,Toast.LENGTH_SHORT).show();
        latLng=list.get(list.size()-1);
        String Destination = latLng.latitude+","+latLng.longitude;
        String WayPoints = Constants.Gmapswaypoints;
        String Optimize = Constants.GmapswaypointsOptimize;
        String True = "true";
        String False = "false";
        String And = Constants.GmapsAnd;
        String Seperator = Constants.GmapswaypointsSeperator;
        String Sensor = Constants.GmapsSensor;
        String Mode = Constants.GmapsMode;
        String ModeStyle = Constants.GmapsModeStyle;
        String Alternatives = Constants.GmapsAlternative;
        String Key = getString(R.string.google_api_key);
        String DataURL = BaseUrl+ReslutType+OriginName+Origin+And+WayPoints+Optimize+True;
        for (int i = 1; i<list.size(); i++){
            latLng=list.get(i);
            DataURL=DataURL+Seperator;
            String value = latLng.latitude+","+latLng.longitude;
            DataURL=DataURL+value;
        }
        DataURL=DataURL+Seperator;
        DataURL=DataURL+And+DestinationName+Destination+And+Sensor+False+And+Mode+ModeStyle+And+Alternatives+True+And+Key;
        Log.d("KEYURL",DataURL);
        Bundle bundle = new Bundle();
        bundle.putString(STRING_CONSTANT,DataURL);
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> details = loaderManager.getLoader(POLYLINE_LOADER_ID);
        if (details == null) {
            loaderManager.initLoader(POLYLINE_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(POLYLINE_LOADER_ID, bundle, this);
        }
    }

    public void drawPath(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            Log.d("ROUTES", routeArray.toString());
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))
                    .geodesic(true)
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
