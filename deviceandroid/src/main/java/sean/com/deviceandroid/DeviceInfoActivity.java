package sean.com.deviceandroid;

import android.app.Activity;
import android.os.Bundle;

public class DeviceInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new DeviceInfoFragment()).commit();
    }




}