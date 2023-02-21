package com.example.guedira.swcontol;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.SeekBar;
import com.example.guedira.swcontol.SeekBarPreference;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class InterfaceSettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    SeekBar sbp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        //sbp=new SeekBarPreference(this.getApplicationContext(),);

    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            final Preference psh=this.findPreference("custom_ratio");
            final Preference psh2=this.findPreference("cont_vib");
            final Preference psh3=this.findPreference("cont_snd");
            try{
              if(this.findPreference("ratrat").getSharedPreferences().getString("ratrat","Golden").compareToIgnoreCase("Custom")==0)
                 psh.setEnabled(true);
              //sbp.setOnSeekBarChangeListener(n
                else
                  psh.setEnabled(false);
            }catch(Exception e){

            }
            this.findPreference("ratrat").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if(preference.getKey().compareToIgnoreCase("ratrat")==0){
                                if (newValue.toString().compareToIgnoreCase("Custom")==0) {
                                    psh.setEnabled(true);
                                }else{
                                    psh.setEnabled(false);
                                }
                            }
                            return true;
                        }
                    });

            this.findPreference("en_vib").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if(preference.getKey().compareToIgnoreCase("en_vib")==0){
                                if (newValue.toString().compareToIgnoreCase("true")==0)
                                    psh2.setEnabled(true);
                                else psh2.setEnabled(false);
                            }
                            return true;
                        }
                    });

            this.findPreference("en_snd").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if(preference.getKey().compareToIgnoreCase("en_snd")==0){
                                if (newValue.toString().compareToIgnoreCase("true")==0)
                                    psh3.setEnabled(true);
                                else psh3.setEnabled(false);
                            }
                            return true;
                        }
                    });

        }
    }




}
