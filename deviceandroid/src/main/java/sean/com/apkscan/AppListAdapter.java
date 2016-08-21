package sean.com.apkscan;

/**
 * Created by Sean on 2016/6/15.
 */
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import sean.com.deviceandroid.R;

public class AppListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ApkInfo> apkList = new ArrayList<ApkInfo>();

    public AppListAdapter(Context context){
        mContext = context;
    }

    public void addItem(ApkInfo apkInfo){
        apkList.add(apkInfo);
    }

    public ArrayList<ApkInfo> getAdapterData(){
        return apkList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return apkList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return apkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ApkInfo apkInfo = apkList.get(position);
        if(convertView == null && mContext != null){
            View v = LayoutInflater.from(mContext).inflate(R.layout.apkitem, parent, false);
            convertView = v;
        }

        ImageView icon = (ImageView)convertView.findViewById(R.id.apkicon);
        TextView name = (TextView)convertView.findViewById(R.id.apkname);
        TextView apk_state = (TextView)convertView.findViewById(R.id.apkstate);
        if(apkInfo != null){
            icon.setImageDrawable(apkInfo.appIcon);
            name.setText((position+1) + ". " + apkInfo.appName);
            if(apkInfo.installed){
                apk_state.setText(mContext
                        .getResources().getString(R.string.state_installed));
            }else{
                apk_state.setText(mContext
                        .getResources().getString(R.string.state_uninstalled));
            }
        }/*else{
			icon.setImageDrawable(null);//set the default icon
			name.setText("");//unkonwn
			apk_state.setText(mContext.getResources().getString(R.string.state_uninstalled));
		}*/

        return convertView;
    }
}
