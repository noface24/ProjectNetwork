package net.majorkernelpanic.networking;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BlueToothService {
	private final Handler mHandler;
	private int mState;

	private ConnectThread mConnectThread = null;
	private ConnectedThread mConnectedThread = null;

	public static final int STATE_NONE = 0;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	public static final String TAG = "TAG";

	public static final int MESSAGE_STATE_CHANGE = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_READ = 6;
	public static final int MESSAGE_WRITE = 7;

	public static final String TOAST = "toast";

	public BlueToothService(Handler handler) {
		mHandler = handler;
	}

	public synchronized void setState(int state) {
		mState = state;
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	public synchronized int getState() {
		return mState;
	}

	public void connect(BluetoothDevice device, UUID uuid) {
		cancelConnectThread();
		mConnectThread = new ConnectThread(device, uuid);
		mConnectThread.start();
		setToast("Connecting...");
		setState(STATE_CONNECTING);
	}

	public void close() {
		cancelConnectThread();
		cancelConnectedThread();
		setState(STATE_NONE);
	}

	public void connected(BluetoothSocket socket) {
		cancelConnectedThread();
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
		setToast("Connection successfull!");
		setState(STATE_CONNECTED);
	}

	public void write(byte[] buffer) {
		if (mConnectedThread != null && mState == STATE_CONNECTED) {
			mConnectedThread.write(buffer);
		}
	}

	private void cancelConnectThread() {
		if (mConnectThread != null)
			mConnectThread.cancel();
	}

	private void cancelConnectedThread() {
		if (mConnectedThread != null)
			mConnectedThread.cancel();
	}

	private synchronized void setToast(String toast) {
		Message message = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, toast);
		message.setData(bundle);
		message.sendToTarget();
	}

	private synchronized void connectionFailed() {
		setToast("Device connection failed.");
		setState(STATE_NONE);
	}

	private synchronized void connectionLost() {
		setToast("Device connection lost.");
		setState(STATE_NONE);
	}

	public class ConnectThread extends Thread {
		public final BluetoothDevice mmDevice;
		public final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice device, UUID uuid) {
			Log.i("TAG", "CREATE ConnectThread");
			mmDevice = device;

			BluetoothSocket tmpSocket = null;
			try {
				Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				tmpSocket = (BluetoothSocket) m.invoke(device, 1);
				//tmpSocket = device.createRfcommSocketToServiceRecord(uuid);
			} catch (Exception createException) {
				Log.e("TAG",
						"Socket create failed: " + createException.getMessage());
			}
			mmSocket = tmpSocket;
		}

		public void run() {
			Log.i("TAG", "BEGIN ConnectThread");

			try {
				mmSocket.connect();
			} catch (IOException connectException) {
				try {
					Log.e("TAG",
							"Socket connect failed: "
									+ connectException.getMessage());
					mmSocket.close();
				} catch (IOException closeException) {
					Log.e("TAG",
							"Socket close failed: "
									+ closeException.getMessage());
				}
				connectionFailed();
			}
			connected(mmSocket);
		}

		public void cancel() {
			Log.i("TAG", "CANCEL ConnectThread");

			try {
				mmSocket.close();
			} catch (IOException closeException) {
				Log.e("TAG",
						"Socket close failed: " + closeException.getMessage());
			}
		}

	}

	public class ConnectedThread extends Thread {

		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final BluetoothSocket mmSocket;

		public ConnectedThread(BluetoothSocket socket) {
			Log.i("TAG", "CREATE ConnectedThread");
			mmSocket = socket;

			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException streamException) {
				Log.e(TAG, "Failed to get input and output streams: "
						+ streamException.getMessage());
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN ConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes = 0;

			while (true) {

				try {
					bytes = mmInStream.read(buffer);
					if (bytes >= 0) {
						mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
								.sendToTarget();
					}

				} catch (IOException readException) {
					Log.e(TAG, "Failed to read from input stream: "
							+ readException.getMessage());
					connectionLost();
					break;
				}
			}
		}

		public void write(byte[] buffer) {

			try {
				for (byte b : buffer) {
					mmOutStream.write(new byte[] { b });
					Thread.sleep(20);
				}
				mmOutStream.flush();
				mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
						.sendToTarget();

			} catch (Exception writeException) {
				Log.e(TAG, "Failed to write to output stream: "
						+ writeException.getMessage());
				connectionLost();
			}

		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (Exception closeException) {
				Log.e(TAG, "Failed to close socket connection: "
						+ closeException.getMessage());
			}
		}
	}
}
