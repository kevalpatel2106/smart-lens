/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.smartlens.infopage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseImageView;
import com.kevalpatel2106.smartlens.base.BaseRecyclerViewAdapter;
import com.kevalpatel2106.smartlens.base.BaseTextView;
import com.kevalpatel2106.smartlens.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Keval on 29-Jul-17.
 */

class RecommendedItemAdapter extends BaseRecyclerViewAdapter<RecommendedItemAdapter.RecommendedViewHolder> {

    private final Context mContext;
    private final ArrayList<InfoModel> mInfoModels;

    RecommendedItemAdapter(@NonNull Context context,
                           @NonNull ArrayList<InfoModel> infoModels) {
        super(infoModels);
        mContext = context;
        mInfoModels = infoModels;
    }

    @Override
    public void bindView(RecommendedViewHolder holder, int position) {
        InfoModel infoModel = mInfoModels.get(position);

        if (infoModel != null) {
            Glide.with(mContext)
                    .load(infoModel.getImageUrl())
                    .into(holder.mInfoImageView);
            holder.mInfoTextView.setText(Utils.getCamelCaseText(infoModel.getLabel()));

            holder.mRootView.setOnClickListener(view -> InfoActivity.launch((AppCompatActivity) mContext,
                    infoModel.getLabel(), holder.mInfoImageView));
        }
    }

    @Override
    public RecommendedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendedViewHolder(LayoutInflater
                .from(mContext).inflate(R.layout.column_recommended_item, parent, false));
    }

    class RecommendedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wiki_image_view)
        BaseImageView mInfoImageView;
        @BindView(R.id.wiki_text_view)
        BaseTextView mInfoTextView;
        @BindView(R.id.row_root)
        View mRootView;

        RecommendedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
