package net.m3aak.parentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONObject;

/**
 * Created by BD-2 on 8/13/2015.
 */
public class ForGotPassActivity extends AppCompatActivity implements View.OnClickListener {
    private Context appContext;
    String email, mobile_number, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass_layout);
        appContext = this;

        if (Utility.getSharedPreferences(ForGotPassActivity.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.forgot_pass_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        init();
    }

    private void init() {
      try {
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
          ((TextView) findViewById(R.id.title)).setText(getString(R.string.forgotpwd_heading));
          ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
          ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  onBackPressed();
              }
          });

          ((TextView) findViewById(R.id.contact_us_txt)).setOnClickListener(this);
          ((TextView) findViewById(R.id.submit_txt)).setOnClickListener(this);


          ((EditText) findViewById(R.id.uEmail)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
              @Override
              public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                  if (actionId == EditorInfo.IME_ACTION_GO) {
                      attemptForgotPassword();
                  }
                  return false;
              }
          });
      }catch (Exception e) {e.printStackTrace();}
      }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_us_txt:
                startActivity(new Intent(appContext, ContactUsActivity.class));
                finish();
                break;
            case R.id.submit_txt:
                attemptForgotPassword();
                break;
        }
    }

    private void attemptForgotPassword() {
        email = ((EditText) findViewById(R.id.uEmail)).getText().toString();
        if (!Utility.isStringNullOrBlank(email)) {
            // Toast.makeText(this, "You Will receive message soon !", Toast.LENGTH_SHORT).show();
            if (Utility.isEmailAddressValid(email)) {
                if (Utility.isConnectingToInternet(ForGotPassActivity.this)) {
                    new ForgotPasswordTak().execute(((EditText) findViewById(R.id.uEmail)).getText().toString());
                } else {
                    Toast.makeText(ForGotPassActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.fieldempty), Toast.LENGTH_SHORT).show();
        }
    }

    private class ForgotPasswordTak extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(ForGotPassActivity.this);

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
            String URL = ConstantKeys.SERVER_URL + "forgot_password?" + "user_email" + "=" + params[0];
           /* String responce= Utility.findJSONFromUrl(URL);
            Log.e("ForgotPasswordTak",""+responce);
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
            System.out.println(s);
            Log.e("forgot pass responce", "" + s);
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        Toast.makeText(appContext, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                        ((EditText) findViewById(R.id.uEmail)).setText("");
                        ((EditText) findViewById(R.id.uName)).setText("");
                        ((EditText) findViewById(R.id.uNumber)).setText("");
                    } else {
                        Toast.makeText(appContext, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                Log.e("ForgotPasswordTak Exc", "" + e);
            }
            super.onPostExecute(s);
        }
    }
}
