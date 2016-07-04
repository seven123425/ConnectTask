package com.android.connecttask.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.android.connecttask.util.Constants.NETWORK_ERROR;
import static com.android.connecttask.util.DataUtils.jsonToMap;

public class JsonPostTask extends AsyncTask<List<NameValuePair>, Void, ArrayList<HashMap<String, Object>>> {

    protected String URLPath;

    public JsonPostTask(String URLPath) {
        this.URLPath = URLPath;
    }

    @Override
    protected ArrayList<HashMap<String, Object>> doInBackground(List<NameValuePair>... params) {
        ArrayList arrayList = new ArrayList();
        String input = readBugzilla(params[0]);
        if (!input.equals("")) {
            try {
                JSONArray json = new JSONArray(input);
                for (int i = 0; i < json.length(); i++) {
                    arrayList.add(jsonToMap(json.getJSONObject(i)));
                }
            } catch (Exception e) {
                try {
                    JSONObject json = new JSONObject(input);
                    arrayList.add(jsonToMap(json));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(NETWORK_ERROR, NETWORK_ERROR);
            arrayList.add(hm);
        }
        return arrayList;
    }

    private String readBugzilla(List<NameValuePair> params) {
        StringBuilder builder = new StringBuilder();
        HttpPost httpPost = new HttpPost(URLPath);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = (new DefaultHttpClient()).execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Error", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
