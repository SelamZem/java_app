package com.form.registrationform.models;

import java.util.Date;


public class RegistrationData {
   
    private String firstName;        
    private String lastName;        
    private Date dateOfBirth;        
    private String password;         
    private String confirmPassword;  
    private String photoPath;       
    private String comment;         

 
    public RegistrationData() {
        this.firstName = null;
        this.lastName = null;
        this.dateOfBirth = null;
        this.password = null;
        this.confirmPassword = null;
        this.photoPath = null;
        this.comment = "";
    }

    public RegistrationData(String firstName, String lastName, Date dateOfBirth, 
                           String password, String confirmPassword, 
                           String photoPath, String comment) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.photoPath = photoPath;
        this.comment = comment;
    }


    public String getFirstName() { 
        return firstName; 
    }
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    public String getLastName() { 
        return lastName; 
    }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
     public Date getDateOfBirth() { 
        return dateOfBirth; 
    }

    public void setDateOfBirth(Date dateOfBirth) { 
        this.dateOfBirth = dateOfBirth; 
    }

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
    public String getConfirmPassword() { 
        return confirmPassword; 
    }
    public void setConfirmPassword(String confirmPassword) { 
        this.confirmPassword = confirmPassword; 
    }
    public String getPhotoPath() { 
        return photoPath; 
    }
    public void setPhotoPath(String photoPath) { 
        this.photoPath = photoPath; 
    }
    public String getComment() { 
        return comment; 
    }
    public void setComment(String comment) { 
        this.comment = comment; 
    }
    public boolean isValidPassword() {
      
        return password != null && confirmPassword != null && password.equals(confirmPassword);
    }

    public boolean isValidForm() {

        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               dateOfBirth != null &&
               password != null && !password.trim().isEmpty() &&
               confirmPassword != null && !confirmPassword.trim().isEmpty() &&
               photoPath != null && !photoPath.trim().isEmpty() &&
               isValidPassword();
    }
}
