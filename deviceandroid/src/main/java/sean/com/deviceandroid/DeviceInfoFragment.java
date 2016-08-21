package sean.com.deviceandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Sean on 2016/5/15.
 */
public class DeviceInfoFragment extends PreferenceFragment{
    private static final String KEY_ANDROID_VERSION_RELEASE = "android_version_release";
    private static final String KEY_ANDROID_SDK_VERSION = "android_version_sdk";
    private static final String KEY_LCD_DENSITY = "android_lcd_density";
    private static final String KEY_ANDROID_BRAND = "android_brand";
    private static final String KEY_RAM = "ram";
    private static final String KEY_ROM = "rom";
    private static final String KEY_SDCARD = "sdcard";
    private static final String KEY_KERNEL_VERSION = "kernel_version";
    private static final String KEY_PHONENUMBER = "phonenumber";
    private static final String KEY_IMEI = "imei";
    private static final String KEY_SCAN_APKS = "scan_apks";

    private AsyncTask<Void, Void, Void> mTaskLoader;
    private AsyncTask<Void, TaskDescription, Void> mInitPreferenceLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.device_info);
        loadTasksInBackground();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTaskLoader != null){
            mTaskLoader.cancel(false);
            mTaskLoader = null;
        }

        if(mInitPreferenceLoader != null){
            mInitPreferenceLoader.cancel(false);
            mInitPreferenceLoader = null;
        }
    }

    private void loadTasksInBackground(){
        final LinkedBlockingQueue<TaskDescription> mTaskWatingForLoading = new LinkedBlockingQueue<TaskDescription>();
        mTaskLoader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if(findPreference(KEY_ANDROID_VERSION_RELEASE) != null){
                    TaskDescription task = new TaskDescription(KEY_ANDROID_VERSION_RELEASE);
                    task.setSummery(DeviceInfoUtils.getVersionRelease());
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_ANDROID_SDK_VERSION) != null){
                    TaskDescription task = new TaskDescription(KEY_ANDROID_SDK_VERSION);
                    task.setSummery(String.valueOf(DeviceInfoUtils.getSDKVersion()));
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_LCD_DENSITY) != null){
                    TaskDescription task = new TaskDescription(KEY_LCD_DENSITY);
                    task.setSummery(String.valueOf(DeviceInfoUtils.getLcdDensity(getActivity())));
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_ANDROID_BRAND) != null){
                    TaskDescription task = new TaskDescription(KEY_ANDROID_BRAND);
                    task.setSummery(DeviceInfoUtils.getBrand());
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_RAM) != null){
                    TaskDescription task = new TaskDescription(KEY_RAM);
                    task.setSummery(DeviceInfoUtils.getRAMInfo(getActivity()));
                    publicResult(mTaskWatingForLoading, task);
                }

                if(findPreference(KEY_ROM) != null){
                    TaskDescription task = new TaskDescription(KEY_ROM);
                    task.setSummery(DeviceInfoUtils.getROMInfo());
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_SDCARD) != null){
                    TaskDescription task = new TaskDescription(KEY_SDCARD);
                    task.setSummery(DeviceInfoUtils.getSDCARDInfo());
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_KERNEL_VERSION) != null){
                    TaskDescription task = new TaskDescription(KEY_KERNEL_VERSION);
                    task.setSummery(DeviceInfoUtils.getKernelVersion());
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_PHONENUMBER) != null){
                    TaskDescription task = new TaskDescription(KEY_PHONENUMBER);
                    task.setSummery(DeviceInfoUtils.getPhoneNumber(getActivity()));
                    publicResult(mTaskWatingForLoading, task);
                }
                if(findPreference(KEY_IMEI) != null){
                    TaskDescription task = new TaskDescription(KEY_IMEI);
                    task.setSummery(DeviceInfoUtils.getIMEI(getActivity()));
                    publicResult(mTaskWatingForLoading, task);
                }
                return null;
            }
        };
        mTaskLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        initPreferences(mTaskWatingForLoading);
    }

    private void initPreferences(final LinkedBlockingQueue<TaskDescription> taskWatingForLoading){
        mInitPreferenceLoader = new AsyncTask<Void, TaskDescription, Void>() {

            @Override
            protected void onProgressUpdate(TaskDescription... values) {
                if(!isCancelled()){
                    TaskDescription td = values[0];
                    if(td != null){
                        Preference preference = findPreference(td.preferenceKey);
                        preference.setSummary(td.getSummery());
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                while(true) {
                    if (isCancelled()) {
                        break;
                    }

                    TaskDescription td = null;
                    while(td == null){
                        try {
                            td = taskWatingForLoading.take();
                        }catch(InterruptedException e){

                        }
                    }
                    publishProgress(td);
                }

                return null;
            }

        };
        mInitPreferenceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void publicResult(final LinkedBlockingQueue<TaskDescription> blockingQueue, TaskDescription task){
        while(true) {
            try {
                blockingQueue.put(task);
                break;
            }catch(InterruptedException e){

            }
        }
    }

}
