/*
 * Copyright (C) 2011 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.majorkernelpanic.spydroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.majorkernelpanic.networking.HTTPD_Server;
import net.majorkernelpanic.networking.RtspServer;
import net.majorkernelpanic.networking.Session;
import net.majorkernelpanic.streaming.video.H264Stream;
import net.majorkernelpanic.streaming.video.VideoQuality;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Spydroid launches an RtspServer, clients can then connect to it and receive
 * audio/video streams from the phone
 */
public class SpydroidActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	private HTTPD_Server httpd;
	static final public String TAG = "SpydroidActivity";

	private CustomHttpServer httpServer = null;
	private PowerManager.WakeLock wl;
	private RtspServer rtspServer = null;
	private SurfaceHolder holder;
	private SurfaceView camera;
	private TextView line1, line2, signWifi, signStreaming;
	private ImageView buttonSettings, buttonClient, buttonAbout;
	private LinearLayout signInformation;
	private Context context;
	private Animation pulseAnimation;

	/**
	 * The HttpServer will use those variables to send reports about the state
	 * of the app to the http interface
	 **/
	public static boolean activityPaused = true;
	public static Exception lastCaughtException;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		camera = (SurfaceView) findViewById(R.id.smallcameraview);
		context = this.getApplicationContext();
		line1 = (TextView) findViewById(R.id.line1);
		line2 = (TextView) findViewById(R.id.line2);
		buttonSettings = (ImageView) findViewById(R.id.button_settings);
		// buttonClient = (ImageView)findViewById(R.id.button_client);
		buttonAbout = (ImageView) findViewById(R.id.button_about);
		signWifi = (TextView) findViewById(R.id.advice);
		signStreaming = (TextView) findViewById(R.id.streaming);
		signInformation = (LinearLayout) findViewById(R.id.information);
		pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
		Button go = (Button) findViewById(R.id.button1);
		Button left = (Button) findViewById(R.id.button2);
		Button stop = (Button) findViewById(R.id.button3);
		Button right = (Button) findViewById(R.id.button4);
		Button back = (Button) findViewById(R.id.button5);
		Button gl = (Button) findViewById(R.id.button6);
		Button gr = (Button) findViewById(R.id.button7);
		Button bl = (Button) findViewById(R.id.button8);
		Button br = (Button) findViewById(R.id.button9);

		// /------------ Button --------------//
		go.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("n");
					httpd.sentcommand("g");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		left.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("l");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		stop.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		right.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("r");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		back.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("b");
					httpd.sentcommand("n");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		gl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("g");
					httpd.sentcommand("l");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		gr.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("r");
					httpd.sentcommand("g");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		bl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("b");
					httpd.sentcommand("l");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});
		br.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					httpd.sentcommand("b");
					httpd.sentcommand("r");
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					httpd.sentcommand("n");
					httpd.sentcommand("s");
				}
				return false;
			}
		});

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		H264Stream.setPreferences(settings);

		settings.registerOnSharedPreferenceChangeListener(this);

		camera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder = camera.getHolder();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"net.majorkernelpanic.spydroid.wakelock");

		Session.setSurfaceHolder(holder);
		Session.setHandler(handler);
		Session.setDefaultAudioEncoder(settings.getBoolean("stream_audio",
				false) ? Integer.parseInt(settings.getString("audio_encoder",
				"3")) : 0);
		Session.setDefaultVideoEncoder(settings
				.getBoolean("stream_video", true) ? Integer.parseInt(settings
				.getString("video_encoder", "2")) : 0);
		Session.setDefaultVideoQuality(new VideoQuality(settings.getInt(
				"video_resX", 0), settings.getInt("video_resY", 0), Integer
				.parseInt(settings.getString("video_framerate", "0")), Integer
				.parseInt(settings.getString("video_bitrate", "0")) * 1000));

		rtspServer = new RtspServer(8086, handler);
		httpServer = new CustomHttpServer(8080, this.getApplicationContext(),
				handler);
		try {
			httpd = new HTTPD_Server();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buttonSettings.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Starts QualityListActivity where user can change the quality
				// of the stream
				Intent intent = new Intent(context, OptionsActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		/*
		 * buttonClient.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { // Starts ClientActivity, the user can then capture
		 * the stream from another phone running Spydroid Intent intent = new
		 * Intent(context,ClientActivity.class); startActivityForResult(intent,
		 * 0); } });
		 */
		buttonAbout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Display some information
				Intent intent = new Intent(context, AboutActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		ShowDialogBlueTooth();
	}

	public void ShowDialogBlueTooth() {
		// ---------------- list devices ----------------//
		final ArrayAdapter<String> listBt = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		Set<BluetoothDevice> bluetooths = httpd.btAdt.getBondedDevices();
		if (bluetooths.size() > 0)
			for (BluetoothDevice device : bluetooths)
				listBt.add(device.getName() + "//" + device.getAddress());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connect BlueTooth Devices");
		builder.setAdapter(listBt, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "Connect...",
						Toast.LENGTH_SHORT).show();
				String cs = listBt.getItem(which);
				Set<BluetoothDevice> bluetoothsl = httpd.btAdt
						.getBondedDevices();
				if (bluetoothsl.size() > 0)
					for (BluetoothDevice device : bluetoothsl)
						if ((device.getName() + "//" + device.getAddress())
								.equals(cs)) {
							httpd.btSv.connect(device, UUID.randomUUID());
						}
				return;
			}
		});
		builder.create().show();
		// ------------------ list event ---------------//
	}

	// Save preferences when modified in the OptionsActivity
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("video_resX")) {
			Session.defaultVideoQuality.resX = sharedPreferences.getInt(
					"video_resX", 0);
		} else if (key.equals("video_resY")) {
			Session.defaultVideoQuality.resY = sharedPreferences.getInt(
					"video_resY", 0);
		} else if (key.equals("video_framerate")) {
			Session.defaultVideoQuality.frameRate = Integer
					.parseInt(sharedPreferences.getString("video_framerate",
							"0"));
		} else if (key.equals("video_bitrate")) {
			Session.defaultVideoQuality.bitRate = Integer
					.parseInt(sharedPreferences.getString("video_bitrate", "0")) * 1000;
		} else if (key.equals("stream_audio") || key.equals("audio_encoder")) {
			Session.setDefaultAudioEncoder(sharedPreferences.getBoolean(
					"stream_audio", true) ? Integer.parseInt(sharedPreferences
					.getString("audio_encoder", "3")) : 0);
		} else if (key.equals("stream_video") || key.equals("video_encoder")) {
			Session.setDefaultVideoEncoder(sharedPreferences.getBoolean(
					"stream_video", true) ? Integer.parseInt(sharedPreferences
					.getString("video_encoder", "2")) : 0);
		} else if (key.equals("enable_http")) {
			if (sharedPreferences.getBoolean("enable_http", true)) {
				if (httpServer == null)
					httpServer = new CustomHttpServer(8080,
							this.getApplicationContext(), handler);
			} else {
				if (httpServer != null)
					httpServer = null;
			}
		} else if (key.equals("enable_rtsp")) {
			if (sharedPreferences.getBoolean("enable_rtsp", true)) {
				if (rtspServer == null)
					rtspServer = new RtspServer(8086, handler);
			} else {
				if (rtspServer != null)
					rtspServer = null;
			}
		}
	}

	public void onStart() {
		super.onStart();

		// Lock screen
		wl.acquire();

		Intent notificationIntent = new Intent(this, SpydroidActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		Notification notification = builder.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis())
				.setTicker(getText(R.string.notification_title))
				.setSmallIcon(R.drawable.icon)
				.setContentTitle(getText(R.string.notification_title))
				.setContentText(getText(R.string.notification_content)).build();
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
				.notify(0, notification);

	}

	public void onStop() {
		super.onStop();
		wl.release();
	}

	public void onResume() {
		super.onResume();
		// Determines if user is connected to a wireless network & displays ip
		if (!streaming)
			displayIpAddress();
		activityPaused = true;
		startServers();
		registerReceiver(wifiStateReceiver, new IntentFilter(
				WifiManager.NETWORK_STATE_CHANGED_ACTION));
	}

	public void onPause() {
		super.onPause();
		activityPaused = false;
		unregisterReceiver(wifiStateReceiver);
	}

	public void onDestroy() {
		super.onDestroy();
		// Remove notification
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(0);
		if (httpServer != null)
			httpServer.stop();
		if (rtspServer != null)
			rtspServer.stop();
	}

	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		/*
		 * case R.id.client: // Starts ClientActivity where user can view stream
		 * from another phone intent = new
		 * Intent(this.getBaseContext(),ClientActivity.class);
		 * startActivityForResult(intent, 0); return true;
		 */
		case R.id.options:
			// Starts QualityListActivity where user can change the streaming
			// quality
			intent = new Intent(this.getBaseContext(), OptionsActivity.class);
			startActivityForResult(intent, 0);
			return true;
		case R.id.quit:
			// Quits Spydroid i.e. stops the HTTP server
			if (httpServer != null)
				httpServer.stop();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startServers() {
		if (rtspServer != null) {
			try {
				rtspServer.start();
			} catch (IOException e) {
				log("RtspServer could not be started : "
						+ (e.getMessage() != null ? e.getMessage()
								: "Unknown error"));
			}
		}
		if (httpServer != null) {
			try {
				httpServer.start();
			} catch (IOException e) {
				log("HttpServer could not be started : "
						+ (e.getMessage() != null ? e.getMessage()
								: "Unknown error"));
			}
		}
	}

	// BroadcastReceiver that detects wifi state changements
	private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// This intent is also received when app resumes even if wifi state
			// hasn't changed :/
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				if (!streaming)
					displayIpAddress();
			}
		}
	};

	private boolean streaming = false;

	// The Handler that gets information back from the RtspServer and Session
	private final Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RtspServer.MESSAGE_ERROR:
				Exception e = (Exception) msg.obj;
				lastCaughtException = e;
				log(e.getMessage() != null ? e.getMessage()
						: "An error occurred !");
				break;
			case RtspServer.MESSAGE_LOG:
				log((String) msg.obj);
				break;
			case Session.MESSAGE_START:
				streaming = true;
				streamingState(1);
				break;
			case Session.MESSAGE_STOP:
				streaming = false;
				displayIpAddress();
				break;
			}
		}

	};

	private void displayIpAddress() {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info != null && info.getNetworkId() > -1) {
			int i = info.getIpAddress();
			String ip = String.format("%d.%d.%d.%d", i & 0xff, i >> 8 & 0xff,
					i >> 16 & 0xff, i >> 24 & 0xff);
			line1.setText("HTTP://");
			line1.append(ip);
			line1.append(":8080");
			line2.setText("RTSP://");
			line2.append(ip);
			line2.append(":8086");
			streamingState(0);
		} else {
			line1.setText("HTTP://xxx.xxx.xxx.xxx:8080");
			line2.setText("RTSP://xxx.xxx.xxx.xxx:8086");
			streamingState(2);
		}
	}

	public void log(String s) {
		Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	}

	private void streamingState(int state) {
		// Not streaming
		if (state == 0) {
			signStreaming.clearAnimation();
			signWifi.clearAnimation();
			signStreaming.setVisibility(View.GONE);
			signInformation.setVisibility(View.VISIBLE);
			signWifi.setVisibility(View.GONE);
		} else if (state == 1) {
			// Streaming
			signWifi.clearAnimation();
			signStreaming.setVisibility(View.VISIBLE);
			signStreaming.startAnimation(pulseAnimation);
			signInformation.setVisibility(View.INVISIBLE);
			signWifi.setVisibility(View.GONE);
		} else if (state == 2) {
			// No wifi !
			signStreaming.clearAnimation();
			signStreaming.setVisibility(View.GONE);
			signInformation.setVisibility(View.INVISIBLE);
			signWifi.setVisibility(View.VISIBLE);
			signWifi.startAnimation(pulseAnimation);
		}
	}

}