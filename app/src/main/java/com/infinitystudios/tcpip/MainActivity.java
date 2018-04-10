package com.infinitystudios.tcpip;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTxtMessage;
    private EditText mEditMessage;
    private TCPClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtMessage = findViewById(R.id.txtMessage);
        mEditMessage = findViewById(R.id.editMessage);

        startClient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new DisconnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onSendClick(View view) {
        String message = mEditMessage.getText().toString().trim();
        if (message.isEmpty()) return;
        mEditMessage.setText(null);
        new SendMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
    }

    private void startClient() {
        App app = (App) getApplicationContext();
        new ConnectTask().execute(app.getValue("ip"), app.getValue("port"));
    }


    private class SendMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            mClient.sendMessage(strings[0]);
            return null;
        }
    }

    private class ConnectTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            mClient = new TCPClient(strings[0], strings[1], new TCPClient.OnMessageListener() {
                @Override
                public void onMessageReceived(String message) {
                    publishProgress(message);
                }

                @Override
                public void onConnected() {
                    publishProgress("You have connected");
                }
            });
            mClient.startClient();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mTxtMessage.setText(values[0]);
            if (values[0].endsWith("%NAME")) {
                new SendMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "&NAME|Client" + System.currentTimeMillis());
            }
        }
    }

    private class DisconnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mClient.stopClient();
            mClient = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTxtMessage.setText("You have disconnected");
        }
    }
}
