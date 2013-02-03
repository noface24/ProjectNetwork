package teaonly.droideye;

import ict.overload.OverClyne.R;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

public class TeaServer extends NanoHTTPD {
	public BluetoothAdapter btAdt;
	public BluetoothDevice btDv;
	private CameraView cameraView_;
	public BlueToothService btSv;
	static final String TAG = "TEAONLY";

	public TeaServer(int port, Context ctx) throws IOException {
		super(port, ctx.getAssets());
		// this.main = m;
		btAdt = BluetoothAdapter.getDefaultAdapter();
		btSv = new BlueToothService(output);
	}

	public TeaServer(int port, String wwwroot) throws IOException {
		super(port, new File(wwwroot).getAbsoluteFile());
		btAdt = BluetoothAdapter.getDefaultAdapter();
		btSv = new BlueToothService(output);
	}

	Handler output = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BlueToothService.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
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
				// construct a string from the valid bytes in the buffer
				readMessage = new String(readBuf, 0, msg.arg1);
				break;
			case BlueToothService.MESSAGE_WRITE:
				// mIsWriting = false;
				break;
			case BlueToothService.MESSAGE_TOAST:
				break;
			}
		};
	};
	private String readMessage = "";
	public static boolean flashOn = false;

	public void sentcommand(String cmd) {
		btSv.write(cmd.getBytes());

	}

//	public class flash {
//		public boolean status() {
//			return flashOn;
//		}
//	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		Log.d(TAG, "httpd request >>" + method + " '" + uri + "' " + "   "
				+ parms);
		
		if (parms.getProperty("comm").equals("SetServoON")) {
			sentcommand("v");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");
		}
		if (parms.getProperty("comm").equals("SetServoOFF")) {
			sentcommand("o");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");
		}
		if (parms.getProperty("comm").equals("x")) {
			sentcommand("x");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");
		}
		if (parms.getProperty("comm").equals("y")) {
			sentcommand("y");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");
		}
		if (parms.getProperty("comm").equals("sonic")) {
			sentcommand("u");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");
		}
		if (parms.getProperty("comm").equals("sonic_int")) {
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, readMessage);
		}
		if (parms.getProperty("comm").equals("sn")) {
			sentcommand("n");
			sentcommand("s");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("beep")) {
			MediaPlayer mp = MediaPlayer.create(MainActivity.getIns(),
					R.raw.war_explosion2);
			mp.start();
		}
		if (parms.getProperty("comm").equals("gn")) {
			sentcommand("g");
			sentcommand("n");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("gr")) {
			sentcommand("g");
			sentcommand("r");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("gl")) {
			sentcommand("g");
			sentcommand("l");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("br")) {
			sentcommand("b");
			sentcommand("r");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("bn")) {
			sentcommand("b");
			sentcommand("n");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("bl")) {
			sentcommand("b");
			sentcommand("l");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("sn")) {
			sentcommand("s");
			sentcommand("n");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}

		if (parms.getProperty("comm").equals("sl")) {
			sentcommand("s");
			sentcommand("l");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");

		}
		if (parms.getProperty("comm").equals("sr")) {
			sentcommand("s");
			sentcommand("r");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "0");
		}
		if (parms.getProperty("comm").equals("flash")) {
			MainActivity.flashcamera();
		}



		if (uri.startsWith("/cgi/")) {
			return serveCGI(uri, method, header, parms, files);
		} else if (uri.startsWith("/stream/")) {
			return serveStream(uri, method, header, parms, files);
		} else {
			return super.serve(uri, method, header, parms, files);
		}
	}

	public Response serveStream(String uri, String method, Properties header,
			Properties parms, Properties files) {
		CommonGatewayInterface cgi = cgiEntries.get(uri);
		if (cgi == null)
			return null;

		InputStream ins;
		ins = cgi.streaming(parms);
		if (ins == null)
			return null;

		Random rnd = new Random();
		String etag = Integer.toHexString(rnd.nextInt());
		String mime = parms.getProperty("mime");
		if (mime == null)
			mime = "application/octet-stream";
		Response res = new Response(HTTP_OK, mime, ins);
		res.addHeader("ETag", etag);
		res.isStreaming = true;

		return res;
	}

	public Response serveCGI(String uri, String method, Properties header,
			Properties parms, Properties files) {
		CommonGatewayInterface cgi = cgiEntries.get(uri);
		if (cgi == null)
			return null;

		String msg = cgi.run(parms);
		if (msg == null)
			return null;

		Response res = new Response(HTTP_OK, MIME_PLAINTEXT, msg);
		return res;
	}

	@Override
	public void serveDone(Response r) {
		try {
			if (r.isStreaming) {
				r.data.close();
			}
		} catch (IOException ex) {
		}
	}

	public static interface CommonGatewayInterface {
		public String run(Properties parms);

		public InputStream streaming(Properties parms);
	}

	private HashMap<String, CommonGatewayInterface> cgiEntries = new HashMap<String, CommonGatewayInterface>();

	public void registerCGI(String uri, CommonGatewayInterface cgi) {
		if (cgi != null)
			cgiEntries.put(uri, cgi);
	}

}
