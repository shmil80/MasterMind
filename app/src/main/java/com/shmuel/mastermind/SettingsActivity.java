package com.shmuel.mastermind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import android.view.View;

import com.shmuel.mastermind.shared.beans.Configuration;
import com.shmuel.mastermind.ui.beans.SettingsData;

import java.util.Map;

public class SettingsActivity extends PreferenceActivity {


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            getListView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
            getListView().setTextDirection(View.TEXT_DIRECTION_RTL);
        }
        addPreferencesFromResource(R.xml.setting);

    }
    public static void start(Activity sender)
    {
        Intent intent = new Intent(sender, SettingsActivity.class);
        sender.startActivityForResult(intent, Consts.SETTING_REQUEST_CODE);

    }
    public static SettingsData initPreferences(Context context) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> map = sharedPreferences.getAll();

        Object allowDuplicateValue = map.get("allowDuplicate");
        boolean allowDuplicate= allowDuplicateValue ==null?false:(Boolean) allowDuplicateValue;

        Object allowEmptyValue = map.get("allowEmpty");
        boolean allowEmpty= allowEmptyValue ==null?false:(Boolean) allowEmptyValue;

        Object optionLengthValue = map.get("optionLength");
        byte optionLength= optionLengthValue ==null?(byte)4:Byte.parseByte((String) optionLengthValue);

        Object optionPosibilities = map.get("optionPosibilities");
        byte optionPossibilities= optionPosibilities ==null?(byte)8:Byte.parseByte((String) optionPosibilities);


        if(!allowDuplicate&&(allowEmpty? optionLength-1:optionLength)>optionPossibilities)
            allowDuplicate=true;

        Object soundValue = map.get("allow_music");
        boolean sound= soundValue ==null?true:(Boolean) soundValue;

        Configuration configuration=new Configuration(allowDuplicate,optionLength,optionPossibilities, allowEmpty);
        return new SettingsData(configuration,sound);
    }

}
