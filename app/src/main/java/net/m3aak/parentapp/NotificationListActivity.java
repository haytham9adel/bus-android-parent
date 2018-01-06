package net.m3aak.parentapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.m3aak.parentapp.Beans.NotificationBean;
import net.m3aak.parentapp.Beans.Student;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Android Developer on 12/4/2015.
 */
public class NotificationListActivity extends AppCompatActivity {
    private Context appContext;
    ListView notification_list;
    List<Student> studInfiList;
    //private  String[] MOBILE_MODELS = {"Notification1","Notification2","Notification3","Notification4","Notification5","Notification6","Notification7","Notification8"};
    ArrayList<String> ROUTES_ARRAY;
    NotificationListAdapter listAdapterNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list);
        appContext = this;

        if (Utility.getSharedPreferences(NotificationListActivity.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.notificaton_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        } else {
            ViewCompat.setLayoutDirection(findViewById(R.id.notificaton_root_view), ViewCompat.LAYOUT_DIRECTION_LOCALE);
        }

        Utility.setSharedPreference(appContext, ConstantKeys.COUNT_OTHER_NOTI, "");
        // MainActivityNew.UpdateNavigation();
        init();
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
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.notification));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // GCMIntentService.noti_count=0;


        listAdapterNoti = new NotificationListAdapter();
        new GetNotiListTask().execute(Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID));
        notification_list = (ListView) findViewById(R.id.notification_list);
        notification_list.setAdapter(listAdapterNoti);
        // notification_list.setAdapter(new ArrayAdapter<String>(appContext,android.R.layout.simple_list_item_1,MOBILE_MODELS));
        notification_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                NotificationBean nb1 = arList.get(position);
//
//                new SweetAlertDialog(appContext).setTitleText("" + nb1.getType())
//                        .setContentText("" + nb1.getMessage())
//                        .show();
            }
        });

    }

    private class NotificationListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arList.size();
        }

        @Override
        public NotificationBean getItem(int position) {
            return arList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                viewHolderNoti viewHolder;

                if (convertView == null) {
                    LayoutInflater li = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = li.inflate(R.layout.notification_list_row, null);

                    viewHolder = new viewHolderNoti(convertView);

                    convertView.setTag(viewHolder);
                } else {
                    // we've just avoided calling findViewById() on resource everytime
                    // just use the viewHolder
                    viewHolder = (viewHolderNoti) convertView.getTag();
                }
           /* nb1=new NotificationBean();
            nb1=arList.get(position);*/
                NotificationBean nb1 = getItem(position);
                //Log.e("nb1",""+nb1.getMessage());
                viewHolder.no_txt.setText((position + 1) + ".");
                viewHolder.noti_message.setText(nb1.getMessage().toString());


                viewHolder.noti_type.setText(getType ( nb1.getType().toString() ) );
                viewHolder.time.setText(nb1.getTime().toString());
                // notifyDataSetChanged();
            }catch (Exception e) {e.printStackTrace();}
            return convertView;
        }
    }

    public class viewHolderNoti {
        TextView noti_message, no_txt, noti_type, time;

        viewHolderNoti(View v1) {
            noti_message = (TextView) v1.findViewById(R.id.noti_message);
            no_txt = (TextView) v1.findViewById(R.id.no_txt);
            noti_type = (TextView) v1.findViewById(R.id.noti_type);
            time = (TextView) v1.findViewById(R.id.time);
        }
    }

    //Task for get notification list
    List<NotificationBean> arList = new ArrayList<>();

    private class GetNotiListTask extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(NotificationListActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle("");
            dialog.setMessage(getString(R.string.wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String routes = Utility.getSharedPreferences(appContext, ConstantKeys.ROUTEID);
            Log.e("routes", Utility.getSharedPreferences(appContext, ConstantKeys.ROUTEID));
            Log.e("userid", "" + Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID));
            String URL = ConstantKeys.NOTY_UTL + routes + "&parent_id=" + params[0];

            Log.e("URL",URL);
            /*String responce= Utility.findJSONFromUrl(URL);
            Log.e("GetNotiListTask", "" + responce);
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
            dialog.dismiss();
            Log.e("notification", "" + s);
       /*     {"result":"success","notifications":[{"noti_id":1,"route_id":1,"student_id":0,"noti_type":"Wrong Route","date":"2016-02-12 01:28:26.0",
                    "msg":"Bus of Route-1 is moving on wrong path","parent_id":0}]},*/

            NotificationBean nb;
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        // Toast.makeText(appContext,""+networkResponse.getString("responseMessage"),Toast.LENGTH_LONG).show();
                        if (networkResponse.has("notifications")) {
                            if (networkResponse.getJSONArray("notifications").length() > 0) {
                                arList.clear();
                                for (int i = 0; i < networkResponse.getJSONArray("notifications").length(); i++) {
                                    JSONObject jsonObject = networkResponse.getJSONArray("notifications").getJSONObject(i);
                                    nb = new NotificationBean();
                                    nb.setMessage(jsonObject.getString("msg"));
                                    nb.setType(jsonObject.getString("noti_type"));
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = simpleDateFormat.parse(jsonObject.getString("date"));
                                    String dateTime = simpleDateFormat1.format(date);
                                    nb.setTime(convertInLocalTime(dateTime));
                                    arList.add(nb);
                                }
                                // Log.e("arList",""+arList);
                            }
                        }
                        listAdapterNoti.notifyDataSetChanged();

                    } else {
                        Toast.makeText(appContext, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                // Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                Log.e("GetNotiListTask Exc", "" + e);
            }
            super.onPostExecute(s);
        }


        /**
         * Get new date after converting in local time.
         *
         * @param serverDate Server date.
         * @return Return new date according to device time zone.
         * @throws ParseException Throw parsing exception.
         */
        private String convertInLocalTime(String serverDate) throws ParseException {
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            String strDate = "";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            try {
                TimeZone utcZone = TimeZone.getTimeZone("UTC");
                sdf.setTimeZone(utcZone);// Set UTC time zone
                Date myDate = sdf.parse(serverDate);
                sdf.setTimeZone(TimeZone.getDefault());// Set device time zone
                strDate = sdf.format(myDate);
                return strDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strDate;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

// Clear all notification
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    String getType(String json ) {
       switch (json ) {
          case "Instant Message" : return getResources().getString(R.string.instant_message)  ;
           case "Check in" : return getResources().getString(R.string.checkin_msg)  ;
           case "Check out " : return getResources().getString(R.string.checkOut_msg)  ;
           case "Morning Before" : return getResources().getString(R.string.morning_msg)  ;
           case "Evening Before" : return getResources().getString(R.string.evening_msg)  ;

       }
        return getResources().getString(R.string.driver_instant_message)  ;
    }
}