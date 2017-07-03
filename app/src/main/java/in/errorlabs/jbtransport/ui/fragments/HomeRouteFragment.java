package in.errorlabs.jbtransport.ui.fragments;


import android.graphics.Color;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;
import okhttp3.OkHttpClient;

import static in.errorlabs.jbtransport.ui.activities.HomeActivity.COORDINATES_LOADER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeRouteFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<List<LatLng>> {

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    SharedPrefs sharedPrefs;
    private GoogleMap mMap;
    List<LatLng> coordinatesarray_List = new ArrayList<LatLng>();
    public static final int POLYLINE_LOADER_ID = 100;
    public static final String STRING_CONSTANT = "CONSTANT";
    SupportMapFragment mapFragment;

    public HomeRouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_route, container, false);
        sharedPrefs = new SharedPrefs(getContext());
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> details = loaderManager.getLoader(COORDINATES_LOADER_ID);
        if (details == null) {
            loaderManager.initLoader(COORDINATES_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(COORDINATES_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<LatLng>> onCreateLoader(int id, final Bundle args) {
        if (id == COORDINATES_LOADER_ID) {
            return new AsyncTaskLoader<List<LatLng>>(getContext()) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                }
                @Override
                public List<LatLng> loadInBackground() {
                    AndroidNetworking.post(Constants.Coordinates)
                            .setOkHttpClient(okHttpClient)
                            .setPriority(Priority.HIGH)
                            .addBodyParameter(Constants.AppKey, String.valueOf(R.string.transportAppKey))
                            .addBodyParameter(Constants.RouteNumber, sharedPrefs.getSelectedRouteNumber())
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    coordinatesarray_List= generatePath(response);
                                    if (coordinatesarray_List!=null&& coordinatesarray_List.size()>0){
                                        generateURL(coordinatesarray_List);
                                    }
                                }
                                @Override
                                public void onError(ANError anError) {
                                    Log.d("LOG", anError.toString());
                                }
                            });
                    return coordinatesarray_List;
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
        if (loader.getId() == COORDINATES_LOADER_ID) {
            if (data==null){
                Log.d("DATA","NULLLL");
            }
            if (data!=null){
                Log.d("DATA","NOTNULLLL");
                Log.d("DATAA",data.toString());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<LatLng>> loader) {

    }

    public List<LatLng> generatePath(JSONObject response){
        Log.d("LOG", response.toString());
        if (response.length() > 0) {
            if (response.has(Constants.HomeRouteCoordinatesObjectName)) {
                try {
                    JSONArray coOrdinatesArray = response.getJSONArray(Constants.HomeRouteCoordinatesObjectName);
                    if (coOrdinatesArray.length() > 0) {
                        coordinatesarray_List.clear();
                        for (int i = 0; i <= coOrdinatesArray.length(); i++) {
                            int middle = coOrdinatesArray.length() / 4;
                            JSONObject ordinates = coOrdinatesArray.getJSONObject(i);
                            Double lat = Double.valueOf(ordinates.getString(HomeConstants.latitude));
                            Double lng = Double.valueOf(ordinates.getString(HomeConstants.longitude));
                            LatLng latLng = new LatLng(lat, lng);
                            String name = ordinates.getString(HomeConstants.stopID);
                            coordinatesarray_List.add(latLng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(name);
                            mMap.addMarker(markerOptions);
                            if (i == middle) {
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)
                                        .zoom(12)
                                        .bearing(90)
                                        .tilt(30)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.d("LIST00000", coordinatesarray_List.toString());
        }
        return coordinatesarray_List;
    }

    public void getRoutePolyLine(String Url) {

        AndroidNetworking.post(Url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MAP", response.toString());
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
        String DataURL = BaseUrl+ReslutType+OriginName+Origin+And+WayPoints+Optimize+False;
        for (int i=1;i<list.size();i++){
            DataURL=DataURL+Seperator;
            latLng=list.get(i);
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
            //Tranform the string into a json object
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
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
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
    @Override
    public void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapFragment.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

}
