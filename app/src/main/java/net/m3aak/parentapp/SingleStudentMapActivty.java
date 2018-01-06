package net.m3aak.parentapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import net.m3aak.parentapp.Adapters.ChildListAdapter;
import net.m3aak.parentapp.MapUtility.AppConstants;
import net.m3aak.parentapp.MapUtility.HttpConnection;
import net.m3aak.parentapp.MapUtility.PathJSONParser;
import net.m3aak.parentapp.MyWidgets.CircleImageView;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;
import net.m3aak.parentapp.services.UpdateBlinkServiceCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BD-2 on 8/24/2015.
 */
public class SingleStudentMapActivty extends AppCompatActivity implements UpdateBlinkServiceCallback {
    View v;
    private Context cn;
    public final static String MODE_DRIVING = "driving";
    GoogleMap mGoogleMap;
    private LatLng LOWER_MANHATTAN;//= new LatLng(22.691887,75.86665440000002);
    private LatLng BROOKLYN_BRIDGE;//= new LatLng(22.7195687, 75.85772580000003);
    private LatLng WALL_STREET;//= new LatLng(22.7036793, 75.87333890000002);
    ArrayList<LatLng> INTERMIDIATE_STOP = new ArrayList<LatLng>();
    Marker movingMarker = null;
    boolean isFirstTime = true;
    public static Handler h = new Handler();
    String isBlink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlestud_mapfragment);

        if (Utility.getSharedPreferences(SingleStudentMapActivty.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.map_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        cn = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isBlink = bundle.getString("blink_status");
        }
        init();
        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        if (ChildListAdapter.STUD_ID != null) {
            new GetLongLat().execute(ChildListAdapter.STUD_ID);
        }
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception ignored) {
        }
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.student_tracking));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void UpdateBlink() {
        try {
            isBlink = "1";
            init();
            mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            if (ChildListAdapter.STUD_ID != null) {
                new GetLongLat().execute(ChildListAdapter.STUD_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.out.println("GetLongLat " + s);
/*{"result":"success","student_lat":"22.740084545617844","destination_lng":"75.89570239999999","source_lng":"75.86763380000001",
"lng""75.89269638061523","75.89235305786133","75.88479995727539"],"student_lng":"75.89269638061523",
"destination_lat":"22.7684301","source_lat":"22.6925763","lat""22.740084545617844","22.736606786317097","22.71475663243386"]}*/
//{"result":"success","student_lat":"22.89586991985336","destination_lng":"75.8577258","source_lng":"75.8577258","lng":["76.71280860900879","75.9811019897461"],"student_lng":"75.9811019897461","destination_lat":"22.7195687","source_lat":"22.7195687","lat":["23.041214425432685","22.89586991985336"]}
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(cn, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
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
                        setMap();
                        scheduleRealTimeLocation();//Function for scheduling realtime location
                    } else {
                        Toast.makeText(cn, "" + networkResponse.getString("error"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                //  Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }

    private void setMap() {
        addMarkers();
    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = AppConstants.GOOGLE_MAPS_DIRECTION_API_URL
                + "origin=" + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "&destination=" + BROOKLYN_BRIDGE.latitude + "," + BROOKLYN_BRIDGE.longitude
                + "&sensor=false&units=metric&mode=" + MODE_DRIVING + "&alternatives=true&key=" + AppConstants.APP_GOOGLE_SERVER_API_KEY + "&waypoints=optimize:true|";

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
        try {
            if (mGoogleMap != null) {

           /* for (int i = 0; i < INTERMIDIATE_STOP.size(); i++) {
                mGoogleMap.addMarker(new MarkerOptions().position(INTERMIDIATE_STOP.get(i)))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }*/
                mGoogleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                        .title(getString(R.string.destination)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_school)));
                mGoogleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                        .title(getString(R.string.source)).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
                Log.e("STUD_NAME :", "" + ChildListAdapter.STUD_NAME);
                View viewMarker = ((LayoutInflater) cn.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.marker_student, null);
                CircleImageView myImage = (CircleImageView) viewMarker.findViewById(R.id.img_id);

                if (!Utility.isStringNullOrBlank(ChildListAdapter.STUD_IMAGE)) {
                    Picasso.with(cn)
                            .load(ConstantKeys.IMAGE_URL + ChildListAdapter.STUD_IMAGE)
                            .into(myImage);
                }

                ImageView imgBg = (ImageView) viewMarker.findViewById(R.id.bg_arrow);
                if (isBlink.equals("1")) {
                    imgBg.setImageResource(R.drawable.down_arrow_green);
                } else {
                    imgBg.setImageResource(R.drawable.down_arrow_yellow);
                }

                Bitmap bmp = createDrawableFromView(cn, viewMarker);
                mGoogleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                        .title(ChildListAdapter.STUD_NAME + "'s Home")).setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOWER_MANHATTAN,
                        13));
                // .title(ChildListAdapter.STUD_NAME + " Home")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        }catch (Exception e) { e.printStackTrace();}
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
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
           try {
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
           }catch (Exception e) {e.printStackTrace();}
            //         mGoogleMap.addPolyline(polyLineOptions);
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
                h.postDelayed(this, 2500);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleRealTimeLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (h != null) {
            h.removeCallbacks(null);
            h.removeMessages(0);
        }
    }

    /*   ------------------------------>CODE FOR GET REAL TIME LOCATION OF BUS<---------------------------------   */
    public class GetRealTimeLongLat extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;

        @Override
        protected void onPreExecute() {
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
            // System.out.println(s);
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

                            LatLng loc_real = new LatLng(lat, lng);
                            if (movingMarker != null) {
                                ArrayList<LatLng> directioList = new ArrayList<>();
                                LatLng markerLocation = movingMarker.getPosition();
                                Location prevLoc = new Location("");
                                Location currLoc = new Location("");
                                prevLoc.setLatitude(markerLocation.latitude);
                                prevLoc.setLongitude(markerLocation.longitude);
                                movingMarker.remove();
                                currLoc.setLatitude(loc_real.latitude);
                                currLoc.setLongitude(loc_real.longitude);

                                double bearing = bearingBetweenLocations(new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude()), new LatLng(currLoc.getLatitude(), currLoc.getLongitude()));
                                //If you have a bearing, you can set the rotation of the marker using MarkerOptions.rotation():
                                Log.e("bearing Second", "" + bearing);
                                movingMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(loc_real)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_ic))
                                        .anchor(0.5f, 0.5f)
                                        .rotation((float) bearing)
                                        .flat(true));
                                directioList.add(new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude()));
                                directioList.add(new LatLng(currLoc.getLatitude(), currLoc.getLongitude()));
                                //animateMarker3(mGoogleMap, movingMarker, directioList, false);//to be opened
                                if (bearing > 0)
                                    rotateMarker(movingMarker, (float) bearing);

                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_real));
                            } else {
                                Log.e("marker", "marker null");
                              /*  movingMarker = mGoogleMap
                                        .addMarker(new MarkerOptions()
                                                .position(loc_real)
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.bus)));
                                animateMarker(movingMarker, loc_real,
                                        false, 0);
                                animateMarker(movingMarker, loc_real,
                                        false, 0);*/ //to be opened
                                Location prevLoc = new Location("");
                                Location currLoc = new Location("");
                                prevLoc.setLatitude(LOWER_MANHATTAN.latitude);
                                prevLoc.setLongitude(LOWER_MANHATTAN.longitude);
                                currLoc.setLatitude(loc_real.latitude);
                                currLoc.setLongitude(loc_real.longitude);
                                //float bearing = prevLoc.bearingTo(currLoc) ;
                                double bearing = bearingBetweenLocations(new LatLng(LOWER_MANHATTAN.latitude, LOWER_MANHATTAN.longitude), new LatLng(currLoc.getLatitude(), currLoc.getLongitude()));
                                //If you have a bearing, you can set the rotation of the marker using MarkerOptions.rotation():
                                Log.e("bearing First", "" + bearing);
                                movingMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(loc_real)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_ic))
                                        .anchor(0.5f, 0.5f)
                                        .rotation((float) bearing)
                                        .flat(true));
                                if (bearing > 0)
                                    rotateMarker(movingMarker, (float) bearing);
                            }
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_real));

                           /* if(isFirstTime==true) {
                                isFirstTime=false;
                                // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_real));
                                // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                                movingMarker = mGoogleMap
                                        .addMarker(new MarkerOptions()
                                                .position(loc_real)
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.bus)));
                                animateMarker(movingMarker, loc_real,
                                        false, 0);
                                Log.e("marker", "marker first time");
                               *//* LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(LOWER_MANHATTAN)
                                        .include(BROOKLYN_BRIDGE).include(WALL_STREET)
                                        .include(loc_real).build();

                                Point displaySize = new Point();
                                getWindowManager().getDefaultDisplay().getSize(displaySize);

                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 500, 15));*//*
                               // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }*/

                        }
                    }
                }
            } catch (Exception e) {
                Log.e("RealTimeLongLat Exce", "" + e);
            }
            super.onPostExecute(s);
        }

    }


    /*-----------------------------------CODE FOR SHOW MARKER---------------------------------*/
    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                }
            }
        });
    }

    private static void animateMarker3(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                       final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;


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
    }

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

    public void animateMarker2(final Marker marker, final LatLng toPosition,
                               final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
