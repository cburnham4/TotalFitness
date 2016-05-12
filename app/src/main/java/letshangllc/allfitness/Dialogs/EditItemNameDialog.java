package letshangllc.allfitness.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 5/12/2016.
 */
public class EditItemNameDialog extends DialogFragment {
    /* Callback for when user presses Add */
    Listener mListener;
    private String title;
    private String itemName;

    public interface Listener {
        public void onDialogPositiveClick(String name);
    }

    public void setDialogTitle(String title){
        this.title = title;
    }
    public void setItemName(String itemName){
        this.itemName = itemName;
    }


    public void setCallback(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_edit_item_name, null);

        final EditText et_item_name = (EditText) view.findViewById(R.id.et_newMuscleGroup);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_title);

        if(title!=null){
            tv_title.setText(title);
        }
        if(itemName!= null){
            et_item_name.setText(itemName);
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = et_item_name.getText().toString();
                        mListener.onDialogPositiveClick(name);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditItemNameDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


}