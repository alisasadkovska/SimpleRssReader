package com.alisasadkovska.simplerssreader.common;

import android.app.Activity;
import com.alisasadkovska.simplerssreader.R;


/**
 * Created by Death on 22/12/2017.
 */

public class Utils {
    private final static int THEME_DEFAULT = 1;
    private final static int THEME_DARK = 0;


    public static void onActivityCreateSetTheme(Activity activity, int sTheme)
    {
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.DarkTheme);
                break;
        }
    }
}
