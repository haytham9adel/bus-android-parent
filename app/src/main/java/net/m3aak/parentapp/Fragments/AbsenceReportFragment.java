package net.m3aak.parentapp.Fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.m3aak.parentapp.Adapters.ChildListAdapter;
import net.m3aak.parentapp.MyWidgets.CircleImageView;
import net.m3aak.parentapp.NetworkHelperGet;
import net.m3aak.parentapp.R;
import net.m3aak.parentapp.SingleStudentMapActivty;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

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

/**
 * Created by Android Developer on 12/5/2015.
 */
public class AbsenceReportFragment extends Fragment implements View.OnClickListener {

    View v;
    private Context appContext;
    private static final String tag = "AbsenceReportActivity";
    private TextView currentMonth;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private ImageView trackStudent;
    private GridView calendarView;
    int holidaysss, present, absent;
    private GridCellAdapter adapter;
    private JSONArray holidayarray, presendayarray;
    private Calendar _calendar;
    private int month, year;
    private final DateFormat dateFormatter = new DateFormat();
    private static final String dateTemplate = "MMMM yyyy";
    private TextView mFromTxt, mToTxt;
    private Button mActionSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.adsence_layout, null);

        if (Utility.getSharedPreferences(getActivity(), ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(v.findViewById(R.id.absent_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        v.postDelayed(new Runnable() {

            @Override
            public void run() {
              try {
                  appContext = getActivity();
                  ((TextView) getActivity().findViewById(R.id.title)).setText(getString(R.string.student_report));
                  ((TextView) getActivity().findViewById(R.id.child_name1)).setText("" + ChildListAdapter.STUD_NAME);
                  // Log.e("STUD_ID",""+ChildListAdapter.STUD_ID);
                  if (!Utility.isStringNullOrBlank(ChildListAdapter.STUD_IMAGE))
                      Picasso.with(appContext)
                              .load(ConstantKeys.IMAGE_URL + ChildListAdapter.STUD_IMAGE)
                              .into(((CircleImageView) getActivity().findViewById(R.id.child_img1)));
                  init();
              }catch(Exception e) {e.printStackTrace();}
            }

        }, 500);
        return v;
    }

    private void init() {
        ((ImageView) v.findViewById(R.id.trackStudent)).setOnClickListener(this);

        mFromTxt = (TextView) v.findViewById(R.id.from_txt);
        mToTxt = (TextView) v.findViewById(R.id.to_txt);
        mActionSearch = (Button) v.findViewById(R.id.action_search);

        setCalender();

        if (!Utility.isStringNullOrBlank(ChildListAdapter.STUD_ID)) {
            if (Utility.isConnectingToInternet(appContext))
                new GetAttendance().execute(ChildListAdapter.STUD_ID);
            else
                Toast.makeText(appContext, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }

        mFromTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(mFromTxt);
            }
        });

        mToTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(mToTxt);
            }
        });

        mActionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fromStr = mFromTxt.getText().toString().trim();
                String toStr = mToTxt.getText().toString().trim();
                if (fromStr.isEmpty() && toStr.isEmpty()) {

                } else {
                    /*{"start_date":"2016-10-01","end_date":"2016-11-30","school_id":"3","student_id":"4"}*/

                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date fromDate = df.parse(fromStr);
                        Date toDate = df.parse(toStr);

                        if (fromDate.compareTo(toDate) < 0) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("start_date", fromStr);
                                jsonObject.put("end_date", toStr);
                                jsonObject.put("school_id", Utility.getSharedPreferences(getActivity(), ConstantKeys.SCHOOL_ID));
                                jsonObject.put("student_id", ChildListAdapter.STUD_ID);
                                Log.e("search request", jsonObject.toString());
                                new attemptSearch().execute(jsonObject.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.incorrect_dates), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private class attemptSearch extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.wait), getString(R.string.LOADING), false, false);
        }

        @Override
        protected String doInBackground(String... params) {

            NetworkHelperGet networkHelperGet = new NetworkHelperGet(ConstantKeys.SERVER_URL + "get_student_present_absent");
            return networkHelperGet.performPostCall(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            Log.e("responce search ", "" + s);
            /*{"result":"success","absent_day":50,"holiday_days":5,"present_day":5}*/

            try {
                if (s != null && !s.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    if (result.equals("success")) {
                        String absent_day = jsonObject.getString("absent_day");
                        String holiday_days = jsonObject.getString("holiday_days");
                        String present_day = jsonObject.getString("present_day");

                        ((TextView) v.findViewById(R.id.present_days_txt1)).setText("" + present_day);
                        ((TextView) v.findViewById(R.id.absent_days_txt1)).setText("" + absent_day);
                        ((TextView) v.findViewById(R.id.holi_days_txt1)).setText("" + holiday_days);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.data_not_available), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.servernotresponding), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.data_not_available), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void show(final TextView textView) {
        String txt = textView.getText().toString().trim();
        int y, m, d;
        Calendar calendar = Calendar.getInstance();
        if (txt.isEmpty()) {
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String a[] = txt.split("-");
            y = Integer.parseInt(a[0]);
            m = Integer.parseInt(a[1]) - 1;
            d = Integer.parseInt(a[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                textView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, y, m, d);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    /**
     * @param month
     * @param year
     */
    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter(getActivity(),
                R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    private void setTextViewdays() {
        ((TextView) v.findViewById(R.id.present_days_txt)).setText("" + present);
        ((TextView) v.findViewById(R.id.absent_days_txt)).setText("" + absent);
        ((TextView) v.findViewById(R.id.holi_days_txt)).setText("" + holidaysss);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevMonth:
                if (month <= 1) {
                    month = 12;
                    year--;
                } else {
                    month--;
                }
//			Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
                setGridCellAdapterToDate(month, year);
                break;
            case R.id.nextMonth:

                if (month > 11) {
                    month = 1;
                    year++;
                } else {
                    month++;
                }
//			Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
//					+ month + " Year: " + year);
                setGridCellAdapterToDate(month, year);
                break;
            case R.id.trackStudent:
                Log.e("trackStudent", "trackStudent");
                Intent intent = new Intent(getActivity(), SingleStudentMapActivty.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onDestroy() {
//		Log.d(tag, "Destroying View ...");
        super.onDestroy();
    }

    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements View.OnClickListener {
        private static final String tag = "GridCellAdapter";
        private final Context _context;
        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = getResources().getStringArray(R.array.week_name);
        private final String[] months = getResources().getStringArray(R.array.month);
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;
        private final HashMap<String, Integer> eventsPerMonthMap;
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "dd-MMM-yyyy");

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId,
                               int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
//			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
//					+ "Year: " + year);
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
//			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
//			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
//			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());
            // Print Month
            printMonth(month, year);
            setTextViewdays();
            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
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

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        private void printMonth(int mm, int yy) {
//			Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;
            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);
//			Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
//					+ daysInMonth + " days.");
            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
//			Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
//				Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
//						+ prevMonth + " NextMonth: " + nextMonth
//						+ " NextYear: " + nextYear);
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
//				Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
//						+ prevMonth + " NextMonth: " + nextMonth
//						+ " NextYear: " + nextYear);
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
//				Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
//						+ prevMonth + " NextMonth: " + nextMonth
//						+ " NextYear: " + nextYear);
            }
            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;
//			Log.d(tag, "Week Day:" + currentWeekDay + " is "
//					+ getWeekDayAsString(currentWeekDay));
//			Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
//			Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++daysInMonth;
                else if (mm == 3)
                    ++daysInPrevMonth;
            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
//				Log.d(tag,
//						"PREV MONTH:= "
//								+ prevMonth
//								+ " => "
//								+ getMonthAsString(prevMonth)
//								+ " "
//								+ String.valueOf((daysInPrevMonth
//										- trailingSpaces + DAY_OFFSET)
//										+ i));
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i)
                        + "-GREY"
                        + "-"
                        + getMonthAsString(prevMonth)
                        + "-"
                        + prevYear);
            }
            // Current Month Days
            holidaysss = 0;
            present = 0;
            absent = 0;
            for (int i = 1; i <= daysInMonth; i++) {
//				Log.d(currentMonthName, String.valueOf(i) + " "
//						+ getMonthAsString(currentMonth) + " " + yy);
                String da = dayhol_present(i, currentMonth, yy);
                if (da.equals("holiday")) {
                    list.add(String.valueOf(i) + "-HOLD" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                    holidaysss++;
                } else if (da.equals("present")) {
                    list.add(String.valueOf(i) + "-PRESENT" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                    present++;
                } else if (da.equals("futureday")) {
                    list.add(String.valueOf(i) + "-WHITE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                } else {
                    list.add(String.valueOf(i) + "-ABSENT" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                    absentcalculate(i, currentMonth, yy);
                }
            }
            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
//				Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
                list.add(String.valueOf(i + 1) + "-GREY" + "-"
                        + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }

        /**
         * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
         * ALL entries from a SQLite database for that month. Iterate over the
         * List of All entries, and get the dateCreated, which is converted into
         * day.
         *
         * @param year
         * @param month
         * @return
         */
        private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            return map;
        }

        private void absentcalculate(int date, int month, int year) {
            Calendar calendar = Calendar.getInstance();
            //Set the time for the notification to occur.
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, date);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

            } else {
                absent++;
            }
        }

        private void absentcalculate1(int date, int month, int year) {
            Calendar calendar = Calendar.getInstance();
//Set the time for the notification to occur.
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, date);
            absent--;
        }

        private String dayhol_present(int date, int month, int year1) {
            String for_resultdate;
            String for_resultmonth;
            int monthnew = month + 1;
            if (date < 10) {
                for_resultdate = "0" + date;
            } else {
                for_resultdate = "" + date;
            }
            if (monthnew < 10) {
                for_resultmonth = "0" + monthnew;
            } else {
                for_resultmonth = "" + monthnew;
            }
            if (holidayarray != null) {
                for (int i = 0; i < holidayarray.length(); i++) {
                    try {
                        if (holidayarray.getJSONObject(i).getString("holiday_date").equals("" + year1 + "-" + for_resultmonth + "-" + for_resultdate)) {
                            return "holiday";
                        }
                    } catch (Exception ig) {

                    }
                }
            }

            Calendar cal = Calendar.getInstance();
            int todate = cal.get(Calendar.DATE);
            int toyear = cal.get(Calendar.YEAR);
            int tomonth = cal.get(Calendar.MONTH) + 1;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date getDate = format.parse("" + year1 + "-" + for_resultmonth + "-" + for_resultdate);
                Date current = format.parse("" + toyear + "-" + tomonth + "-" + todate);
                if (current.compareTo(getDate) < 0) {
                    return "futureday";
                } else {
                    for (int i = 0; i < presendayarray.length(); i++) {
                        if (presendayarray.getJSONObject(i).getString("date").equals("" + year1 + "-" + for_resultmonth + "-" + for_resultdate)) {
                            return "present";
                        }

                    }
                }
            } catch (Exception ig) {
                ig.printStackTrace();
            }
            return "";
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

//			Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
            String[] day_color = list.get(position).split("-");
            String theday = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];
            if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                if (eventsPerMonthMap.containsKey(theday)) {
                    num_events_per_day = (TextView) row
                            .findViewById(R.id.num_events_per_day);
                    Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                    num_events_per_day.setText(numEvents.toString());
                }
            }
            gridcell.setTextColor(getResources().getColor(R.color.white));
            // Set the Day GridCell
            gridcell.setTag(theday + "-" + themonth + "-" + theyear);
            Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
                    + theyear);
            if (day_color[1].equals("GREY")) {
                gridcell.setBackgroundResource(R.drawable.current);
            }
            if (day_color[1].equals("WHITE")) {
                gridcell.setBackgroundResource(R.drawable.new_current);
                gridcell.setText(theday);
                gridcell.setOnClickListener(this);
            }
            if (day_color[1].equals("HOLD")) {
                gridcell.setText(theday);
                gridcell.setBackgroundResource(R.drawable.holiday);
                gridcell.setOnClickListener(this);
            }
            if (day_color[1].equals("PRESENT")) {
                gridcell.setBackgroundResource(R.drawable.present);
                gridcell.setText(theday);
                gridcell.setOnClickListener(this);
            }
            if (day_color[1].equals("ABSENT")) {
                gridcell.setBackgroundResource(R.drawable.absent);
                gridcell.setText(theday);
                gridcell.setOnClickListener(this);
            }
            if (day_color[1].equals("BLUE")) {
                gridcell.setText(theday);
                gridcell.setOnClickListener(this);
            }
            if (position % 7 == 0) {
                gridcell.setBackgroundResource(R.drawable.sunday);
                gridcell.setTextColor(Color.parseColor("#000000"));
            }
            return row;
        }

        @Override
        public void onClick(View view) {
            String date_month_year = (String) view.getTag();
//			Log.e("Selected date", date_month_year);
            try {
                Date parsedDate = dateFormatter.parse(date_month_year);
//				Log.d(tag, "Parsed Date: " + parsedDate.toString());

            } catch (ParseException e) {
                e.printStackTrace();
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

    /*   ------------------------------>CODE FOR GET ATTENDANCE REPORT<---------------------------------   */
    public class GetAttendance extends AsyncTask<String, String, String> {
        JSONObject networkResponse = null;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.LOADING));
            dialog.setMessage(getString(R.string.wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = ConstantKeys.SERVER_URL + "student_attendance?" + "student_id" + "=" + params[0];
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
            //System.out.println(s);

            try {
                networkResponse = new JSONObject(s);
                Log.e("GetAttendence Response ", "" + networkResponse.toString());
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.norecordfound), Toast.LENGTH_LONG).show();
                } else {
                    if (networkResponse.getString(ConstantKeys.RESULT).equals("success")) {
                        if (networkResponse.getJSONArray("holiday").length() > 0) {
//                            setDB(networkResponse.getJSONArray("child"));
                            holidayarray = networkResponse.getJSONArray("holiday");
                        }
                        if (networkResponse.getJSONArray("present").length() > 0) {
//                            setDB(networkResponse.getJSONArray("child"));
                            presendayarray = networkResponse.getJSONArray("present");
                        }
                        setCalender();
                    } else {
                        Toast.makeText(getActivity(), "" + networkResponse.getString("error"), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("GetAttendance Exce", "" + e);
            }
            ConstantKeys.click = true;
            super.onPostExecute(s);
        }

    }

    private void setCalender() {
        ((GridView) v.findViewById(R.id.days)).setAdapter(new GridCellAdapter1(getActivity(),
                R.id.calendar_day_gridcell));
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
//		Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
//				+ year);
        prevMonth = (ImageView) v.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);
        currentMonth = (TextView) v.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        nextMonth = (ImageView) v.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);
        calendarView = (GridView) v.findViewById(R.id.calendar);
        // Initialised
        adapter = new GridCellAdapter(getActivity(),
                R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            // launch your AsyncTask here, if the task has not been executed yet
            Utility.setSharedPreference(getActivity(), "WHICHACTIVITY", "ChildInformation");
            // init();
        }
    }
}

