package letshangllc.allfitness.adapters.cardio;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.ClassObjects.cardio.PastCardioItem;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 7/1/2016.
 */
public class CardioHistoryAdapter extends RecyclerView.Adapter<CardioHistoryAdapter.ViewHolder> {
    public ArrayList<PastCardioItem> items;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardioHistoryAdapter(ArrayList<PastCardioItem> items) {
        this.items = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardioHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        PastCardioItem pastCardioItem = items.get(position);

        /* If there are no lifts then display nothing */

        for(CardioSet item: pastCardioItem.cardioSets){
            sets += (String.format(Locale.US,"%dh %dm %ds | %.2fmi\n", item.hours, item.minutes, item.seconds, item.distance));
        }

        viewHolder.tv_info_text.setText(sets);
                /* todo get from resources */
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        DateFormat dateFormat1 = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(pastCardioItem.date);
            String dateFormatted = dateFormat1.format(date);
            viewHolder.tv_date.setText(dateFormatted);

        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.tv_date.setText(pastCardioItem.date);
        }

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

