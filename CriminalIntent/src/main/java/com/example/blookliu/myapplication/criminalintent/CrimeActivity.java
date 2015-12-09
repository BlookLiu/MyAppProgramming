package com.example.blookliu.myapplication.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.blookliu.myapplication.criminalintent.model.Crime;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity implements CrimeFragment.OnFragmentInteractionListener{
    private static final String TAG = "CrimeActivity";

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new CrimeFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }*/

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }

    @Override
    public void updateTitle(CharSequence title) {
        setTitle(title);
    }

    @Override
    public void onCrimeUpdate(Crime crime) {

    }
}
