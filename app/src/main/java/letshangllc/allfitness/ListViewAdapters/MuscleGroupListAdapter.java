package letshangllc.allfitness.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 4/23/2016.
 */
public class MuscleGroupListAdapter extends ArrayAdapter<MuscleGroup> {

    private ArrayList<MuscleGroup> items;

    private static class ViewHolder {
        TextView item;
    }

    public MuscleGroupListAdapter(Context context, ArrayList<MuscleGroup> items) {
        super(context, R.layout.item_name, items);
        items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MuscleGroup item = getItem(position);
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
        viewHolder.item.setText(item.getMuscleGroupName());
        // Return the completed view to render on screen
        return convertView;
    }
}
