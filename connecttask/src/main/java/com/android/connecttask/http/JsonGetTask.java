package com.android.connecttask.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.android.connecttask.util.Constants.NETWORK_ERROR;
import static com.android.connecttask.util.DataUtils.jsonToMap;

public class JsonGetTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {

    protected String URLPath;

    public JsonGetTask(String URLPath) {
        this.URLPath = URLPath;
    }

    @Override
    protected ArrayList<HashMap<String, Object>> doInBackground(Void... params) {
        ArrayList arrayList = new ArrayList();
        String input = readBugzilla();
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

    private String readBugzilla() {
        StringBuilder builder = new StringBuilder();
        HttpGet httpGet = new HttpGet(URLPath);
        try {
            HttpResponse response = (new DefaultHttpClient()).execute(httpGet);
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

