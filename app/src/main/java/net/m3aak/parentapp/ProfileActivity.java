package net.m3aak.parentapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

/**
 * Created by BD-2 on 8/17/2015.
 */
public class ProfileActivity extends AppCompatActivity {
    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        appContext = this;

        if (Utility.getSharedPreferences(ProfileActivity.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.profile_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

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
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.parent_profile));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setData();
    }

    private void setData() {
       /* Picasso.with(appContext)
                .load(ConstantKeys.SERVER_URL + "/profile_pic/father.jpg")
                .into(((CircleImageView) findViewById(R.id.user_pro_pic)));*///To be opened
        ((TextView) findViewById(R.id.user_name)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.USER_NAME).isEmpty()) ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.USER_NAME));
        ((TextView) findViewById(R.id.number_txt)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.MOBILE_NO).isEmpty() ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.MOBILE_NO)));
        ((TextView) findViewById(R.id.email_txt)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.USER_EMAIL)).isEmpty() ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.USER_EMAIL));
        ((TextView) findViewById(R.id.FirstName)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.FIRST_NAME)).isEmpty() ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.FIRST_NAME));
        ((TextView) findViewById(R.id.MiddleName)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.MIDDLE_NAME)).isEmpty() ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.MIDDLE_NAME));
        ((TextView) findViewById(R.id.FamilyName)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.FAMILY_NAME)).isEmpty() ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.FAMILY_NAME));
        ((TextView) findViewById(R.id.ContactNumber)).setText((Utility.getSharedPreferences(appContext, ConstantKeys.CONTACT_NO)).isEmpty() ? "N/A" : Utility.getSharedPreferences(appContext, ConstantKeys.CONTACT_NO));

        // To be opened when webservice uploaded to server
    }
}