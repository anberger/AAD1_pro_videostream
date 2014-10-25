package com.aad1.aad1_pro_videostream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class SocketClient extends Thread{
	private Socket mSocket;
	private CameraPreview mCameraPreview;
	private static final String TAG = "socket";
	private String mIP = "";
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
		
	 	byte[] b = mCameraPreview.getImageBuffer();
	 
		String encodedImage = new String(b, "UTF-8");
	
	 	//String encodedImage = Base64.encodeToString(b, 0, b.length, Base64.NO_WRAP);
	 	
		JsonObject jsonObj = new JsonObject();
	 	jsonObj.addProperty("length", mCameraPreview.getPreviewLength());
	    jsonObj.addProperty("width", mCameraPreview.getPreviewWidth());
	    jsonObj.addProperty("height", mCameraPreview.getPreviewHeight());
	    jsonObj.addProperty("clienttype", "video");
	 	jsonObj.addProperty( "IMAGE", encodedImage);
			return jsonObj;
			
	} //function creating the package of datas to send

	
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			mSocket = new Socket();
        	JsonObject jObject = packageBuilder("10.192.23.78", "10.192.70.153", "video");
        	JsonElement encodedImageTransmitted = jObject.get("IMAGE"); //gets the image coded
        	String a = encodedImageTransmitted.toString(); //turns the encoded image into a string
        	//byte[] f = Base64.decode(a,Base64.NO_WRAP);
        	
        	byte[] f = a.getBytes("UTF-8");

        	//byte[] f = a.getBytes();
        	Bitmap bitmap = BitmapFactory.decodeByteArray(f , 0, f.length);        	
        	//byte[] data1 = Base64.decode(a, Base64.DEFAULT);
        	//Bitmap decodedByte = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			int i=0;
			mSocket.connect(new InetSocketAddress(mIP, mPort), 10000); // hard-code server address
			BufferedOutputStream outputStream = new BufferedOutputStream(mSocket.getOutputStream());
			//BufferedInputStream inputStream = new BufferedInputStream(mSocket.getInputStream());     
			
			
            while (mSocket.isConnected()) {
            	outputStream.write((jObject.toString()  + "\n").getBytes());
                //outputStream.write(mCameraPreview.getImageBuffer());
                //outputStream.flush();
                if (Thread.currentThread().isInterrupted())
                    break;
            }  
            
           byte[] buff = new byte[256];
           int len = 0;
           String msg = null;

//            while ((len = inputStream.read(buff)) != -1) {
//                msg = new String(buff, 0, len);
//                // JSON analysis
//                JsonParser parser = new JsonParser();
//                boolean isJSON = true;
//                JsonElement element = null;
//                
//                try {
//                    element =  parser.parse(msg);
//                }
//                catch (JsonParseException e) {
//                    Log.e(TAG, "exception: " + e);
//                    isJSON = false;
//                }
//                if (isJSON && element != null) {
//                    JsonObject obj = element.getAsJsonObject();
//                    element = obj.get("state");
//                    
//                    if (element != null && element.getAsString().equals("ok")) {
//                        // send data
//                        while (true) {
//                        	String coucou = mCameraPreview.getImageBuffer().toString();
//                        	
//                        	JsonObject jObject = packageBuilder("10.192.23.78", "10.192.70.153", "video", coucou);
//                        	
//                        	outputStream.write(jObject.toString().getBytes());
//                            outputStream.write(mCameraPreview.getImageBuffer());
//                            outputStream.flush();
//                            if (Thread.currentThread().isInterrupted())
//                                break;
//                        }  
//                        break;
//                    }
//                }
//                else {
//                    break;
//                }
//            }

			//outputStream.close();
			//inputStream.close();
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
