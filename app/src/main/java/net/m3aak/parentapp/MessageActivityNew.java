package net.m3aak.parentapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import net.m3aak.parentapp.Beans.ChatMessage;
import net.m3aak.parentapp.ChatSocket.Utils;
import net.m3aak.parentapp.ChatSocket.WsConfig;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;
import net.m3aak.parentapp.services.ServiceCallbacks;
import net.m3aak.parentapp.services.UpdateChatListService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by BD-2 on 8/17/2015.
 */
public class MessageActivityNew extends AppCompatActivity implements ServiceCallbacks {

    private Context appContext;
    private static final String TAG = "ChatActivity";
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    Intent intent;
    private boolean side = false;
    private UpdateChatListService myService;
    private boolean bound = false;
    List<ChatMessage> chatArrayList = new ArrayList<>();

    private Utils utils;
    private WebSocket ws;
    AVLoadingIndicatorView typeIndecatorView;
    private boolean isTypingShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        try {
            appContext = this;
            Utility.setSharedPreference(appContext, ConstantKeys.COUNT_CHAT_NOTI, "");
            typeIndecatorView = (AVLoadingIndicatorView) findViewById(R.id.anim_view);

            buttonSend = (Button) findViewById(R.id.buttonSend);
            listView = (ListView) findViewById(R.id.listView1);

            chatArrayAdapter = new ChatArrayAdapter(MessageActivityNew.this, chatArrayList);
            listView.setAdapter(chatArrayAdapter);

            chatText = (EditText) findViewById(R.id.chatText);
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    sendChatMessage1();
                }
            });

            init();

            connectSocket();

            startTypingListener();
        }catch (Exception e) {e.printStackTrace();}
    }

    private void startTypingListener() {
        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ws.sendText(utils.getSendMessageJSON(Utils.TYPE));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void removeUnread() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int j = chatArrayList.size() - 1; j >= 0; j--) {
                    ChatMessage chatMessage = chatArrayList.get(j);
                    if (chatMessage.getStatus() != null && chatMessage.getStatus().equals("0")) {
                        chatMessage.setStatus("1");
                        chatArrayList.set(j, chatMessage);
                    } else {
                        break;
                    }
                }
                /*for (int i = 0; i < chatArrayList.size(); i++) {
                    ChatMessage chatMessage = chatArrayList.get(i);
                    if (chatMessage.getStatus() != null && chatMessage.getStatus().equals("0")) {
                        chatMessage.setStatus("1");
                        chatArrayList.set(i, chatMessage);
                    }
                }*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void addTyping() {
        if (!isTypingShow) {
            isTypingShow = true;
            typeIndecatorView.smoothToShow();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isTypingShow = false;
                    typeIndecatorView.smoothToHide();
                }
            }, 1500);
        }
    }

    private void connectSocket() {
        try {
            WebSocketFactory factory = new WebSocketFactory();
            ws = factory.createSocket(WsConfig.URL_WEBSOCKET + "" + Utility.getSharedPreferences(MessageActivityNew.this, ConstantKeys.USER_ID));
            ws.addListener(new SocketListenere());
            ws.connectAsynchronously();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSession(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            if (jsonObject.getString("flag").equals("self")) {
                Utils utils = new Utils(MessageActivityNew.this);
                utils.storeSessionId(jsonObject.getString("sessionId"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String GetUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    private void sendChatMessage1() {
        String chatMsg = chatText.getText().toString();
        if (Utility.isStringNullOrBlank(chatMsg) == false) {
            if (ws != null) {
                ws.sendText(utils.getSendMessageJSON(chatMsg));
                ChatMessage chmsg = new ChatMessage();
                chmsg.setSide("1");
                chmsg.setMessage(chatMsg);
                chmsg.setStatus("0");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                String formattedDate = simpleDateFormat1.format(c.getTime());
                chmsg.setTime(GetUTCdatetimeAsString());
                // chmsg.setSender(jsonObject.getString("user_name"));
                chatArrayList.add(chmsg);
                chatText.setText("");
                chatArrayAdapter.notifyDataSetChanged();
                listView.setSelection(listView.getCount());
            }
            new SendMessageToAdmin().execute(Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID),
                    Utility.getSharedPreferences(appContext, ConstantKeys.Reciever_ID), chatMsg);
        } else {
            Toast.makeText(appContext, "Please write message and try again !", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {

        utils = new Utils(MessageActivityNew.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception ignored) {
        }
        ((TextView) findViewById(R.id.title)).setText("Chat");
        ((ImageView) findViewById(R.id.toggle_btn)).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.title)).setText(Utility.getSharedPreferences(appContext, ConstantKeys.school_name));
        if (!Utility.isStringNullOrBlank(Utility.getSharedPreferences(appContext, ConstantKeys.SCHOOL_LOGO))) {
            Picasso.with(appContext)
                    .load(ConstantKeys.SCHOOL_IMAGE_URL + Utility.getSharedPreferences(appContext, ConstantKeys.SCHOOL_LOGO))
                    .fit()
                    .into(((ImageView) findViewById(R.id.imgViewSchoolLogo)));
        }
        findViewById(R.id.imgViewSos).setVisibility(View.GONE);

        new GetChatListTask().execute(Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID));


        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //AsynchTask to send message to parent of selected child
    class SendMessageToAdmin extends AsyncTask<String, String, String> {
        Context cntx;
        JSONObject networkResponse = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
           /*  http://24x7world.com/tracking_bus/send_message.php?sender_id=73&reciever_id=71&msg=hello
            {"result":"success"}*/
            /*Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID),
                    Utility.getSharedPreferences(appContext,ConstantKeys.Reciever_ID), chatMsg*/
            String URL = ConstantKeys.SERVER_URL + "send_message?sender_id=" + params[0] + "&reciever_id=" + params[1] + "&msg=" + params[2];
            String URL1 = URL.replaceAll(" ", "%20").replace("  ", "");
            Log.e("sender_id", Utility.getSharedPreferences(appContext, ConstantKeys.USER_ID));
            Log.e("reciever_id", Utility.getSharedPreferences(appContext, ConstantKeys.Reciever_ID));
            Log.e("chatMsg", params[2]);
            NetworkHelperGet putRequest = new NetworkHelperGet(URL1);
            try {
                return putRequest.sendGet();
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // dialog.dismiss();
            System.out.println(s);
            try {
                networkResponse = new JSONObject(s);
                Log.e("Send Message", "" + networkResponse);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, appContext.getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {

                    } else {
                        Toast.makeText(appContext, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("SendMessage Exception", "" + e);
            }
            super.onPostExecute(s);
        }
    }

    //Task for get Chat list
    private class GetChatListTask extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(MessageActivityNew.this);
        ChatMessage chmsg = null;

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle("Loading");
            dialog.setMessage("Please Wait...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = ConstantKeys.SERVER_URL + "chatting?reciever_id=" + params[0];
            Log.e("URL", URL);
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
            Log.e("chat list ", "" + s);
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, appContext.getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    /* status=0 means unread and status=1 means read*/
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        // Toast.makeText(appContext,""+networkResponse.getString("responseMessage"),Toast.LENGTH_LONG).show();
                        if (networkResponse.has("details")) {
                            if (networkResponse.getJSONArray("details").length() > 0) {
                                chatArrayList.clear();
                                for (int i = 0; i < networkResponse.getJSONArray("details").length(); i++) {
                                    JSONObject jsonObject = networkResponse.getJSONArray("details").getJSONObject(i);
                                    chmsg = new ChatMessage();
                                    chmsg.setSide(jsonObject.getString("sender"));
                                    chmsg.setMessage(jsonObject.getString("msg"));
                                    chmsg.setStatus(jsonObject.getString("status"));
                                    String dateTimeStr = jsonObject.getString("time");
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                                    Date date = simpleDateFormat.parse(dateTimeStr);
                                    String dateTime = simpleDateFormat1.format(date);
                                    chmsg.setTime(dateTime);
                                    chatArrayList.add(chmsg);
                                }
                                chatArrayAdapter.notifyDataSetChanged();
                                Log.e("chatArrayList", "" + chatArrayList.toString());
                            }
                        }
                    } else {
                        Toast.makeText(appContext, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("GetChatListTask Exce", "" + e);
            }
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.setSharedPreference(appContext, ConstantKeys.ISCHATVISIBLE, "1");

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.setSharedPreference(appContext, ConstantKeys.ISCHATVISIBLE, "0");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to Service
        Intent intent = new Intent(this, UpdateChatListService.class);
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

    /**
     * Callbacks for service binding, passed to bindService()
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            UpdateChatListService.LocalBinder binder = (UpdateChatListService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(MessageActivityNew.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    /* Defined by ServiceCallbacks interface */
    @Override
    public void UpdateChatList() {

       /* Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(appContext, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChatMessage chmsg = new ChatMessage();
        chmsg.setSide("0");
        chmsg.setMessage(GCMIntentService.new_message);
        chmsg.setStatus("0");
        // chmsg.setSender(jsonObject.getString("user_name"));
        chatArrayList.add(chmsg);
        chatArrayAdapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount());
        Log.e("test msg", "SetMessageComesFromAdmin");*/
    }

    private void addMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
/*{
    "message": "dfs",
    "flag": "message",
    "sessionId": "89",
    "name": "9",
    "user_id": "20",
    "sender_name": "Ajay",
    "chat_type": "2"
}*/
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String message = jsonObject.getString("message");
                    if (!jsonObject.optString("user_id", Utility.getSharedPreferences(MessageActivityNew.this, ConstantKeys.Reciever_ID)).equals(Utility.getSharedPreferences(MessageActivityNew.this, ConstantKeys.Reciever_ID))) {
                        removeUnread();
                        if (message.equals("1")) {// user is typing
                            addTyping();
                        } else if (message.equals("read_unread_check")) { // read messsagere
                            removeUnread();
                        } else { // there is new message
                            isTypingShow = false;
                            typeIndecatorView.smoothToHide();
                            ChatMessage chmsg = new ChatMessage();
                            chmsg.setSide("0");
                            chmsg.setMessage(message);
                            chmsg.setStatus("1");
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                            String formattedDate = simpleDateFormat1.format(c.getTime());
                            chmsg.setTime(formattedDate);
                            chatArrayList.add(chmsg);
                            chatArrayAdapter.notifyDataSetChanged();
                            listView.setSelection(listView.getCount());
                            ws.sendText(utils.getSendMessageJSON(Utils.READ));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class SocketListenere implements WebSocketListener {
        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {

        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            showToast("onConnected");
            try {
                ws.sendText(utils.getSendMessageJSON(Utils.READ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
            showToast("onConnectError");
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            showToast("onDisconnected");
        }

        @Override
        public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onFrame");
        }

        @Override
        public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onContinuationFrame");
        }

        @Override
        public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onTextFrame");
        }

        @Override
        public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onBinaryFrame");
        }

        @Override
        public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onCloseFrame");
        }

        @Override
        public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            setSession(text);
            addMessage(text);
            showToast(text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            showToast("onBinaryMessage");
        }

        @Override
        public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onSendingFrame");
        }

        @Override
        public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onFrameSent");
        }

        @Override
        public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
            showToast("onFrameUnsent");
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            showToast("onError");
        }

        @Override
        public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
            showToast("onFrameError");
        }

        @Override
        public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
            showToast("onMessageError");
        }

        @Override
        public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
            showToast("onMessageDecompressionError");
        }

        @Override
        public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
            showToast("onTextMessageError");
        }

        @Override
        public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
            showToast("onSendError");
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
            showToast("onUnexpectedError");
        }

        @Override
        public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
            showToast("handleCallbackError");
        }

        @Override
        public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
            showToast("onSendingHandshake");
        }

        private void showToast(String message) {
            Log.e("socket log ", message);
        }
    }



}