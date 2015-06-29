package wappi.iRoadCommunityPolicing.ServerCommunication;


import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {
    public static final String TAG = JSONParser.class.getSimpleName();
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public JSONParser() {
    }

    public JSONArray makeHttpRequest(String url, String method,JSONObject obj ) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                StringEntity se = new StringEntity(obj.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();


            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                String json=obj.toString();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex){
            Log.d("Netxxxxx", ex.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        JSONArray array=null;
        // try parse the string to a JSON object
        try {
            array = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return array;

    }

    public JSONObject makeHttpRequestReturnJsonObject(String url, String method,JSONObject obj ) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                StringEntity se = new StringEntity(obj.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();


            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                String json=obj.toString();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex){
            Log.d("Netxxxxx", ex.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        JSONObject jsonObject=null;
        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jsonObject;

    }

    public JSONObject dhis2HttpRequest(String url, String method, String username, String password, JSONObject obj) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){

                HttpParams myParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(myParams, 10000);
                HttpConnectionParams.setSoTimeout(myParams, 10000);
                HttpClient httpclient = new DefaultHttpClient(myParams );
                String json=obj.toString();
                Log.d(TAG, "string created from json" + json);

                try {

                    HttpPost httppost = new HttpPost(url.toString());
                    httppost.setHeader("Content-type", "application/json");
                    String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                            (username + ":" + password).getBytes(),
                            Base64.NO_WRAP);

                    Log.d(TAG,"encoded credentials = "+base64EncodedCredentials);

                    httppost.setHeader("Authorization", base64EncodedCredentials);


                    Log.d(TAG, "URL Host: "+httppost.getURI().getHost());
                    Log.d(TAG, "URL Path: "+httppost.getURI().getPath());

                    Log.d(TAG, "URL: "+url.trim());

                    StringEntity se = new StringEntity(obj.toString());
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httppost.setEntity(se);

                    HttpResponse httpResponse = httpclient.execute(httppost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

            }else if(method == "PUT"){

                HttpParams myParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(myParams, 10000);
                HttpConnectionParams.setSoTimeout(myParams, 10000);
                HttpClient httpclient = new DefaultHttpClient(myParams );
                String json=obj.toString();
                Log.d(TAG,"string created from json"+json);

                try {

                    HttpPut httpput = new HttpPut(url.toString());
                    httpput.setHeader("Content-type", "application/json");
                    String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                            (username + ":" + password).getBytes(),
                            Base64.NO_WRAP);

                    Log.d(TAG,"encoded credentials = "+base64EncodedCredentials);

                    httpput.setHeader("Authorization", base64EncodedCredentials);


                    Log.d(TAG, "URL Host: " + httpput.getURI().getHost());
                    Log.d(TAG, "URL Path: "+httpput.getURI().getPath());

                    Log.d(TAG, "URL: " + url.trim());

                    StringEntity se = new StringEntity(obj.toString());
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httpput.setEntity(se);

                    HttpResponse httpResponse = httpclient.execute(httpput);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                        (username + ":" + password).getBytes(),
                        Base64.NO_WRAP);

                Log.d(TAG,"encoded credentials = "+base64EncodedCredentials);

                httpGet.setHeader("Authorization", base64EncodedCredentials);

                httpGet.setHeader("Accept","application/json");

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();



            }

        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "UnsupportedEncodingException = "+e.getMessage());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.d(TAG, "ClientProtocolException = "+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException = "+e.getMessage());
            e.printStackTrace();
        } catch (Exception ex){
            Log.d(TAG,"Network exception"+ex.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        JSONObject object=null;
        // try parse the string to a JSON object
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return object;

    }




}