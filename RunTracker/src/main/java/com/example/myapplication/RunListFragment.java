package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.example.myapplication.loader.SQLiteCursorLoader;
import com.example.myapplication.manager.RunManager;
import com.example.myapplication.model.Run;

/**
 * Created by liuxi on 2015/12/20.
 */
public class RunListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "RunListFragment";
    private static final int REQUEST_NEW_RUN = 0x01;
    private static final int RUN_LIST_LOADER_ID = "run_list_loader_id".hashCode();
    private RunDatabaseHelper.RunCursor mCursor;
    private RunManager mRunManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mCursor = RunManager.getInstance(getActivity()).queryRuns();
//        setListAdapter(new RunCursorAdapter(getActivity(), null));
        getLoaderManager().initLoader(RUN_LIST_LOADER_ID, null, this);
        setHasOptionsMenu(true);
        mRunManager = RunManager.getInstance(getActivity());
    }

    @Override
    public void onDestroy() {
//        mCursor.close();
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
            /*mCursor.requery();
            notifyDataSetChanged();*/
            Log.d(TAG, "return from new run");
            getLoaderManager().restartLoader(RUN_LIST_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "create loader");
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "load finished, " + data.isClosed());
        RunCursorAdapter adapter = (RunCursorAdapter) getListAdapter();
        // first start
        if (adapter == null) {
            adapter = new RunCursorAdapter(getActivity(), (RunDatabaseHelper.RunCursor) data);
            setListAdapter(adapter);
        } else {
            adapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "loader reset");
        ((RunCursorAdapter) getListAdapter()).changeCursor(null);
    }

    private void notifyDataSetChanged() {
        if (getListAdapter() != null)
            ((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class RunCursorAdapter extends CursorAdapter {
//        private RunDatabaseHelper.RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
//            mRunCursor = cursor; //
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.listitem_run_list, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
//            Run run = mRunCursor.getRun();
            Run run = ((RunDatabaseHelper.RunCursor) cursor).getRun();
            TextView startDateTv = (TextView) view.findViewById(R.id.run_title_tv);
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTv.setText(cellText);
            ImageView trackingIv = (ImageView) view.findViewById(R.id.run_tracking_iv);
            Log.v(TAG, "visible: " + (run.getId() == mRunManager.getCurrentId()));
            if (run.getId() == mRunManager.getCurrentId()) {
                trackingIv.setVisibility(View.VISIBLE);
            } else {
                trackingIv.setVisibility(View.INVISIBLE);
            }
        }
    }

    private static class RunListCursorLoader extends SQLiteCursorLoader {
        public RunListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            return RunManager.getInstance(getContext()).queryRuns();
        }
    }
}
