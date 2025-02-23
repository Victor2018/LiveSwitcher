package com.quick.liveswitcher.virtualscreen;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xtx on 2023/10/31.
 */
public class SharedPreferencesUtil {
    public static final String NAME = "config_data";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void setValue(Context context, String key, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getValue(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(key, null);
    }

    public static void clearValues(Context context, List<String> keyList) {
        if (keyList.size() > 0) {
            SharedPreferences sp = getSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            for (String key : keyList) {
                editor.putString(key, null);
            }
            editor.apply();
        }
    }

    public static List<String> getPackageNameList(Context context) {
        List<String> packageNameList = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences(context);
//        String value = sp.getString(MultiScreenActivity.WINDOW_INFO_1, null);
//        if (value != null) {
//            packageNameList.add(value);
//        }
//        value = sp.getString(MultiScreenActivity.WINDOW_INFO_2, null);
//        if (value != null) {
//            packageNameList.add(value);
//        }
//        value = sp.getString(MultiScreenActivity.WINDOW_INFO_3, null);
//        if (value != null) {
//            packageNameList.add(value);
//        }
//        value = sp.getString(MultiScreenActivity.WINDOW_INFO_4, null);
//        if (value != null) {
//            packageNameList.add(value);
//        }
        return packageNameList;
    }
}
