package com.example.ludopatics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    public static Context applyLocale(Context context) {
        // Siempre usar el idioma del sistema
        String languageCode = Locale.getDefault().getLanguage();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
