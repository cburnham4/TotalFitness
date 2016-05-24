package letshangllc.allfitness.ListViewAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.ClassObjects.PastLiftSet;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 10/31/2015.
 */
public class PastCardViewAdapter extends RecyclerView.Adapter<PastCardViewAdapter.ViewHolder> {
    public ArrayList<PastLiftSet> items;



    // Provide a suitable constructor (depends on the kind of dataset)
    public PastCardViewAdapter(ArrayList<PastLiftSet> items) {
        this.items = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PastCardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_pastdate, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        String sets = "";
        PastLiftSet itemSet = items.get(position);
        for(LiftSet item: itemSet.getLiftSets()){
            sets += (String.format(Locale.US,"REPS: %2d  |  WEIGHT: %.1f\n", item.getReps(), item.getWeight()));
        }

        viewHolder.tv_info_text.setText(sets);
        viewHolder.tv_date.setText(itemSet.getDate());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return  items.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_info_text;
        public TextView tv_date;

        public ViewHolder(View view) {
            super(view);
            tv_info_text = (TextView) view.findViewById(R.id.info_text);
            tv_date = (TextView) view.findViewById(R.id.tv_cardview_date);
        }
    }

}