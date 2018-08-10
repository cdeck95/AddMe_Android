package tc2.addme.com.addme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by cdeck_000 on 4/4/2017.
 */

public class CustomAppsAdapter extends ArrayAdapter<App>  {

    private static final String TAG = "CustomGroupsAdapter";

    public CustomAppsAdapter(Context context, int resource, ArrayList<App> apps) {
        super(context, resource, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_apps_row, parent, false);

        final App singleApp = getItem(position);

        TextView txtAppDisplayName = (TextView)customView.findViewById(R.id.txtAppDisplayName);
        txtAppDisplayName.setText(singleApp.getDisplayName());

        Switch txtAppSwitch = (Switch) customView.findViewById(R.id.appSwitch);
        txtAppSwitch.setChecked(singleApp.isAppSwitchIsOn());
        txtAppSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Checked:" +  isChecked);
                singleApp.setAppSwitchIsOn(isChecked);
                Log.d(TAG, "Switch is on: " + singleApp.isAppSwitchIsOn());
            }
        });


        TextView txtAppID = (TextView) customView.findViewById(R.id.txtAppID);
        String appID = singleApp.getAppID() + "";
        txtAppID.setText(appID);

        ImageView appImage = (ImageView) customView.findViewById(R.id.imageView);
        String platform = singleApp.getPlatform();

        switch (platform){
            case "Facebook":
                appImage.setImageResource(R.mipmap.ic_facebook);
                break;
            case "Twitter":
                appImage.setImageResource(R.mipmap.ic_twitter);
                break;
            case "Instagram":
                appImage.setImageResource(R.mipmap.ic_instagram);
                break;
            case "Snapchat":
                appImage.setImageResource(R.mipmap.ic_snapchat);
                break;
            case "GooglePlus":
                appImage.setImageResource(R.mipmap.ic_google_plus);
                break;
            case "LinkedIn":
                appImage.setImageResource(R.mipmap.ic_linked_in);
                break;
            case "Xbox":
                appImage.setImageResource(R.mipmap.ic_launcher);
                break;
            case "PSN":
                appImage.setImageResource(R.mipmap.ic_launcher);
                break;
            case "Twitch":
                appImage.setImageResource(R.mipmap.ic_launcher);
                break;
            default:
                appImage.setImageResource(R.mipmap.ic_launcher); //need custom
                break;
        }

        return customView;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
