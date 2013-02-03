package teaonly.droideye;

import ict.overload.OverClyne.R;
import java.io.File;
import java.io.IOException;
import java.util.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class HTTPD_Server extends NanoHTTPD {
	public BluetoothAdapter btAdt;
	public BluetoothDevice btDv;
	public BlueToothService btSv;

	public HTTPD_Server() throws IOException {
		super(8090, new File("."));
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
				// float f = Helpers.byteArrayToFloat(readBuf);
				break;
			case BlueToothService.MESSAGE_WRITE:
				// mIsWriting = false;
				break;
			case BlueToothService.MESSAGE_TOAST:
				break;
			}
		};
	};

	public void sentcommand(String cmd) {
		btSv.write(cmd.getBytes());
	}

	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		if (parms.getProperty("comm").equals("sn")) {
			sentcommand("n");
			sentcommand("s");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "sn");
		}
		if (parms.getProperty("comm").equals("gn")) {
			sentcommand("g");
			sentcommand("n");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "gn");
		}
		if (parms.getProperty("comm").equals("gr")) {
			sentcommand("g");
			sentcommand("r");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "gr");
		}
		if (parms.getProperty("comm").equals("sr")) {
			sentcommand("g");
			sentcommand("r");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "sr");
		}
		if (parms.getProperty("comm").equals("br")) {
			sentcommand("r");
			sentcommand("b");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "br");
		}
		if (parms.getProperty("comm").equals("bn")) {
			sentcommand("b");
			sentcommand("n");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "bn");
		}
		if (parms.getProperty("comm").equals("gl")) {
			sentcommand("g");
			sentcommand("l");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "gl");
		}

		if (parms.getProperty("comm").equals("bl")) {
			sentcommand("b");
			sentcommand("l");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "bl");
		}
		if (parms.getProperty("comm").equals("sl")) {
			sentcommand("g");
			sentcommand("l");
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "sl");
		}

		return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, "1");

	}

	public static void main(String[] args) {
		try {
			new HTTPD_Server();
		} catch (IOException ioe) {
			System.err.println("Couldn't start server:\n" + ioe);
			System.exit(-1);
		}
		System.out.println("Listening on port 8080. Hit Enter to stop.\n");
		try {
			System.in.read();
		} catch (Throwable t) {
		}
		;
	}
}
