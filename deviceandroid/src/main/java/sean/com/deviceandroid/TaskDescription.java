package sean.com.deviceandroid;

/**
 * Created by Sean on 2016/5/16.
 */
public class TaskDescription {
    final String preferenceKey;

    private String mSummery;

    public TaskDescription(String key){
        preferenceKey = key;
    }

    public void setSummery(String summery){
        mSummery = summery;
    }

    public String getSummery(){
        return mSummery;
    }
}
