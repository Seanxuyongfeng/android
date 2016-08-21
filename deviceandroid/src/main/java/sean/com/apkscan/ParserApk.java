package sean.com.apkscan;

/**
 * Created by Sean on 2016/6/15.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
//import android.content.pm.PackageParser;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;

import sean.com.deviceandroid.R;

public class ParserApk {
    private Context mContext;
    static final int SCAN_MONITOR = 1<<0;
    static final int SCAN_NO_DEX = 1<<1;
    static final int SCAN_NO_PATHS = 1<<5;
    private final ArrayList<ApkInfo> allApk ;
    private final Handler mHandler;
    private AppListAdapter mAdapter;

    public ParserApk(Context context, AppListAdapter adapter, Handler handler){
        mContext = context;
        mAdapter = adapter;
        allApk = adapter.getAdapterData();
        mHandler = handler;
        File sdCardDir = Environment.getExternalStorageDirectory();
        scanDirLI(sdCardDir,/* PackageParser.PARSE_IS_SYSTEM
                        | PackageParser.PARSE_IS_SYSTEM_DIR*/0,
                SCAN_MONITOR | SCAN_NO_PATHS | SCAN_NO_DEX, 0L);
    }

    public ArrayList<ApkInfo> getParseResult(){
        return allApk;
    }

    private void scanDirLI(File dir, int flags, int scanMode, long currentTime) {
        if(isdirExist(dir.toString()) == false){

            return ;
        }
        String[] files = dir.list();
        if (files == null) {

            return;
        }

        Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
        for(int i = 0; i < files.length; i++){
            File file = new File(dir, files[i]);

            if(!isApkFilename(files[i])){
                //if the file is not the apk file, ignore it
                continue;
            }
            scanPackageLI(file, flags, scanMode, currentTime);
        }
    }

    private void scanPackageLI(File scanFile, int parseFlags,
                               int scanMode, long currentTime){
        String scanPath = scanFile.getPath();
        getInfoFromApkFileOnSdCard(scanPath);
    }

    private static final boolean isApkFilename(String name) {
        return name != null && name.endsWith(".apk");
    }

    protected void getInfoFromApkFileOnSdCard(String fileName) {
        final PackageManager pm = mContext.getPackageManager();
        String fullPath = fileName;
        int lastSlash = fileName.lastIndexOf('/');
        String apkFileName = "";
        if(lastSlash > 0){
            lastSlash++;
            if(lastSlash < fileName.length()){
                apkFileName = fileName.substring(lastSlash);
            }
        }
        int lastDot = apkFileName.lastIndexOf('.');
        if (lastDot > 0) {
            apkFileName = apkFileName.substring(0, lastDot);
        }

        PackageInfo packageInfo = pm.getPackageArchiveInfo(fullPath, 0);

        Resources pRes = mContext.getResources();
        AssetManager assmgr = new AssetManager();
        assmgr.addAssetPath(fullPath);
        Resources res = new Resources(assmgr,
                pRes.getDisplayMetrics(),
                pRes.getConfiguration());
        ApkInfo apkInfo = new ApkInfo();
        allApk.add(apkInfo);
        apkInfo.packageInfo = packageInfo;
        apkInfo.res = res;
        apkInfo.fullPath = fullPath;
        String label;Drawable icon;
        if(packageInfo != null){
            if(packageInfo.applicationInfo.labelRes != 0){
                label = res.getText(packageInfo.applicationInfo.labelRes).toString();
            }else{
                label = apkFileName;
            }

            if(packageInfo.applicationInfo.icon != 0){
                icon = res.getDrawable(packageInfo.applicationInfo.icon);
            }else{
                icon = pRes.getDrawable(R.drawable.icon_default);
            }

            apkInfo.appIcon = icon;
            apkInfo.appName = label;
            apkInfo.packageName = (packageInfo.packageName == null)?packageInfo
                    .applicationInfo.packageName : packageInfo.packageName;
            if(packageInfo.packageName != null){
                apkInfo.installed = checkApkStateByPackageName(mContext,apkInfo.packageName);
            }else{
                apkInfo.installed = checkApkStateByPackageName(mContext,null);
            }

        }else{
            //文件损坏
            apkInfo.appName = apkFileName;
            apkInfo.appIcon = pRes.getDrawable(R.drawable.icon_default);
            apkInfo.packageName = null;
            apkInfo.installed = checkApkStateByPackageName(mContext,null);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    private boolean checkApkStateByPackageName(Context context, String packageName){
        if (packageName == null || "".equals(packageName)){
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private boolean isdirExist(String dir){
        File file = new File(dir);

        if (!file.exists()){

            if (file.mkdir()) {

                return true;
            }else{

                return false;
            }
        }
        return true;
    }
}
class ApkInfo{
    Resources res;
    String fullPath;
    PackageInfo packageInfo;
    String packageName;
    Drawable appIcon;
    String appName;
    boolean installed = false;
}
