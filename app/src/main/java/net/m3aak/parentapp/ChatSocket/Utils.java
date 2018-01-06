package net.m3aak.parentapp.ChatSocket;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import static net.m3aak.parentapp.Utilities.Utility.appContext;

public class Utils {

    private Context context;
    private SharedPreferences sharedPref;

    private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_SESSION_ID = "sessionId",
            FLAG_MESSAGE = "message";
    public static String TYPE = "type"; // 1
    public static String MESSAGE = "message"; //
    public static String READ = "read";

    public Utils(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF,
                KEY_MODE_PRIVATE);
    }

    public void storeSessionId(String sessionId) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.commit();
    }

    public String getSessionId() {
        return sharedPref.getString(KEY_SESSION_ID, null);
    }

    public String getSendMessageJSON(String message) {
        String json = null;

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", getSessionId());
            jsonObject.put("message", message);
            jsonObject.put("flag", "message");
            jsonObject.put("chat_type", "1");
            jsonObject.put("user_id", Utility.getSharedPreferences(context, ConstantKeys.Reciever_ID));
            jsonObject.put("sender_name", Utility.getSharedPreferences(appContext, ConstantKeys.FIRST_NAME) + " " + Utility.getSharedPreferences(appContext, ConstantKeys.MIDDLE_NAME));
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
