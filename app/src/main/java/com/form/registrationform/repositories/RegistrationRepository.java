package com.form.registrationform.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.form.registrationform.models.RegistrationData;

import java.util.Date;

public class RegistrationRepository {
  
    private static final String PREFS_NAME = "RegistrationPrefs";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CONFIRM_PASSWORD = "confirm_password";
    private static final String KEY_PHOTO_PATH = "photo_path";
    private static final String KEY_COMMENT = "comment";

    
    private final SharedPreferences sharedPreferences;

    public RegistrationRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveRegistration(RegistrationData data) {
    
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
 
        editor.putString(KEY_FIRST_NAME, data.getFirstName());
        editor.putString(KEY_LAST_NAME, data.getLastName());
        editor.putString(KEY_PASSWORD, data.getPassword());
        editor.putString(KEY_CONFIRM_PASSWORD, data.getConfirmPassword());
        editor.putString(KEY_PHOTO_PATH, data.getPhotoPath());
        editor.putString(KEY_COMMENT, data.getComment());
        
        if (data.getDateOfBirth() != null) {
            editor.putLong(KEY_DATE_OF_BIRTH, data.getDateOfBirth().getTime());
        }
        
    
        editor.apply();
    }


    public RegistrationData getRegistration() {
       
        RegistrationData data = new RegistrationData();
    
        data.setFirstName(sharedPreferences.getString(KEY_FIRST_NAME, ""));
        data.setLastName(sharedPreferences.getString(KEY_LAST_NAME, ""));
        data.setPassword(sharedPreferences.getString(KEY_PASSWORD, ""));
        data.setConfirmPassword(sharedPreferences.getString(KEY_CONFIRM_PASSWORD, ""));
        data.setPhotoPath(sharedPreferences.getString(KEY_PHOTO_PATH, ""));
        data.setComment(sharedPreferences.getString(KEY_COMMENT, ""));

        long dateOfBirth = sharedPreferences.getLong(KEY_DATE_OF_BIRTH, 0);
        if (dateOfBirth != 0) {
            data.setDateOfBirth(new Date(dateOfBirth));
        }
        
        return data;
    }

    public void clearRegistration() {
      
        sharedPreferences.edit()
                .clear()
                .apply();
    }

    public boolean hasRegistration() {
        return !sharedPreferences.getString(KEY_FIRST_NAME, "").isEmpty();
    }
}
