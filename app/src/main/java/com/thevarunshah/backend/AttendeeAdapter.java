package com.thevarunshah.backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thevarunshah.checkin.R;

import java.util.ArrayList;

public class AttendeeAdapter extends ArrayAdapter<User> {

    private final static String TAG = "AttendeeAdapter"; //for debugging purposes

    private final ArrayList<User> attendeeList; //the list the adapter manages
    private final Context context; //context attached to adapter

    /**
     * the bucket list adapter
     *  @param context the application context
     * @param attendeeList the list of items
     */
    public AttendeeAdapter(Context context, ArrayList<User> attendeeList) {

        super(context, R.layout.row_view, attendeeList);
        this.context = context;
        this.attendeeList = attendeeList;
    }

    /**
     * a view holder for each item in the row
     */
    private class ViewHolder {

        CheckBox done;
        TextView item;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            //inflate view and link each component to the holder
            LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.row_view, null);
            holder.item = (TextView) convertView.findViewById(R.id.row_text);
            holder.done = (CheckBox) convertView.findViewById(R.id.row_check);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.done.setClickable(false);

        //get item and link references to holder
        User user = attendeeList.get(position);
        holder.item.setText(user.name);
        holder.done.setChecked(user.checkedIn);
        holder.done.setTag(user);

        return convertView;
    }
}