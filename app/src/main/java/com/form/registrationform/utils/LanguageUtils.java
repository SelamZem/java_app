package com.form.registrationform.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LanguageUtils {
    private static final String PREF_NAME = "language_prefs";
    private static final String KEY_LANGUAGE = "language";

    public static void setAppLanguage(Context context, String languageCode) {
        Locale locale = new Locale.Builder().setLanguage(languageCode).build();
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        
        Context updatedContext = context.createConfigurationContext(config);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public static void loadSavedLanguage(Context context) {
        String savedLanguage = getSavedLanguage(context);
        setAppLanguage(context, savedLanguage);
    }
}
