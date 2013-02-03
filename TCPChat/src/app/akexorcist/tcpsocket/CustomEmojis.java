package app.akexorcist.tcpsocket;

import android.R.*;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CustomEmojis extends BaseAdapter{
	private Activity activity;
	private static LayoutInflater inflater = null;
	
	public final int[] images = new int[] {
			R.drawable.a, R.drawable.b,
			R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f,
			R.drawable.g, R.drawable.h, R.drawable.j,
			R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n,
			R.drawable.o, R.drawable.p, R.drawable.q, R.drawable.r,
			R.drawable.s, R.drawable.t, R.drawable.u, R.drawable.v,
			R.drawable.w, R.drawable.x, R.drawable.y, R.drawable.z,
			R.drawable.aa,R.drawable.bb,R.drawable.cc,R.drawable.dd,
			R.drawable.ee,R.drawable.ff,R.drawable.gg,R.drawable.hh,
			R.drawable.ii,R.drawable.jj,R.drawable.kk,R.drawable.ll,
			R.drawable.mm,R.drawable.nn,R.drawable.oo,};
	
	public CustomEmojis(Activity act) {
		activity = act;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.grid_row, null);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.imageView.setImageResource(images[position]);
		return convertView;
	}
	public static class ViewHolder{
		public ImageView imageView;
	}

}
