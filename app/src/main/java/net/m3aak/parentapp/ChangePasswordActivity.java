package net.m3aak.parentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONObject;

/**
 * Created by Android Developer-1 on 22-08-2016.
 */
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    Context ctxChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pwd);
        ctxChangePwd = this;

        if (Utility.getSharedPreferences(ChangePasswordActivity.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.change_pass_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        init();

        ((TextView) findViewById(R.id.btnSubmit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                String current_pwd = ((EditText) findViewById(R.id.edtTextCurrentPwd)).getText().toString();
                String new_pwd = ((EditText) findViewById(R.id.edtTextNewPwd)).getText().toString();
                String confirm_pwd = ((EditText) findViewById(R.id.edtTextNewConfirmPwd)).getText().toString();
                if (!Utility.isStringNullOrBlank(current_pwd) && !Utility.isStringNullOrBlank(new_pwd) && !Utility.isStringNullOrBlank(confirm_pwd)) {
                    if (Utility.isConnectingToInternet(ctxChangePwd)) {
                        if (new_pwd.equals(confirm_pwd)) {
                            new ChangePasswordTak().execute(Utility.getSharedPreferences(ctxChangePwd, ConstantKeys.USER_NAME), current_pwd, new_pwd);
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.confirm_pwd_different), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.fieldempty), Toast.LENGTH_SHORT).show();
                }
                break;
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
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.change_pass));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class ChangePasswordTak extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(ctxChangePwd);

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
            // http://192.168.1.4:8080/Tracking_bus/webservices/changePassword?user_email=prakash.kumar&user_pass=123456&first_name=admin
            String URL = ConstantKeys.SERVER_URL + "changePassword?" + "user_email" + "=" + params[0] + "&" + "user_pass" + "=" + params[1]
                    + "&" + "first_name" + "=" + params[2];
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
            System.out.println(s);
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(ctxChangePwd, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        Toast.makeText(ctxChangePwd, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ctxChangePwd, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                // Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                Log.e("ForgotPasswordTak Exc", "" + e);
            }
            super.onPostExecute(s);
        }

    }
}
