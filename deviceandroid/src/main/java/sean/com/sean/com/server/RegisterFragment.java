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

import com.sean.Log.DebugUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sean.com.deviceandroid.R;
/**
 * Created by Sean on 2016/8/4.
 */
public class RegisterFragment extends PreferenceFragment {
    private static final String TAG = "RegisterFragment";
    private LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = mInflater.inflate(R.layout.register_layout, container, false);
        final EditText usernameEditText = (EditText) view.findViewById(R.id.username);
        final EditText ageEditText = (EditText) view.findViewById(R.id.age);
        final EditText nicknameEditText = (EditText) view.findViewById(R.id.nickname);
        final EditText birthEditText =  (EditText) view.findViewById(R.id.birthday);
        Button button = (Button)view.findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String age = ageEditText.getText().toString().trim();
                String nickName = nicknameEditText.getText().toString().trim();
                String birthday = birthEditText.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    Utils.showMessage(getActivity(), R.string.tip_username_none);
                    return;
                }
                final Map<String, String> userParams = new HashMap<String, String>();
                userParams.put("username", username);
                userParams.put("password", username);
                userParams.put("age", age);
                userParams.put("nickname", nickName);
                userParams.put("birthday", birthday);
                new UserRegister(userParams).execute();
            }

        });
        return view;
    }

    private class UserRegister extends AsyncTask<Void, Void, String> {
        private Map<String, String> mUserParams;

        public UserRegister(Map<String, String> userParams) {
            mUserParams = userParams;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            try {
                result = HttpOperation.postRequest(Constants.USER_REGISTER, mUserParams);
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
            Utils.showMessage(getActivity(), result);
            if (!TextUtils.isEmpty(result)){

            }else{
               Utils.showMessage(getActivity(), "结果为空");
            }
        }
    };
}
