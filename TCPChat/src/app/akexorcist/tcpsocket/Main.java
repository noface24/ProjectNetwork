package app.akexorcist.tcpsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	public static final int TCP_SERVER_PORT = 21111;

	TextView txtIP, txtStatus;
	ListView listChat;
	Button btnSend;
	EditText etxtMessage;
	String Name = "";

	ArrayList<String> arr_list;
	List<Integer> arr_gravity;
	InService inTask;
	final Context context = this;

	private Button emo;

	private SharedPreferences preferences;

	private String ipset;

	private TextView iptar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		arr_list = new ArrayList<String>();
		arr_gravity = new ArrayList<Integer>();
		listChat = (ListView) findViewById(R.id.listChat);
		etxtMessage = (EditText) findViewById(R.id.etxtMessage);
		txtIP = (TextView) findViewById(R.id.txtIP);
		txtIP.setText("Your IP : " + getIP());
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		iptar = (TextView) findViewById(R.id.iptar);
		emo = (Button) findViewById(R.id.Emo);

		SharedPreferences settings = getSharedPreferences("Pref", 0);

		inTask = new InService(getApplicationContext(), TCP_SERVER_PORT,
				listChat, arr_list, arr_gravity);
		inTask.execute();

		btnSend = (Button) findViewById(R.id.btnSend);
		emo.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				emoC = true;
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(
						"app.akexorcist.tcpsocket",
						"app.akexorcist.tcpsocket.EmojiSelection"));
				startActivity(intent);
			}
		});
		btnSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!(Name.length() > 0)) {
					Toast.makeText(getApplicationContext(),
							"Please input a name.", Toast.LENGTH_LONG).show();
				}
				if (!(iptar.getText().toString().length() > 0)) {
					Toast.makeText(getApplicationContext(),
							"Please input Target IP.", Toast.LENGTH_LONG).show();
				}

				if (etxtMessage.getText().toString().length() > 0
						&& Name.length() > 0) {

					txtStatus.setText("Sending...");
					sendMessage(iptar.getText().toString(), Name + etxtMessage.getText().toString());
					etxtMessage.setText("");
				}
			}
		});
	}

	public void sendMessage(String ip, String message) {
		final String IP_ADDRESS = ip;
		final String MESSAGE = message;

		Runnable runSend = new Runnable() {
			public void run() {
				try {
					Socket s = new Socket(IP_ADDRESS, TCP_SERVER_PORT);
					s.setSoTimeout(5000);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(s.getInputStream()));
					BufferedWriter out = new BufferedWriter(
							new OutputStreamWriter(s.getOutputStream()));
					String outgoingMsg = MESSAGE
							+ System.getProperty("line.separator");
					out.write(outgoingMsg);
					out.flush();
					Log.i("TcpClient", "sent: " + outgoingMsg);
					String inMsg = in.readLine()
							+ System.getProperty("line.separator");

					Handler refresh = new Handler(Looper.getMainLooper());
					refresh.post(new Runnable() {
						public void run() {
							arr_gravity.add(Gravity.RIGHT);
							arr_list.add(MESSAGE);
							listChat.setAdapter(new CustomListViewBlack(
									getApplicationContext(),
									android.R.layout.simple_list_item_1,
									arr_list, arr_gravity));
							listChat.setSelection(listChat.getCount());
							txtStatus.setText("Message has been sent.");
							etxtMessage.setText("");
						}
					});

					Log.i("Message Response", inMsg);
					s.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					setText("No device on this IP address.");
				} catch (Exception e) {
					e.printStackTrace();
					setText("Connection failed. Please try again.");
				}
			}

			public void setText(String str) {
				final String string = str;
				Handler refresh = new Handler(Looper.getMainLooper());
				refresh.post(new Runnable() {
					public void run() {
						txtStatus.setText(string);
					}
				});
			}
		};
		new Thread(runSend).start();
	}

	public String getIP() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "."
				+ ((ipAddress >> 16) & 0xFF) + "." + ((ipAddress >> 24) & 0xFF);
		if (ip.equals("0.0.0.0"))
			ip = "Please connect WIFI";
		return ip;
	}

	public void onPause() {
		super.onPause();
		SharedPreferences settings = getSharedPreferences("Pref", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("IP", iptar.getText().toString());
		editor.commit();
		inTask.killTask();
	}

	CharSequence cs;
	CustomEmojis emojis;
	int index = 0;
	private boolean emoC=false;

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		inTask = new InService(getApplicationContext(), TCP_SERVER_PORT,
				listChat, arr_list, arr_gravity);
		inTask.execute();
		
			Name = preferences.getString("Input_Name", "").trim() + " : ";
			ipset = preferences.getString("Target_IP", "").trim();
			iptar.setText(ipset);
		emojis = new CustomEmojis(this);
		SharedPreferences preferences = this.getSharedPreferences("pref",
				this.MODE_WORLD_READABLE);
		index = preferences.getInt("smiley", 0) + 1;
		if(emoC==true){
			etxtMessage.setText(etxtMessage.getText().toString() + "%"
					+ String.valueOf(index) + "%");
			emoC = false;
		}else{
			etxtMessage.setText(etxtMessage.getText().toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_xml, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item1:
			Intent i = new Intent(Main.this, Preferences.class);
			startActivity(i);
			break;
		}

		return true;
	}
}
