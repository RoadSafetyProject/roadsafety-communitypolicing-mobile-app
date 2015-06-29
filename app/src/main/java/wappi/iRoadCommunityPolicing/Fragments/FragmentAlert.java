package wappi.iRoadCommunityPolicing.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wappi.iRoadCommunityPolicing.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import wappi.iRoadCommunityPolicing.Dhis2.DHIS2Config;
import wappi.iRoadCommunityPolicing.Dhis2.DHIS2Modal;
import wappi.iRoadCommunityPolicing.IroadDatabase;
import wappi.iRoadCommunityPolicing.MainActivity;
import wappi.iRoadCommunityPolicing.RegistrationDialogue;
import wappi.iRoadCommunityPolicing.ServerCommunication.JSONParser;
import wappi.iRoadCommunityPolicing.Utils.AndroidMultiPartEntity;
import wappi.iRoadCommunityPolicing.Utils.Functions;

public class FragmentAlert extends Fragment  implements RegistrationDialogue.OnCompleteListener{
    private RelativeLayout contentView;
    private Uri fileUri;
    private String orgUnit;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "TanescoFiles";
    private boolean isImage;
    private ImageView imagePreveiw;
    private VideoView videoPreview;
    private String imagePath,videoPath;
    private ImageButton imageCapture,videoCapture;
    private CardView sendbutton;
    private static final String TAG=FragmentAlert.class.getSimpleName();
    private double mLat,mLong;
    private Timer gpsTimer = new Timer();
    private Location lastLocation=null;
    private long totalSize = 0;
    private MaterialEditText title, description;
    private String ipaddress;
    private String placeName;
    private ProgressBar progressBar;
    private TextView signupText;
    private String urlVideo="",urlImage="",eventTypeSelected="Accident";
    private SharedPreferences sharedPrefs;
    private String phoneNumber;

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    IroadDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        startRecording();


        db=new IroadDatabase(getActivity());
        contentView=(RelativeLayout)inflater.inflate(R.layout.fragment_alerts, container, false);

        imagePreveiw = (ImageView) contentView.findViewById(R.id.image_preview);
        imageCapture = (ImageButton)contentView.findViewById(R.id.capture_image);
        videoCapture = (ImageButton)contentView.findViewById(R.id.capture_video);
        videoPreview = (VideoView)contentView.findViewById(R.id.video_preview);
        description = (MaterialEditText)contentView.findViewById(R.id.message);
        title = (MaterialEditText)contentView.findViewById(R.id.title);
        progressBar = (ProgressBar)contentView.findViewById(R.id.progressBar);
        signupText = (TextView)contentView.findViewById(R.id.signup_text);

        final List<String> eventType = new ArrayList<String>();
        eventType.add(" Offence  ");
        eventType.add(" Accident ");
        eventType.add(" Others   ");


        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,eventType);
        eventTypeAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        Spinner eventTypeSpinner = (Spinner)contentView.findViewById(R.id.event_type);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        eventTypeSelected = "Offence";
                        break;
                    case 1:
                        eventTypeSelected = "Accident";
                        break;
                    case 2:
                        eventTypeSelected = "Others";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sendbutton = (CardView)contentView.findViewById(R.id.send);

        imageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        videoCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Alert orgunit = "+orgUnit);
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                phoneNumber = sharedPrefs.getString("phoneNumber","");

                if(phoneNumber.equals("")) {
                    RegistrationDialogue registrationDialogue = new RegistrationDialogue();
                    registrationDialogue.show(getFragmentManager(), "registrationDialogue");
                }else {
                    Log.d(TAG, " send button clicked ");
                    try {
                        Location location = getBestLocation();
                        mLat = location.getLatitude();
                        mLong = location.getLongitude();
                        placeName = getAddress(mLat, mLong);

                        File[] files = new File[4];
                        try {
                            files[0] = new File(videoPath);

                        } catch (Exception e) {
                            files[0] = new File("");
                        }


                        try {
                            files[1] = new File(imagePath);

                        } catch (Exception e) {
                            files[1] = new File("");
                        }

                        UploadFileToServer.execute(files);
                    } catch (NullPointerException e) {
                        Functions.displayPromptForEnablingGPS(getActivity());

                    }
                }

            }
        });

        return contentView;
    }

    /**
     * Capturing Camera Image will launch camera app request image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Recording video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // successfully captured the image
                // display it in image view
                setFullImageFromFilePath(fileUri.getPath(), imagePreveiw);
                imagePath=fileUri.getPath();
                isImage=true;
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                previewVideo();
                isImage=false;
                videoPath = fileUri.getPath();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        //videoPreview.setVisibility(View.GONE);

        imagePreveiw.setVisibility(View.VISIBLE);
        videoPreview.setVisibility(View.INVISIBLE);
        // Get the dimensions of the View
        int targetW = 720;
        int targetH = 500;//previewLayout.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);


    }

    /**
     * Previewing recorded video
     */
    private void previewVideo() {
        try {

            videoPath=fileUri.getPath();

            //TODO change this
            videoPreview.setVisibility(View.VISIBLE);
            imagePreveiw.setVisibility(View.INVISIBLE);

            videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        imagePreveiw.setImageBitmap(resizedBitmap);
        return resizedBitmap;

    }


    AsyncTask  UploadFileToServer = new  AsyncTask<File, Integer, JSONObject>() {

        @Override
        protected JSONObject doInBackground(File... params) {
            File [] files = params;
            int counter = params.length;
            for(int i=0;i<counter;i++) {
                JSONObject resultjson = uploadFile(files[i]);
                try {
                    String responceUrl = resultjson.getJSONArray("documents").getJSONObject(0).getString("href");
                    switch (i){
                        case 0:
                            urlVideo = responceUrl;
                            break;
                        case 1:
                            urlImage = responceUrl;
                            break;
                    }

                } catch (Exception e) {
                    switch (i){
                        case 0:
                            urlVideo = "";
                            break;
                        case 1:
                            urlImage = "";
                            break;
                    }
                }
            }

            Location location =getBestLocation();
            mLat=location.getLatitude();
            mLong=location.getLongitude();

            String placeName=getAddress(mLat,mLong);
            Calendar cl = Calendar.getInstance();


            JSONObject eventCommunityPolicing = new JSONObject();
            DHIS2Modal communityPoliceModal = new DHIS2Modal("Community Police",null, MainActivity.username,MainActivity.password);

            String communityPoliceProgramUid = communityPoliceModal.getProgramByName("Community Police").getId();

            JSONObject coordinatesObject = new JSONObject();
            try {
                coordinatesObject.put("latitude",mLat);
                coordinatesObject.put("longitude",mLong);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                eventCommunityPolicing.put("program", communityPoliceProgramUid);
                eventCommunityPolicing.put("orgUnit", orgUnit);
                eventCommunityPolicing.put("eventDate", Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));
                eventCommunityPolicing.put("coordinate", coordinatesObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray values = new JSONArray();
            JSONObject CommunityReportDescriptionDataElement = new JSONObject();
            String CommunityReportDescriptioneUid = communityPoliceModal.getDataElementByName("Community Report Description").getId();
            try {
                CommunityReportDescriptionDataElement.put("dataElement",CommunityReportDescriptioneUid);
                CommunityReportDescriptionDataElement.put("value",description.getText().toString());
                values.put(CommunityReportDescriptionDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject CommunityReportTitlerDataElement = new JSONObject();
            String CommunityReportTitlerDataElementUid = communityPoliceModal.getDataElementByName("Community Report Title").getId();
            try {
                CommunityReportTitlerDataElement.put("dataElement",CommunityReportTitlerDataElementUid);
                CommunityReportTitlerDataElement.put("value",title.getText().toString());
                values.put(CommunityReportTitlerDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject CommunityReportTypeDataElement = new JSONObject();
            String CommunityReportTypeDataElementDataElementtUid = communityPoliceModal.getDataElementByName("Community Report Type").getId();
            try {
                CommunityReportTypeDataElement.put("dataElement",CommunityReportTypeDataElementDataElementtUid);
                CommunityReportTypeDataElement.put("value",eventTypeSelected);
                values.put(CommunityReportTypeDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject CommunityReportImageDataElement = new JSONObject();
            String CommunityReportImageDataElementUid = communityPoliceModal.getDataElementByName("Community Report Image").getId();
            try {
                CommunityReportImageDataElement.put("dataElement",CommunityReportImageDataElementUid);
                CommunityReportImageDataElement.put("value",urlImage);
                values.put(CommunityReportImageDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject CommunityReportVideoDataElement = new JSONObject();
            String CommunityReportVideoDataElementUid = communityPoliceModal.getDataElementByName("Community Report Video").getId();
            try {
                CommunityReportVideoDataElement.put("dataElement",CommunityReportVideoDataElementUid);
                CommunityReportVideoDataElement.put("value",urlVideo);
                values.put(CommunityReportVideoDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                eventCommunityPolicing.put("dataValues", values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG," community policing sent to the server  = "+eventCommunityPolicing);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL+"api/events","POST",MainActivity.username,MainActivity.password,eventCommunityPolicing);

            Log.d(TAG," responce from the server  = "+jsonObject);
            return jsonObject;
        }


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            title.setKeyListener(null);
            description.setKeyListener(null);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            signupText.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity().getApplicationContext(),
                    "uploading Files and information to the server", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject aVoid) {
            super.onPostExecute(aVoid);
            title.setTag(title.getKeyListener());
            title.setKeyListener((KeyListener)title.getTag());


            description.setTag(description.getKeyListener());
            description.setKeyListener((KeyListener)description.getTag());

            signupText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity().getApplicationContext(),
                    "Files uploaded successfully", Toast.LENGTH_LONG).show();

            Log.d(TAG,"video file url = "+urlVideo);
            Log.d(TAG,"image file url = "+urlImage);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            super.onProgressUpdate(progress);
        }

        @SuppressWarnings("deprecation")
        private JSONObject uploadFile(File sourceFile) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(DHIS2Config.BASE_URL+"dhis-web-reporting/saveDocument.action");
            String base64EncodedCredentials = "Basic " + Base64.encodeToString((MainActivity.username + ":" + MainActivity.password).getBytes(),Base64.NO_WRAP);

            Log.d(TAG,"encoded credentials = "+base64EncodedCredentials);

            httppost.setHeader("Authorization", base64EncodedCredentials);


            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                //TODO obtain both video path and imagePath from Accident object
                try{
                    Log.d(TAG,"file name = "+sourceFile.getName());
                    entity.addPart("upload", new FileBody(sourceFile));
                    entity.addPart("name", new StringBody(sourceFile.getName()));
                    entity.addPart("external", new StringBody("false"));
                    entity.addPart("id", new StringBody(""));
                    entity.addPart("url", new StringBody("http://"));
                }catch (Exception e){
                    Log.d(TAG, "error adding a file to a post");
                }


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                    JSONParser jsonParser = new JSONParser();
                    try {
                        JSONObject object = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL + "api/documents.json?paging=false&filter=name:eq:" + sourceFile.getName(), "GET", MainActivity.username, MainActivity.password, null);
                        return object;
                    }catch (Exception e){
                        return null;
                    }
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return null;
        }
    };

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            fileUri = savedInstanceState.getParcelable("file_uri");
        }catch (NullPointerException e){}
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private Location getLocationByProvider(String provider) {
        Location location = null;
        LocationManager locationManager = (LocationManager)getActivity().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot acces Provider " + provider);
        }
        return location;
    }

    /**
     * Start listening and recording locations
     */
    public void startRecording() {
        gpsTimer.cancel();
        gpsTimer = new Timer();
        long checkInterval = 60*1000;
        long minDistance = 1000;
        // receive updates
        LocationManager locationManager = (LocationManager)getActivity().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        for (String s : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, new LocationListener() {

                        @Override
                        public void onStatusChanged(String provider,
                                                    int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}

                        @Override
                        public void onLocationChanged(Location location) {
                            // if this is a gps location, we can use it
                            if (location.getProvider().equals(
                                    LocationManager.GPS_PROVIDER)) {
                                doLocationUpdate(location, true);
                            }
                        }
                    });
        }
        // start the gps receiver thread
        gpsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Location location = getBestLocation();
                doLocationUpdate(location, false);
            }
        }, 0, checkInterval);
    }

    /**
     * Performe a location update either by force or due to location or distance change
     * @param l
     * @param force
     */
    public void doLocationUpdate(Location l, boolean force) {
        long minDistance = 1000;
        Log.d(TAG, "update received:" + l);
        if (l == null) {
            Log.d(TAG, "Empty location");
            if (force)
                Toast.makeText(getActivity(), "Current location not available",
                        Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastLocation != null) {
            float distance = l.distanceTo(lastLocation);
            Log.d(TAG, "Distance to last: " + distance);
            if (l.distanceTo(lastLocation) < minDistance && !force) {
                Log.d(TAG, "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= lastLocation.getAccuracy()
                    && l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
                Log.d(TAG,
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
        }
    }

    /**
     * try to get the 'best' location selected from all providers
     */
    private Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =  getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d(TAG, "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d(TAG, "No Network Location available");
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - 1*60*60*1000;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add ="";
            if(obj.getAdminArea()!=null){
                add = add + obj.getAdminArea();
            }
            if (obj.getSubAdminArea()!=null){
                add = add + ", "+obj.getSubAdminArea();
            }
            if (obj.getAddressLine(0)!=null){
                add = add + ", "+ obj.getAddressLine(0);
            }
            address = add;

            Log.v("IGA", "Address" + add);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){

        }
        return address;
    }


    @Override
    public void onComplete(boolean saved, String phoneNumber) {
        if(saved){
            this.phoneNumber=phoneNumber;
        }
    }
}
