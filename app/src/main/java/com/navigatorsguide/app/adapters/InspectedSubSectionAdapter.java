package com.navigatorsguide.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.navigatorsguide.app.R;
import com.navigatorsguide.app.database.entities.SubSection;
import com.navigatorsguide.app.ui.report.ReportBuilderFragment;

import java.util.List;

public class InspectedSubSectionAdapter extends
        RecyclerView.Adapter<InspectedSubSectionAdapter.ViewHolder> {
    private List<SubSection> filterList;
    private Context context;
    private ReportBuilderFragment fragment;
    private boolean isFixedSize;

    public InspectedSubSectionAdapter(Context ctx, ReportBuilderFragment fragment, List<SubSection> filterModelList, boolean size) {
        context = ctx;
        this.fragment = fragment;
        filterList = filterModelList;
        isFixedSize = size;
    }

    @Override
    public InspectedSubSectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_report_sub_section_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubSection subSection = filterList.get(position);
        if (subSection != null) {
            holder.subSectionName.setText(subSection.getSubsname());
            if (subSection.getStatus() != null && subSection.getStatus() == 1) {
                holder.subSectionCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorEligibleItem));
                holder.eligibleStatus.setChecked(true);
                holder.eligibleStatus.setClickable(true);
                holder.eligibleStatus.setEnabled(true);
                holder.subSectionName.setTextColor(ContextCompat.getColor(context, R.color.smsp_base_color));
            } else {
                holder.subSectionCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDefaultItem));
                holder.eligibleStatus.setChecked(false);
                holder.eligibleStatus.setClickable(false);
                holder.eligibleStatus.setEnabled(false);
                holder.eligibleStatus.setOnClickListener(null);
                holder.subSectionName.setTextColor(ContextCompat.getColor(context, R.color.smsp_hint_color));
            }
        }

        holder.eligibleStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.checkBoxOnClick(holder.eligibleStatus.isChecked(), position, subSection.getSubsname());
                Toast.makeText(context, "This action will keep out subsection to cover on the report, tap on RESET to reset.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (isFixedSize) {
            if (filterList.size() >= 8)
                return 8;
            else
                return filterList.size();
        } else {
            return filterList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView subSectionCardView;
        public TextView subSectionName;
        public CheckBox eligibleStatus;

        public ViewHolder(View view) {
            super(view);
            subSectionCardView = (CardView) view.findViewById(R.id.sub_section_card_view);
            subSectionName = (TextView) view.findViewById(R.id.sub_section_name);
            eligibleStatus = (CheckBox) view.findViewById(R.id.eligible_status);

            //item click event listener
            view.setOnClickListener(this);

            //checkbox click event handling
            eligibleStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (isChecked) {
//                        Toast.makeText(context, "Clicked true", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(context, "Clicked false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }
}
