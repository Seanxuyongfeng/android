package sean.com.sean.com.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sean on 2016/7/31.
 */
public class Utils {


    public static Account.Type getAccountType(String accountName){
        if(accountName == null){
            return Account.Type.ACCOUNT_TYPE_INVALIDE;
        }

        if(isNumber(accountName)){
            if(accountName.length() == 11 && accountName.startsWith("1")){
                return Account.Type.ACCOUNT_TYPE_PHONENUMBER;
            }
        }else if(isEmailAddress(accountName)){
            return Account.Type.ACCOUNT_TYPE_EMAIL;
        }
        return Account.Type.ACCOUNT_TYPE_INVALIDE;
    }

    private static boolean isEmailAddress(String accountName){
        if(accountName == null){
            return false;
        }
        if(TextUtils.isEmpty(accountName)){
            return false;
        }
        return isValidEmailAddress(accountName);

    }

    private static boolean isValidEmailAddress(String address) {
        Pattern p = Pattern
                .compile("^((\\u0022.+?\\u0022@)|(([\\Q-!#$%&'*+/=?^`{}|~\\E\\w])+(\\.[\\Q-!#$%&'*+/=?^`{}|~\\E\\w]+)*@))"
                        + "((\\[(\\d{1,3}\\.){3}\\d{1,3}\\])|(((?=[0-9a-zA-Z])[-\\w]*(?<=[0-9a-zA-Z])\\.)+[a-zA-Z]{2,6}))$");
        Matcher m = p.matcher(address);
        return m.matches();
    }

    private static boolean isNumber(String accountName){
        if(accountName == null){
            return false;
        }
        if(TextUtils.isEmpty(accountName)){
            return false;
        }
        Pattern pattern = Pattern.compile("^1[34578]{1}[0-9]{1}[0-9]{8}$");
        return pattern.matcher(accountName).matches();
    }


    public static int getAvailableNetWorkType(Context context) {

        int NO_NETWORK_AVAILABLE = -1;
        int netWorkType = NO_NETWORK_AVAILABLE;
        try {
            ConnectivityManager connetManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connetManager == null) {
                return NO_NETWORK_AVAILABLE;
            }
            NetworkInfo[] infos = connetManager.getAllNetworkInfo();
            if (infos == null) {
                return NO_NETWORK_AVAILABLE;
            }
            for (int i = 0; i < infos.length && infos[i] != null; i++){
                if (infos[i].isConnected() && infos[i].isAvailable()) {
                    netWorkType = infos[i].getType();
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return netWorkType;
    }

    public static void showMessage(Context context, int stringId){
        Toast.makeText(context, context.getResources().getText(stringId), Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
