package com.firejun.bannerviewhelperdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firejun.bannerviewhelper.BannerViewHelper;

import java.util.List;

public class BannerViewAdapter extends RecyclerView.Adapter {
    public static final int DEFAULT_VIEW = 0;
    public static final int CLIP_VIEW = 1;

    private List<Integer> mImages;

    public BannerViewAdapter(List<Integer> images) {
        mImages = images;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return CLIP_VIEW;
        } else {
            return DEFAULT_VIEW;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (i) {
            case CLIP_VIEW:
                view = inflater.inflate(R.layout.layout_clip_item_view, viewGroup, false);
                return new ViewHolderClipBanner(view);
            default:
                view = new BannerViewHelper(viewGroup.getContext(), 10 / 6);
                return new ViewHolderDefaultBanner(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderClipBanner) {
            ((ViewHolderClipBanner) viewHolder).setData(mImages);
        } else {
            ((ViewHolderDefaultBanner) viewHolder).setBannerData(mImages);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
