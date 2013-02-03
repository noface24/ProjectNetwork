package app.akexorcist.tcpsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

public class InService extends AsyncTask<Void, Void, Void> {
	ServerSocket ss;
    ListView LISTVIEW;
	ArrayList<String> ARR_LIST;
	List<Integer> ARR_GRAVITY;
	int TCP_SERVER_PORT;
	Context CONTEXT;
	Boolean TASK_STATE = true;
	
    public InService(Context context, int port, ListView lv
    		, ArrayList<String> arr_list, List<Integer> arr_gravity ) {
    	CONTEXT = context;
    	TCP_SERVER_PORT = port;
    	LISTVIEW = lv;
    	ARR_LIST = arr_list;
    	ARR_GRAVITY = arr_gravity;
    }
    
    public void killTask() {
    	TASK_STATE = false;
    }
    
	protected Void doInBackground(Void... params) {  
		try {
			ss = new ServerSocket(TCP_SERVER_PORT);
			ss.setSoTimeout(1000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(TASK_STATE) {
			try {
				Socket s = ss.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				final String incomingMsg = in.readLine() + System.getProperty("line.separator");
				Log.i("Message Incoming", incomingMsg);
				Handler refresh = new Handler(Looper.getMainLooper());
				refresh.post(new Runnable() {
					public void run() {
						ARR_GRAVITY.add(Gravity.LEFT);
						ARR_LIST.add(incomingMsg.replace(System.getProperty("line.separator"), "")); 		
						LISTVIEW.setAdapter(new CustomListViewBlack(CONTEXT
				   				, android.R.layout.simple_list_item_1
				   				, ARR_LIST, ARR_GRAVITY));
					    LISTVIEW.setSelection(LISTVIEW.getCount());
					    Toast.makeText(CONTEXT, "Message Incoming", Toast.LENGTH_SHORT).show();
					}
				});
				String outgoingMsg = "OK" + System.getProperty("line.separator");
				out.write(outgoingMsg);
				out.flush();

			} catch (IOException e) { }
		}
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
