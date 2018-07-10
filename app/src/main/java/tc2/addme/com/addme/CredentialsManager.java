package tc2.addme.com.addme;

import com.google.android.gms.common.data.DataHolder;

public class CredentialsManager {
    private String cognitoId;
    public String getCognitoId() {return cognitoId;}
    public void setCognitoId(String cognitoId) {this.cognitoId = cognitoId;}

    private static final CredentialsManager holder = new CredentialsManager();
    public static CredentialsManager getInstance() {return holder;}
}
