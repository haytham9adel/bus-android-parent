package net.m3aak.parentapp.Local;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by RWS 6 on 11/29/2016.
 */
public class LocaleUtils {
    private static Locale sLocale;

    public static void setLocale(Locale locale) {
        sLocale = locale;
        if (sLocale != null) {
            Locale.setDefault(sLocale);
        }
    }

    public static void updateConfig(Application app, Configuration configuration) {
          try {
              //Wrapping the configuration to avoid Activity endless loop
              Configuration config = new Configuration(configuration);
              config.locale = sLocale;
              Resources res = app.getBaseContext().getResources();
              res.updateConfiguration(config, res.getDisplayMetrics());
          }catch (Exception e) {e.printStackTrace();}
    }
}
