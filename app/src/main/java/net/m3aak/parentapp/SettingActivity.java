package net.m3aak.parentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by BD-2 on 8/17/2015.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Context appContext;
    private ImageView imgViewEnglish, imgViewArabic, img2Onn, img2Off, imgViewMessageSoungOff, imgViewMessageSoungOnn,
            imgViewCheckedInOn, imgViewCheckedInOff, imgViewCheckedOutOn, imgViewCheckedOutOff, imgSpeedOn, imgSpeedOff,
            imgWrongRouteOn, imgWrongRouteOff, imgDriverOn, imgDriverOff, imgMorningOn, imgMorningOff, imgEveningOn, imgEveningOff, imgViewCheckedInOnsms, imgViewCheckedInOffsms, imgViewCheckedOutOnsms, imgViewCheckedOutOffsms,
            imgSpeedOnsms, imgSpeedOffsms, imgWrongRouteOnsms, imgWrongRouteOffsms, imgDriverOnsms, imgDriverOffsms, imgMorningOnsms, imgMorningOffsms, imgEveningOnsms, imgEveningOffsms;
    private SeekBar sb, sb_sms;
    private TextView txtSpeed, txtSpeedsms;
    private String language;
    private String sound_noti;
    private String sound_chat;
    private String checkedin_noti;
    private String checkedout_noti;
    private String speed_noti;
    private String wrongroute_noti;
    private String user_id;
    private String speed = "";
    private String speed_sms;
    private String checkedin_notisms;
    private String checkedout_notisms;
    private String speed_notisms;
    private String wrongroute_notisms, driver_noti, driver_notisms, morning_noti, morning_notisms, evening_noti, evening_notisms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        appContext = this;

        if (Utility.getSharedPreferences(SettingActivity.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.setting_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        } else {
            ViewCompat.setLayoutDirection(findViewById(R.id.setting_root_view), ViewCompat.LAYOUT_DIRECTION_LOCALE);
        }

        user_id = Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID);
        SpeedProgressSeekBar();
        SpeedProgressSeekBarSMS();
        init();
    }

    private void SpeedProgressSeekBar() {
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        sb = (SeekBar) findViewById(R.id.seekBarSpeed);
        sb.setMax(6);
        sb.setProgress(0); // Set it to zero so it will start at the left-most edge
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        txtSpeed.setText("80" + " " + getString(R.string.km));
                        speed = "80";
                    } else {
                        int prgs = (progress * 10) + 80;
                        txtSpeed.setText("" + prgs + " " + getString(R.string.km));
                        speed = "" + prgs;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    private void SpeedProgressSeekBarSMS() {
        txtSpeedsms = (TextView) findViewById(R.id.txtSpeedsms);
        sb_sms = (SeekBar) findViewById(R.id.seekBarSpeedsms);
        sb_sms.setMax(6);
        sb_sms.setProgress(0); // Set it to zero so it will start at the left-most edge
        sb_sms.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        txtSpeedsms.setText("80" + " " + getString(R.string.km));
                        speed_sms = "80";
                    } else {
                        int prgs = (progress * 10) + 80;
                        txtSpeedsms.setText("" + prgs + " " + getString(R.string.km));
                        speed_sms = "" + prgs;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
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
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.settings));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout relLaylEnglsh = (RelativeLayout) findViewById(R.id.relLaylEnglsh);
        relLaylEnglsh.setOnClickListener(this);
        RelativeLayout relLayArabic = (RelativeLayout) findViewById(R.id.relLayArabic);
        relLayArabic.setOnClickListener(this);

        RelativeLayout reLLayNotiSoundOnn = (RelativeLayout) findViewById(R.id.reLLayNotiSoundOnn);
        reLLayNotiSoundOnn.setOnClickListener(this);

        RelativeLayout reLLayNotiSoundOff = (RelativeLayout) findViewById(R.id.reLLayNotiSoundOff);
        reLLayNotiSoundOff.setOnClickListener(this);

        RelativeLayout reLLayNotiMsgOnn = (RelativeLayout) findViewById(R.id.reLLayNotiMsgOnn);
        reLLayNotiMsgOnn.setOnClickListener(this);

        RelativeLayout reLLayNotiMsgOff = (RelativeLayout) findViewById(R.id.reLLayNotiMsgOff);
        reLLayNotiMsgOff.setOnClickListener(this);

        RelativeLayout relLayCheckedInOn = (RelativeLayout) findViewById(R.id.relLayCheckedInOn);
        relLayCheckedInOn.setOnClickListener(this);

        RelativeLayout relLayCheckedInOff = (RelativeLayout) findViewById(R.id.relLayCheckedInOff);
        relLayCheckedInOff.setOnClickListener(this);

        RelativeLayout relLayCheckedOutOn = (RelativeLayout) findViewById(R.id.relLayCheckedOutOn);
        relLayCheckedOutOn.setOnClickListener(this);

        RelativeLayout relLayCheckedOutOff = (RelativeLayout) findViewById(R.id.relLayCheckedOutOff);
        relLayCheckedOutOff.setOnClickListener(this);

        RelativeLayout relLayWrongRouteOn = (RelativeLayout) findViewById(R.id.relLayWrongRouteOn);
        relLayWrongRouteOn.setOnClickListener(this);

        RelativeLayout relLayWrongRouteOff = (RelativeLayout) findViewById(R.id.relLayWrongRouteOff);
        relLayWrongRouteOff.setOnClickListener(this);

        RelativeLayout relLaySpeedOn = (RelativeLayout) findViewById(R.id.relLaySpeedOn);
        relLaySpeedOn.setOnClickListener(this);

        RelativeLayout relLaySpeedOff = (RelativeLayout) findViewById(R.id.relLaySpeedOff);
        relLaySpeedOff.setOnClickListener(this);

        //new code
        RelativeLayout relLayDriverOn = (RelativeLayout) findViewById(R.id.relLayinstantMsgOn);
        RelativeLayout relLayDriverOff = (RelativeLayout) findViewById(R.id.relLayInstantMsgOff);
        RelativeLayout relLayMorningOn = (RelativeLayout) findViewById(R.id.relLayMorningOn);
        RelativeLayout relLayMorningOff = (RelativeLayout) findViewById(R.id.relLayMorningOff);
        RelativeLayout relLayEveningOn = (RelativeLayout) findViewById(R.id.relLayEveningOn);
        RelativeLayout relLayEveningOff = (RelativeLayout) findViewById(R.id.relLayEveningOff);

        RelativeLayout relLayDriverOnsms = (RelativeLayout) findViewById(R.id.relLayinstantMsgOnsms);
        RelativeLayout relLayDriverOffsms = (RelativeLayout) findViewById(R.id.relLayInstantMsgOffsms);
        RelativeLayout relLayMorningOnsms = (RelativeLayout) findViewById(R.id.relLayMorningOnsms);
        RelativeLayout relLayMorningOffsms = (RelativeLayout) findViewById(R.id.relLayMorningOffsms);
        RelativeLayout relLayEveningOnsms = (RelativeLayout) findViewById(R.id.relLayEveningOnsms);
        RelativeLayout relLayEveningOffsms = (RelativeLayout) findViewById(R.id.relLayEveningOffsms);
        relLayDriverOn.setOnClickListener(this);
        relLayDriverOff.setOnClickListener(this);
        relLayMorningOn.setOnClickListener(this);
        relLayMorningOff.setOnClickListener(this);
        relLayEveningOn.setOnClickListener(this);
        relLayEveningOff.setOnClickListener(this);
        relLayDriverOnsms.setOnClickListener(this);
        relLayDriverOffsms.setOnClickListener(this);
        relLayMorningOnsms.setOnClickListener(this);
        relLayMorningOffsms.setOnClickListener(this);
        relLayEveningOnsms.setOnClickListener(this);
        relLayEveningOffsms.setOnClickListener(this);

        RelativeLayout relLayCheckedInOnsms = (RelativeLayout) findViewById(R.id.relLayCheckedInOnsms);
        relLayCheckedInOnsms.setOnClickListener(this);

        RelativeLayout relLayCheckedInOffsms = (RelativeLayout) findViewById(R.id.relLayCheckedInOffsms);
        relLayCheckedInOffsms.setOnClickListener(this);

        RelativeLayout relLayCheckedOutOnsms = (RelativeLayout) findViewById(R.id.relLayCheckedOutOnsms);
        relLayCheckedOutOnsms.setOnClickListener(this);

        RelativeLayout relLayCheckedOutOffsms = (RelativeLayout) findViewById(R.id.relLayCheckedOutOffsms);
        relLayCheckedOutOffsms.setOnClickListener(this);

        RelativeLayout relLayWrongRouteOnsms = (RelativeLayout) findViewById(R.id.relLayWrongRouteOnsms);
        relLayWrongRouteOnsms.setOnClickListener(this);

        RelativeLayout relLayWrongRouteOffsms = (RelativeLayout) findViewById(R.id.relLayWrongRouteOffsms);
        relLayWrongRouteOffsms.setOnClickListener(this);

        RelativeLayout relLaySpeedOnsms = (RelativeLayout) findViewById(R.id.relLaySpeedOnsms);
        relLaySpeedOnsms.setOnClickListener(this);

        RelativeLayout relLaySpeedOffsms = (RelativeLayout) findViewById(R.id.relLaySpeedOffsms);
        relLaySpeedOffsms.setOnClickListener(this);


        imgViewEnglish = (ImageView) findViewById(R.id.imgViewEnglish);
        imgViewArabic = (ImageView) findViewById(R.id.imgViewArabic);
        img2Onn = (ImageView) findViewById(R.id.img2Onn);
        img2Off = (ImageView) findViewById(R.id.img2Off);
        imgViewMessageSoungOff = (ImageView) findViewById(R.id.imgViewMessageSoungOff);
        imgViewMessageSoungOnn = (ImageView) findViewById(R.id.imgViewMessageSoungOnn);

        imgViewCheckedInOn = (ImageView) findViewById(R.id.imgViewCheckedInOn);
        imgViewCheckedInOff = (ImageView) findViewById(R.id.imgViewCheckedInOff);

        imgViewCheckedOutOn = (ImageView) findViewById(R.id.imgViewCheckedOutOn);
        imgViewCheckedOutOff = (ImageView) findViewById(R.id.imgViewCheckedOutOff);

        imgSpeedOn = (ImageView) findViewById(R.id.imgSpeedOn);
        imgSpeedOff = (ImageView) findViewById(R.id.imgSpeedOff);

        imgWrongRouteOn = (ImageView) findViewById(R.id.imgWrongRouteOn);
        imgWrongRouteOff = (ImageView) findViewById(R.id.imgWrongRouteOff);


        imgViewCheckedInOnsms = (ImageView) findViewById(R.id.imgViewCheckedInOnsms);
        imgViewCheckedInOffsms = (ImageView) findViewById(R.id.imgViewCheckedInOffsms);

        imgViewCheckedOutOnsms = (ImageView) findViewById(R.id.imgViewCheckedOutOnsms);
        imgViewCheckedOutOffsms = (ImageView) findViewById(R.id.imgViewCheckedOutOffsms);

        imgSpeedOnsms = (ImageView) findViewById(R.id.imgSpeedOnsms);
        imgSpeedOffsms = (ImageView) findViewById(R.id.imgSpeedOffsms);

        imgWrongRouteOnsms = (ImageView) findViewById(R.id.imgWrongRouteOnsms);
        imgWrongRouteOffsms = (ImageView) findViewById(R.id.imgWrongRouteOffsms);

        imgMorningOn = (ImageView) findViewById(R.id.imgViewMorningOn);
        imgMorningOff = (ImageView) findViewById(R.id.imgViewMorningOff);
        imgEveningOn = (ImageView) findViewById(R.id.imgViewEveningOn);
        imgEveningOff = (ImageView) findViewById(R.id.imgViewEveningOff);
        imgDriverOn = (ImageView) findViewById(R.id.imgViewInstantMsgOn);
        imgDriverOff = (ImageView) findViewById(R.id.imgViewInstantMsgOff);

        imgMorningOnsms = (ImageView) findViewById(R.id.imgViewMorningOnsms);
        imgMorningOffsms = (ImageView) findViewById(R.id.imgViewMorningOffsms);
        imgEveningOnsms = (ImageView) findViewById(R.id.imgViewEveningOnsms);
        imgEveningOffsms = (ImageView) findViewById(R.id.imgViewEveningOffsms);
        imgDriverOnsms = (ImageView) findViewById(R.id.imgViewInstantMsgOnsms);
        imgDriverOffsms = (ImageView) findViewById(R.id.imgViewInstantMsgOffsms);

        ((TextView) findViewById(R.id.btnSaveSetting)).setOnClickListener(this);

        SetSavedSettings();
    }

    private void SetSavedSettings() {

        // wrongroute_noti,user_id;
        language = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_Language);
        sound_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_notisound);
        sound_chat = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_msgsound);
        checkedin_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_CheckedInNoti);
        checkedout_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_CheckedOutNoti);
        speed_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_SpeedOnOff);
        wrongroute_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_WrongRoute);
        speed = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_Speed);

        checkedin_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_CheckedInNotiSMS);
        checkedout_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_CheckedOutNotiSMS);
        speed_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_SpeedOnOffSMS);
        wrongroute_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_WrongRouteSMS);
        speed_sms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_SpeedSMS);


        driver_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_DriverNoti);
        driver_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_DriverNotiSMS);

        morning_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_MorningNoti);
        morning_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_MorningNotiSMS);

        evening_noti = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_EveningNoti);
        evening_notisms = Utility.getSharedPreferences(appContext, ConstantKeys.Setting_EveningNotiSms);


        //For Language
        switch (language) {
            case "0":
                imgViewEnglish.setImageResource(R.drawable.on);
                imgViewArabic.setImageResource(R.drawable.off);
                break;
            case "1":
                imgViewEnglish.setImageResource(R.drawable.off);
                imgViewArabic.setImageResource(R.drawable.on);
                break;
            default:
                imgViewEnglish.setImageResource(R.drawable.on);
                imgViewArabic.setImageResource(R.drawable.off);
                language = "0";
                break;
        }

        //For NotiSound
        switch (sound_noti) {
            case "0":
                img2Onn.setImageResource(R.drawable.on);
                img2Off.setImageResource(R.drawable.off);
                break;
            case "1":
                img2Onn.setImageResource(R.drawable.off);
                img2Off.setImageResource(R.drawable.on);
                break;
            default:
                img2Onn.setImageResource(R.drawable.on);
                img2Off.setImageResource(R.drawable.off);
                sound_noti = "0";
                break;
        }

        //For MsgSound
        switch (sound_chat) {
            case "0":
                imgViewMessageSoungOnn.setImageResource(R.drawable.on);
                imgViewMessageSoungOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgViewMessageSoungOnn.setImageResource(R.drawable.off);
                imgViewMessageSoungOff.setImageResource(R.drawable.on);
                break;
            default:
                imgViewMessageSoungOnn.setImageResource(R.drawable.on);
                imgViewMessageSoungOff.setImageResource(R.drawable.off);
                sound_chat = "0";
                break;
        }

        //For Checkedin Notification
        switch (checkedin_noti) {
            case "0":
                imgViewCheckedInOn.setImageResource(R.drawable.on);
                imgViewCheckedInOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgViewCheckedInOn.setImageResource(R.drawable.off);
                imgViewCheckedInOff.setImageResource(R.drawable.on);
                break;
            default:
                imgViewCheckedInOn.setImageResource(R.drawable.on);
                imgViewCheckedInOff.setImageResource(R.drawable.off);
                checkedin_noti = "0";
                break;
        }

        //For Checkedin SMS
        switch (checkedin_notisms) {
            case "0":
                imgViewCheckedInOnsms.setImageResource(R.drawable.on);
                imgViewCheckedInOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgViewCheckedInOnsms.setImageResource(R.drawable.off);
                imgViewCheckedInOffsms.setImageResource(R.drawable.on);
                break;
            default:
                imgViewCheckedInOnsms.setImageResource(R.drawable.off);
                imgViewCheckedInOffsms.setImageResource(R.drawable.on);
                checkedin_notisms = "1";
                break;
        }

        //For CheckedOut Notification
        switch (checkedout_noti) {
            case "0":
                imgViewCheckedOutOn.setImageResource(R.drawable.on);
                imgViewCheckedOutOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgViewCheckedOutOn.setImageResource(R.drawable.off);
                imgViewCheckedOutOff.setImageResource(R.drawable.on);
                break;
            default:
                imgViewCheckedOutOn.setImageResource(R.drawable.on);
                imgViewCheckedOutOff.setImageResource(R.drawable.off);
                checkedout_notisms = "0";
                break;
        }

        //For CheckedOut SMS
        switch (checkedout_notisms) {
            case "0":
                imgViewCheckedOutOnsms.setImageResource(R.drawable.on);
                imgViewCheckedOutOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgViewCheckedOutOnsms.setImageResource(R.drawable.off);
                imgViewCheckedOutOffsms.setImageResource(R.drawable.on);
                break;
            default:
                imgViewCheckedOutOnsms.setImageResource(R.drawable.off);
                imgViewCheckedOutOffsms.setImageResource(R.drawable.on);
                checkedout_noti = "1";
                break;
        }

        //For Over Speed Notification
        switch (speed_noti) {
            case "0":
                imgSpeedOn.setImageResource(R.drawable.on);
                imgSpeedOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgSpeedOn.setImageResource(R.drawable.off);
                imgSpeedOff.setImageResource(R.drawable.on);
                ((LinearLayout) findViewById(R.id.linLaySpeedProgress)).setVisibility(View.GONE);
                break;
            default:
                imgSpeedOn.setImageResource(R.drawable.on);
                imgSpeedOff.setImageResource(R.drawable.off);
                speed_noti = "1";
                ((LinearLayout) findViewById(R.id.linLaySpeedProgress)).setVisibility(View.GONE);
                break;
        }

        //For Over Speed SMS
        switch (speed_notisms) {
            case "0":
                imgSpeedOnsms.setImageResource(R.drawable.on);
                imgSpeedOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgSpeedOnsms.setImageResource(R.drawable.off);
                imgSpeedOffsms.setImageResource(R.drawable.on);
                ((LinearLayout) findViewById(R.id.linLaySpeedProgresssms)).setVisibility(View.GONE);
                break;
            default:
                imgSpeedOnsms.setImageResource(R.drawable.off);
                imgSpeedOffsms.setImageResource(R.drawable.on);
                speed_notisms = "1";
                ((LinearLayout) findViewById(R.id.linLaySpeedProgresssms)).setVisibility(View.GONE);
                break;
        }

        //For Over Wrong Route
        switch (wrongroute_noti) {
            case "0":
                imgWrongRouteOn.setImageResource(R.drawable.on);
                imgWrongRouteOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgWrongRouteOn.setImageResource(R.drawable.off);
                imgWrongRouteOff.setImageResource(R.drawable.on);
                break;
            default:
                imgWrongRouteOn.setImageResource(R.drawable.off);
                imgWrongRouteOff.setImageResource(R.drawable.on);
                wrongroute_noti = "1";
                break;
        }


        //For Over Wrong Route
        switch (wrongroute_notisms) {
            case "0":
                imgWrongRouteOnsms.setImageResource(R.drawable.on);
                imgWrongRouteOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgWrongRouteOnsms.setImageResource(R.drawable.off);
                imgWrongRouteOffsms.setImageResource(R.drawable.on);
                break;
            default:
                imgWrongRouteOnsms.setImageResource(R.drawable.off);
                imgWrongRouteOffsms.setImageResource(R.drawable.on);
                wrongroute_notisms = "1";
                break;
        }


        ///set setting defaults
        switch (driver_noti) {
            case "0":
                imgDriverOn.setImageResource(R.drawable.on);
                imgDriverOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgDriverOn.setImageResource(R.drawable.off);
                imgDriverOff.setImageResource(R.drawable.on);
                break;
            default:
                imgDriverOn.setImageResource(R.drawable.on);
                imgDriverOff.setImageResource(R.drawable.off);
                driver_noti = "0";
                break;
        }

        switch (driver_notisms) {
            case "0":
                imgDriverOnsms.setImageResource(R.drawable.on);
                imgDriverOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgDriverOnsms.setImageResource(R.drawable.off);
                imgDriverOffsms.setImageResource(R.drawable.on);
                break;
            default:
                imgDriverOnsms.setImageResource(R.drawable.off);
                imgDriverOffsms.setImageResource(R.drawable.on);
                driver_notisms = "1";
                break;
        }


        switch (morning_noti) {
            case "0":
                imgMorningOn.setImageResource(R.drawable.on);
                imgMorningOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgMorningOn.setImageResource(R.drawable.off);
                imgMorningOff.setImageResource(R.drawable.on);
                break;
            default:
                imgMorningOn.setImageResource(R.drawable.on);
                imgMorningOff.setImageResource(R.drawable.off);
                morning_noti = "0";
                break;
        }

        switch (morning_notisms) {
            case "0":
                imgMorningOnsms.setImageResource(R.drawable.on);
                imgMorningOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgMorningOnsms.setImageResource(R.drawable.off);
                imgMorningOffsms.setImageResource(R.drawable.on);
                break;
            default:
                imgMorningOnsms.setImageResource(R.drawable.off);
                imgMorningOffsms.setImageResource(R.drawable.on);
                morning_notisms = "1";
                break;
        }


        switch (evening_noti) {
            case "0":
                imgEveningOn.setImageResource(R.drawable.on);
                imgEveningOff.setImageResource(R.drawable.off);
                break;
            case "1":
                imgEveningOn.setImageResource(R.drawable.off);
                imgEveningOff.setImageResource(R.drawable.on);
                break;
            default:
                imgEveningOn.setImageResource(R.drawable.on);
                imgEveningOff.setImageResource(R.drawable.off);
                evening_noti = "0";
                break;
        }


        switch (evening_notisms) {
            case "0":
                imgEveningOnsms.setImageResource(R.drawable.on);
                imgEveningOffsms.setImageResource(R.drawable.off);
                break;
            case "1":
                imgEveningOnsms.setImageResource(R.drawable.off);
                imgEveningOffsms.setImageResource(R.drawable.on);
                break;
            default:
                imgEveningOnsms.setImageResource(R.drawable.off);
                imgEveningOffsms.setImageResource(R.drawable.on);
                evening_notisms = "1";
                break;
        }

        if (!speed.isEmpty() && Integer.parseInt(speed) > 80) {
            sb.setProgress((Integer.parseInt(speed) - 80) / 10);
        } else {
            speed = "" + 100;
            sb.setProgress((Integer.parseInt(speed) - 80) / 10);
        }
        txtSpeed.setText(speed + " " + getString(R.string.km));

        if (!speed_sms.isEmpty() && Integer.parseInt(speed_sms) > 80) {
            sb_sms.setProgress((Integer.parseInt(speed_sms) - 80) / 10);
        } else {
            speed_sms = "" + 100;
            sb_sms.setProgress((Integer.parseInt(speed_sms) - 80) / 10);
        }
        txtSpeedsms.setText(speed_sms + " " + getString(R.string.km));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.relLaylEnglsh:
                imgViewEnglish.setImageResource(R.drawable.on);
                imgViewArabic.setImageResource(R.drawable.off);
                language = "0";
                break;
            case R.id.relLayArabic:
                imgViewEnglish.setImageResource(R.drawable.off);
                imgViewArabic.setImageResource(R.drawable.on);
                language = "1";
                break;
            case R.id.reLLayNotiSoundOnn://For Notification sound keep onn
                img2Onn.setImageResource(R.drawable.on);
                img2Off.setImageResource(R.drawable.off);
                sound_noti = "0";
                break;
            case R.id.reLLayNotiSoundOff://For Notification sound keep off
                img2Onn.setImageResource(R.drawable.off);
                img2Off.setImageResource(R.drawable.on);
                sound_noti = "1";
                break;
            case R.id.reLLayNotiMsgOnn://For  Message Soung  keep on
                imgViewMessageSoungOnn.setImageResource(R.drawable.on);
                imgViewMessageSoungOff.setImageResource(R.drawable.off);
                sound_chat = "0";
                break;
            case R.id.reLLayNotiMsgOff://For Message sound keep off
                imgViewMessageSoungOnn.setImageResource(R.drawable.off);
                imgViewMessageSoungOff.setImageResource(R.drawable.on);
                sound_chat = "1";
                break;
            case R.id.relLayCheckedInOn://For CheckIn notification On
                imgViewCheckedInOn.setImageResource(R.drawable.on);
                imgViewCheckedInOff.setImageResource(R.drawable.off);
                checkedin_noti = "0";
                break;
            case R.id.relLayCheckedInOff://For CheckIn notification Off
                imgViewCheckedInOn.setImageResource(R.drawable.off);
                imgViewCheckedInOff.setImageResource(R.drawable.on);
                checkedin_noti = "1";

                break;
            case R.id.relLayCheckedOutOn://For CheckOut notification On
                imgViewCheckedOutOn.setImageResource(R.drawable.on);
                imgViewCheckedOutOff.setImageResource(R.drawable.off);
                checkedout_noti = "0";

                break;
            case R.id.relLayCheckedOutOff://For CheckOut notification Off
                imgViewCheckedOutOn.setImageResource(R.drawable.off);
                imgViewCheckedOutOff.setImageResource(R.drawable.on);
                checkedout_noti = "1";

                break;
            case R.id.relLaySpeedOn:
                ((LinearLayout) findViewById(R.id.linLaySpeedProgress)).setVisibility(View.VISIBLE);
                imgSpeedOn.setImageResource(R.drawable.on);
                imgSpeedOff.setImageResource(R.drawable.off);
                speed_noti = "0";
                break;
            case R.id.relLaySpeedOff:
                ((LinearLayout) findViewById(R.id.linLaySpeedProgress)).setVisibility(View.GONE);
                imgSpeedOn.setImageResource(R.drawable.off);
                imgSpeedOff.setImageResource(R.drawable.on);
                speed_noti = "1";
                break;
            case R.id.relLayWrongRouteOn:
                imgWrongRouteOn.setImageResource(R.drawable.on);
                imgWrongRouteOff.setImageResource(R.drawable.off);
                wrongroute_noti = "0";
                break;
            case R.id.relLayWrongRouteOff:
                imgWrongRouteOn.setImageResource(R.drawable.off);
                imgWrongRouteOff.setImageResource(R.drawable.on);
                wrongroute_noti = "1";
                break;

            case R.id.relLayCheckedInOnsms://For CheckIn notification On
                imgViewCheckedInOnsms.setImageResource(R.drawable.on);
                imgViewCheckedInOffsms.setImageResource(R.drawable.off);
                checkedin_notisms = "0";

                break;
            case R.id.relLayCheckedInOffsms://For CheckIn notification Off
                imgViewCheckedInOnsms.setImageResource(R.drawable.off);
                imgViewCheckedInOffsms.setImageResource(R.drawable.on);
                checkedin_notisms = "1";

                break;
            case R.id.relLayCheckedOutOnsms://For CheckOut notification On
                imgViewCheckedOutOnsms.setImageResource(R.drawable.on);
                imgViewCheckedOutOffsms.setImageResource(R.drawable.off);
                checkedout_notisms = "0";

                break;
            case R.id.relLayCheckedOutOffsms://For CheckOut notification Off
                imgViewCheckedOutOnsms.setImageResource(R.drawable.off);
                imgViewCheckedOutOffsms.setImageResource(R.drawable.on);
                checkedout_notisms = "1";

                break;
            case R.id.relLaySpeedOnsms:
                ((LinearLayout) findViewById(R.id.linLaySpeedProgresssms)).setVisibility(View.VISIBLE);
                imgSpeedOnsms.setImageResource(R.drawable.on);
                imgSpeedOffsms.setImageResource(R.drawable.off);
                speed_notisms = "0";
                break;
            case R.id.relLaySpeedOffsms:
                ((LinearLayout) findViewById(R.id.linLaySpeedProgresssms)).setVisibility(View.GONE);
                imgSpeedOnsms.setImageResource(R.drawable.off);
                imgSpeedOffsms.setImageResource(R.drawable.on);
                speed_notisms = "1";
                break;
            case R.id.relLayWrongRouteOnsms:
                imgWrongRouteOnsms.setImageResource(R.drawable.on);
                imgWrongRouteOffsms.setImageResource(R.drawable.off);
                wrongroute_notisms = "0";
                break;
            case R.id.relLayWrongRouteOffsms:
                imgWrongRouteOnsms.setImageResource(R.drawable.off);
                imgWrongRouteOffsms.setImageResource(R.drawable.on);
                wrongroute_notisms = "1";
                break;

            case R.id.relLayinstantMsgOn:

                imgDriverOn.setImageResource(R.drawable.on);
                imgDriverOff.setImageResource(R.drawable.off);
                driver_noti = "0";
                break;
            case R.id.relLayInstantMsgOff:
                imgDriverOff.setImageResource(R.drawable.on);
                imgDriverOn.setImageResource(R.drawable.off);
                driver_noti = "1";
                break;
            case R.id.relLayMorningOn:
                imgMorningOn.setImageResource(R.drawable.on);
                imgMorningOff.setImageResource(R.drawable.off);
                morning_noti = "0";
                break;
            case R.id.relLayMorningOff:
                imgMorningOn.setImageResource(R.drawable.off);
                imgMorningOff.setImageResource(R.drawable.on);
                morning_noti = "1";
                break;
            case R.id.relLayEveningOn:
                imgEveningOn.setImageResource(R.drawable.on);
                imgEveningOff.setImageResource(R.drawable.off);
                evening_noti = "0";
                break;
            case R.id.relLayEveningOff:
                imgEveningOn.setImageResource(R.drawable.off);
                imgEveningOff.setImageResource(R.drawable.on);
                evening_noti = "1";
                break;
            case R.id.relLayinstantMsgOnsms:
                imgDriverOnsms.setImageResource(R.drawable.on);
                imgDriverOffsms.setImageResource(R.drawable.off);
                driver_notisms = "0";
                break;
            case R.id.relLayInstantMsgOffsms:
                imgDriverOffsms.setImageResource(R.drawable.on);
                imgDriverOnsms.setImageResource(R.drawable.off);
                driver_notisms = "1";
                break;
            case R.id.relLayMorningOnsms:
                imgMorningOnsms.setImageResource(R.drawable.on);
                imgMorningOffsms.setImageResource(R.drawable.off);
                morning_notisms = "0";
                break;
            case R.id.relLayMorningOffsms:
                imgMorningOnsms.setImageResource(R.drawable.off);
                imgMorningOffsms.setImageResource(R.drawable.on);
                morning_notisms = "1";
                break;
            case R.id.relLayEveningOnsms:
                imgEveningOnsms.setImageResource(R.drawable.on);
                imgEveningOffsms.setImageResource(R.drawable.off);
                evening_notisms = "0";
                break;
            case R.id.relLayEveningOffsms:
                imgEveningOnsms.setImageResource(R.drawable.off);
                imgEveningOffsms.setImageResource(R.drawable.on);
                evening_notisms = "1";
                break;


            case R.id.btnSaveSetting:
                if (Utility.isConnectingToInternet(appContext))
                    new SaveSetting().execute();
                else
                    Toast.makeText(SettingActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                break;
        }
    }

      /*   ------------------------------>CODE FOR Save Settings<---------------------------------   */

    public class SaveSetting extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        final ProgressDialog dialog = new ProgressDialog(SettingActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.update_setting));
            dialog.setMessage(getString(R.string.wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
          /*  http://localhost:8080/Tracking_bus/webservices/save_parent_setting?lang=1¬i_on=1&chat_on=1&checked_in_on=1&checked_out_on=1&speed_on=1&max_speed=50&wrong_route_on=1&user_id=12
            (05:36:47) {"result":"success"}
            (05:37:02) Ye success ka response hai
                    (05:37:07) aur failed me
                    (05:37:08) {"result":"failed"}
            (05:37:12) ye aayega
            (05:37:56) ager 0 hai to yes aur 1 par no hai*/
            /* sms_checked_in_on, sms_checked_out_on, sms_speed_on, sms_max_speed, sms_wrong_route_on*/


            /*http://localhost:8080/Tracking_bus/webservices/save_parent_setting?user_id=12&lang=0¬i_on=0&chat_on=0&checked_in_on=0&checked_out_on=0&speed_on=0&max_speed=60&wrong_route_on=60&instant_message=1&morning_before=1&evening_before=1&sms_instant_message=1&sms_morning_before=1&sms_evening_before=1&sms_checked_in_on=1&sms_checked_out_on=1&sms_speed_on=1&sms_max_speed=1&sms_wrong_route_on=1*/
            String URL = ConstantKeys.SERVER_URL + "save_parent_setting?lang=" + language + "&noti_on=" + sound_noti + "&chat_on=" + sound_chat + "&checked_in_on=" + checkedin_noti
                    + "&checked_out_on=" + checkedout_noti + "&speed_on=" + speed_noti + "&max_speed=" + speed + "&wrong_route_on=" + wrongroute_noti + "&user_id=" + user_id
                    + "&sms_checked_in_on=" + checkedin_notisms + "&sms_checked_out_on=" + checkedout_notisms + "&sms_speed_on=" + speed_notisms + "&sms_max_speed="
                    + speed_sms + "&sms_wrong_route_on=" + wrongroute_notisms +
                    "&instant_message=" + driver_noti + "&morning_before=" + morning_noti + "&evening_before=" + evening_noti + "&sms_instant_message=" + driver_notisms + "&sms_morning_before=" + morning_notisms + "&sms_evening_before=" + evening_notisms + "&sound_setting=" + sound_noti + "&chat_sound=" + sound_chat;
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
/**/
            try {
                if (!s.isEmpty()) {
                    networkResponse = new JSONObject(s);
                    Log.e("setting Response---", "" + s);
                    if (networkResponse == null || networkResponse.equals("")) {
                        Toast.makeText(appContext, appContext.getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                    } else {
                        if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                            saveSetting();
                        } else {
                            Toast.makeText(appContext, getString(R.string.setting_not_saved), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    saveSetting();
                }
            } catch (Exception e) {
                saveSetting();
                // Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                Log.e("Setting Exception", "" + e);
            }
            super.onPostExecute(s);
        }
    }

    private void saveSetting() {
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

        Utility.setSharedPreference(appContext, ConstantKeys.Setting_Language, language);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_notisound, sound_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_msgsound, sound_chat);

        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedInNoti, checkedin_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedOutNoti, checkedout_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_SpeedOnOff, speed_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_WrongRoute, wrongroute_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_Speed, speed);

        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedInNotiSMS, checkedin_notisms);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_CheckedOutNotiSMS, checkedout_notisms);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_SpeedOnOffSMS, speed_notisms);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_WrongRouteSMS, wrongroute_notisms);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_SpeedSMS, speed_sms);

        Utility.setSharedPreference(appContext, ConstantKeys.Setting_DriverNoti, driver_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_DriverNotiSMS, driver_notisms);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_EveningNoti, evening_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_EveningNotiSms, evening_notisms);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_MorningNoti, morning_noti);
        Utility.setSharedPreference(appContext, ConstantKeys.Setting_MorningNotiSMS, morning_notisms);

        Toast.makeText(appContext, getString(R.string.setting_saved), Toast.LENGTH_LONG).show();

        if (language.equals("1")) {
            Locale locale = new Locale("ar");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        finish();
    }
}