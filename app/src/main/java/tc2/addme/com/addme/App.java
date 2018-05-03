package tc2.addme.com.addme;

import java.io.Serializable;

/**
 * Created by Tom on 3/24/17.
 */

public class App implements Serializable
{

    private int appID;
    private String platform;
    private String displayName;
    private String url;
    private boolean appSwitchIsOn;

    public App(){

    }

    public App(int appID, String displayName, String platform, String url, Boolean appSwitchIsOn)
    {
        this.appID = appID;
        this.displayName = displayName;
        this.platform = platform;
        this.url = url;
        this.appSwitchIsOn = appSwitchIsOn;
    }

    public App(int appID, String displayName, String platform, String url)
    {
        this.appID = appID;
        this.displayName = displayName;
        this.platform = platform;
        this.url = url;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAppSwitchIsOn() {
        return appSwitchIsOn;
    }

    public void setAppSwitchIsOn(boolean appSwitchIsOn) {
        this.appSwitchIsOn = appSwitchIsOn;
    }


}
