package com.example.blookliu.myapplication.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.example.blookliu.myapplication.criminalintent.model.Crime;

/**
 * Created by BlookLiu on 2015/10/4.
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.OnFragmentInteractionListener, CrimeFragment.OnFragmentInteractionListener {
    private static final String TAG = "CrimeListActivity";

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onItemSelected(Crime crime) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            Intent i = new Intent(this, CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(i);
        } else {
            Log.i(TAG, "tablet mode");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldFragment = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newFragment = CrimeFragment.newInstance(crime.getId());
            if (oldFragment != null) {
                ft.remove(oldFragment);
            }
            ft.add(R.id.detailFragmentContainer, newFragment);
            ft.commit();
        }
    }

    @Override
    public void updateTitle(CharSequence title) {

    }

    @Override
    public void onCrimeUpdate(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment fragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
        fragment.updateUI();
    }
}
