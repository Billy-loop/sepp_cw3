package model;


public class AuthenticatedUser extends User {
    private String email;
    private String role;

    public AuthenticatedUser(String email, String role){
        if (email == null){
            throw new IllegalArgumentException("User email cannot be null");

        }
        if (!role.equals("TeachingStaff") && !role.equals("Student") && !role.equals("AdminStaff")){
            throw  new IllegalArgumentException("Unsupported user role");
        }
        this.email = email;
        this.role = role;
    }

    public String getEmail(){
        return email;
    }

    public String getRole(){
        return role;
    }

}
