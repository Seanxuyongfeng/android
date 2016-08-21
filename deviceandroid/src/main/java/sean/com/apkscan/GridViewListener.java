package sean.com.apkscan;

/**
 * Created by Sean on 2016/6/15.
 */

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

public class GridViewListener implements AdapterView.OnItemClickListener{
    private ArrayList<ApkInfo> apkList;
    Context mContext;
    public GridViewListener(Context context, ArrayList<ApkInfo> list){
        apkList = list;
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // TODO Auto-generated method stub
        System.out.println("Click position: " + position + ", id : " + id);
        String fullPath = (String)apkList.get(position).fullPath;
        File file = new File(fullPath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

}
