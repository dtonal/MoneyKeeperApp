package de.dtonal.moneykeeperapp;

/**
 * Created by dtonal on 28.09.16.
 */

public class AuthResponse {
    private Boolean success;

    private String token;

    public AuthResponse(Boolean success, String token) {
        this.success = success;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Boolean getSuccess() {
        return success;
    }

}
