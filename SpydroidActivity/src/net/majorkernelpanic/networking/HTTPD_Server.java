package net.majorkernelpanic.networking;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.widget.Toast;

public class HTTPD_Server extends NanoHTTPD {
	public  BluetoothAdapter btAdt;
	public  BluetoothDevice btDv;
	public  BlueToothService btSv;
	public HTTPD_Server() throws IOException {
		super(8088, new File("."));
		btAdt = BluetoothAdapter.getDefaultAdapter();        
	    btSv = new BlueToothService(output);

	}
	Handler output = new Handler(){
    	@Override
    	public void handleMessage(android.os.Message msg) {
    		 switch(msg.what){
             case BlueToothService.MESSAGE_STATE_CHANGE:
                 switch(msg.arg1){
                     case BlueToothService.STATE_NONE:
                        
                         break;
                     case BlueToothService.STATE_CONNECTING:
                         
                         break;
                     case BlueToothService.STATE_CONNECTED:
                         break;
                 }
                 break;
             case BlueToothService.MESSAGE_READ:
                 byte[] readBuf = (byte[]) msg.obj;
                 //float f = Helpers.byteArrayToFloat(readBuf);
                 break;
             case BlueToothService.MESSAGE_WRITE:
                 //mIsWriting = false;
                 break;
             case BlueToothService.MESSAGE_TOAST:
                 break;
    		 }
    	};
    };
    public void sentcommand(String cmd){
    	btSv.write(cmd.getBytes());
    }
    

	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		System.out.println(method + " '" + uri + "' ");
			if (parms.getProperty("username").equals("g")) {
				return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, parms.getProperty("username"));
			}
			if (parms.getProperty("username").equals("b")) {
				return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, parms.getProperty("username"));
			}
			if (parms.getProperty("username") == "s") {
				return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, parms.getProperty("username"));
			}
			if (parms.getProperty("username") == "r") {
				return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, parms.getProperty("username"));
			}
			if (parms.getProperty("username") == "l") {
				return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, parms.getProperty("username"));
			}
			if (parms.getProperty("username") == "n") {
				return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, parms.getProperty("username"));
			}
		return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "1");
	}
	
	

}
