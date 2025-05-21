package com.form.registrationform.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.form.registrationform.R;
import com.form.registrationform.databinding.ActivityRegistrationBinding;
import com.form.registrationform.models.RegistrationData;
import com.form.registrationform.utils.LanguageUtils;
import com.form.registrationform.viewmodels.RegistrationViewModel;
import com.form.registrationform.viewmodels.RegistrationViewModel.ValidationState;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {
    private RegistrationViewModel viewModel;
    private ActivityRegistrationBinding binding;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LanguageUtils.loadSavedLanguage(this);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        viewModel.setPhotoUri(selectedImageUri);
                        binding.photoImageView.setImageURI(selectedImageUri);
                    }
                }
        );


        binding.submitButton.setEnabled(true);

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });


        String initialLang = LanguageUtils.getSavedLanguage(this);
        viewModel.setLanguage(initialLang);
        

        LanguageUtils.setAppLanguage(this, initialLang);
        updateUITexts(initialLang);

        binding.languageToggle.setChecked("am".equals(initialLang));


        binding.languageToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String language = isChecked ? "am" : "en";
            

            viewModel.setLanguage(language);
            

            LanguageUtils.setAppLanguage(this, language);
            

            updateUITexts(language);
            

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });

        // Select photo
        binding.selectPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Register for context menu on photo image view
        registerForContextMenu(binding.photoImageView);

        // Submit button
        binding.submitButton.setOnClickListener(v -> {
            try {
                // Get values from form fields
                String firstName = binding.firstNameEditText.getText().toString().trim();
                String lastName = binding.lastNameEditText.getText().toString().trim();
                String dateOfBirthText = binding.dateOfBirthEditText.getText().toString().trim();
                String password = binding.passwordEditText.getText().toString();
                String confirmPassword = binding.confirmPasswordEditText.getText().toString();
                String comment = binding.commentEditText.getText().toString();
                
                // Update ViewModel with current form values
                viewModel.setFirstName(firstName);
                viewModel.setLastName(lastName);
                viewModel.setDateOfBirth(dateOfBirthText);
                viewModel.setPassword(password);
                viewModel.setConfirmPassword(confirmPassword);
                viewModel.setComment(comment);
                
                // Validate the form
                ValidationState validationState = viewModel.validateForm();
                if (!validationState.isValid()) {
                    Toast.makeText(this, validationState.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Get photo URI
                String photoPath = selectedImageUri != null ? selectedImageUri.toString() : "";
                
                // Create intent to start DisplayActivity
                Intent intent = new Intent(this, DisplayActivity.class);
                
                // Pass individual data items as extras instead of using Parcelable
                intent.putExtra("first_name", firstName);
                intent.putExtra("last_name", lastName);
                intent.putExtra("date_of_birth", dateOfBirthText);
                intent.putExtra("photo_path", photoPath);
                intent.putExtra("comment", comment);
                
                // Start the activity
                startActivity(intent);
                
            } catch (Exception e) {

                Toast.makeText(this, "Error submitting form: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        setupObservers();
        setupDatePicker();
    }

    private void setupObservers() {
        // Observe validation state
        viewModel.getValidationState().observe(this, state -> {
            if (state != null) {
                // Update UI based on validation state if needed
                binding.submitButton.setEnabled(true); // Always enable for better UX
                
                // Show error message if validation fails
                if (!state.isValid()) {
                    Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Observe language changes
        viewModel.getLanguage().observe(this, language -> {
            // Update UI language if needed
            binding.languageToggle.setChecked("am".equals(language));
        });
    }

    private void setupDatePicker() {
        binding.dateOfBirthEditText.setFocusable(false);
        binding.dateOfBirthEditText.setClickable(true);
        
        binding.dateOfBirthEditText.setOnClickListener(v -> {
            // Get current date for initial selection
            final Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int day = currentDate.get(Calendar.DAY_OF_MONTH);
            
            // Create date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Create a Calendar instance for the selected date
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year1);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        
                        // Use a consistent date format: yyyy/MM/dd
                        String formattedDate = String.format("%d/%02d/%02d", year1, monthOfYear + 1, dayOfMonth);
                        
                        // Store the date in the ViewModel
                        viewModel.setDateOfBirth(formattedDate);
                        
                        // Display the formatted date
                        binding.dateOfBirthEditText.setText(formattedDate);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

    // Option Menu Implementation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            viewModel.clearForm();
            binding.photoImageView.setImageResource(android.R.drawable.ic_menu_gallery);
            selectedImageUri = null;
            return true;
        } else if (id == R.id.action_language) {
            String currentLanguage = viewModel.getLanguage().getValue();
            String newLanguage = "en".equals(currentLanguage) ? "am" : "en";
            viewModel.setLanguage(newLanguage);
            LanguageUtils.setAppLanguage(this, newLanguage);
            recreate(); // Restart activity to apply language change
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Context Menu Implementation
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.photoImageView) {
            getMenuInflater().inflate(R.menu.context_menu_photo, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_select_photo) {
            // Using startActivityForResult (deprecated but shown for demonstration)
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1001);
            return true;
        } else if (id == R.id.action_remove_photo) {
            viewModel.setPhotoUri(null);
            selectedImageUri = null;
            binding.photoImageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // Legacy method for handling image selection (for demonstration purposes)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            viewModel.setPhotoUri(selectedImage);
            selectedImageUri = selectedImage;
            binding.photoImageView.setImageURI(selectedImage);
        }
    }

    private void updateUITexts(String language) {
        try {
            if ("en".equals(language)) {

                binding.firstNameEditText.setHint(R.string.first_name);
                binding.lastNameEditText.setHint(R.string.last_name);
                binding.dateOfBirthEditText.setHint(R.string.date_of_birth);
                binding.passwordEditText.setHint(R.string.password);
                binding.confirmPasswordEditText.setHint(R.string.confirm_password);
                binding.commentEditText.setHint(R.string.comment);
                binding.submitButton.setText(R.string.submit);
                binding.selectPhotoButton.setText(R.string.select_photo);
                

                setTitle(R.string.app_name);
            } else {
                // Get the correct resources for Amharic
                Resources res = getResources();
                Configuration config = new Configuration(res.getConfiguration());
                
                // Create locale with language tag to avoid deprecation warning
                Locale locale = new Locale.Builder().setLanguage(language).build();
                config.setLocale(locale);
                
                // Create a context with the new configuration
                Context context = createConfigurationContext(config);
                Resources updatedRes = context.getResources();
                
                // Amharic strings - need to use the updated resources
                binding.firstNameEditText.setHint(updatedRes.getString(R.string.first_name));
                binding.lastNameEditText.setHint(updatedRes.getString(R.string.last_name));
                binding.dateOfBirthEditText.setHint(updatedRes.getString(R.string.date_of_birth));
                binding.passwordEditText.setHint(updatedRes.getString(R.string.password));
                binding.confirmPasswordEditText.setHint(updatedRes.getString(R.string.confirm_password));
                binding.commentEditText.setHint(updatedRes.getString(R.string.comment));
                binding.submitButton.setText(updatedRes.getString(R.string.submit));
                binding.selectPhotoButton.setText(updatedRes.getString(R.string.select_photo));
                
                // Set title bar language
                setTitle(updatedRes.getString(R.string.app_name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
