package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREF_NAME = "real_estate_prefs";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_DATE_FORMAT = "date_format";

    public static String getCurrency(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENCY, "USD");
    }

    public static void setCurrency(Context context, String currency) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_CURRENCY, currency).apply();
    }

    public static String getDateFormat(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_DATE_FORMAT, "dd/MM/yyyy");
    }

    public static void setDateFormat(Context context, String dateFormat) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_DATE_FORMAT, dateFormat).apply();
    }
}
