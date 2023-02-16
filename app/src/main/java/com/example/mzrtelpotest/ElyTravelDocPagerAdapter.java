package com.example.mzrtelpotest;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ElyTravelDocPagerAdapter extends FragmentPagerAdapter {

    public ElyTravelDocPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch(position) {
            case 0:
                return ElyTravelDocFragment.newInstance(position + 1);
            case 1:
                return ElyTravelDocLogFragment.newInstance(position + 1);
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Ely Travel Document";
            case 1:
                return "Ely Travel Document Log";
        }
        return null;
    }
}