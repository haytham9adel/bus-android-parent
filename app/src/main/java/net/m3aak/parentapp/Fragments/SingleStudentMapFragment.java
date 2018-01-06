package net.m3aak.parentapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import net.m3aak.parentapp.Adapters.ChildListAdapter;
import net.m3aak.parentapp.MainActivityNew;
import net.m3aak.parentapp.MapUtility.AppConstants;
import net.m3aak.parentapp.MapUtility.HttpConnection;
import net.m3aak.parentapp.MapUtility.PathJSONParser;
import net.m3aak.parentapp.NetworkHelperGet;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Android Developer on 12/2/2015.
 */
public class SingleStudentMapFragment extends Fragment {

    View v;
    private Context cn;
    public final static String MODE_DRIVING = "driving";
    GoogleMap mGoogleMap;
    private LatLng LOWER_MANHATTAN;//= new LatLng(22.691887,75.86665440000002);
    private LatLng BROOKLYN_BRIDGE;//= new LatLng(22.7195687, 75.85772580000003);
    private LatLng WALL_STREET;//= new LatLng(22.7036793, 75.87333890000002);
    ArrayList<LatLng> INTERMIDIATE_STOP = new ArrayList<LatLng>();
    ArrayList<LatLng> arrayRealTimeLocation;
    Marker movingMarker = null;
    boolean isFirstTime = true;
    public static Handler h = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.singlestud_mapfragment, null);
        Log.e("Student Fragment", "");

        if (Utility.getSharedPreferences(getActivity(), ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(v.findViewById(R.id.map_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        cn = getActivity();
        v.postDelayed(new Runnable() {

            @Override
            public void run() {
                mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                initStudnetMap();
                //setRetainInstance(true);
            }

        }, 1000);
        return v;
    }

    public void initStudnetMap() {
        cn = getActivity();
        Log.e("Stu_id :", "" + ChildListAdapter.STUD_ID);
       /* if(StudentListFragment.STUD_ID!=null) {
            new GetLongLat().execute(StudentListFragment.STUD_ID);
        }*/
    }

    /*   ------------------------------>CODE FOR GET STUDENT<---------------------------------   */
    public class GetLongLat extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        // ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
          /*  dialog.setCancelable(false);
            dialog.setTitle("Loading...");
            dialog.setMessage("Please Wait.......");
            dialog.show();*/
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = ConstantKeys.SERVER_URL + "student_lat_lng?" + "student_id" + "=" + params[0];
         /*   String responce = Utility.findJSONFromUrl(URL);
            return responce;*/
            NetworkHelperGet putRequest = new NetworkHelperGet(URL);
            try {
                return putRequest.sendGet();
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // dialog.dismiss();
            System.out.println(s);
/*{"result":"success","student_lat":"22.740084545617844","destination_lng":"75.89570239999999","source_lng":"75.86763380000001",
"lng""75.89269638061523","75.89235305786133","75.88479995727539"],"student_lng":"75.89269638061523",
"destination_lat":"22.7684301","source_lat":"22.6925763","lat""22.740084545617844","22.736606786317097","22.71475663243386"]}*/
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(cn, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        LOWER_MANHATTAN = new LatLng(Double.parseDouble(networkResponse.getString("source_lat"))
                                , Double.parseDouble(networkResponse.getString("source_lng")));
                        arrayRealTimeLocation = new ArrayList<LatLng>();
                        arrayRealTimeLocation.add(LOWER_MANHATTAN);
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
                        setMap();
                        scheduleRealTimeLocation();//Function for scheduling realtime location
                    } else {
                        Toast.makeText(cn, "" + networkResponse.getString("error"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                //  Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }

    }

    private void setMap() {

        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(WALL_STREET);
        mGoogleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                13));

        // Zoom in the Google Map
        // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        // mGoogleMap.animateCamera();
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
            Log.e("STUD_NAME :", "" + ChildListAdapter.STUD_NAME);
            mGoogleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title(ChildListAdapter.STUD_NAME + "'s Home")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
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
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
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
                polyLineOptions.color(Color.RED);
            }
            mGoogleMap.addPolyline(polyLineOptions);
        }
    }


    private void scheduleRealTimeLocation() {
        // final Handler h = new Handler();
        int delay = 1000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (ChildListAdapter.STUD_ID != null && ChildListAdapter.ROUTE_ID != null) {
                    new GetRealTimeLongLat().execute(ChildListAdapter.STUD_ID, ChildListAdapter.ROUTE_ID);
                } else {
                    // h.removeCallbacksAndMessages(null);
                    Log.e("", "Handler Else");
                }
                h.postDelayed(this, 50000);
            }
        }, 1000);
    }


    /*   ------------------------------>CODE FOR GET REAL TIME LOCATION OF BUS<---------------------------------   */
    public class GetRealTimeLongLat extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        //    ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
         /*   dialog.setCancelable(false);
            dialog.setTitle("Getting realtime location...");
            dialog.setMessage("Please Wait.......");
            dialog.show();*/
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = ConstantKeys.SERVER_URL + "get_realtime_location?" + "student_id" + "=" + params[0] + "&" + "route_id=" + params[1];
            NetworkHelperGet putRequest = new NetworkHelperGet(URL);
            try {
                return putRequest.sendGet();
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // dialog.dismiss();
            System.out.println(s);
            try {
                networkResponse = new JSONObject(s);
                Log.e("Response realtime loc:", "" + networkResponse);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(cn, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.has("route_lat_lng")) {
                        JSONArray jsonarray = networkResponse.getJSONArray("route_lat_lng");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            double lat = Double.parseDouble(jsonobject.getString("lat"));
                            double lng = Double.parseDouble(jsonobject.getString("lng"));
                            Log.e("RealtimeLat, long :", "" + lat + " , " + lng);

                            LatLng loc_real = new LatLng(lat, lng);
                            arrayRealTimeLocation.add(loc_real);
                            PolylineOptions polyLineOptionMoving = new PolylineOptions().width(5).geodesic(true);
                            polyLineOptionMoving.addAll(arrayRealTimeLocation);
                            polyLineOptionMoving.color(Color.GREEN);
                            mGoogleMap.addPolyline(polyLineOptionMoving);


                            if (movingMarker != null) {

                                movingMarker.remove();
                                movingMarker = mGoogleMap
                                        .addMarker(new MarkerOptions()
                                                .position(loc_real)
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.bus)));
                                animateMarker(movingMarker, loc_real,
                                        false, 0);

                            } else {
                                movingMarker = mGoogleMap
                                        .addMarker(new MarkerOptions()
                                                .position(loc_real)
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.bus)));
                                animateMarker(movingMarker, loc_real,
                                        false, 0);
                            }

                            if (isFirstTime == true) {
                                isFirstTime = false;
                                // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_real));
                                // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(LOWER_MANHATTAN)
                                        .include(BROOKLYN_BRIDGE).include(WALL_STREET)
                                        .include(loc_real).build();

                                Point displaySize = new Point();
                                getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);

                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 500, 15));
                                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }

                        }
                    }
                   /* if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
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
                        setMap();
                    } else {
                        Toast.makeText(appContext, "" + networkResponse.getString("error"), Toast.LENGTH_LONG).show();
                    }*/
                }
            } catch (Exception e) {
                Log.e("RealTimeLongLat Exce", "" + e);
            }
            super.onPostExecute(s);
        }

    }


    /*-----------------------------------CODE FOR SHOW MARKER---------------------------------*/
    private void animateMarker(final Marker marker, final LatLng toPosition,
                               final boolean hideMarker, final float deg) {
        try {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            com.google.android.gms.maps.Projection proj = mGoogleMap.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 500;
            final LinearInterpolator interpolator = new LinearInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));
                    float t1 = interpolator.getInterpolation((float) elapsed / duration);
                    float rot = t1 * deg + (1 - t1) * startRotation;
                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarker) {
                            marker.setVisible(false);
                        } else {
                            marker.setVisible(true);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //initStudnetMap();
        Log.e("------", "OnResume :Stdent Map Fragment");
    }

   /* @Override
    public void onPause() {
        super.onPause();
        Log.e("------", "onPause :Stdent Map Fragment");
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(new MainActivityNew());
        // initStudnetMap();
        // setRetainInstance(true);
    }

  /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initStudnetMap();
        Log.e("------", "OnCreate :Stdent Map Fragment");
    }*/

  /*  @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("------", "onViewCreated :Stdent Map Fragment");
    }*/

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("------", "onActivityCreated :Stdent Map Fragment");
    }*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            // launch your AsyncTask here, if the task has not been executed yet
            Utility.setSharedPreference(getActivity(), "WHICHACTIVITY", "MainActivityNew");
            if (ChildListAdapter.STUD_ID != null) {
                new GetLongLat().execute(ChildListAdapter.STUD_ID);

            }

        }
    }

    public void TestFunction() {
        //Toast.makeText(getActivity(),"Fuction Call",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
