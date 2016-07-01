package letshangllc.allfitness.adapters.bodyweight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.bodyweight.BodyWeightSet;
import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 6/30/2016.
 */
public class BodyWeightSetAdapter extends ArrayAdapter<BodyWeightSet> {


    private static class ViewHolder {
        TextView item;
    }

    public BodyWeightSetAdapter(Context context, ArrayList<BodyWeightSet> items) {
        super(context, R.layout.item_name, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BodyWeightSet item = getItem(position);
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
        String repString = String.format(Locale.US,"%2dreps", item.reps);
        String timeString = String.format(Locale.US,"%2dm %2ds", item.minutes, item.seconds);
        if(item.reps != 0 && (item.minutes != 0 || item.seconds!=0)){
            viewHolder.item.setText(timeString + " | " + repString);
        }else if(item.reps == 0){
            viewHolder.item.setText(timeString);
        }else{
            viewHolder.item.setText(repString);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}