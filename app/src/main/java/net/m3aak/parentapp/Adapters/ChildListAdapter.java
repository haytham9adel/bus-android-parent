package net.m3aak.parentapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.m3aak.parentapp.Beans.Student;
import net.m3aak.parentapp.ChildInformation;
import net.m3aak.parentapp.MyWidgets.CircleImageView;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.SingleStudentMapActivty;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import java.util.List;

/**
 * Created by BD-2 on 8/24/2015.
 */
public class ChildListAdapter extends BaseAdapter {
    List<Student> search_item;
    private Activity mContext;
    private List<Student> mList;
    private LayoutInflater mLayoutInflater = null;
    public static String STUD_ID, STUD_NAME, ROUTE_ID, STUD_FNAME, STUD_FATHER_NAME, STUD_GRAND_NAME, STUD_FAMILY_NAME,
            STUD_GENDER, STUD_CLASS, STUD_BIRTHDATE, STUD_NATIONALITY, STUD_BLOODTYPE, STUD_IMAGE, STUD_LAT, STUD__LONG;

    public ChildListAdapter(Activity context, List<Student> list) {
        mContext = context;
        search_item = list;
        this.mList = search_item;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return search_item.size();
    }

    @Override
    public Student getItem(int pos) {
        return search_item.get(pos);
    }

    @Override
    public long getItemId(int posChild) {
        return posChild;
    }

    @Override
    public View getView(final int posChild, View convertView, ViewGroup parent) {
        View v = convertView;
        ChildListAdapterViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.child_single, null);
            viewHolder = new ChildListAdapterViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ChildListAdapterViewHolder) v.getTag();
        }
        try {

        if (getItem(posChild).getDriverName().equals("")) {
            viewHolder.driver_row.setVisibility(View.GONE);
        } else {
            viewHolder.driver_row.setVisibility(View.VISIBLE);
            viewHolder.driver_name.setText(getItem(posChild).getDriverName());
            viewHolder.calldriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItem(posChild).getDriverNumber() != null && !getItem(posChild).getDriverNumber().isEmpty()) {
                        String strNumber = "" + getItem(posChild).getDriverNumber();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + strNumber));
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        viewHolder.viewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student hm1 = getItem(posChild);
                STUD_NAME = "" + hm1.getS_fname() + " " + hm1.getS_lname();
                STUD_ID = "" + hm1.getID();
                ROUTE_ID = "" + hm1.getS_route_id();
                STUD_FNAME = hm1.getS_fname();
                STUD_FATHER_NAME = hm1.getS_father_name();
                STUD_GRAND_NAME = hm1.getS_grand_name();
                STUD_FAMILY_NAME = hm1.getS_family_name();
                STUD_NATIONALITY = hm1.getS_nationality();
                STUD_BLOODTYPE = hm1.getS_blood_type();
                STUD_IMAGE = hm1.getS_s_image_path();
                STUD_GENDER = hm1.getS_gender();
                STUD_CLASS = hm1.getS_class();
                STUD_BIRTHDATE = hm1.getS_dob();
                STUD_LAT = hm1.getS_latitude();
                STUD__LONG = hm1.getS_longitude();
                ConstantKeys.click = false;
                Intent intent = new Intent(mContext, ChildInformation.class);
                mContext.startActivity(intent);
            }
        });

        Student hm = getItem(posChild);
        viewHolder.mTVItem.setText(hm.getName() + " " + hm.getS_lname());
        if (!Utility.isStringNullOrBlank(hm.getS_s_image_path())) {
            Picasso.with(mContext)
                    .load(ConstantKeys.IMAGE_URL + hm.getS_s_image_path()).placeholder(R.drawable.profile_big)
                    .into(viewHolder.img);
        }

        if (hm.getS_BlinkStatus().equals("1")) {
            //Blink
            blink(viewHolder.trackStudent);
        } else {

        }
        viewHolder.trackStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Student hm1 = getItem(posChild);
                if (hm1.getS_BlinkStatus().equals("1")) {
                    //Redirected to tracking
                    STUD_NAME = "" + hm1.getS_fname() + " " + hm1.getS_lname();
                    STUD_ID = "" + hm1.getID();
                    ROUTE_ID = "" + hm1.getS_route_id();
                    STUD_FNAME = hm1.getS_fname();
                    STUD_FATHER_NAME = hm1.getS_father_name();
                    STUD_GRAND_NAME = hm1.getS_grand_name();
                    STUD_FAMILY_NAME = hm1.getS_family_name();
                    STUD_NATIONALITY = hm1.getS_nationality();
                    STUD_BLOODTYPE = hm1.getS_blood_type();
                    STUD_IMAGE = hm1.getS_s_image_path();
                    STUD_GENDER = hm1.getS_gender();
                    STUD_CLASS = hm1.getS_class();
                    STUD_BIRTHDATE = hm1.getS_dob();
                    STUD_LAT = hm1.getS_latitude();
                    STUD__LONG = hm1.getS_longitude();
                    Intent intent = new Intent(mContext, SingleStudentMapActivty.class);
                    intent.putExtra("blink_status", hm1.getS_BlinkStatus());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Your child is not in Bus.", Toast.LENGTH_SHORT).show();
                    String status = Utility.getSharedPreferences(mContext, "within");
                    if (status.equals("1")) {
                        STUD_NAME = "" + hm1.getS_fname() + " " + hm1.getS_lname();
                        STUD_ID = "" + hm1.getID();
                        ROUTE_ID = "" + hm1.getS_route_id();
                        STUD_FNAME = hm1.getS_fname();
                        STUD_FATHER_NAME = hm1.getS_father_name();
                        STUD_GRAND_NAME = hm1.getS_grand_name();
                        STUD_FAMILY_NAME = hm1.getS_family_name();
                        STUD_NATIONALITY = hm1.getS_nationality();
                        STUD_BLOODTYPE = hm1.getS_blood_type();
                        STUD_IMAGE = hm1.getS_s_image_path();
                        STUD_GENDER = hm1.getS_gender();
                        STUD_CLASS = hm1.getS_class();
                        STUD_BIRTHDATE = hm1.getS_dob();
                        STUD_LAT = hm1.getS_latitude();
                        STUD__LONG = hm1.getS_longitude();
                        Intent intent = new Intent(mContext, SingleStudentMapActivty.class);
                        intent.putExtra("blink_status", hm1.getS_BlinkStatus());
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        if ((posChild % 2) == 0) {
            v.setBackgroundColor(Color.parseColor("#f5f5f5"));
        } else {
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        } catch (Exception e) {  e.printStackTrace(); }
        return v;
    }

    private void blink(final ImageView viewBlink) {
        final Handler handlerBlink = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                int timeToBlink = 1000;    //in milissegunds

                    Thread.sleep(timeToBlink);

                handlerBlink.post(new Runnable() {
                    @Override
                    public void run() {
                      try {
                        if (viewBlink.isShown() == true) {
                            viewBlink.setVisibility(View.INVISIBLE);
                            viewBlink.setImageResource(R.drawable.gps_blink2);
                        } else {
                            viewBlink.setVisibility(View.VISIBLE);
                            viewBlink.setImageResource(R.drawable.gps_blink1);
                        }
                        blink(viewBlink);
                        Log.e("Blink", "Blink");
                      } catch (Exception e) {  e.printStackTrace(); }
                    }
                });

            } catch (Exception e) { e.printStackTrace(); }
           }
        }).start();
    }

    class ChildListAdapterViewHolder {
        public TextView mTVItem, viewStudent, driver_name;
        public CircleImageView img;
        ImageView trackStudent, calldriver;
        RelativeLayout driver_row;

        public ChildListAdapterViewHolder(View base) {
            mTVItem = (TextView) base.findViewById(R.id.ch_name);
            img = (CircleImageView) base.findViewById(R.id.child_img);
            viewStudent = (TextView) base.findViewById(R.id.viewStudent);
            driver_name = (TextView) base.findViewById(R.id.driver_name);
            trackStudent = (ImageView) base.findViewById(R.id.trackStudent);
            calldriver = (ImageView) base.findViewById(R.id.calldriver);
            driver_row = (RelativeLayout) base.findViewById(R.id.driver_row);
        }
    }

}
