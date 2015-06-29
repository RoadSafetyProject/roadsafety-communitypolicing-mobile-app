package wappi.iRoadCommunityPolicing;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;
import com.wappi.iRoadCommunityPolicing.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import wappi.iRoadCommunityPolicing.Adapters.DrawerListCustomAdapter;
import wappi.iRoadCommunityPolicing.Dhis2.DHIS2Config;
import wappi.iRoadCommunityPolicing.Dhis2.DHIS2Modal;
import wappi.iRoadCommunityPolicing.Dhis2.Data.DataElements;
import wappi.iRoadCommunityPolicing.Dhis2.Data.Program;
import wappi.iRoadCommunityPolicing.Fragments.FragmentAlert;
import wappi.iRoadCommunityPolicing.Fragments.PharmaciesMapFragment;
import wappi.iRoadCommunityPolicing.ServerCommunication.CommonUtilities;
import wappi.iRoadCommunityPolicing.ServerCommunication.JSONParser;
import wappi.iRoadCommunityPolicing.ServerCommunication.WakeLocker;
import wappi.iRoadCommunityPolicing.Utils.SystemBarTintManager;
import wappi.iRoadCommunityPolicing.Utils.ViewServer;


public class MainActivity extends ActionBarActivity implements RegistrationDialogue.OnCompleteListener{
    private static DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private static RelativeLayout mDrawer;
    public static ListView mDrawerList;
    public static String[] DrawerList;
    public static IroadDatabase db;
    static Context context;
    public static android.support.v4.app.FragmentManager manager;
    public static FragmentManager fragmentManager;
    public static Typeface Roboto_Condensed,Roboto_Black,Roboto_Bold,Roboto_Light,Roboto_BoldCondensedItalic,Roboto_BoldCondensed,Rosario_Regular,Rosario_Bold,Rosario_Italic,Roboto_Regular,Roboto_Medium;
    private FeedbackDialog mFeedbackDialog;
    private static final String KEY="AF-FE2D52FED88E-A8";
    private static final String TAG=MainActivity.class.getSimpleName();
    public  static int  dis_height;
    public  static int dis_width;
    private RelativeLayout content;
    private TextView drawerTitle;
    public Toolbar toolbar;
    private DrawerListCustomAdapter drawerListCustomAdapter;
    static final int LOGIN_REQUEST = 1;
    static final int CHANGE_LANGUAGE_REQUEST = 2;
    public static String username="admin",password="district",orgUnit="";
    private static final String ORGANISATION_UNIT="org_unit";
    private SharedPreferences sharedpreferences;
    public static List<Program> programs = new ArrayList<>();
    public static List<DataElements> dataElements = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new IroadDatabase(this);
        manager=this.getSupportFragmentManager();
        fragmentManager=this.getFragmentManager();
        context=MainActivity.this;

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        orgUnit =sharedpreferences.getString(ORGANISATION_UNIT,"");

        Log.d(TAG,"OrgUnit = "+orgUnit);


        /**
         * Initializing the send feedback dialogue
         * to send or receive feedback from the developers
         */
        FeedbackSettings feedbackSettings=new FeedbackSettings();
        feedbackSettings.setYourComments(this.getResources().getString(R.string.feedback_comments));
        feedbackSettings.setRadioButtons(false); // Disables radio buttons
        feedbackSettings.setToast(this.getResources().getString(R.string.feedback_toast));
        feedbackSettings.setReplyTitle(this.getResources().getString(R.string.feedback_reply_title));
        feedbackSettings.setText(this.getResources().getString(R.string.feedback_text));
        feedbackSettings.setGravity(Gravity.CENTER);
        feedbackSettings.setReplyCloseButtonText(this.getResources().getString(R.string.feedback_close_button));
        feedbackSettings.setReplyRateButtonText(this.getResources().getString(R.string.feedback_rate_button));

        mFeedbackDialog = new FeedbackDialog(this,KEY);
        mFeedbackDialog.setDebug(true);
        mFeedbackDialog.setSettings(feedbackSettings);


        WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        dis_width=display.getWidth();
        dis_height=display.getHeight();


        /**
         * Type faces used for setting fonts thoughout the app
         */
        Roboto_Light= Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        Roboto_Black= Typeface.createFromAsset(context.getAssets(), "Roboto-Black.ttf");
        Roboto_Condensed= Typeface.createFromAsset(context.getAssets(), "Roboto-Condensed.ttf");
        Roboto_BoldCondensedItalic= Typeface.createFromAsset(getAssets(), "Roboto-BoldCondensedItalic.ttf");
        Roboto_BoldCondensed= Typeface.createFromAsset(getAssets(), "Roboto-BoldCondensed.ttf");
        Roboto_Regular= Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Roboto_Medium= Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Rosario_Regular= Typeface.createFromAsset(getAssets(), "Rosario-Regular.ttf");
        Rosario_Italic= Typeface.createFromAsset(getAssets(), "Rosario-Italic.ttf");
        Rosario_Bold= Typeface.createFromAsset(getAssets(), "Rosario-Bold.ttf");
        Roboto_Bold= Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        ColorDrawable colorDrawable= new ColorDrawable(getResources().getColor(R.color.dark_dvs));
        tintManager.setTintDrawable(colorDrawable);

        DrawerList=this.getResources().getStringArray(R.array.drawer_list);


        load();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                /**
                 * Register on our server
                 * On server creates a new user
                 */
                try {
                }catch (Exception e){}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Log.d(TAG, "Done...  Loading all movies on to the veiw pager in backgroung");


            }
        }.execute();


    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void load(){
        Log.d(TAG,"Log called");
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView drawerPoster = (ImageView)findViewById(R.id.drawer_posters);

        Glide.with(getApplicationContext()).load(R.drawable.road_to_death_valley)
                .into(drawerPoster);



        /**
         * list view for the drawer
         */
        mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList=(ListView)findViewById(R.id.drawer_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,Gravity.START);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);


        content=(RelativeLayout)findViewById(R.id.activityMain_content_frame);
        drawerTitle=(TextView)findViewById(R.id.drawer_title);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide (View drawerView, float slideOffset){
                int alpha_value=(int)(slideOffset *  255);
                float MIN_SCALE = 0.93f;
                content.setAlpha(1 - slideOffset*2/3);
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        drawerListCustomAdapter=new DrawerListCustomAdapter(this,DrawerList);
        mDrawerList.setAdapter(drawerListCustomAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        if (orgUnit.equals("")) {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (!orgUnit.equals("")) {
                        Fragment newFragment = new FragmentAlert();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activityMain_content_frame, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        drawerTitle.setText(DrawerList[0]);
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject object = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL + "api/me.json", "GET",username,password,null);
                    Log.d(TAG,"received = "+object.toString());
                    String id = "";
                    try {
                        id=object.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    if (!id.equals("")) {
                        try {
                            JSONObject orgJson = object.getJSONArray("organisationUnits").getJSONObject(0);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(ORGANISATION_UNIT, orgJson.getString("id"));
                            editor.commit();

                            orgUnit = orgJson.getString("id");

                            Log.d(TAG, "cozes= " + orgUnit);

                            String url = DHIS2Config.BASE_URL + "api/programs?paging=false";
                            JSONParser dhis2parser = new JSONParser();
                            JSONObject objectProgram = dhis2parser.dhis2HttpRequest(url, "GET", username, password, null);
                            JSONArray jsonPrograms = null;
                            try {
                                jsonPrograms = objectProgram.getJSONArray("programs");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int counter = jsonPrograms.length();
                            for (int i = 0; i < counter; i++) {
                                JSONObject jsonProgram = null;
                                ContentValues values = new ContentValues();
                                try {
                                    jsonProgram = jsonPrograms.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Program program = new Program();
                                program.setModel(jsonProgram, program);
                                MainActivity.programs.add(i, program);

                                values.put(IroadDatabase.KEY_ID, program.getId());
                                values.put(IroadDatabase.KEY_NAME, program.getName());
                                values.put(IroadDatabase.KEY_CREATED, program.getCreated());
                                values.put(IroadDatabase.KEY_LAST_UPDATED, program.getLastUpdated());
                                values.put(IroadDatabase.KEY_HREF, program.getHref());
                                try {
                                    db.insert(IroadDatabase.TABLE_PROGRAMS, null, values);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }


                            String urlDataElements = DHIS2Config.BASE_URL + "api/dataElements.json?paging=false&fields=id,name,type,optionSet[id,name,options[id,name],version]";
                            JSONObject objectDataElements = dhis2parser.dhis2HttpRequest(urlDataElements, "GET", username, password, null);
                            JSONArray jsonDatalements = null;
                            try {
                                jsonDatalements = objectDataElements.getJSONArray("dataElements");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int cou = jsonDatalements.length();
                            for (int i = 0; i < cou; i++) {
                                JSONObject jsonDataElement = null;
                                ContentValues values = new ContentValues();
                                try {
                                    jsonDataElement = jsonDatalements.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                DataElements dataElement = new DataElements();
                                dataElement.setModel(jsonDataElement, dataElement);
                                MainActivity.dataElements.add(i, dataElement);

                                values.put(IroadDatabase.KEY_ID, dataElement.getId());
                                values.put(IroadDatabase.KEY_NAME, dataElement.getName());
                                values.put(IroadDatabase.KEY_CREATED, dataElement.getCreated());
                                values.put(IroadDatabase.KEY_LAST_UPDATED, dataElement.getLastUpdated());
                                values.put(IroadDatabase.KEY_HREF, dataElement.getHref());
                                try {
                                    db.insert(IroadDatabase.TABLE_DATA_ELEMENTS, null, values);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                try {
                                    JSONObject optionSetObject = jsonDataElement.getJSONObject("optionSet");
                                    JSONArray optionsArray = optionSetObject.getJSONArray("options");
                                    int size = optionsArray.length();
                                    for (int j = 0; j < size; j++) {
                                        JSONObject option = optionsArray.getJSONObject(j);

                                        Log.d(TAG, "option set = " + optionSetObject.getString("name") + "|" + " Option naame =  " + option.getString("name"));
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(IroadDatabase.KEY_ID, option.getString("id"));
                                        contentValues.put(IroadDatabase.KEY_NAME, optionSetObject.getString("name"));
                                        contentValues.put(IroadDatabase.KEY_OPTION_NAME, option.getString("name"));
                                        db.insert(IroadDatabase.TABLE_OPTION_SETS, null, contentValues);
                                    }
                                } catch (JSONException e) {

                                } catch (Exception e) {

                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }.execute();
            Log.d(TAG, "app running for the first time");
        }else {
            Cursor cursor=db.query("SELECT * FROM " + IroadDatabase.TABLE_PROGRAMS);
            int counter=cursor.getCount();
            for(int i=0;i<counter;i++){
                cursor.moveToPosition(i);
                Program program= new Program();
                program=(Program)program.setModel(cursor,program);
                Log.d(TAG,"adding program "+program.getName()+" with uid = "+program.getId());
                programs.add(program);
            }

            Cursor cursor2=db.query("SELECT * FROM "+IroadDatabase.TABLE_DATA_ELEMENTS);
            int count=cursor2.getCount();
            for(int i=0;i<count;i++){
                cursor2.moveToPosition(i);
                DataElements dataElement= new DataElements();
                dataElement=(DataElements)dataElement.setModel(cursor2,dataElement);
                Log.d(TAG,"adding dataElement "+dataElement.getName()+" with uid = "+dataElement.getId());
                dataElements.add(dataElement);
            }

            Fragment newFragment =  new FragmentAlert();
            ((FragmentAlert)newFragment).setOrgUnit(orgUnit);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.activityMain_content_frame, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            drawerTitle.setText(DrawerList[0]);
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
        db.close();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * The action bar home/up action should open or close the drawer.
     * ActionBarDrawerToggle will take care of this.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Populates the activity's options menu.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onComplete(boolean saved, String phoneNumber) {
        FragmentAlert fragment = (FragmentAlert)getFragmentManager().findFragmentById(R.id.activityMain_content_frame);
        fragment.onComplete(saved, phoneNumber);
    }


    /**
     * The click listener for ListView in the navigation drawer
     **/
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
            DrawerListCustomAdapter.selection=position;
            System.gc();
            Log.d(TAG, "Drawer item clicked is = " + DrawerList[position]);
            if(position==0){
                mDrawerLayout.closeDrawer(mDrawer);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(280);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void v){
                        Fragment newFragment = new FragmentAlert();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activityMain_content_frame, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        mDrawerLayout.closeDrawer(mDrawer);
                        drawerListCustomAdapter.selection = 0;
                        drawerListCustomAdapter.notifyDataSetChanged();
                        drawerTitle.setText(DrawerList[position]);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else if(position==1){
                mDrawerLayout.closeDrawer(mDrawer);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(280);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void v){
                        Fragment newFragment = new PharmaciesMapFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activityMain_content_frame, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        mDrawerLayout.closeDrawer(mDrawer);
                        drawerListCustomAdapter.selection = 1;
                        drawerListCustomAdapter.notifyDataSetChanged();
                        drawerTitle.setText(DrawerList[position]);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else if(position==2){
                mDrawerLayout.closeDrawer(mDrawer);
                Log.d(TAG, "Entering Send Feedback");
                mFeedbackDialog.show();
            }
        }
    }


    @Override
    protected void onPause() {
        unregisterReceiver(mHandleMessageReceiver);
        super.onPause();
        mFeedbackDialog.dismiss();
    }

    @Override
    protected void onResume() {
        registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
        ViewServer.get(this).setFocusedWindow(this);
        super.onResume();
    }



    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
            /**
             * Waking up mobile if it is sleeping
             */
            WakeLocker.acquire(getApplicationContext());

            /**
             * Releasing wake lock
             */
            WakeLocker.release();
        }
    };

}