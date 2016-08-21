package sean.com.sean.com.server;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sean.Log.DebugUtils;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import sean.com.deviceandroid.FragmentUtils;
import sean.com.deviceandroid.R;

/**
 * Created by Sean on 2016/7/20.
 */
public class ServerAPIFragment extends PreferenceFragment {
    private static final String TAG = "ServerAPIFragment";

    private TextView mTextView;
    private LayoutInflater mInflater;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void findViewsById(View root){
        mUsernameEdit = (EditText)root.findViewById(R.id.username_edit);
        mPasswordEdit = (EditText)root.findViewById(R.id.password_edit);
        mTextView = (TextView)root.findViewById(R.id.result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View apiRoot = mInflater.inflate(R.layout.api_layout_test, container, false);
        findViewsById(apiRoot);

        mTextView.setText("Server API");

        Button registerBtn = (Button)apiRoot.findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hanleRegister();
                Bundle args = new Bundle();

                FragmentUtils.startPreferencePanel(getActivity(), RegisterFragment.class.getName(),
                        args, 0, RegisterFragment.class.getName(), null, 0);
            }
        });
        Button loginBtn = (Button)apiRoot.findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("暂不支持登录接口");
            }
        });
        return apiRoot;
    }

    private void hanleRegister(){
        String username = mUsernameEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        if(TextUtils.isEmpty(username)){
            mUsernameEdit.requestFocus();
            Utils.showMessage(getActivity(), R.string.tip_username_none);
            return;
        }

        if(TextUtils.isEmpty(password)){
            mPasswordEdit.requestFocus();
            Utils.showMessage(getActivity(), R.string.tip_password_none);
            return;
        }

        if(Utils.getAvailableNetWorkType(getActivity()) == -1){
            Utils.showMessage(getActivity(), R.string.network_wrong_text);
            return;
        }

        if(Utils.getAccountType(username) == Account.Type.ACCOUNT_TYPE_INVALIDE){
            Utils.showMessage(getActivity(), R.string.msg_error_accountname);
            return;
        }
        new UserRegister(username, password).execute();
    }

    private class UserRegister extends AsyncTask<Void, Void, String> {
        private String mUserName;
        private String mPassword;

        public UserRegister(String userName, String pwd) {
            mUserName = userName;
            mPassword = pwd;
        }

        @Override
        protected String doInBackground(Void... params) {
            final Map<String, String> userParams = new HashMap<String, String>();
            userParams.put("username", mUserName);
            userParams.put("password", mPassword);
            String result = null;
            try {
                result = HttpOperation.postRequest(Constants.USER_REGISTER, userParams);
                DebugUtils.i(TAG, "result : " + result);
                JSONObject jsonObject = new JSONObject(result);
                return jsonObject.toString();
            }catch (Exception e){
                if(DebugUtils.DEBUG){
                    DebugUtils.i(TAG, "e : " + e +"" + e.getMessage());
                }
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)){
                mTextView.setText(result);
            }else{
                mTextView.setText("null");
            }
        }
    };
}
