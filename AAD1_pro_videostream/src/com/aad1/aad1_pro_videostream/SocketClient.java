package com.aad1.aad1_pro_videostream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import com.google.gson.JsonObject;
import android.util.Log;

public class SocketClient extends Thread{
	private Socket mSocket;
	private CameraPreview mCameraPreview;
	private static final String TAG = "socket";
	private String mIP = "10.192.70.223";
	private boolean firstTime = true;
	private int mPort = 6000;
	
	
	
	public SocketClient(CameraPreview preview) {
	    mCameraPreview = preview;
		start();
	} //in case of no connection
	
	
	public SocketClient(CameraPreview preview, String ip, int port) {
	    mCameraPreview = preview;
	    mIP = ip;
	    mPort = port;
		start(); //in case of connection
	}
	
	public JsonObject packageBuilder(String origin, String destination, String type) throws UnsupportedEncodingException{
		JsonObject jsonObj = new JsonObject();
	 	jsonObj.addProperty("origin", origin);
	    jsonObj.addProperty("destination", destination);
	    jsonObj.addProperty("type", "video");
	    jsonObj.addProperty("message", "online");
		return jsonObj;
			
	} //function creating the package of datas to send

	
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {		
			InetAddress serverAddr = InetAddress.getByName(mIP);
			mSocket = new Socket(serverAddr, mPort);
	
			if(firstTime){
				JsonObject jObject = packageBuilder("10.192.23.78", mIP, "video");
				sendMessage2Server(jObject.toString());
				firstTime = false;
			}
	
			while(!Thread.currentThread().isInterrupted()){
				byte[] array = mCameraPreview.getImage();				
				sendArray2Server(array);
				Thread.sleep(50);
                if (Thread.currentThread().isInterrupted())
                    break;
            }  

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		} 
		finally {
			try {
				mSocket.close();
				mSocket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} //making the class work

	private void sendArray2Server(byte[] array){
		OutputStream out;
		try {
			out = mSocket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
	        dos.writeInt(array.length);
	        dos.write(array, 0, array.length);
	        dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}         
	}
	private void sendMessage2Server(String message){
		OutputStream out;
		try {
			out = mSocket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
	        dos.write((message + "\n").getBytes());
	        dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}         
	}
	
	public void close() {
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} //finishing the class
	
}
