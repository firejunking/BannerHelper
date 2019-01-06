package com.firejun.bannerviewhelperdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.firejun.bannerviewhelper.BannerViewHelper;
import com.firejun.bannerviewhelper.Utils;
import com.firejun.bannerviewhelper.ViewHolderBannerCreator;
import com.firejun.bannerviewhelper.ViewHolderBaseBanner;

import java.util.List;

public class ViewHolderDefaultBanner extends RecyclerView.ViewHolder {
    private BannerViewHelper mBannerView;

    public ViewHolderDefaultBanner(@NonNull View itemView) {
        super(itemView);
    }

    public void setBannerData(List<Integer> imageBeans) {
        if (null == mBannerView) {
            mBannerView = (BannerViewHelper) itemView;
        }
        mBannerView.setBannerData(imageBeans, new ViewHolderBannerCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
    }

    public static class BannerViewHolder implements ViewHolderBaseBanner<Integer> {
        private ImageView mImageView;

        private int imgWidth;
        private int imgHeight;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_banner_item_image_view, null, false);
            mImageView = (ImageView) view.findViewById(R.id.banner_view_item_image);
            int width = Utils.getScreenWidthInPx(context);
            imgWidth = width;
            imgHeight = width * 6 / 10;

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            layoutParams.width = imgWidth;
            layoutParams.height = imgHeight;
            mImageView.setLayoutParams(layoutParams);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            Glide.with(context).load(data).into(mImageView);
        }
    }
}
