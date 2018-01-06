package net.m3aak.parentapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.m3aak.parentapp.Adapters.ChildListAdapter;
import net.m3aak.parentapp.Beans.Student;
import net.m3aak.parentapp.NetworkHelperGet;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAKHAN on 6/12/2015.
 */
public class StudentListFragment extends Fragment {
    View v;
    Context ctxStudList;
    ArrayList<Student> arrayListStudent = new ArrayList<>();
    ChildListAdapter adapterChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.student_list_layout, null);
        ctxStudList = getActivity();

        if (Utility.getSharedPreferences(getActivity(), ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(v.findViewById(R.id.student_list_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }
        v.postDelayed(new Runnable() {

            @Override
            public void run() {
                init();
            }

        }, 1000);
        return v;
    }

    private void init() {
        adapterChild = new ChildListAdapter(getActivity(), arrayListStudent);
        ((ListView) v.findViewById(R.id.child_list)).setAdapter(adapterChild);
        ((ListView) v.findViewById(R.id.child_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Student sn = students.get(position);
                STUD_ID = sn.getStudent_id();
                STUD_NAME = "" + sn.getName() + " " + sn.getS_lname();
                ROUTE_ID = "" + sn.getS_route_id();
                Log.e("ROUTE_ID Student List", "" + ROUTE_ID);
                Log.e("STUD_ID Student List", "" + STUD_ID);
                Log.e("STUD_NAME Student List", "" + STUD_NAME);*/ //to be opened

            }
        });

        if (Utility.isConnectingToInternet(ctxStudList))
            new GetAllStudent1().execute(Utility.getSharedPreferences(ctxStudList, ConstantKeys.USER_ID));
        else
            Toast.makeText(ctxStudList, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // launch your AsyncTask here, if the task has not been executed yet
            Utility.setSharedPreference(getActivity(), "WHICHACTIVITY", "MainActivityNew");
        }
    }

    /*   ------------------------------>CODE FOR GET STUDENT<---------------------------------   */
    public class GetAllStudent1 extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(ctxStudList);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.fetching_list));
            dialog.setMessage(getString(R.string.wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = ConstantKeys.SERVER_URL + "getStudentByParent?" + ConstantKeys.PARENT_ID + "=" + params[0];
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
            System.out.println("All Student " + s);
            Log.e("All Student", "All Student " + s);
/*{
  "result": "success",
  "child": [
    {
      "student_lat": "",
      "gender": "Female",
      "s_lname": "null",
      "grand_name": "123123123",
      "s_parent_id": 14,
      "s_route_id": 5,
      "school_name": "Choithram School",
      "student_id": 6,
      "s_school_id": 1235,
      "s_address": "null",
      "s_contact": "123123",
      "student_class": "18",
      "p_status_id": 1235,
      "nationality": "India",
      "father_name": "asdsadsa",
      "dob": "1999-08-10",
      "blood_type": "B+",
      "student_lng": "",
      "s_email": "31231231@mailinator.com",
      "s_fname": "asdasd",
      "family_name": "asdsadsa",
      "s_image_path": "1231231425342145.png"
    }
  ]
}*/
            try {
                networkResponse = new JSONObject(s);
                // Log.e("All Student",""+networkResponse);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(ctxStudList, ctxStudList.getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        if (networkResponse.getJSONArray("child").length() > 0) {
                            JSONArray child = networkResponse.getJSONArray("child");
                            for (int i = 0; i < child.length(); i++) {
                                Log.d("Insert: ", "Inserting ..");
                                try {
                                    JSONObject jsonObject = child.getJSONObject(i);
                                    Student student = new Student();
                                    student.setID(Integer.parseInt(jsonObject.getString("student_id")));
                                    student.setS_lname(jsonObject.getString("family_name"));
                                    student.setS_grand_name(jsonObject.getString("grand_name"));
                                    student.setS_route_id(jsonObject.getString("s_route_id"));
                                    student.setS_school_name(jsonObject.getString("school_name"));
                                    student.setS_address(jsonObject.getString("s_address"));
                                    student.setS_contact(jsonObject.getString("s_contact"));
                                    student.setS_nationality(jsonObject.getString("nationality"));
                                    student.setS_father_name(jsonObject.getString("father_name"));
                                    student.setS_blood_type(jsonObject.getString("blood_type"));
                                    student.setS_email(jsonObject.getString("s_email"));
                                    student.setS_fname(jsonObject.getString("s_fname"));
                                    student.setS_family_name(jsonObject.getString("family_name"));
                                    student.setS_s_image_path(jsonObject.getString("s_image_path"));
                                    student.setS_gender(jsonObject.getString("gender"));
                                    student.setS_latitude(jsonObject.getString("student_lat"));
                                    student.setS_class(jsonObject.getString("student_class"));
                                    student.setS_dob(jsonObject.getString("dob"));
                                    student.setS_longitude(jsonObject.getString("student_lng"));
                                    student.setS_BlinkStatus(jsonObject.getString("blink_status"));
                                    student.setDriverName(jsonObject.getString("s_zip"));
                                    student.setDriverNumber(jsonObject.getString("s_pass"));

                                    Utility.setSharedPreference(ctxStudList, ConstantKeys.ROUTEID, jsonObject.getString("s_route_id"));

                                    arrayListStudent.add(student);

                                } catch (JSONException e) {
                                    Log.e("JSONException", "" + e);
                                }
                            }
                            adapterChild.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ctxStudList, "" + networkResponse.getString("error"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("GetAllStudent Exc", "" + e);
            }
            super.onPostExecute(s);
        }
    }
}