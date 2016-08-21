package sean.com.deviceandroid;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Sean on 2016/5/16.
 */
public class DeviceInfoUtils {

    public static String getVersionRelease(){
        return Build.VERSION.RELEASE;
    }

    public static int getSDKVersion(){
        return Build.VERSION.SDK_INT;
    }

    public static String getLcdDensity(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        float lcd_density = displayMetrics.density;
        return String.valueOf(lcd_density) + " " + String.valueOf(width) + "x" + String.valueOf(height);
    }

    public static String getBrand(){
        return Build.BRAND;
    }

    public static String getKernelVersion(){
        String kernelVersion = "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/version");
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return kernelVersion;
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1024 * 8);
        String info = "";
        try {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
                inputStream.close();
            }catch (Exception e){

            }
        }
        try {
            if (!TextUtils.isEmpty(info)) {
                final String keyword = "version";
                int index = info.indexOf(keyword);
                kernelVersion = info.substring(index + keyword.length());
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return kernelVersion;
    }

    public static String getRAMInfo(Context context){
        String availRAM = String.valueOf(getAvailMemory(context));
        String totalRAM = String.valueOf(getTotalMemory());
        return availRAM + " M/" + totalRAM + " M";
    }

    private static long getAvailMemory(Context context){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem/(1024*1024);
    }

    private static long getTotalMemory(){
        long totalRAM = 0L;
        try {
            FileReader fileReader = new FileReader("/proc/meminfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader, 1024*8);
            String firstLine = bufferedReader.readLine();
            String[] stringArray = firstLine.split("\\s+");//one or more space
            totalRAM = Integer.valueOf(stringArray[1]) * 1024;//byte
            bufferedReader.close();

        }catch(Exception e){

        }
        return totalRAM/(1024*1024);
    }


    public static String getROMInfo(){
        String rom = "";
        try{
            File dataPath =  Environment.getDataDirectory();
            StatFs sf = new StatFs(dataPath.getPath());
            long blockSize = sf.getBlockSize();
            long totalBlocks = sf.getBlockCount();
            long availableBlocks = sf.getAvailableBlocks();
            rom = String.valueOf(availableBlocks * blockSize/(1024*1024)) + " M/" + String.valueOf(totalBlocks * blockSize/(1024*1024))+" M";
        }catch (Exception e){

        }
        return rom;
    }

    private static boolean externalStorageAvailable(){
        return android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDCARDInfo(){
        String result = "None";
        try {
            if (externalStorageAvailable()) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdCardDir.getPath());
                long blockSize = sf.getBlockSize();
                long blockNum = sf.getBlockCount();
                long valiBlock = sf.getAvailableBlocks();
                result = String.valueOf(valiBlock * blockSize/(1024*1024)) + " M/" + String.valueOf(blockNum * blockSize/(1024*1024) + " M");
            }else{
                result = "None";
            }
        }catch(Exception e){

        }

        return result;
    }

    public static String getPhoneNumber(Context context){
        return PhoneInfo.getPhoneNumber(context);
    }

    public static String getIMEI(Context context){
        return PhoneInfo.getIMEI(context);
    }

    static class PhoneInfo{
        public static String getPhoneNumber(Context context){
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getLine1Number();
        }

        public static String getIMEI(Context context){
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }
    }

}
