package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.db.RunDatabaseHelper;
import com.example.myapplication.manager.RunManager;
import com.example.myapplication.model.Run;

/**
 * Created by liuxi on 2015/12/20.
 */
public class RunListFragment extends ListFragment {
    private static final String TAG = "RunListFragment";
    private static final int REQUEST_NEW_RUN = 0x01;
    private RunDatabaseHelper.RunCursor mCursor;
    private RunManager mRunManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCursor = RunManager.getInstance(getActivity()).queryRuns();
        setListAdapter(new RunCursorAdapter(getActivity(), mCursor));
        setHasOptionsMenu(true);
        mRunManager = RunManager.getInstance(getActivity());
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_RUN) {
            mCursor.requery();
            notifyDataSetChanged();
        }
    }

    private void notifyDataSetChanged() {
        ((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class RunCursorAdapter extends CursorAdapter {
        private RunDatabaseHelper.RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.listitem_run_list, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Run run = mRunCursor.getRun();
            TextView startDateTv = (TextView) view.findViewById(R.id.run_title_tv);
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTv.setText(cellText);
            ImageView trackingIv = (ImageView) view.findViewById(R.id.run_tracking_iv);
            Log.d(TAG, "visible: " + (run.getId() == mRunManager.getCurrentId()));
            if (run.getId() == mRunManager.getCurrentId()) {
                trackingIv.setVisibility(View.VISIBLE);
            } else {
                trackingIv.setVisibility(View.INVISIBLE);
            }
        }
    }
}
