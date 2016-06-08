package sth.com.httprequesttemplate;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import sth.com.httprequesttemplate.services.HttpService;

/**
 * Created by sth on 6/8/16.
 */
public class HttpGetFragment extends Fragment {

    private HttpService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((HttpService.HttpServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Bind to httpService
        Activity activity = getActivity();
        Intent connectServiceIntent = new Intent(activity.getApplicationContext(), HttpService.class);
        activity.bindService(connectServiceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        activity.unbindService(connection);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_http_get_fragment, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupUIComponents();
    }

    private void setupUIComponents() {
        View mainView = getView();
        assert mainView != null;
        final EditText urlEditText = (EditText) mainView.findViewById(R.id.getUrlEditText);
        Button sendBtn = (Button) mainView.findViewById(R.id.sendGetBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (service != null) {
                    String url = urlEditText.getText().toString();
                    service.get(url, null, 100000, "application/json", null);
                }
            }
        });

    }
}
