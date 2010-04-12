package co.uk.gauntface.android.mobileeye.bluetooth;

import java.util.ArrayList;

import co.uk.gauntface.android.mobileeye.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BluetoothListAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<String> mBluetoothDevices;
	
	public BluetoothListAdapter(Context c)
	{
		mContext = c;
		mBluetoothDevices = new ArrayList<String>();
	}
	
	public void add(String s)
	{
		mBluetoothDevices.add(s);
	}
	
	public void clear()
	{
		mBluetoothDevices.clear();
	}
	
	public int getCount()
	{
		return mBluetoothDevices.size();
	}

	public String getItem(int position)
	{
		return mBluetoothDevices.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v;
		
		if(convertView == null)
		{
			v = (LinearLayout) View.inflate(mContext, R.layout.list_item_text, null);
		}
		else
		{
			v = convertView;
		}
		
		TextView t = (TextView) v.findViewById(R.id.ListItemTextView);
		t.setText(mBluetoothDevices.get(position));
		
		return v;
	}

}
