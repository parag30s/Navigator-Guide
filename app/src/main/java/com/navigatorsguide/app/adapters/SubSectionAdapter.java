package com.navigatorsguide.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.navigatorsguide.app.R;
import com.navigatorsguide.app.database.entities.Section;
import com.navigatorsguide.app.database.entities.SubSection;
import com.navigatorsguide.app.utils.AppUtils;

import java.util.List;

public class SubSectionAdapter extends RecyclerView.Adapter<SubSectionAdapter.SubSectionHolder> {
    private Context mContext;
    private List<SubSection> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(SubSection item);
    }

    public SubSectionAdapter(Context context, List<SubSection> list, OnItemClickListener listener) {
        this.mContext = context;
        this.mList = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SubSectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.row_sub_section_item, parent, false);
        return new SubSectionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubSectionHolder holder, int position) {
        holder.mTextView.setText(mList.get(position).getSubsname());
        holder.bind(mList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class SubSectionHolder extends RecyclerView.ViewHolder {
        CardView mSectionView;
        TextView mTextView;

        public SubSectionHolder(@NonNull View itemView) {
            super(itemView);
            mSectionView = itemView.findViewById(R.id.section_cardview);
            mTextView = itemView.findViewById(R.id.section_title);
        }

        public void bind(SubSection item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
