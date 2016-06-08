package sth.com.httprequesttemplate.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by sth on 6/8/16.
 */
public class HttpService extends Service {

    private final HttpServiceBinder mBinder = new HttpServiceBinder();

    // service's handler
    private HandlerThread thread;
    private Handler handler;


    @Override
    public void onCreate() {
        super.onCreate();
        thread = new HandlerThread("HttpServiceThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper looper = thread.getLooper();
        handler = new Handler(looper);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.quit();
    }



    public boolean get(final String fullUrl, final HashMap<String, String> queries, final int timeout, final String acceptType, final ResultReceiver resultReceiver) {
        return handler.post(new Runnable() {
            @Override
            public void run() {
                // build url
                String urlString = "http://"+fullUrl+"/?";
                if (queries != null) {
                    for (String key : queries.keySet()) {
                        String value = queries.get(key);
                        urlString += key + "=" + value + "&";
                    }
                }

                try {
                    URL url = new URL(urlString);
                    // open connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(timeout);
                    connection.setRequestProperty("Accept", acceptType);
                    connection.connect();
                    if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        String responseString = sb.toString();
                        Bundle resultBundle = new Bundle();
                        resultBundle.putString("stringResponse", responseString);
                        System.out.println(responseString);
                        // callback
                        if (resultReceiver != null) {
                            resultReceiver.send(HttpResultCodes.GET, resultBundle);
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class HttpServiceBinder extends Binder {
        public HttpService getService() {
            return HttpService.this;
        }
    }
}
