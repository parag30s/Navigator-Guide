package com.navigatorsguide.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.navigatorsguide.app.R;
import com.navigatorsguide.app.database.entities.Section;
import com.navigatorsguide.app.utils.AppUtils;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionHolder> {
    private Context mContext;
    private List<Section> mList;
    private OnItemClickListener mListener;
    private int mWidth;

    public interface OnItemClickListener {
        public void onItemClick(Section item);
    }

    public SectionAdapter(Context context, List<Section> list, int width, OnItemClickListener listener) {
        this.mContext = context;
        this.mList = list;
        this.mWidth = width;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.row_dashboard_item, parent, false);
        return new SectionHolder(view, mWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionHolder holder, int position) {
        holder.mTextView.setText(mList.get(position).getSectionName());
        holder.mImageView.setImageResource(AppUtils.Companion.getSectionThumbnail(mList.get(position).getSectionid()));
        holder.bind(mList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class SectionHolder extends RecyclerView.ViewHolder {
        CardView mSectionView;
        ImageView mImageView;
        TextView mTextView;

        public SectionHolder(@NonNull View itemView, int mWidth) {
            super(itemView);
            mSectionView = itemView.findViewById(R.id.section_cardview);
            mImageView = itemView.findViewById(R.id.section_image);
            mTextView = itemView.findViewById(R.id.section_title);
            mSectionView.getLayoutParams().height = (mWidth/3);
            mSectionView.getLayoutParams().width = (mWidth/2);
        }

        public void bind(Section item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
