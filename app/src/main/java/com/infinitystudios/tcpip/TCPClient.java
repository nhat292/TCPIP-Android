package com.infinitystudios.tcpip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Nhat on 4/10/18.
 */

public class TCPClient {

    public interface OnMessageListener {
        void onMessageReceived(String message);

        void onConnected();
    }

    private String mServerIp;
    private String mPort;
    private OnMessageListener mListener;
    private PrintWriter mPrintWriter;
    private BufferedReader mBufferedReader;
    private boolean mRun = false;
    private String mServerMessage;

    public TCPClient(String serverIp, String port, OnMessageListener listener) {
        this.mServerIp = serverIp;
        this.mPort = port;
        this.mListener = listener;
    }

    /**
     * Send message to server
     *
     * @param message
     */
    public void sendMessage(String message) {
        if (mPrintWriter != null && !mPrintWriter.checkError()) {
            mPrintWriter.println(message);
            mPrintWriter.flush();
        }
    }

    /**
     * Stop TCP/IC connection
     */
    public void stopClient() {
        mRun = false;

        if (mPrintWriter != null) {
            mPrintWriter.flush();
            mPrintWriter.close();
        }

        mListener = null;
        mBufferedReader = null;
        mPrintWriter = null;
    }

    /**
     * Start TCP/IP connection
     */
    public void startClient() {

        mRun = true;

        try {
            // Create server address
            InetAddress serverAddress = InetAddress.getByName(mServerIp);

            // Create a socket connect to server
            try (Socket socket = new Socket(serverAddress, Integer.parseInt(mPort))) {

                // Sends message to server
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                // Receives message from server
                mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Send callback to tells client that socket connected successfully
                if (mListener != null) {
                    mListener.onConnected();
                }

                // Listens for message from server
                while (mRun) {
                    mServerMessage = mBufferedReader.readLine();
                    if (mServerMessage != null && mListener != null) {
                        mListener.onMessageReceived(mServerMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
