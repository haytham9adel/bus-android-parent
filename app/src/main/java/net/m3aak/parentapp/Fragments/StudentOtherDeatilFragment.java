package net.m3aak.parentapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import net.m3aak.parentapp.Adapters.ChildListAdapter;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import java.util.ArrayList;

/**
 * Created by Android Developer on 12/15/2015.
 */
public class StudentOtherDeatilFragment extends Fragment {

    View v;
    //Var for google location and map
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.a_report_layout, null);

        if (Utility.getSharedPreferences(getActivity(), ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(v.findViewById(R.id.report_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        v.postDelayed(new Runnable() {

            @Override
            public void run() {
                initOtherInfo();
            }

        }, 1000);
        return v;
    }

    private void initOtherInfo() {
            /*public static String STUD_ID, STUD_NAME, ROUTE_ID,STUD_FNAME,STUD_FATHER_NAME,STUD_GRAND_NAME,STUD_FAMILY_NAME,
            STUD_GENDER,STUD_CLASS,STUD_BIRTHDATE,STUD_NATIONALITY,STUD_BLOODTYPE,STUD_IMAGE;*/
        if (!Utility.isStringNullOrBlank(ChildListAdapter.STUD_IMAGE)) {
            Picasso.with(getActivity())
                    .load(ConstantKeys.IMAGE_URL + ChildListAdapter.STUD_IMAGE)
                    .into((ImageView) v.findViewById(R.id.child_img));
        }
        ((TextView) v.findViewById(R.id.child_name)).setText("" + ChildListAdapter.STUD_NAME.trim());
        ((TextView) v.findViewById(R.id.Fname)).setText("" + ChildListAdapter.STUD_FNAME.trim());
        ((TextView) v.findViewById(R.id.Fathername)).setText("" + ChildListAdapter.STUD_FATHER_NAME.trim());
        ((TextView) v.findViewById(R.id.Grandname)).setText("" + ChildListAdapter.STUD_GRAND_NAME.trim());
        ((TextView) v.findViewById(R.id.FamilyName)).setText("" + ChildListAdapter.STUD_FAMILY_NAME.trim());
        ((TextView) v.findViewById(R.id.Nationality)).setText("" + ChildListAdapter.STUD_NATIONALITY.trim());
        ((TextView) v.findViewById(R.id.bloodtype)).setText("" + ChildListAdapter.STUD_BLOODTYPE.trim());
        ((TextView) v.findViewById(R.id.Gender)).setText("" + ChildListAdapter.STUD_GENDER.trim());
        ((TextView) v.findViewById(R.id.Class)).setText("" + ChildListAdapter.STUD_CLASS.trim());
        ((TextView) v.findViewById(R.id.DateOfBirth)).setText("" + ChildListAdapter.STUD_BIRTHDATE.trim());

        SetGoogleMap();
    }

    private void SetGoogleMap() {
        ArrayList<Marker> markers = new ArrayList<>();
        Marker markerPickup;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = mapFragment.getMap();

        if (!Utility.isStringNullOrBlank(ChildListAdapter.STUD_LAT) && !Utility.isStringNullOrBlank(ChildListAdapter.STUD__LONG)) {
            LatLng currentPosition = new LatLng(Double.parseDouble(ChildListAdapter.STUD_LAT), Double.parseDouble(ChildListAdapter.STUD__LONG));
            markerPickup = mGoogleMap.addMarker(new MarkerOptions()
                    .position(currentPosition)
                    .snippet("")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title(""));
            markerPickup.hideInfoWindow();
            markers.add(markerPickup);
        }

        if (markers.size() > 0) {
           /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            //Change the padding as per needed
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,250,250, 0);
            mGoogleMap.animateCamera(cu);*/
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(ChildListAdapter.STUD_LAT), Double.parseDouble(ChildListAdapter.STUD__LONG))));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}
