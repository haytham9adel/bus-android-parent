package net.m3aak.parentapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.m3aak.parentapp.Fragments.SingleStudentMapFragment;
import net.m3aak.parentapp.Fragments.StudentListFragment;
import net.m3aak.parentapp.NavigationPack.NavigationDrawerAdapter;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;
import net.m3aak.parentapp.services.UpdateBlinkService;
import net.m3aak.parentapp.services.UpdateBlinkServiceCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivityNew extends AppCompatActivity implements UpdateBlinkServiceCallback {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerPanel;
    private static Context apContext;
    private static String[] titles = null;
    private static int[] list_img = null;
    public static int TabOption = 2;
    public static NavigationDrawerAdapter navigationDrawerAdapter;
    UpdateBlinkService myService;
    private boolean bound = false;
    public static boolean isVisibleMainActivity = false;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_new);
        try {
            //for arabic language.
            if (Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_Language).equals("1")) {
                ViewCompat.setLayoutDirection(findViewById(R.id.drawer_layout), ViewCompat.LAYOUT_DIRECTION_RTL);
            } else {
                ViewCompat.setLayoutDirection(findViewById(R.id.drawer_layout), ViewCompat.LAYOUT_DIRECTION_LOCALE);
            }

            apContext = this;
            Utility.setSharedPreference(apContext, ConstantKeys.ISCHATVISIBLE, "0");
            titles = new String[]{getString(R.string.home), getString(R.string.profile), getString(R.string.chat_with_school), getString(R.string.notification), getString(R.string.report_absent), getString(R.string.change_pass), getString(R.string.change_language), getString(R.string.setting), getString(R.string.log_out)};
            list_img = new int[]{R.drawable.home, R.drawable.profile, R.drawable.message, R.drawable.notification, R.drawable.report_absent, R.drawable.change_pass, R.drawable.lang, R.drawable.setting, R.drawable.signout};
            Utility.setSharedPreference(apContext, "WHICHACTIVITY", "MainActivityNew");
        }catch (Exception e ) {e.printStackTrace();}

        }

    private void init() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
        xfragmentTransaction.replace(R.id.containerView, new StudentListFragment()).commit();
    }

    private void setUpNavigationDrawerHome() {
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
        if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(apContext, ConstantKeys.school_name))) {
            ((TextView) findViewById(R.id.title)).setText(Utility.getSharedPreferences(apContext, ConstantKeys.school_name));
        } else {
            ((TextView) findViewById(R.id.title)).setText(getString(R.string.title));
        }
        if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(apContext, ConstantKeys.SCHOOL_LOGO))) {
            Picasso.with(apContext)
                    .load(ConstantKeys.SCHOOL_IMAGE_URL + Utility.getSharedPreferences(apContext, ConstantKeys.SCHOOL_LOGO))
                    .into(((ImageView) findViewById(R.id.imgViewSchoolLogo)));
        }
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(mDrawerPanel) == true) {
                    mDrawerLayout.closeDrawer(mDrawerPanel);
                } else {
                    mDrawerLayout.openDrawer(mDrawerPanel);
                }
            }
        });
        ListView mDrawerListView = (ListView) findViewById(R.id.navDrawerList);
        mDrawerPanel = (LinearLayout) findViewById(R.id.navDrawerPanel);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawerAdapter = new NavigationDrawerAdapter(apContext, titles, list_img);
//      ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menulist));
        mDrawerListView.setAdapter(navigationDrawerAdapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(mDrawerPanel);
                openActivites(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void openActivites(int pos) {
        //{"Home", "Profile", "Chat With School", "Notifications", "Report Absent", "Change Password", "Settings", "Log Out"};
        switch (pos) {
            case 0:
                // Home
                FragmentManager mFragmentManager = getSupportFragmentManager();
                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                xfragmentTransaction.replace(R.id.containerView, new StudentListFragment()).commit();
                break;
            case 1:
                //Profile
                startActivity(new Intent(apContext, ProfileActivity.class));
                break;
            case 2:
                //Message
                // startActivity(new Intent(apContext, ChatSocketActivity.class));
                startActivity(new Intent(apContext, MessageActivityNew.class));
                break;
            case 3:
                //Notificaiton
                startActivity(new Intent(apContext, NotificationListActivity.class));
                break;
            case 4:
                //Absent Report
                startActivity(new Intent(apContext, SetAbsentActivity.class));
                break;
            case 5:
                startActivity(new Intent(apContext, ChangePasswordActivity.class));
                break;
            case 6:
                showSingleChoiceAlert(MainActivityNew.this);
                break;
            case 7:
                //Settings
                startActivity(new Intent(apContext, SettingActivity.class));
                break;
            case 8://Logout
                Logout();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerPanel)) {
            mDrawerLayout.closeDrawer(mDrawerPanel);
        } else {
            super.onBackPressed();
        }
    }

    public void Logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityNew.this);
        builder.setMessage(getString(R.string.want_logout));
        builder.setTitle(getString(R.string.please_confirm));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //new LogoutTask().execute(Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.USER_ID));
                logoutT(Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.USER_ID));
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void logoutT(String parent_id) {
        try {
            NetworkHelperGet networkHelperGet = new NetworkHelperGet(ConstantKeys.SERVER_URL + "parentLogout");
            JSONStringer jsonStringer = new JSONStringer().object()
                    .key("parent_id").value(parent_id).endObject();
            networkHelperGet.postRequest(ConstantKeys.SERVER_URL + "parentLogout", jsonStringer, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String s = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (s != null & !s.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (jsonObject.getString("result").equals("success")) {
                                        doneLogout();
                                    } else {
                                        Toast.makeText(MainActivityNew.this, jsonObject.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(MainActivityNew.this, getString(R.string.servernotresponding), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LogoutTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
/*{"parent_id":"12"}*/
            NetworkHelperGet networkHelperGet = new NetworkHelperGet(ConstantKeys.SERVER_URL + "parentLogout");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("parent_id", params[0]);
                return networkHelperGet.performPostCall(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("log out responce ", "logout " + s);
/*{"result":"success","responseMessage":"Parent logged out successfully"}*/

            if (s != null & !s.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("result").equals("success")) {
                        doneLogout();
                    } else {
                        Toast.makeText(MainActivityNew.this, jsonObject.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivityNew.this, getString(R.string.servernotresponding), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doneLogout() {
        Utility.setSharedPreference(apContext, ConstantKeys.USER_ID, "");
        Utility.setSharedPreference(apContext, "DBNULL", "");
        SingleStudentMapFragment.h.removeCallbacksAndMessages(null);
        SharedPreferences settings = apContext.getSharedPreferences("Tracking Bus", 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.clear();
        //      editor.commit();

        File database = getApplicationContext().getDatabasePath("StudentManager");

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.e("Database", "Not Found");
        } else {
            Log.e("Database", "Found");
            database.delete();
        }
        Utility.setSharedPreference(MainActivityNew.this, ConstantKeys.ALREADY_LOGIN, "No");
        startActivity(new Intent(apContext, LoginActivity.class));
        finish();
    }

    public void showSingleChoiceAlert(final Context context) {

        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_picker_popup);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        lp.width = width - 50;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView englishTxt = (TextView) dialog.findViewById(R.id.english_txt);
        final TextView arabicTxt = (TextView) dialog.findViewById(R.id.arabic_txt);
        final LinearLayout arabicTxtLYT = (LinearLayout) dialog.findViewById(R.id.arabic_txt_layout);
        final LinearLayout englishTxtLYT = (LinearLayout) dialog.findViewById(R.id.english_txt_layout);

        if (Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_Language).equals("1")) {
            language = "1";
            englishTxtLYT.setBackgroundColor(Color.WHITE);
            arabicTxtLYT.setBackgroundResource(R.drawable.round_corner_fill);
            englishTxt.setTextColor(Color.BLACK);
            arabicTxt.setTextColor(Color.WHITE);
        } else {
            language = "0";
            englishTxtLYT.setBackgroundResource(R.drawable.round_corner_fill);
            arabicTxtLYT.setBackgroundColor(Color.WHITE);
            englishTxt.setTextColor(Color.WHITE);
            arabicTxt.setTextColor(Color.BLACK);
        }

        arabicTxtLYT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language = "1";
                englishTxtLYT.setBackgroundColor(Color.WHITE);
                arabicTxtLYT.setBackgroundResource(R.drawable.round_corner_fill);
                englishTxt.setTextColor(Color.BLACK);
                arabicTxt.setTextColor(Color.WHITE);
            }
        });

        englishTxtLYT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language = "0";
                englishTxtLYT.setBackgroundResource(R.drawable.round_corner_fill);
                arabicTxtLYT.setBackgroundColor(Color.WHITE);
                englishTxt.setTextColor(Color.WHITE);
                arabicTxt.setTextColor(Color.BLACK);
            }
        });

        Button saveBtn = (Button) dialog.findViewById(R.id.action_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage(Integer.parseInt(language));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setLanguage(int tag) {
        try {
            new SettingTask().execute();

            Utility.setSharedPreference(MainActivityNew.this, ConstantKeys.Setting_Language, "" + tag);
            if (tag == 1) {
                Locale locale = new Locale("ar");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            }
              startActivity(new Intent(MainActivityNew.this, MainActivityNew.class));
              finish();
        }catch (Exception e) {e.printStackTrace();}

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisibleMainActivity = true;
        Utility.setSharedPreference(apContext, "WHICHACTIVITY", "MainActivityNew");
        //for arabic language.
        if (Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.drawer_layout), ViewCompat.LAYOUT_DIRECTION_RTL);
        } else {
            ViewCompat.setLayoutDirection(findViewById(R.id.drawer_layout), ViewCompat.LAYOUT_DIRECTION_LTR);
        }
        titles = new String[]{getString(R.string.home), getString(R.string.profile), getString(R.string.chat_with_school), getString(R.string.notification), getString(R.string.report_absent), getString(R.string.change_pass), getString(R.string.change_language), getString(R.string.setting), getString(R.string.log_out)};
        list_img = new int[]{R.drawable.home, R.drawable.profile, R.drawable.message, R.drawable.notification, R.drawable.report_absent, R.drawable.change_pass, R.drawable.lang, R.drawable.setting, R.drawable.signout};
        setUpNavigationDrawerHome();
        init();
        navigationDrawerAdapter = new NavigationDrawerAdapter(apContext, titles, list_img);
        navigationDrawerAdapter.notifyDataSetChanged();

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerPanel)) {
            mDrawerLayout.closeDrawer(mDrawerPanel);
        }

        registerReceiver(logoutReceiver, new IntentFilter("logout_receiver"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisibleMainActivity = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to Service
        Intent intent = new Intent(this, UpdateBlinkService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (bound) {
            myService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            bound = false;
        }
    }


    private class SettingTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String sound_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_notisound);
            String sound_chat = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_msgsound);
            String checkedin_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_CheckedInNoti);
            String checkedout_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_CheckedOutNoti);

            String speed_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_SpeedOnOff);
            String wrongroute_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_WrongRoute);
            String speed = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_Speed);

            String checkedin_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_CheckedInNotiSMS);
            String checkedout_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_CheckedOutNotiSMS);
            String speed_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_SpeedOnOffSMS);
            String wrongroute_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_WrongRouteSMS);
            String speed_sms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_SpeedSMS);


            String driver_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_DriverNoti);
            String driver_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_DriverNotiSMS);
            String evening_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_EveningNoti);
            String evening_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_EveningNotiSms);
            String morning_noti = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_MorningNoti);
            String morning_notisms = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.Setting_MorningNotiSMS);


            String user_id = Utility.getSharedPreferences(MainActivityNew.this, ConstantKeys.USER_ID);

            String URL = ConstantKeys.SERVER_URL + "save_parent_setting?lang=" + language + "&noti_on=" + sound_noti + "&chat_on=" + sound_chat + "&checked_in_on=" + checkedin_noti
                    + "&checked_out_on=" + checkedout_noti + "&speed_on=" + speed_noti + "&max_speed=" + speed + "&wrong_route_on=" + wrongroute_noti + "&user_id=" + user_id
                    + "&sms_checked_in_on=" + checkedin_notisms + "&sms_checked_out_on=" + checkedout_notisms + "&sms_speed_on=" + speed_notisms + "&sms_max_speed="
                    + speed_sms + "&sms_wrong_route_on=" + wrongroute_notisms +
                    "&instant_message=" + driver_noti + "&morning_before=" + morning_noti + "&evening_before=" + evening_noti + "&sms_instant_message=" + driver_notisms + "&sms_morning_before=" + morning_notisms + "&sms_evening_before=" + evening_notisms;
            ;
            NetworkHelperGet putRequest = new NetworkHelperGet(URL);
            try {
                return putRequest.sendGet();
            } catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * Callbacks for service binding, passed to bindService()
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            UpdateBlinkService.LocalBinder binder = (UpdateBlinkService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(MainActivityNew.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    public static void UpdateNavigation() {
        navigationDrawerAdapter = new NavigationDrawerAdapter(apContext, titles, list_img);
        navigationDrawerAdapter.notifyDataSetChanged();
    }

    @Override
    public void UpdateBlink() {
        init();
    }


    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}