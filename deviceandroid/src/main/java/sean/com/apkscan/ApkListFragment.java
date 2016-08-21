package sean.com.apkscan;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import java.io.File;
import android.app.Activity;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import sean.com.deviceandroid.R;
import sean.com.deviceandroid.TaskWorker;

/**
 * Created by Sean on 2016/6/12.
 */
public class ApkListFragment extends PreferenceFragment {
    private static final String TAG = "ApkListFragment";

    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;

    private GridView mGridView;
    private AppListAdapter adapter = null;
    private AppsReceiver appsReceiver;
    private TextView textToast;

    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.apk_list_main, container, false);
        mGridView = (GridView)view.findViewById(R.id.applist);
        textToast = (TextView)view.findViewById(R.id.textToast);
        adapter = new AppListAdapter(getActivity());
        setAdapters();
        setListeners();
        registerReceivers(getActivity());

        if(mExternalStorageAvailable){
            LoadDataTask loadData = new LoadDataTask(getActivity());
            loadData.execute(0, 0, 0);
        }else{
            mGridView.setVisibility(View.GONE);
            view.findViewById(R.id.textToast).setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateExternalStorageState();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterReceivers(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void registerReceivers(Context context){
        appsReceiver = new AppsReceiver(adapter,adapter.getAdapterData());
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addAction("android.intent.action.PACKAGE_INSTALL");
        filter.addDataScheme("package");
        context.registerReceiver(appsReceiver, filter);
    }

    private void unRegisterReceivers(Context context){
        if(appsReceiver != null)
            context.unregisterReceiver(appsReceiver);
    }

    private void setListeners(){
        if(mGridView != null){
            mGridView.setOnItemClickListener(new GridViewListener(getActivity(),
                    adapter.getAdapterData()));
        }
    }

    private void setAdapters(){
        if(adapter != null){
            mGridView.setAdapter(adapter);
        }else{
            Log.e(TAG, "appList adapter null");
        }
    }

    private void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    class LoadDataTask extends AsyncTask<Object, Integer, Long>{
        private Context mContext;
        public LoadDataTask(Context context){
            mContext = context;
        }

        @Override
        protected Long doInBackground(Object... arg0) {
            // TODO Auto-generated method stub
            if(mExternalStorageAvailable){
                ParserApk parserApk = new ParserApk(getActivity(), adapter, mHandler);
            }

            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
            if(adapter.getCount() != 0){
                mGridView.setVisibility(View.VISIBLE);
            }else{
                mGridView.setVisibility(View.GONE);
                textToast.setText(mContext.getResources()
                        .getString(R.string.empty_folder).toString());
                textToast.setVisibility(View.VISIBLE);
            }
        }
    }
}
