package net.m3aak.parentapp.NavigationPack;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.m3aak.parentapp.Fragments.AbsenceReportFragment;
import net.m3aak.parentapp.Fragments.SingleStudentMapFragment;
import net.m3aak.parentapp.Fragments.StudentListFragment;
import net.m3aak.parentapp.Fragments.StudentOtherDeatilFragment;
import net.m3aak.parentapp.MainActivityNew;

/**
 * Copyright (C) 2015 Mustafa Ozcan
 * Created on 06 May 2015 (www.mustafaozcan.net)
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * *
 * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ContentFragmentAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 1;
    private final Context c;
   // private String[] titles = {"Bus Map", "Student List"};
   public static String[] titles ;//= new String[]{"Student List", "Bus Map"};


    public ContentFragmentAdapter(FragmentManager fragmentManager, Context context, int item_count) {
        super(fragmentManager);
        NUM_ITEMS = item_count;
        c = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //if(Utility.getSharedPreferences(c,"WHICHACTIVITY")=="MainActivityNew")
                if(MainActivityNew.TabOption==1)
            {
                return new StudentListFragment();
           }
           else
                {
                 //   Log.d("else :","else");
                    return new AbsenceReportFragment();
                }

            case 1:
               // if(Utility.getSharedPreferences(c,"WHICHACTIVITY")=="MainActivityNew")
                if(MainActivityNew.TabOption==1)
                {
                    return new SingleStudentMapFragment();

               }
                else {
                   // Log.d("else :","else");
                    return new StudentOtherDeatilFragment();
                }

            default:
                return null;
        }



    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
