package letshangllc.allfitness.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 3/17/2016.
 * Array Adapter for ExerciseItems
 * Set the
 */

/*todo change to resource compat
 */
public class ExerciseListAdapter extends ArrayAdapter<ExerciseItem>{

    private Resources resources;
    private Resources.Theme theme;
    private Filter filter;
    private ArrayList<ExerciseItem> exerciseItems;

    private static class ViewHolder {
        TextView tv_exercise;
        ImageView img_category;
    }

    public ExerciseListAdapter(Context context, ArrayList<ExerciseItem> items){
        super(context, R.layout.item_exercise, items);
        resources = context.getResources();
        theme = context.getTheme();
        exerciseItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ExerciseItem exerciseItem  = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_exercise, parent, false);
            viewHolder.tv_exercise = (TextView) convertView.findViewById(R.id.tv_exerciseName);
            viewHolder.img_category = (ImageView) convertView.findViewById(R.id.img_category);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tv_exercise.setText(exerciseItem.getExerciseName());

        /* if the os is atleast kitkat then add icons to exercises */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (exerciseItem.getExerciseType()) {
                case LIFT:
                    viewHolder.img_category.setImageDrawable(resources.getDrawable(R.drawable.dumbbell_50, theme));
                    break;
                case BODYWEIGHT:
                    viewHolder.img_category.setImageDrawable(resources.getDrawable(R.drawable.pilates_50, theme));
                    break;
                //case TIMED:
                  //  viewHolder.img_category.setImageDrawable(resources.getDrawable(R.drawable.watch_50, theme));
                    //break;
                case CARDIO:
                    viewHolder.img_category.setImageDrawable(resources.getDrawable(R.drawable.sports_mode_50, theme));
                    break;
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //filter = new AppFilter<ExerciseItem>(exerciseItems);
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<ExerciseItem>(exerciseItems);
        return filter;
    }

    public class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<T>();

                for (T object : sourceObjects) {
                    // the filtering itself:
                    if (object.toString().toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((ExerciseItem) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}
