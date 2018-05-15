package tc2.addme.com.addme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Level;

public class ProfileActivity extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ProfileActivity";
    ListView appList;
    SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgProfile;
    private TextView namneTV;
    private ArrayList<App> apps;
    private App app1, app2, app3, app4;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.profile_tab, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        appList = (ListView) rootView.findViewById(R.id.appsListView);
        app1 = new App(1, "Personal Facebook", "Facebook", "http://www.facebook.com", Boolean.TRUE);
        app2 = new App(2, "Personal Twitter", "Twitter", "http://www.facebook.com",  Boolean.TRUE);
        app3 = new App(3, "Personal Instagram", "Instagram", "http://www.facebook.com",  Boolean.TRUE);
        app4 = new App(4, "Business Facebook", "Facebook", "http://www.facebook.com",  Boolean.TRUE);
        apps = new ArrayList<App>();
        apps.add(app1);
        apps.add(app2);
        apps.add(app3);
        apps.add(app4);
        populateApps(1, rootView);

        appList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (appList == null || appList.getChildCount() == 0) ?
                                0 : appList.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "----Refreshed----");
                populateApps(1, rootView);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


       // new MyAsyncTask(getActivity(), mListView).execute("");


        return rootView;
    }

    private void populateApps(int i, View v) {
        ListAdapter adapter = new CustomAppsAdapter(getContext(), 0, apps);
        appList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        Log.d(TAG, "----------in group list view on click listener---------------");
        Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG).show();
    }

//    class MyAsyncTask extends AsyncTask<String, String, String>
//    {
//        GridView mGridView;
//        Activity mContex;
//        public  MyAsyncTask(Activity contex,GridView gview)
//        {
//            this.mGridView=gview;
//            this.mContex=contex;
//        }
//
//        protected String doInBackground(String... params)
//        {
//
//            //fetch data
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            {
//
//                for(Sales sales : this.response.sales){
//                    HashMap<String, String> hm = new HashMap<String,String>();
//
//                    if (sales.getCategories1().contains("12")){
//                        //hm.put("sale_title", "" + sales.getShort_title());
//                        for(Shop shop : this.response.shops){
//                            String image_file = new String(        Environment.getExternalStorageDirectory().getAbsolutePath()
//                                    + "/images/" + shop.getImage());
//                            if(shop.getId().equals(sales.getShop_id())){
//                                hm.put("shop_image", image_file );
//                                System.out.println(shop_image);
//                            }
//                        }
//                    }
//                }
//                if(hm.size()>0)
//                    mcontext.mGridView.setAdapter(new ImageAdapter(mContext),hm);
//            }


}
