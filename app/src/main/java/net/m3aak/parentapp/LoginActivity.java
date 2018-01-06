package net.m3aak.parentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by BD-2 on 8/11/2015.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Context appContext;
    private String siteAdminNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String language = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_Language);
        try {
            if (language.equals("1")) {
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

            setContentView(R.layout.activity_login_new);

            if (language.equals("1")) {
                ViewCompat.setLayoutDirection(findViewById(R.id.login_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
            } else {
                ViewCompat.setLayoutDirection(findViewById(R.id.login_root_view), ViewCompat.LAYOUT_DIRECTION_LTR);
            }

            //Get Site admin contact number
            new GetContactTask().execute();

            appContext = this;
            init();
        }catch (Exception e) {e.printStackTrace();}
    }

    private void init() {
        ((TextView) findViewById(R.id.login_txt)).setOnClickListener(this);
        ((TextView) findViewById(R.id.forgot_pass_txt)).setOnClickListener(this);
        findViewById(R.id.relLayContactUs).setOnClickListener(this);

        EditText uPass = ((EditText) findViewById(R.id.u_pass));
        EditText uEmail = ((EditText) findViewById(R.id.u_email));


        if (Utility.getSharedPreferences(appContext, ConstantKeys.IS_REMEMBER).equals("Yes")) {
            ((CheckBox) findViewById(R.id.remember_chk)).setChecked(true);
            uEmail.setText(Utility.getSharedPreferences(appContext, ConstantKeys.USER_NAME));
            uPass.setText(Utility.getSharedPreferences(appContext, ConstantKeys.USER_PASS));
        }

        // Registered device for GCM
        if (Utility.isConnectingToInternet(appContext) == true) {

        } else {
            Toast.makeText(appContext,
                    getString(R.string.no_internet), Toast.LENGTH_LONG)
                    .show();
        }

        ((EditText) findViewById(R.id.u_pass)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_txt:
                attemptLogin();
                break;
            case R.id.forgot_pass_txt:
                startActivity(new Intent(appContext, ForGotPassActivity.class));
                break;
            case R.id.relLayContactUs:
                if (siteAdminNumber != null && !siteAdminNumber.isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+966" + siteAdminNumber));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_number), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void attemptLogin() {
        String res = CheckValidation(((EditText) findViewById(R.id.u_email)).getText().toString(), ((EditText) findViewById(R.id.u_pass)).getText().toString());
        if (res.equals("suc")) {
            if (Utility.isConnectingToInternet(appContext) == true) {
                String fcmToken = FirebaseInstanceId.getInstance().getToken();
                if (fcmToken != null) {
                    new Login().execute(((EditText) findViewById(R.id.u_email)).getText().toString(), ((EditText) findViewById(R.id.u_pass)).getText().toString(), fcmToken);
                } else {
                    Toast.makeText(appContext, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(appContext, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
        }
    }

    public String CheckValidation(String email, String pass) {
        if (email.equals("") || pass.equals("")) {
            return getString(R.string.fieldempty);
        } else {
            return "suc";
        }
    }

/*   ------------------------------>CODE FOR LOGIN<---------------------------------   */

    public class Login extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.login));
            dialog.setMessage(getString(R.string.wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String deviceId = Settings.Secure.getString(LoginActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            String URL = ConstantKeys.SERVER_URL + "weblogin?" + ConstantKeys.USER_EMAIL + "=" + params[0] + "&" + ConstantKeys.USER_PASS + "=" + params[1]
                    + "&" + "device_token" + "=" + params[2] + "&device_id=" + deviceId;
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
            // System.out.println("Login Response" + s);
/* {"result":"success","school_logo":"Choithram School1096899965.png","max_speed":100,"chat_on":1,"wrong_route_on":0,"middle_name":"Kumar11",
"user_email":"pk@mailinator.com","lang":0,"school_id":3,"responseMessage":"Login successfull","contact_number":"123456779",
"user_name":"prakash.kumar","first_name":"Prakash1","noti_on":0,"mobile_number":"9981472471","checked_out_on":0,"speed_on":100,
"role":"Parent","family_name":"Singhal","user_id":12,"school_admin":"9","checked_in_on":0}*/
            try {
                Log.e("Login Response---", "" + s.toString());
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, appContext.getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {

                        if (((CheckBox) findViewById(R.id.remember_chk)).isChecked()) {
                            Utility.setSharedPreference(appContext, ConstantKeys.IS_REMEMBER, "Yes");
                            String pass = ((EditText) findViewById(R.id.u_pass)).getText().toString();
                            Utility.setSharedPreference(appContext, ConstantKeys.USER_PASS, pass);
                        } else {
                            Utility.setSharedPreference(appContext, ConstantKeys.IS_REMEMBER, "No");
                        }

                        Utility.setSharedPreference(appContext, ConstantKeys.USER_EMAIL, networkResponse.getString(ConstantKeys.USER_EMAIL));
                        Utility.setSharedPreference(appContext, ConstantKeys.CONTACT_NO, networkResponse.getString(ConstantKeys.CONTACT_NO));
                        Utility.setSharedPreference(appContext, ConstantKeys.MOBILE_NO, networkResponse.getString(ConstantKeys.MOBILE_NO));
                        Utility.setSharedPreference(appContext, ConstantKeys.SCHOOL_ID, networkResponse.getString(ConstantKeys.SCHOOL_ID));
                        Utility.setSharedPreference(appContext, ConstantKeys.ROLE, networkResponse.getString(ConstantKeys.ROLE));
                        Utility.setSharedPreference(appContext, ConstantKeys.USER_ID, networkResponse.getString(ConstantKeys.USER_ID));
                        Utility.setSharedPreference(appContext, ConstantKeys.USER_NAME, networkResponse.getString(ConstantKeys.USER_NAME));
                        Utility.setSharedPreference(appContext, ConstantKeys.Reciever_ID, networkResponse.getString(ConstantKeys.Reciever_ID));
                        Utility.setSharedPreference(appContext, ConstantKeys.FIRST_NAME, networkResponse.getString(ConstantKeys.FIRST_NAME));
                        Utility.setSharedPreference(appContext, ConstantKeys.MIDDLE_NAME, networkResponse.getString(ConstantKeys.MIDDLE_NAME));
                        Utility.setSharedPreference(appContext, ConstantKeys.FAMILY_NAME, networkResponse.getString(ConstantKeys.FAMILY_NAME));
                        Utility.setSharedPreference(appContext, ConstantKeys.school_name, networkResponse.getString(ConstantKeys.school_name));
                        // Utility.setSharedPreference(appContext, ConstantKeys.school_name, networkResponse.getString(ConstantKeys.school_name));

                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_notisound, networkResponse.getString("noti_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_msgsound, networkResponse.getString("chat_on"));

                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedInNoti, networkResponse.getString("checked_in_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedOutNoti, networkResponse.getString("checked_out_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_SpeedOnOff, networkResponse.getString("speed_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_WrongRoute, networkResponse.getString("wrong_route_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_Speed, networkResponse.getString("max_speed"));

                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedInNotiSMS, networkResponse.optString("sms_checked_in_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedOutNotiSMS, networkResponse.optString("sms_checked_out_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_SpeedOnOffSMS, networkResponse.optString("sms_speed_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_WrongRouteSMS, networkResponse.optString("sms_wrong_route_on"));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_SpeedSMS, networkResponse.optString("sms_max_speed"));

                        /*instant_message, morning_before,,evening_before, sms_instant_message,sms_morning_befor,sms_evening_before*/
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_DriverNoti, networkResponse.optString("instant_message", ""));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_DriverNotiSMS, networkResponse.optString("sms_instant_message", ""));

                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_MorningNoti, networkResponse.optString("morning_before", ""));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_MorningNotiSMS, networkResponse.optString("sms_morning_before", ""));

                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_EveningNoti, networkResponse.optString("evening_before", ""));
                        Utility.setSharedPreference(appContext, ConstantKeys.Setting_EveningNotiSms, networkResponse.optString("sms_evening_before", ""));
                        Utility.setSharedPreference(appContext, ConstantKeys.SCHOOL_ADMIN_NAME, networkResponse.optString("school_admin_name", ""));

                        String school_log = networkResponse.getString(ConstantKeys.SCHOOL_LOGO);
                        Utility.setSharedPreference(appContext, ConstantKeys.SCHOOL_LOGO, school_log.replaceAll(" ", "%20"));
                        if (!Utility.getSharedPreferencesBoolean(LoginActivity.this, ConstantKeys.IS_FIRST_TIME)) {
                            Utility.setSharedPreference(appContext, ConstantKeys.Setting_Language, networkResponse.getString("lang"));
                        } else {
                            Utility.setSharedPreferenceBoolean(LoginActivity.this, ConstantKeys.IS_FIRST_TIME, false);
                            new SettingTask().execute();
                        }
                        switchActivity();
                    } else {
                        Toast.makeText(appContext, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                // Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                Log.e("Login Exception", "" + e);
            }
            super.onPostExecute(s);
        }
    }

    private class SettingTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String sound_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_notisound);
            String sound_chat = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_msgsound);
            String checkedin_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_CheckedInNoti);
            String checkedout_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_CheckedOutNoti);

            String speed_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_SpeedOnOff);
            String wrongroute_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_WrongRoute);
            String speed = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_Speed);

            String checkedin_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_CheckedInNotiSMS);
            String checkedout_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_CheckedOutNotiSMS);
            String speed_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_SpeedOnOffSMS);
            String wrongroute_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_WrongRouteSMS);
            String speed_sms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_SpeedSMS);


            String driver_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_DriverNoti);
            String driver_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_DriverNotiSMS);
            String evening_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_EveningNoti);
            String evening_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_EveningNotiSms);
            String morning_noti = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_MorningNoti);
            String morning_notisms = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_MorningNotiSMS);
            String language = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.Setting_Language);

            String user_id = Utility.getSharedPreferences(LoginActivity.this, ConstantKeys.USER_ID);

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

    private void switchActivity() {
        Utility.setSharedPreference(LoginActivity.this, ConstantKeys.ALREADY_LOGIN, "Yes");
        startActivity(new Intent(appContext, MainActivityNew.class));
        finish();
    }

    //Task for Get contact number of site admin
    private class GetContactTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                NetworkHelperGet networkHelperGet = new NetworkHelperGet(ConstantKeys.SERVER_URL + "getSiteNumber");
                return networkHelperGet.sendGet();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("result").equals("success")) {
                        siteAdminNumber = jsonObject.optString("mobile_number", "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}