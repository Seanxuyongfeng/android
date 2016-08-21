package sean.com.deviceandroid;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Sean on 2016/8/4.
 */
public class FragmentUtils {
    public static final String EXTRA_SHOW_FRAGMENT = ":settings:show_fragment";
    public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":settings:show_fragment_title";
    public static final String EXTRA_SHOW_FRAGMENT_TITLE_RESID = ":settings:show_fragment_title_resid";
    public static final String EXTRA_SHOW_FRAGMENT_TITLE_RES_PACKAGE_NAME = ":settings:show_fragment_title_res_package_name";
    public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = "show_fragment_args";

    public static void startPreferencePanel(Context context, String fragmentClass, Bundle args, int titleRes,
                                            CharSequence titleText, Fragment resultTo, int resultRequestCode) {
        String title = null;
        if (titleRes <= 0) {
            if (titleText != null) {
                title = titleText.toString();
            } else {
                // There not much we can do in that case
                title = "";
            }
        }

        startWithFragment(context, fragmentClass, args, resultTo, resultRequestCode,
                titleRes, title, false);
    }

    private static Intent onBuildStartFragmentIntent(Context context, String fragmentName,
                                                    Bundle args, String titleResPackageName, int titleResId, CharSequence title,
                                                    boolean isShortcut) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        intent.putExtra(EXTRA_SHOW_FRAGMENT, fragmentName);
        intent.setClass(context, ComponentActivity.class);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_TITLE, title);
        return intent;
    }

    private static void startWithFragment(Context context, String fragmentName, Bundle args,
                                         Fragment resultTo, int resultRequestCode, int titleResId,
                                         CharSequence title, boolean isShortcut) {
        Intent intent = onBuildStartFragmentIntent(context, fragmentName, args,
                null /* titleResPackageName */, titleResId, title, isShortcut);
        if (resultTo == null) {
            context.startActivity(intent);
        } else {
            resultTo.startActivityForResult(intent, resultRequestCode);
        }
    }
}
