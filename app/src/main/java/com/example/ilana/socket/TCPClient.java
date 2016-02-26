package com.example.ilana.socket;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

//Singleton class
public class TCPClient {

    private static TCPClient instance = null;
    private String serverMessage;
    private final String SERVERIP ;//ip
    public  final int SERVERPORT;//port
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    OutputStream out;
    // PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    private TCPClient(OnMessageReceived listener, String ip1, String port1) {
        mMessageListener = listener;
            SERVERIP = ip1;
            SERVERPORT = Integer.parseInt(port1);
       // out = new PrintWriter(System.out);
    }
    public static TCPClient getInstance(OnMessageReceived listener, String ip1, String port1) {
        if(instance == null) {
            instance = new TCPClient(listener,ip1,port1);
        }
        return instance;
    }
    public static TCPClient getInstance(){
        if(instance != null){
            return instance;
        }
        return null;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
       /* if (out != null && !out.checkError()) {
            //out.println(message);
            out.write(message);
            out.flush();
        }*/

        if (out != null ) {
           // message.getBytes();
           // out.println(message);

            try {
                out.write(message.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopClient(){
        mRun = false;
    }
    //Create socket
    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
            //socket.getOutputStream();

            try {
                //send the message to the server
               // out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
               out = socket.getOutputStream();
               // out =  new OutputStream(socket.getOutputStream());
                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();
                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}