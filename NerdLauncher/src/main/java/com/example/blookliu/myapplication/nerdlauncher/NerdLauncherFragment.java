package com.example.blookliu.myapplication.nerdlauncher;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by BlookLiu on 2015/11/7.
 */
public class NerdLauncherFragment extends ListFragment {
    private static final String TAG = "NerdLauncherFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long start = System.currentTimeMillis();
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pm = getActivity().getPackageManager();
        long start1 = System.currentTimeMillis();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Log.d(TAG, "query intent " + (System.currentTimeMillis() - start1));
        final List<ResolveInfoWrapper> activitiesWrapper = new ArrayList<>();
        Log.i(TAG, String.format("find %s activities.", activities.size()));
        for (ResolveInfo info : activities) {
            Log.v(TAG, String.format("iconRes %d, icon %d", info.getIconResource(), info.icon));
            activitiesWrapper.add(new ResolveInfoWrapper(info));
        }

        AsyncTask<Long, Long, Long> sortTask = new AsyncTask<Long, Long, Long>() {
            @Override
            protected Long doInBackground(Long... params) {
                long start2 = System.currentTimeMillis();
                Collections.sort(activitiesWrapper, new Comparator<ResolveInfoWrapper>() {
                    @Override
                    public int compare(ResolveInfoWrapper lhs, ResolveInfoWrapper rhs) {
                        return String.CASE_INSENSITIVE_ORDER.compare(lhs.mResolveInfo.loadLabel(pm).toString(), rhs.mResolveInfo.loadLabel(pm).toString());
                    }
                });
                return start2;
            }

            @Override
            protected void onPostExecute(Long integer) {
                super.onPostExecute(integer);
                ArrayAdapter<ResolveInfoWrapper> adapter = new ArrayAdapter<ResolveInfoWrapper>(getActivity(), android.R.layout.simple_list_item_1, activitiesWrapper) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        /*if (convertView == null) {
                            convertView = View.inflate(parent.getContext(), R.layout.list_item_img, null);
                        }
                        ImageView appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                        TextView appName = (TextView) convertView.findViewById(R.id.app_name);*/
                        ResolveInfo resolveInfo = getItem(position).mResolveInfo;
                        Drawable icon = resolveInfo.loadIcon(pm);
                        icon.setBounds(0, 0, 45, 45);
                        textView.setCompoundDrawables(icon, null, null, null);
                        textView.setText(resolveInfo.loadLabel(pm));
                        /*appIcon.setImageDrawable(resolveInfo.loadIcon(pm));
                        appName.setText(resolveInfo.loadLabel(pm));*/
                        return textView;
                    }
                };
                setListAdapter(adapter);
                LogDuration(integer, TAG, "compare");
            }
        };
        sortTask.execute();
        /*LogDuration(start2, TAG, "sort list");

        LogDuration(start2, TAG, "render list adapter");*/
        Log.d(TAG, "on create " + (System.currentTimeMillis() - start));
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    private void LogDuration(long startTime, String tag, String wording) {
        Log.d(tag, String.format("%s %d", wording, (System.currentTimeMillis() - startTime)));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfoWrapper resolveInfoWrapper = (ResolveInfoWrapper) l.getAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfoWrapper.mResolveInfo.activityInfo;
        if (activityInfo == null) {
            Log.w(TAG, "empty activity info");
            return;
        }
        Intent i = new Intent(Intent.ACTION_MAIN);
        Log.d(TAG, String.format("packageName1 %s, packageName2 %s", activityInfo.packageName, activityInfo.applicationInfo.packageName));
        i.setClassName(activityInfo.packageName, activityInfo.name);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        List appTasks = am.getAppTasks();
        List runTasks = am.getRunningTasks(200);
        List runProcesses = am.getRunningAppProcesses();
        List runServices = am.getRunningServices(200);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (ActivityManager.AppTask task : am.getAppTasks()) {
                Log.d(TAG, String.format("app task numActivity %d, baseActivity %s, topActivity %s", task.getTaskInfo().numActivities, task.getTaskInfo().baseActivity, task.getTaskInfo().topActivity));
                Log.d(TAG, String.format("app task origActivity %s", task.getTaskInfo().origActivity));
            }
        }
        for (ActivityManager.RunningTaskInfo taskInfo : am.getRunningTasks(200)) {
            Log.d(TAG, String.format("app task numActivity %d, baseActivity %s, topActivity %s, numRunning %d", taskInfo.numActivities, taskInfo.baseActivity, taskInfo.topActivity, taskInfo.numRunning));
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
            Log.d(TAG, String.format("running process reasonCode %s, reasonComponent %s, processName %s", processInfo.importanceReasonCode, processInfo.importanceReasonComponent, processInfo.processName));
        }
        menu.add(Menu.NONE, 1, Menu.NONE, "123");
    }

    class ResolveInfoWrapper {
        ResolveInfo mResolveInfo;

        public ResolveInfoWrapper(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
        }

        @Override
        public String toString() {
            return mResolveInfo.loadLabel(getActivity().getPackageManager()).toString();
        }
    }
}
