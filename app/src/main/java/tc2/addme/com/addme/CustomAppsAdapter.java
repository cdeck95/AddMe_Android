package tc2.addme.com.addme;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

        App singleApp = getItem(position);

        TextView txtAppDisplayName = (TextView)customView.findViewById(R.id.txtAppDisplayName);
        txtAppDisplayName.setText(singleApp.getDisplayName());

        TextView txtAppPlatform = (TextView)customView.findViewById(R.id.txtAppPlatform);
        txtAppPlatform.setText(singleApp.getPlatform());

        TextView txtAppID = (TextView) customView.findViewById(R.id.txtAppID);
        String appID = singleApp.getAppID() + "";
        txtAppID.setText(appID);

        return customView;
    }


}
