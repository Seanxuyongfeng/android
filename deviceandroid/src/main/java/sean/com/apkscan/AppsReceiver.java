package sean.com.apkscan;

/**
 * Created by Sean on 2016/6/15.
 */
import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppsReceiver extends BroadcastReceiver {
    private ArrayList<ApkInfo> apkList;
    private static final String TAG = "AppsReceiver";
    private AppListAdapter mAdapter;
    public AppsReceiver(AppListAdapter adapter, ArrayList<ApkInfo> list){
        apkList = list;
        mAdapter = adapter;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        Log.e(TAG, intent.getDataString().substring(8));
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){

        }else if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
            String packageName = intent.getDataString().substring(8);
            ApkInfo apkInfo = getApkInforByPackageName(packageName);
            if(apkInfo != null){
                apkInfo.installed = true;
                mAdapter.notifyDataSetChanged();
            }
        }else if(intent.getAction().equals("android.intent.action.PACKAGE_CHANGED")){

        }else if(intent.getAction().equals("android.intent.action.PACKAGE_INSTALL")){

        }
    }


    private ApkInfo getApkInforByPackageName(String packageName){
        for(int  i = 0; i <apkList.size(); i++)
        {
            if((apkList.get(i)).packageName.equals(packageName))//假设你的NAME是String类型的
            {
                return apkList.get(i);
            }
        }
        return null;
    }
}
