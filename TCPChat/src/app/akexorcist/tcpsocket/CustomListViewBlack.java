package app.akexorcist.tcpsocket;

import java.util.ArrayList;
import java.util.List;


import android.R.string;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import app.akexorcist.tcpsocket.R.id;

public class CustomListViewBlack extends ArrayAdapter<String> {
	ArrayList<String> STR; 
	LayoutInflater INFLATER;
	List<Integer> GRAVITY;
	
	public CustomListViewBlack(Context context, int textViewResourceId, ArrayList<String> objects, List<Integer> gv) {
		super(context, textViewResourceId, objects);
		INFLATER = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        STR = objects;
        GRAVITY = gv;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = INFLATER.inflate(R.layout.listview_simple_row, parent, false);
		EmojiTextView emojiTextView = (EmojiTextView)row.findViewById(R.id.txt1);		
        emojiTextView.setGravity(GRAVITY.get(position));
       	emojiTextView.setTextColor(Color.BLUE);
		emojiTextView.setEmojiText(STR.get(position));
		return row;
	}
}