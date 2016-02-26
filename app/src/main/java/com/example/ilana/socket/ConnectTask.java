package com.example.ilana.socket;

import android.os.AsyncTask;

public class ConnectTask extends AsyncTask<String,String,TCPClient> {
    String ip1;
    String port1;
    public static TCPClient mTcpClient;

    public ConnectTask(String ip, String port) {
        ip1=ip;
        port1=port;
    }

    @Override
    protected TCPClient doInBackground(String... message) {
        //we create a TCPClient object and
        mTcpClient = TCPClient.getInstance(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        },ip1,port1);
        mTcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

}
