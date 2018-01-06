package net.m3aak.parentapp.Fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import net.m3aak.parentapp.Beans.Student;
import net.m3aak.parentapp.DatabasePackage.DatabaseHandler;
import net.m3aak.parentapp.MapUtility.AppConstants;
import net.m3aak.parentapp.MapUtility.HttpConnection;
import net.m3aak.parentapp.MapUtility.PathJSONParser;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LAKHAN on 6/12/2015.
 */
public class MapFragment extends Fragment {
    View v;
    public final static String MODE_DRIVING = "driving";
    GoogleMap mGoogleMap;
    private LatLng LOWER_MANHATTAN = new LatLng(22.691887,
            75.86665440000002);
    private LatLng BROOKLYN_BRIDGE = new LatLng(22.7195687, 75.85772580000003);
    private LatLng WALL_STREET = new LatLng(22.7036793, 75.87333890000002);
    ArrayList<LatLng> INTERMIDIATE_STOP;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.map_fragment, null);
        v.postDelayed(new Runnable() {

            @Override
            public void run() {
                init();
            }

        }, 1000);
        return v;
    }

  /*  public MapFragment(int i)
    {
        init();
    }*/

    private void init() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        List<Student>students=db.getAllStudents();
        JSONObject networkResponse = null;
        for(int j=0;j<students.size();j++){
            INTERMIDIATE_STOP = new ArrayList<LatLng>();
        try {
            networkResponse = new JSONObject(Utility.getSharedPreferences(getActivity(),"student"+j));
            if (networkResponse.equals(null) || networkResponse.equals("")) {
                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            } else {
                if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                    LOWER_MANHATTAN = new LatLng(Double.parseDouble(networkResponse.getString("source_lat"))
                            , Double.parseDouble(networkResponse.getString("source_lng")));
                    WALL_STREET = new LatLng(Double.parseDouble(networkResponse.getString("student_lat"))
                            , Double.parseDouble(networkResponse.getString("student_lng")));
                    BROOKLYN_BRIDGE = new LatLng(Double.parseDouble(networkResponse.getString("destination_lat"))
                            , Double.parseDouble(networkResponse.getString("destination_lng")));
                    if (networkResponse.getJSONArray("lat").length() > 0) {
                        for (int i = 0; i < networkResponse.getJSONArray("lat").length(); i++) {
                            LatLng latLng1 = new LatLng(Double.parseDouble(networkResponse.getJSONArray("lat").getString(i))
                                    , Double.parseDouble(networkResponse.getJSONArray("lng").getString(i)));
                            INTERMIDIATE_STOP.add(latLng1);
                        }
                    }
                    setMap(j);
                }
            }
        } catch (Exception e) {
        }
    }}

    private void setMap(int i) {
        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(WALL_STREET);
        mGoogleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url,""+i);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                13));
        addMarkers();

    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = AppConstants.GOOGLE_MAPS_DIRECTION_API_URL
                + "origin=" + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "&destination=" + BROOKLYN_BRIDGE.latitude + "," + BROOKLYN_BRIDGE.longitude
                + "&sensor=false&units=metric&mode=" + MODE_DRIVING + "&alternatives=true&key=" + AppConstants.APP_GOOGLE_SERVER_API_KEY + "&waypoints=optimize:true|";
//        String waypoints = "waypoints=optimize:true|"
//                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
//                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
//                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
//                + WALL_STREET.longitude;
        String way = "";
        for (int i = 0; i < INTERMIDIATE_STOP.size(); i++) {
            LatLng points = INTERMIDIATE_STOP.get(i);
            way = way + points.latitude + "," + points.longitude + "|";
            System.out.println(way);
        }
        waypoints = waypoints + way;
        System.out.println(waypoints);
        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return waypoints;
    }
    private void addMarkers() {
        if (mGoogleMap != null) {

            for (int i = 0; i < INTERMIDIATE_STOP.size(); i++) {
                mGoogleMap.addMarker(new MarkerOptions().position(INTERMIDIATE_STOP.get(i)))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
            mGoogleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            mGoogleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            mGoogleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        String da;
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            da=url[1];
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result,da);
        }
    }
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String data;
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            data=jsonData[1];
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions().width(5).geodesic(true);
                List<HashMap<String, String>> path = routes.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polyLineOptions.addAll(points);
                if(data.equals("0")) {
                    polyLineOptions.color(Color.RED);
                }
                if(data.equals("1")){
                    polyLineOptions.color(Color.BLUE);
                }
            }
            mGoogleMap.addPolyline(polyLineOptions);
        }
    }
}