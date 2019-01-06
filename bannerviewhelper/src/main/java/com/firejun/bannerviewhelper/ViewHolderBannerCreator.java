package com.firejun.bannerviewhelper;

public interface ViewHolderBannerCreator<VH extends ViewHolderBaseBanner> {
    /**
     * 创建ViewHolder
     */
    VH createViewHolder();
}
