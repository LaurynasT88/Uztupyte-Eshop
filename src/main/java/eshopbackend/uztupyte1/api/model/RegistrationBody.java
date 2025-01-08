package eshopbackend.uztupyte1.api.model;

public class RegistrationBody {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
