package sean.com.deviceandroid;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Sean on 2016/8/4.
 */
public class ComponentActivity extends PreferenceActivity {
    private static final String TAG = "EditItemFragmentActivity";

    private CharSequence mInitialTitle;
    private int mInitialTitleResId;
    private Bundle mInitialArguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            mInitialArguments = intent.getBundleExtra(FragmentUtils.EXTRA_SHOW_FRAGMENT_ARGUMENTS);
        }
        setTitleFromIntent(intent);
        final String initialFragmentName = intent.getStringExtra(FragmentUtils.EXTRA_SHOW_FRAGMENT);
        switchToFragment(initialFragmentName, mInitialArguments, true, false,
                0, "Title", false);
    }

    private Fragment switchToFragment(String fragmentName, Bundle args, boolean validate,
                                      boolean addToBackStack, int titleResId, CharSequence title, boolean withTransition) {
        if (validate && !isValidFragment(fragmentName)) {
            throw new IllegalArgumentException("Invalid fragment for this activity: "
                    + fragmentName);
        }
        Fragment f = Fragment.instantiate(this, fragmentName, args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, f);

        if (titleResId > 0) {
            transaction.setBreadCrumbTitle(titleResId);
        } else if (title != null) {
            transaction.setBreadCrumbTitle(title);
        }
        transaction.commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
        return f;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // TODO Auto-generated method stub
        return true;
    }

    private void setTitleFromIntent(Intent intent) {
        final int initialTitleResId = intent.getIntExtra(FragmentUtils.EXTRA_SHOW_FRAGMENT_TITLE_RESID, -1);
        if (initialTitleResId > 0) {

            final String initialTitleResPackageName = intent.getStringExtra(
                    FragmentUtils.EXTRA_SHOW_FRAGMENT_TITLE_RES_PACKAGE_NAME);
            if (initialTitleResPackageName != null) {
                try {
                    Context authContext = createPackageContext(initialTitleResPackageName,
                            0 /* flags */);
                    mInitialTitle = authContext.getResources().getText(mInitialTitleResId);
                    setTitle(mInitialTitle);
                    mInitialTitleResId = -1;
                    return;
                } catch (PackageManager.NameNotFoundException e) {

                }
            } else {
                setTitle(mInitialTitleResId);
            }
        } else {
            mInitialTitleResId = -1;
            final String initialTitle = intent.getStringExtra(FragmentUtils.EXTRA_SHOW_FRAGMENT_TITLE);
            mInitialTitle = (initialTitle != null) ? initialTitle : getTitle();
            setTitle(mInitialTitle);
        }
    }
}
