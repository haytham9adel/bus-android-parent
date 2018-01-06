package net.m3aak.parentapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.m3aak.parentapp.Beans.ChatMessage;
import net.m3aak.parentapp.ChatSocket.Utils;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatArrayAdapter extends BaseAdapter {
    private List<ChatMessage> chatMessageList;
    private Activity mContext;

    public ChatArrayAdapter(Activity context, List<ChatMessage> list) {
        mContext = context;
        chatMessageList = list;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final viewHolderChat viewHolder;

        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = li.inflate(R.layout.sms_row, null);
        try {
        viewHolder = new viewHolderChat(row);
        row.setTag(viewHolder);

        ChatMessage chatMessageObj = getItem(position);
        if (chatMessageObj.getSide().equals("0")) {
            //viewHolder.singleMessageContainer.setGravity(Gravity.LEFT);
            viewHolder.linLayOther.setVisibility(View.VISIBLE);
            viewHolder.linLaySelf.setVisibility(View.GONE);
            viewHolder.singleMessage.setText(chatMessageObj.getMessage());
            viewHolder.adminName.setText(Utility.getSharedPreferences(mContext, ConstantKeys.SCHOOL_ADMIN_NAME).replaceAll("null", " "));

                viewHolder.lblMsgTime.setText(convertInLocalTime(chatMessageObj.getTime()));
                viewHolder.lblMsgTimeOther.setText(convertInLocalTime(chatMessageObj.getTime()));

        } else if (chatMessageObj.getSide().equals("1")) {
            viewHolder.linLaySelf.setVisibility(View.VISIBLE);
            viewHolder.linLayOther.setVisibility(View.GONE);
            viewHolder.lblMsgFrom.setText(chatMessageObj.getMessage());
            if (chatMessageObj.getStatus() != null && chatMessageObj.getStatus().equals("0")) {
                viewHolder.readUnreadImg.setImageResource(R.drawable.sent);
            } else {
                viewHolder.readUnreadImg.setImageResource(R.drawable.read);
            }
            viewHolder.userName.setText(Utility.getSharedPreferences(mContext, ConstantKeys.FIRST_NAME).replaceAll("null", " ") + " " + Utility.getSharedPreferences(mContext, ConstantKeys.FAMILY_NAME));
            try {
                viewHolder.lblMsgTime.setText(convertInLocalTime(chatMessageObj.getTime()));
                viewHolder.lblMsgTimeOther.setText(convertInLocalTime(chatMessageObj.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (chatMessageObj.getSide().equals(Utils.TYPE)) {
            viewHolder.linLayOther.setVisibility(View.VISIBLE);
            viewHolder.linLaySelf.setVisibility(View.GONE);
            viewHolder.singleMessage.setText("Typing...");
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    /**
     * Get new date after converting in local time.
     *
     * @param serverDate Server date.
     * @return Return new date according to device time zone.
     * @throws ParseException Throw parsing exception.
     */
    private String convertInLocalTime(String serverDate) throws ParseException {
        String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss a";
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

    class viewHolderChat {
        TextView lblMsgFrom, singleMessage, lblMsgTime, lblMsgTimeOther, adminName, userName;
        LinearLayout linLaySelf, linLayOther;
        ImageView readUnreadImg;

        viewHolderChat(View v1) {
            adminName = (TextView) v1.findViewById(R.id.lblAdminName);
            userName = (TextView) v1.findViewById(R.id.lblUserName);
            lblMsgFrom = (TextView) v1.findViewById(R.id.lblMsgFrom);
            singleMessage = (TextView) v1.findViewById(R.id.singleMessage);
            lblMsgTime = (TextView) v1.findViewById(R.id.lblMsgTime);
            lblMsgTimeOther = (TextView) v1.findViewById(R.id.lblMsgTimeOther);
            linLaySelf = (LinearLayout) v1.findViewById(R.id.linLaySelf);
            linLayOther = (LinearLayout) v1.findViewById(R.id.linLayOther);
            readUnreadImg = (ImageView) v1.findViewById(R.id.read_unread_img);
        }
    }

}