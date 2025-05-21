package com.form.registrationform.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.form.registrationform.repositories.RegistrationRepository;
import com.form.registrationform.models.RegistrationData;

/**
 * RegistrationViewModel - Manages UI data for registration form
 * 
 * This class connects the UI with the data layer and handles form validation,
 * data loading/saving, and other business logic.
 */
public class RegistrationViewModel extends AndroidViewModel {

    /**
     * ValidationState - Enum for different form validation states
     * 
     * Each state represents a possible validation result with an associated
     * error message and validity flag.
     */
    public enum ValidationState {
        VALID(true, ""),                                        // Form is valid
        FIRST_NAME_REQUIRED(false, "First name is required"),   // First name is missing
        LAST_NAME_REQUIRED(false, "Last name is required"),     // Last name is missing
        DATE_OF_BIRTH_REQUIRED(false, "Date of birth is required"), // Date of birth is missing
        PASSWORD_REQUIRED(false, "Password is required"),       // Password is missing
        CONFIRM_PASSWORD_REQUIRED(false, "Confirm password is required"), // Confirm password is missing
        PHOTO_REQUIRED(false, "Photo is required"),             // Photo is missing
        PASSWORDS_DO_NOT_MATCH(false, "Passwords do not match"); // Passwords don't match

        private final boolean isValid;      // Whether this state is considered valid
        private final String errorMessage;   // Error message to display

        /**
         * Constructor for ValidationState
         * 
         * @param isValid Whether this state is valid
         * @param errorMessage Error message for this state
         */
        ValidationState(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        /**
         * Checks if this validation state is valid
         * @return true if valid, false otherwise
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Gets the error message for this validation state
         * @return The error message
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    // Repository for data storage
    private final RegistrationRepository repository;

    // LiveData objects for form fields
    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> dateOfBirth = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>();
    private final MutableLiveData<Uri> photoUri = new MutableLiveData<>();
    private final MutableLiveData<String> comment = new MutableLiveData<>();
    
    // LiveData for form state
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFormValid = new MutableLiveData<>(false);
    private final MutableLiveData<ValidationState> validationState = 
            new MutableLiveData<>(ValidationState.FIRST_NAME_REQUIRED);
    
    // Language preference
    private final MutableLiveData<String> language = new MutableLiveData<>("en");

    /**
     * Constructor - creates a new ViewModel instance
     * 
     * @param application The application context
     */
    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        // Initialize repository
        repository = new RegistrationRepository(application);
        
        // Check if we have saved data and load it
        if (repository.hasRegistration()) {
            RegistrationData savedData = repository.getRegistration();
            displayData(savedData);
        }
    }

    /**
     * Displays registration data in the form fields
     * 
     * @param data The registration data to display
     */
    public void displayData(RegistrationData data) {
        if (data == null) {
            return;
        }
        
        // Set basic text fields
        firstName.setValue(data.getFirstName());
        lastName.setValue(data.getLastName());
        comment.setValue(data.getComment());
        
        // Handle date of birth with null check
        if (data.getDateOfBirth() != null) {
            try {
                // Display date in user-friendly format (day/month/year)
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(data.getDateOfBirth());
                Log.d("RegistrationForm", "Formatted date for display: " + formattedDate);
                dateOfBirth.setValue(formattedDate);
            } catch (Exception e) {
                Log.e("RegistrationForm", "Error formatting date: " + e.getMessage());
                // If date formatting fails, just display empty string
                dateOfBirth.setValue("");
            }
        } else {
            dateOfBirth.setValue("");
        }
        
        // Handle photo URI with null check
        if (data.getPhotoPath() != null && !data.getPhotoPath().isEmpty()) {
            try {
                photoUri.setValue(Uri.parse(data.getPhotoPath()));
            } catch (Exception e) {
                // If URI parsing fails, set null
                photoUri.setValue(null);
            }
        } else {
            photoUri.setValue(null);
        }
        
        // Note: Passwords are not displayed for security reasons
    }

    /**
     * Sets the preferred language
     * 
     * @param language The language code (e.g., "en" for English)
     */
    public void setLanguage(String language) {
        this.language.setValue(language);
    }

    /**
     * Gets the preferred language
     * 
     * @return LiveData containing the language code
     */
    public LiveData<String> getLanguage() {
        return language;
    }

    // Getters for LiveData - these methods allow the UI to observe changes
    
    /**
     * Gets the first name LiveData
     * @return LiveData containing the first name
     */
    public LiveData<String> getFirstName() { return firstName; }
    
    /**
     * Gets the last name LiveData
     * @return LiveData containing the last name
     */
    public LiveData<String> getLastName() { return lastName; }
    
    /**
     * Gets the date of birth LiveData
     * @return LiveData containing the date of birth
     */
    public LiveData<String> getDateOfBirth() { return dateOfBirth; }
    
    /**
     * Gets the password LiveData
     * @return LiveData containing the password
     */
    public LiveData<String> getPassword() { return password; }
    
    /**
     * Gets the confirm password LiveData
     * @return LiveData containing the confirm password
     */
    public LiveData<String> getConfirmPassword() { return confirmPassword; }
    
    /**
     * Gets the photo URI LiveData
     * @return LiveData containing the photo URI
     */
    public LiveData<Uri> getPhotoUri() { return photoUri; }
    
    /**
     * Gets the comment LiveData
     * @return LiveData containing the comment
     */
    public LiveData<String> getComment() { return comment; }
    
    /**
     * Gets the error message LiveData
     * @return LiveData containing the error message
     */
    public LiveData<String> getErrorMessage() { return errorMessage; }
    
    /**
     * Gets the form validity LiveData
     * @return LiveData containing whether the form is valid
     */
    public LiveData<Boolean> getIsFormValid() { return isFormValid; }
    
    /**
     * Gets the validation state LiveData
     * @return LiveData containing the validation state
     */
    public LiveData<ValidationState> getValidationState() { return validationState; }

    // Methods to update values - these methods are called by the UI
    
    /**
     * Sets the first name
     * @param value The first name to set
     */
    public void setFirstName(String value) { firstName.setValue(value); }
    
    /**
     * Sets the last name
     * @param value The last name to set
     */
    public void setLastName(String value) { lastName.setValue(value); }
    
    /**
     * Sets the date of birth
     * @param value The date of birth to set
     */
    public void setDateOfBirth(String value) { dateOfBirth.setValue(value); }
    
    /**
     * Sets the password
     * @param value The password to set
     */
    public void setPassword(String value) { password.setValue(value); }
    
    /**
     * Sets the confirm password
     * @param value The confirm password to set
     */
    public void setConfirmPassword(String value) { confirmPassword.setValue(value); }
    
    /**
     * Sets the photo URI
     * @param uri The photo URI to set
     */
    public void setPhotoUri(Uri uri) { photoUri.setValue(uri); }
    
    /**
     * Sets the comment
     * @param value The comment to set
     */
    public void setComment(String value) { comment.setValue(value); }

    /**
     * Validates the form and updates validation state
     * 
     * @return The validation state result
     */
    public ValidationState validateForm() {
        // Get current values from LiveData
        String firstNameValue = firstName.getValue();
        String lastNameValue = lastName.getValue();
        String dateOfBirthValue = dateOfBirth.getValue();
        String passwordValue = password.getValue();
        String confirmPasswordValue = confirmPassword.getValue();
        Uri photoUriValue = photoUri.getValue();

        // Determine validation state by checking each field
        ValidationState state;
        
        // Check each field in order of appearance on the form
        if (firstNameValue == null || firstNameValue.trim().isEmpty()) {
            state = ValidationState.FIRST_NAME_REQUIRED;
        } else if (lastNameValue == null || lastNameValue.trim().isEmpty()) {
            state = ValidationState.LAST_NAME_REQUIRED;
        } else if (dateOfBirthValue == null || dateOfBirthValue.trim().isEmpty()) {
            state = ValidationState.DATE_OF_BIRTH_REQUIRED;
        } else if (passwordValue == null || passwordValue.trim().isEmpty()) {
            state = ValidationState.PASSWORD_REQUIRED;
        } else if (confirmPasswordValue == null || confirmPasswordValue.trim().isEmpty()) {
            state = ValidationState.CONFIRM_PASSWORD_REQUIRED;
        } else if (photoUriValue == null) {
            state = ValidationState.PHOTO_REQUIRED;
        } else if (!passwordValue.equals(confirmPasswordValue)) {
            state = ValidationState.PASSWORDS_DO_NOT_MATCH;
        } else {
            // All checks passed
            state = ValidationState.VALID;
        }

        // Update LiveData values with validation results
        errorMessage.setValue(state.getErrorMessage());
        isFormValid.setValue(state.isValid());
        validationState.setValue(state);
        
        return state;
    }

    /**
     * Gets the form data and saves it to the repository
     * 
     * @return The registration data object
     */
    public RegistrationData getFormData() {
        // Create a new registration data object with current values
        RegistrationData data = new RegistrationData(
                firstName.getValue(),
                lastName.getValue(),
                null, // Date will be parsed from string
                password.getValue(),
                confirmPassword.getValue(),
                photoUri.getValue() != null ? photoUri.getValue().toString() : null,
                comment.getValue()
        );

        // Save data to repository
        repository.saveRegistration(data);
        return data;
    }

    /**
     * Clears all form fields and saved data
     */
    public void clearForm() {
        // Reset all form fields
        firstName.setValue("");
        lastName.setValue("");
        dateOfBirth.setValue("");
        password.setValue("");
        confirmPassword.setValue("");
        photoUri.setValue(null);
        comment.setValue("");
        
        // Reset form state
        errorMessage.setValue("");
        isFormValid.setValue(false);
        
        // Clear data in repository
        repository.clearRegistration();
    }
}
