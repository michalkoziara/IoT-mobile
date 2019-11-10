package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserGroupAdapter extends BaseAdapter {
    private Context context;

    private List<String> userGroupNames;
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

        MaterialTextView materialTextView = (MaterialTextView) convertView;
        materialTextView.setText((String) getItem(position));

        if (this.selectedPosition != null
                && this.selectedPosition == position) {
            materialTextView.setTextColor(Color.parseColor("#6200EE"));
            materialTextView.setBackgroundColor(Color.parseColor("#e0e0e0"));
        } else {
            materialTextView.setTextColor(Color.parseColor("#757575"));
            materialTextView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        return convertView;
    }

    Integer getSelectedPosition() {
        return selectedPosition;
    }

    void setSelectedPosition(Integer selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
