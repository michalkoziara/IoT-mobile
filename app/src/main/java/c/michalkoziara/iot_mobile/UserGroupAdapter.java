package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.List;

public class UserGroupAdapter extends BaseAdapter {
    List<String> userGroupNames;
    Context context;

    private Integer selectedPosition;

    UserGroupAdapter(Context context, List<String> userGroupNames) {
        this.context = context;
        this.userGroupNames = userGroupNames;
    }

    @Override
    public int getCount() {
        return userGroupNames.size();
    }

    @Override
    public Object getItem(int position) {
        return userGroupNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_user_group, parent, false);
        }

        LinearLayout linearLayout = (LinearLayout) convertView;
        RadioButton radioButton = linearLayout.findViewById(R.id.user_group_radio_button);
        radioButton.setText((String) getItem(position));

        if (this.selectedPosition != null
                && this.selectedPosition == position) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != null && selectedPosition != position) {
                    LinearLayout previouslyLinearLayout = (LinearLayout) getRowAtRealPosition(
                            selectedPosition,
                            (ListView) parent
                    );

                    if (previouslyLinearLayout != null) {
                        RadioButton radioButton = previouslyLinearLayout.findViewById(R.id.user_group_radio_button);

                        if (radioButton != null) {
                            radioButton.setChecked(false);
                        }
                    }
                }

                selectedPosition = position;
            }
        });

        return convertView;
    }

    private View getRowAtRealPosition(int pos, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int childIndex = pos - firstListItemPosition;
        return listView.getChildAt(childIndex);
    }
}
