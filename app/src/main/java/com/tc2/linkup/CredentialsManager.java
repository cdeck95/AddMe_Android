package com.tc2.linkup;

public class CredentialsManager {
    private static final CredentialsManager holder = new CredentialsManager();
    private String cognitoId;

    public static CredentialsManager getInstance() {
        return holder;
    }

    public String getCognitoId() {
        return cognitoId;
    }

    public void setCognitoId(String cognitoId) {
        this.cognitoId = cognitoId;
    }
}
