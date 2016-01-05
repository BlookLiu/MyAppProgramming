package com.example.myapplication.manager;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.example.myapplication.db.RunDatabaseHelper;
import com.example.myapplication.model.Run;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by liuxi on 2015/12/15.
 */
public class RunManager {
    private static final String TAG = "RunManager";
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    public static final String ACTION_LOCATION = "com.example.myapplication.runtracker" +
            ".ACTION_LOCATION";
    public static final int REQ_CHECK_LOCATION_PERMISSION = 0x01;
    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mSharedPreferences;
    private long mCurrentId;

    //baidu定位
    private static LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        private BDLocation preLoc = new BDLocation();

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d(TAG, "receive baidu location");
            final BDLocation t = preLoc;
            preLoc = bdLocation;
            if (bdLocation.getTime().equals(t.getTime()) && bdLocation.getAltitude() == t
                    .getAltitude() && bdLocation.getLatitude() == t.getLatitude() && bdLocation
                    .getLongitude() == t.getLongitude()) {
                Log.d(TAG, "onReceiveLocation: same location skip");
                return;
            }
            Location location = BDLoc2Loc(bdLocation);
            if (location != null)
                broadcastLocation(location);
        }
    };

    private RunManager(Context context) {
        mAppContext = context;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(context);
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentId = mSharedPreferences.getLong(PREF_CURRENT_RUN_ID, -1);

        mLocationClient = new LocationClient(context.getApplicationContext());

    }

    public static RunManager getInstance(Context context) {
        if (sRunManager == null) {
            sRunManager = new RunManager(context);
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent intent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, intent, flags);
    }

    @SuppressWarnings("checkPermission")
    public void startLocationUpdates() {
        Log.d(TAG, "start location update");
        String provider = LocationManager.GPS_PROVIDER;
        /*if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager
                .isProviderEnabled(TEST_PROVIDER)) {
            Log.d(TAG, "using test provider");
            provider = TEST_PROVIDER;
        }*/
        // check permission
        if (ActivityCompat.checkSelfPermission(mAppContext, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(mAppContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (mAppContext instanceof Activity) {
                Activity activity = (Activity) mAppContext;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQ_CHECK_LOCATION_PERMISSION);
            }
            return;
        }

        Location lastLoc = mLocationManager.getLastKnownLocation(provider);
        if (lastLoc != null) {
            Log.d(TAG, "get last location: " + lastLoc.toString());
            lastLoc.setTime(System.currentTimeMillis());
            broadcastLocation(lastLoc);
        }
        PendingIntent pendingIntent = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pendingIntent);
    }

    /**
     * 百度定位调用方法
     *
     * @param type
     */
    public void startLocationUpdates(int type) {
        Log.d(TAG, "start baidu location");
        BDLocation lastLoc = mLocationClient.getLastKnownLocation();
        if (lastLoc != null) {
            Location loc = BDLoc2Loc(lastLoc);
            broadcastLocation(loc);
        }
        mLocationClient.registerLocationListener(mBDLocationListener);
        initLocation();
        mLocationClient.start();
    }

    private void broadcastLocation(Location lastLoc) {
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, lastLoc);
        mAppContext.sendBroadcast(intent);
    }

    public void stopLocationUpdates() {
        PendingIntent pendingIntent = getLocationPendingIntent(false);
        if (pendingIntent != null) {
            mLocationManager.removeUpdates(pendingIntent);
            pendingIntent.cancel();
        }

        if (mLocationClient != null)
            mLocationClient.stop();
    }

    public boolean isTrackingRun() {
//        return getLocationPendingIntent(false) != null;
        return mLocationClient.isStarted();
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentId;
    }

    public Run startNewRun() {
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void stopRun() {
        stopLocationUpdates();
        mCurrentId = -1;
        mSharedPreferences.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    public void insertLocation(Location location) {
        if (mCurrentId != -1) {
            mHelper.insertLocation(mCurrentId, location);
        } else {
            Log.e(TAG, "location received with no tracking run; ignore.");
        }
    }

    public void startTrackingRun(Run run) {
        mCurrentId = run.getId();
        mSharedPreferences.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentId).commit();
        startLocationUpdates(1);
    }

    private Run insertRun() {
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public RunDatabaseHelper.RunCursor queryRuns() {
        return mHelper.queryRuns();
    }

    public Run getRun(long runId) {
        Run run = null;
        RunDatabaseHelper.RunCursor runCursor = mHelper.queryRun(runId);
        runCursor.moveToFirst();
        if (!runCursor.isAfterLast()) {
            run = runCursor.getRun();
        }
        runCursor.close();
        return run;
    }

    public Location getLastLocationForRun(long runId) {
        Location location = null;
        RunDatabaseHelper.LocationCursor locationCursor = mHelper.queryLastLocationForRun(runId);
        locationCursor.moveToFirst();
        if (!locationCursor.isAfterLast()) {
            location = locationCursor.getLocation();
        }
        locationCursor.close();
        return location;
    }

    public RunDatabaseHelper.LocationCursor queryLocationsForRun(long runId) {
        return mHelper.queryLocationsForRun(runId);
    }

    public long getCurrentId() {
        return mCurrentId;
    }

    public Location BDLoc2Loc(BDLocation bdLoc) {
        printLocMsg(bdLoc);
        if (bdLoc.getLocType() != BDLocation.TypeGpsLocation && bdLoc.getLocType() != BDLocation
                .TypeNetWorkLocation && bdLoc.getLocType() != BDLocation.TypeOffLineLocation) {
            Log.w(TAG, "location convert failed, code: " + bdLoc.getLocType());
            return null;
        }
        Location resultLoc = new Location("BAIDU_PROVIDER");
        resultLoc.setAltitude(bdLoc.getAltitude());
        resultLoc.setLatitude(bdLoc.getLatitude());
        resultLoc.setLongitude(bdLoc.getLongitude());
        resultLoc.setTime(convertTime(bdLoc.getTime()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            resultLoc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        resultLoc.setSpeed(bdLoc.getSpeed());
        return resultLoc;
    }

    public long convertTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(time).getTime();
        } catch (ParseException e) {
            Log.e(TAG, "convert time error: ", e);
        }
        return 0;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType(tempcoor);//可选，默认gcj02，设置返回的定位结果坐标系，
        int span = 2000;
        /*try {
            span = Integer.valueOf(frequence.getText().toString());
        } catch (Exception e) {
            // TODO: handle exception
        }*/
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    private void printLocMsg(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
            if (location.getNetworkLocationType().equals("wf")) {
                sb.append("\nprovider: wifi");
            } else if (location.getNetworkLocationType().equals("cl")) {
                sb.append("\nprovider: cell");
            } else if (location.getNetworkLocationType().equals("ll")) {
                sb.append("\nprivider: gps");
            }
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");// 位置语义化信息
        sb.append(location.getLocationDescribe());
        List<Poi> list = location.getPoiList();// POI信息
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        Log.d(TAG, "BDLocation: " + sb.toString());
    }
}
