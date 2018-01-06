package net.m3aak.parentapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.m3aak.parentapp.Adapters.ChildListAdapter;
import net.m3aak.parentapp.MyWidgets.CircleImageView;
import net.m3aak.parentapp.NavigationPack.ContentFragmentAdapter;
import net.m3aak.parentapp.SlidingTabs.SlidingTabLayout;
import net.m3aak.parentapp.Utilities.ConstantKeys;
import net.m3aak.parentapp.Utilities.Utility;

/**
 * Created by Android Developer on 12/4/2015.
 */
public class ChildInformation extends AppCompatActivity {

    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_info);

        if (Utility.getSharedPreferences(ChildInformation.this, ConstantKeys.Setting_Language).equals("1")) {
            ViewCompat.setLayoutDirection(findViewById(R.id.child_info_root_view), ViewCompat.LAYOUT_DIRECTION_RTL);
        } else {
            ViewCompat.setLayoutDirection(findViewById(R.id.child_info_root_view), ViewCompat.LAYOUT_DIRECTION_LTR);
        }

       try {
           appContext = this;
           Utility.setSharedPreference(appContext, "WHICHACTIVITY", "ChildInformation");
           MainActivityNew.TabOption = 2;
           init();
           setTabsChildInfo(2);
       }catch (Exception e) {e.printStackTrace();}
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
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.student_info));
        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setImageResource(R.drawable.back);


        ((ImageView) toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConstantKeys.click) {
                    onBackPressed();
                }
            }
        });
        ((TextView) findViewById(R.id.child_name)).setText(ChildListAdapter.STUD_NAME);
        Picasso.with(appContext)
                .load(ConstantKeys.SERVER_URL + "/profile_pic/child.jpg")
                .into(((CircleImageView) findViewById(R.id.child_img)));
    }


    SlidingTabLayout slidingTabLayout;

    private void setTabsChildInfo(int count) {
        ContentFragmentAdapter.titles = getResources().getStringArray(R.array.child_tab);
        Utility.setSharedPreference(appContext, "WHICHACTIVITY", "ChildInformation");
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        ContentFragmentAdapter adapterViewPager = new ContentFragmentAdapter(getSupportFragmentManager(), this, count);
        vpPager.setAdapter(adapterViewPager);
        // adapterViewPager.notifyDataSetChanged();

        slidingTabLayout.setTextColor(getResources().getColor(R.color.tab_text_color));
        slidingTabLayout.setTextColorSelected(getResources().getColor(R.color.tab_text_color_selected));
        slidingTabLayout.setDistributeEvenly();
        slidingTabLayout.setViewPager(vpPager);
        slidingTabLayout.setTabSelected(0);
        // Change indicator color
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#008A97");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utility.setSharedPreference(appContext, "WHICHACTIVITY", "MainActivityNew");
        MainActivityNew.TabOption = 2;
    }
}
