package net.m3aak.parentapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.m3aak.parentapp.Fragments.SingleStudentMapFragment;
import net.m3aak.parentapp.LoginActivity;
import net.m3aak.parentapp.MainActivityNew;
import net.m3aak.parentapp.MessageActivityNew;
import net.m3aak.parentapp.NetworkHelperGet;
import net.m3aak.parentapp.NotificationListActivity;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.services.UpdateBlinkService;
import net.m3aak.parentapp.services.UpdateChatListService;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.NotificationID;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by RWS 6 on 6/14/2017.
 */

public class MessagingService extends FirebaseMessagingService {

    static Bitmap bitmap;

    static SharedPreferences sh_Pref;
    static String uid;

    static String NOTIFICATION_TYPE, new_message;
    static boolean isChatNotification;
    static int noti_count = 0;
    static JSONObject jObj = new JSONObject();
    private static final String TAG = "PUSH_NOTIFICATION";

    static boolean isNotification = false;
    boolean isBlinkNoti = false;
    Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        context = MessagingService.this;

        Log.e(TAG, "Received message");
        uid = Utility.getSharedPreferences(context, ConstantKeys.USER_ID);
        //Log.e("uid",""+uid);

        //{msg=Check irt, date=2016-08-11 05:05:40, noti_type=chat, noti_id=84}

        // notifies user
        noti_count = noti_count + 1;
        //Toast.makeText(getApplicationContext(),noti_count+" notification",Toast.LENGTH_SHORT).show();
        // PARSE MESSAGE HERE

        isNotification = true;
// Wrong route, speed response Bundle[{msg={"msg":"Bus of Route-1 is moving on wrong path","date":"2016-06-08 03:28:47","noti_id":"1"}, from=744231459759, collapse_key=do_not_collapse}]

        String msg = "";
        try {
            Map<String, String> params = remoteMessage.getData();
            jObj = new JSONObject(params.get("msg"));
            if (jObj.has("noti_type")) {
                NOTIFICATION_TYPE = jObj.getString("noti_type");
                if (NOTIFICATION_TYPE.equals("chat")) {
                    //	NITIFICATION_ID=jObj.getString("noti_id");
                    msg = jObj.getString("msg");
                    isChatNotification = true;
                    if (Utility.getSharedPreferences(MessagingService.this, ConstantKeys.ALREADY_LOGIN).equals("Yes")) {
                        generateNotification(context, 2);
                    }
/*Chat Response  Bundle[{google.sent_time=1470912429217, msg={msg=hi, date=2016-08-11 04:17:12, noti_type=chat, noti_id=79},
 from=744231459759, google.message_id=0:1470912429222100%e409a6d9f9fd7ecd, collapse_key=do_not_collapse}]*/
                } else if (NOTIFICATION_TYPE.equals("blink")) {
                    Utility.setSharedPreference(context, "within", "0");
                    if (MainActivityNew.isVisibleMainActivity == true) {
                        isBlinkNoti = true;
                        context.startService(new Intent(context, UpdateBlinkService.class));
                    }
                } //To be opened for update blink status of student
                else if (NOTIFICATION_TYPE.equals("wrong_route") || NOTIFICATION_TYPE.equals("over_speed") || NOTIFICATION_TYPE.equals("within")) {
                /*	msg={"msg":"Bus of Route-4 is moving on wrong path","date":"2016-09-05 07:07:44","noti_id":"60"}*/
                    //	NITIFICATION_ID=jObj.getString("noti_id");
                    msg = jObj.getString("msg");

                    if (Utility.getSharedPreferences(MessagingService.this, ConstantKeys.ALREADY_LOGIN).equals("Yes")) {
                        generateNotification(context, 1);
                    }
                    /* noti_type=wrong_route for WRONG ROUTE
                     =over_speed for High Speed
                     = within for 15 mnt to reach*/
                    if (NOTIFICATION_TYPE.equals("track_noti")) {
                        Utility.setSharedPreference(context, "within", "1");
                    }
                } else if (NOTIFICATION_TYPE.equals("track_noti")) {
                    Utility.setSharedPreference(context, "within", "1");
                } else if (NOTIFICATION_TYPE.equals("stop_service")) {
                    logoutT(Utility.getSharedPreferences(MessagingService.this, ConstantKeys.USER_ID));
                } else {
                    isChatNotification = false;
                    //	NITIFICATION_ID=jObj.getString("noti_id");
                    msg = jObj.getString("msg");
                    if (Utility.getSharedPreferences(MessagingService.this, ConstantKeys.ALREADY_LOGIN).equals("Yes")) {
                        generateNotification(context, NotificationID.getID());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @SuppressWarnings("deprecation")
    private void generateNotification(Context context, int id) {

        try {
            long when = System.currentTimeMillis();
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context);
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            mBuilder.setAutoCancel(true);
            mBuilder.setContentText(jObj.getString("msg"));
            mBuilder.setShowWhen(true);
            mBuilder.setWhen(when);
            mBuilder.setSmallIcon(R.drawable.parent_app_icon);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(MessagingService.this.getResources(), R.drawable.parent_app_icon));
            if (Utility.getSharedPreferences(context, ConstantKeys.Setting_notisound).equals("1")) {
                mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            } else {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);
            }

            String title = context.getString(R.string.app_name);

            Intent notificationIntent = null;

            if (Utility.isStringNullOrBlank(uid) == false) {
                Log.e("uid", "" + uid);
                if (isChatNotification == true) {
                    if (Utility.getSharedPreferences(context, ConstantKeys.ISCHATVISIBLE).equals("0")) {
                        if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(context, ConstantKeys.COUNT_CHAT_NOTI))) {
                            int chatcount = Integer.parseInt(Utility.getSharedPreferences(context, ConstantKeys.COUNT_CHAT_NOTI));
                            Utility.setSharedPreference(context, ConstantKeys.COUNT_CHAT_NOTI, "" + (chatcount + 1));
                        } else {
                            Utility.setSharedPreference(context, ConstantKeys.COUNT_CHAT_NOTI, "1");
                        }
                        MainActivityNew.UpdateNavigation();
                        notificationIntent = new Intent(context,
                                MessageActivityNew.class);
                        // set intent so it does not start a new activity
                /*notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        );
                        PendingIntent intent = null;
                        if (notificationIntent != null)
                            intent = PendingIntent.getActivity(context, 0,
                                    notificationIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentTitle(title);
                        mBuilder.setContentIntent(intent);
                        //notification.number=noti_count;
                        notificationManager.notify(id, mBuilder.build());
                    } else {
                        Log.e("ChatVisible", "" + Utility.getSharedPreferences(context, ConstantKeys.ISCHATVISIBLE));
                        //MessageActivityNew.SetMessageComesFromAdmin(jObj.getString("msg"));
                        new_message = jObj.getString("msg");
                        context.startService(new Intent(context, UpdateChatListService.class));
                    }
                } else {
                    if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(context, ConstantKeys.COUNT_OTHER_NOTI))) {
                        int chatcount = Integer.parseInt(Utility.getSharedPreferences(context, ConstantKeys.COUNT_OTHER_NOTI));
                        Utility.setSharedPreference(context, ConstantKeys.COUNT_OTHER_NOTI, "" + (chatcount + 1));
                    } else {
                        Utility.setSharedPreference(context, ConstantKeys.COUNT_OTHER_NOTI, "1");
                    }
                    notificationIntent = new Intent(context,
                            NotificationListActivity.class);
                    // set intent so it does not start a new activity
                /*notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    );
                    PendingIntent intent = null;
                    if (notificationIntent != null)
                        intent = PendingIntent.getActivity(context, 0,
                                notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(intent);
                    mBuilder.setContentTitle(title);
                    mBuilder.setContentText(jObj.getString("msg"));
                    //notification.number=noti_count;
                    notificationManager.notify(id, mBuilder.build());
                }

            } else {

                notificationIntent = new Intent(context,
                        LoginActivity.class);
// set intent so it does not start a new activity
                /*notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                );
                PendingIntent intent = null;
                if (notificationIntent != null)
                    intent = PendingIntent.getActivity(context, 0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(intent);
                mBuilder.setContentTitle(title);
                mBuilder.setContentText(jObj.getString("msg"));
                notificationManager.notify(id, mBuilder.build());
            }
        } catch (Throwable t) {
            Log.e("Notification Issue ", " = " + t);
        }
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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (s != null & !s.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (jsonObject.getString("result").equals("success")) {
                                        doneLogout();
                                    } else {
                                        //   Toast.makeText(MainActivityNew.this, jsonObject.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //    Toast.makeText(MainActivityNew.this, getString(R.string.servernotresponding), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doneLogout() {
        Utility.setSharedPreference(MessagingService.this, ConstantKeys.USER_ID, "");
        Utility.setSharedPreference(MessagingService.this, "DBNULL", "");
        SingleStudentMapFragment.h.removeCallbacksAndMessages(null);
        SharedPreferences settings = MessagingService.this.getSharedPreferences("Tracking Bus", 0);
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
        Utility.setSharedPreference(MessagingService.this, ConstantKeys.ALREADY_LOGIN, "No");
        Intent intent = new Intent();
        intent.setAction("logout_receiver");
        sendBroadcast(intent);

        startActivity(new Intent(MessagingService.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
