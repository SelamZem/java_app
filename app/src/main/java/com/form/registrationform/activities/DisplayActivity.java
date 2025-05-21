package com.form.registrationform.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.form.registrationform.R;
import com.form.registrationform.databinding.ActivityDisplayBinding;
import com.form.registrationform.models.RegistrationData;
import com.form.registrationform.viewmodels.RegistrationViewModel;

public class DisplayActivity extends AppCompatActivity {
    private ActivityDisplayBinding binding;
    private RegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        try {
            Intent intent = getIntent();
            if (intent != null) {

                String firstName = intent.getStringExtra("first_name");
                String lastName = intent.getStringExtra("last_name");
                String dateOfBirth = intent.getStringExtra("date_of_birth");
                String photoPath = intent.getStringExtra("photo_path");
                String comment = intent.getStringExtra("comment");
                
                if (firstName != null && lastName != null) {
                    RegistrationData data = new RegistrationData();
                    data.setFirstName(firstName);
                    data.setLastName(lastName);
                    data.setPhotoPath(photoPath);
                    data.setComment(comment);

                    if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                            // Expected format: yyyy/MM/dd
                            String[] parts = dateOfBirth.split("/");
                            if (parts.length == 3) {
                                int year = Integer.parseInt(parts[0]);
                                int month = Integer.parseInt(parts[1]) - 1;
                                int day = Integer.parseInt(parts[2]);
                                
                                java.util.Calendar calendar = java.util.Calendar.getInstance();
                                calendar.set(year, month, day);
                                data.setDateOfBirth(calendar.getTime());
                            }
                    }

                    viewModel.displayData(data);
                    

                    if (photoPath != null && !photoPath.isEmpty()) {
                            Uri photoUri = Uri.parse(photoPath);
                            binding.profileImage.setImageURI(photoUri);
                    } else {

                        binding.profileImage.setImageResource(R.mipmap.ic_launcher);

                    }
                    

                } else {
                    Toast.makeText(this, "Error: Required data is missing", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {

                Toast.makeText(this, "No intent received", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying data: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
