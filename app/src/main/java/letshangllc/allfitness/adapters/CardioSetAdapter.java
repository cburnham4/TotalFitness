package letshangllc.allfitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.CardioSet;
import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 6/30/2016.
 */
public class CardioSetAdapter extends ArrayAdapter<CardioSet> {


    private static class ViewHolder {
        TextView item;
    }

    public CardioSetAdapter(Context context, ArrayList<CardioSet> items) {
        super(context, R.layout.item_name, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CardioSet item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_name, parent, false);
            viewHolder.item = (TextView) convertView.findViewById(R.id.tv_itemName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        String values  = String.format(Locale.US,"%2dh %2dm %2ds | %.2fmi", item.hours, item.minutes, item.seconds, item.distance);
        viewHolder.item.setText(values);
        // Return the completed view to render on screen
        return convertView;
    }
}