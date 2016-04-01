package com.innotech.imap_taxi.activity;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import com.innotech.imap_taxi3.R;

public class Swipe extends FragmentActivity {

    static SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        //via fragment
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //via view
        //		LayoutInflater inflater = LayoutInflater.from(this);
        //		List<View> pages = new ArrayList<View>();
        //        pages.add(inflater.inflate(R.layout.main_menu_chapter_one, null));
        //        pages.add(inflater.inflate(R.layout.main_menu_chapter_two, null));
        //
        //        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        //        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        //        viewPager.setAdapter(pagerAdapter);
        //        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyImapApp.getInstance().paused();
        Log.wtf("","onPause");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.wtf("","onStop");

        //	MyImapApp.getInstance().paused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("","onDestroy");

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("catchOnResume", "fragId = swipe");
        MyImapApp.getInstance().resumed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSectionsPagerAdapter == null)
            mSectionsPagerAdapter = new SectionsPagerAdapter(
                    getSupportFragmentManager());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment();
            //		    switch (position) {
            //		    case 0:
            //		        return fragment = new MenuFragmentPartOne();
            //		    case 1:
            //		        return fragment = new MenuFragmentPartTwo();
            //		    default:
            //		        break;
            //		    }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    //	public class SamplePagerAdapter extends PagerAdapter{
    //
    //	    List<View> pages = null;
    //
    //	    public SamplePagerAdapter(List<View> pages){
    //	        this.pages = pages;
    //	    }
    //
    //	    @Override
    //	    public Object instantiateItem(View collection, int position){
    //	        View v = pages.get(position);
    //	        ((ViewPager) collection).addView(v, 0);
    //	        return v;
    //	    }
    //
    //	    @Override
    //	    public void destroyItem(View collection, int position, Object view){
    //	        ((ViewPager) collection).removeView((View) view);
    //	    }
    //
    //	    @Override
    //	    public int getCount(){
    //	        return pages.size();
    //	    }
    //
    //	    @Override
    //	    public boolean isViewFromObject(View view, Object object){
    //	        return view.equals(object);
    //	    }
    //
    //	    @Override
    //	    public void finishUpdate(View arg0){
    //	    }
    //
    //	    @Override
    //	    public void restoreState(Parcelable arg0, ClassLoader arg1){
    //	    }
    //
    //	    @Override
    //	    public Parcelable saveState(){
    //	        return null;
    //	    }
    //
    //	    @Override
    //	    public void startUpdate(View arg0){
    //	    }
    //	}

}
