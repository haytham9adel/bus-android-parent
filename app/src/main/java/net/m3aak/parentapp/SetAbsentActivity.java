package net.m3aak.parentapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.m3aak.parentapp.Beans.Student;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*{"student_id":"123","absent_date":"2016-05-06,2016-11-11,2016-11-12"}*/
public class SetAbsentActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView currentMonth;
    private ImageView prevMonth;
    private ImageView nextMonth, backImg;
    private GridView calendarView, days;
    private GridCellAdapter adapter;
    private Calendar _calendar;
    private int month, year;
    private static final String dateTemplate = "MMMM yyyy";
    private static final String todaydateTemplate = "yyyy-MM-dd";
    String flag = "abc";
    String date_month_year;
    private ArrayList<String> selectedDateList = new ArrayList<>();
    private AppCompatSpinner appCompatSpinner;
    private Context ctxStudList;
    ArrayList<Student> arrayListStudent = new ArrayList<>();
    ArrayList<String> arrayListStudentStr = new ArrayList<>();
    private String mSelectedStudentId = "";
    private String todayDate = "";
    private EditText reasonEdt;

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // navigation bar height
            int navigationBarHeight = 0;
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // status bar height
            int statusBarHeight = 0;
            resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // display window size for the app layout
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
            int keyboardHeight = findViewById(R.id.activity_set_absent).getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height());

            if (keyboardHeight <= 0) {
                isOpen(false);
            } else {
                isOpen(true);
            }
        }
    };


    private void isOpen(boolean hasOpen) {
        if (hasOpen && isOpenKeyboard) {
            focusOnView();
            isOpenKeyboard = false;
        } else if (!hasOpen) {
            isOpenKeyboard = true;
        }
    }

    private boolean isOpenKeyboard = true;

    private final void focusOnView() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
                scrollView.fullScroll(View.FOCUS_DOWN);
                reasonEdt.requestFocus();
            }
        });
    }

    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_absent);
        ctxStudList = this;

        if (Utility.getSharedPreferences(ctxStudList, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.activity_set_absent), ViewCompat.LAYOUT_DIRECTION_RTL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.activity_set_absent).getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);

        appCompatSpinner = (AppCompatSpinner) findViewById(R.id.child_dropdown);
        prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

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
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.report_absent));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reasonEdt = (EditText) findViewById(R.id.reason_edt);
        currentMonth = (TextView) this.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        todayDate = DateFormat.format(todaydateTemplate, _calendar.getTime()).toString();

        nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        findViewById(R.id.action_submit).setOnClickListener(this);

        calendarView = (GridView) this.findViewById(R.id.calendar);
        days = (GridView) this.findViewById(R.id.days);

        // Initialised
        adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        // Initialised
        GridCellAdapter1 adapter = new GridCellAdapter1(getApplicationContext(), R.id.calendar_day_gridcell);
        adapter.notifyDataSetChanged();
        days.setAdapter(adapter);

        reasonEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptSubmit();
                }

                return false;
            }
        });

        if (Utility.isConnectingToInternet(SetAbsentActivity.this))
            new GetAllStudent1().execute(Utility.getSharedPreferences(SetAbsentActivity.this, ConstantKeys.USER_ID));
        else
            Toast.makeText(SetAbsentActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();


        appCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedStudentId = "" + arrayListStudent.get(position).getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class submitAbsentTask extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(ctxStudList);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.updating_attendance));
            dialog.setMessage(getString(R.string.wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = ConstantKeys.SERVER_URL + "add_student_absent";
            NetworkHelperGet putRequest = new NetworkHelperGet(URL);
            try {
                return putRequest.performPostCall(params[0]);
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.e("submit responce", "" + s);
            /*{"responseMessage":"absent has been added successfully","result":"success","student_id":"4"}*/
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(ctxStudList, getString(R.string.servernotresponding), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        Toast.makeText(ctxStudList, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ctxStudList, "" + networkResponse.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                // Toast.makeText(appContext, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                Log.e("ForgotPasswordTak Exc", "" + e);
            }
            super.onPostExecute(s);
        }
    }

    /*   ------------------------------>CODE FOR GET STUDENT<---------------------------------   */
    public class GetAllStudent1 extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(ctxStudList);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.fetching));
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
                                    student.setS_lname(jsonObject.getString("s_lname"));
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

                                    Utility.setSharedPreference(ctxStudList, ConstantKeys.ROUTEID, jsonObject.getString("s_route_id"));

                                    arrayListStudent.add(student);
                                    arrayListStudentStr.add(jsonObject.optString("s_fname", "") + " " + jsonObject.optString("s_lname", "") + " " + jsonObject.optString("family_name", ""));
                                } catch (JSONException e) {
                                    Log.e("JSONException", "" + e);
                                }
                            }
                            ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(ctxStudList, android.R.layout.simple_spinner_dropdown_item, arrayListStudentStr);
                            appCompatSpinner.setAdapter(stringArrayAdapter);
                            // new GetRoutes1().execute();
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

    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else
                month--;
            //  selectedDateList.clear();
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else
                month++;
            //    selectedDateList.clear();
            setGridCellAdapterToDate(month, year);
        }

        if (v.getId() == R.id.action_submit) {
            attemptSubmit();
        }
    }


    private void attemptSubmit() {
        if (selectedDateList != null && selectedDateList.size() != 0) {
                /*{"student_id":"123","absent_date":"2016-05-06,2016-11-11,2016-11-12"}*/
            try {

                for (int i = 0; i < selectedDateList.size(); i++) {
                    String date[] = selectedDateList.get(i).split("-");
                    /*theyear + "-" + themonth + "-" + theday*/
                    String day = date[2];
                    if (day.length() == 1) {
                        day = "0" + day;
                        selectedDateList.set(i, date[0] + "-" + date[1] + "-" + day);
                    }
                }

                String reason = reasonEdt.getText().toString().trim();
                if (!reason.isEmpty()) {

                    String dates = StringUtils.join(selectedDateList, ",");

                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("student_id", mSelectedStudentId);
                    jsonObject.put("absent_date", dates);
                    jsonObject.put("reason", reason);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SetAbsentActivity.this);
                    builder.setTitle(getString(R.string.please_confirm));
                    builder.setMessage(getString(R.string.notgoingschool));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.continue_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new submitAbsentTask().execute(jsonObject.toString());
                        }
                    });

                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    Toast.makeText(SetAbsentActivity.this, getString(R.string.reason), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SetAbsentActivity.this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SetAbsentActivity.this, getString(R.string.selectdates), Toast.LENGTH_SHORT).show();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////
    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements View.OnClickListener {
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;
        private final HashMap<String, Integer> eventsPerMonthMap;

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId, int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        private void printMonth(int mm, int yy) {
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);


            // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
                ++daysInMonth;
            }

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                if (i == getCurrentDayOfMonth())
                    list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                else
                    list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }

        private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            return map;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
           try {
               if (row == null) {
                   LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
               }

               // Get a reference to the Day gridcell
               gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
               gridcell.setOnClickListener(this);

               // ACCOUNT FOR SPACING
               String[] day_color = list.get(position).split("-");
               String theday = day_color[0];
               String themonth = day_color[2];
               String theyear = day_color[3];
               if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                   if (eventsPerMonthMap.containsKey(theday)) {
                       num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
                       Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                       num_events_per_day.setText(numEvents.toString());
                   }
               }

               // Set the Day GridCell
               gridcell.setText(theday);
               gridcell.setTag(theyear + "-" + themonth + "-" + theday);

               if (day_color[1].equals("GREY"))
                   gridcell.setBackgroundColor(Color.LTGRAY);

               if (day_color[1].equals("WHITE"))
                   gridcell.setBackgroundColor(Color.WHITE);

               if (day_color[1].equals("BLUE"))
                   gridcell.setBackgroundColor(Color.BLACK);


               if (selectedDateList.contains(theyear + "-" + themonth + "-" + theday)) {
                   gridcell.setBackgroundColor(Color.RED);
                   gridcell.setTextColor(Color.WHITE);
               } else {
                   gridcell.setBackgroundColor(Color.WHITE);
                   gridcell.setTextColor(Color.BLACK);

                   if (theday.length() == 1) {
                       theday = "0" + theday;
                   }
                   if (todayDate.equals(theyear + "-" + themonth + "-" + theday)) {
                       gridcell.setBackgroundColor(Color.GREEN);
                       gridcell.setTextColor(Color.WHITE);
                   }
               }

           }catch (Exception e) {e.printStackTrace();}
            return row;
        }

        @Override
        public void onClick(View view) {
            date_month_year = (String) view.getTag();
            ////

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(date_month_year);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(convertedDate);
            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -1);
                Date dateBefore1Days = cal.getTime();
                if (convertedDate.after(dateBefore1Days)) {
                    if (!selectedDateList.contains(date_month_year)) {
                        selectedDateList.add(date_month_year);
                    } else {
                        try {
                            selectedDateList.remove(date_month_year);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }


    public class GridCellAdapter1 extends BaseAdapter {
        private final Context _context;
        private final String[] weekdaysno = new String[]{"S", "M", "T",
                "W", "T", "F", "S"};

        private Button gridcell;

        // Days in Current Month
        public GridCellAdapter1(Context context, int textViewResourceId) {
            super();
            this._context = context;
        }

        @Override
        public int getCount() {
            return weekdaysno.length;
        }

        @Override
        public String getItem(int position) {
            return weekdaysno[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.screen_gridcell, parent, false);
            }
            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            // ACCOUNT FOR SPACING
            gridcell.setTextColor(getResources().getColor(R.color.white));
            gridcell.setBackgroundResource(R.drawable.daysname);
            // Set the Day GridCell
            gridcell.setText(getItem(position));
            return row;
        }

    }
}