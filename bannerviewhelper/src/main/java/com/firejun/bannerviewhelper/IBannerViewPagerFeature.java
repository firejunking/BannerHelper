package com.firejun.bannerviewhelper;

import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;

public interface IBannerViewPagerFeature {
    void startPlaying();

    void stopPlaying();

    void addPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setBannerPageClickListener(BannerViewHelper.BannerPageClickListener bannerPageClickListener);

    ViewPager getViewPager();

    void setIndicatorRes(@DrawableRes int unSelectRes, @DrawableRes int selectRes);

    void setBannerWidth(int bannerWidth);

    void setBannerHeight(int bannerHeight);

    /**
     * Enable auto-scroll mode
     *
     * @param intervalInMillis The interval time to scroll in milliseconds.
     */
    void setAutoScroll(int intervalInMillis);

    /**
     * Set an infinite loop
     *
     * @param enable enable or disable
     */
    void setInfiniteLoop(boolean enable);

    /**
     * Set the Screen ratio width : height
     */
    void setScreenRatio(float ratio);

    void setNormalIndicator(int resource);

    void setSelectedIndicator(int resource);

    void setDotMargin(int margin);

    void setDuration(int durationInMillis);

    void setOffscreenPageLimit(int count);

    void isOpenClipMode(boolean isOpen);

    void setIndicatorAlign(int indicatorAlign);

    void setIndicatorPaddingLeft(int indicatorPaddingLeft);

    void setIndicatorPaddingRight(int indicatorPaddingRight);

    void setIndicatorPaddingTop(int indicatorPaddingTop);

    void setIndicatorPaddingBottom(int indicatorPaddingBottom);

    void setClipModelPadding(int size);

    void setClipModelPageMargin(int size);

    void setIndicatorVisible(boolean visible);

    void setIsAutoPlay(boolean autoPlay);

    void setStrictModel(boolean var1);
}
