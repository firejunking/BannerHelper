package com.firejun.bannerviewhelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerViewHelper<T> extends FrameLayout implements IBannerViewPagerFeature {
    private static final String TAG = "BannerViewHelper";

    private ViewPager mViewPager;
    private DefaultBannerPagerAdapter mAdapter;
    private List<T> mData;
    /**
     * 是否自动播放,默认自动播放
     */
    private boolean mIsAutoPlay = true;
    /**
     * 标记自动滑动是否已经加入队列延迟执行
     */
    private boolean mIsPlaying;
    /**
     * 当前位置
     */
    private int mCurrentItem = 0;
    private Handler mHandler = new Handler();
    /**
     * Banner 切换时间间隔
     */
    private int mDelayedTime = 3000;
    /**
     * 开启切割Banner效果
     */
    private boolean mIsOpenClipMode = false;
    /**
     * 是否无限轮播图片
     */
    private boolean mIsCanLoop = true;
    /**
     * indicator容器
     */
    private LinearLayout mIndicatorContainer;
    private ArrayList<ImageView> mIndicators = new ArrayList<>();
    /**
     * mIndicatorRes[0] 为未选中，mIndicatorRes[1]为选中
     */
    private int[] mIndicatorRes = new int[2];
    /**
     * indicator 距离左边的距离
     */
    private int mIndicatorPaddingLeft = 0;
    /**
     * indicator 距离右边的距离
     */
    private int mIndicatorPaddingRight = 0;
    /**
     * indicator 距离上边的距离
     */
    private int mIndicatorPaddingTop = 0;
    /**
     * indicator 距离下边的距离
     */
    private int mIndicatorPaddingBottom = 0;
    private int mIndicatorAlign = 1;
    /**
     * 在切割模式下，由于前后显示了上下一个页面的部分，因此需要计算这部分padding
     * 这个值是中间图边界与屏幕边界的距离
     */
    private int mClipModePadding = 0;
    /**
     * 这个值是左右小图，显示的那部分距离
     */
    private int mClipModePageMargin = 0;
    /**
     * 轮播切换时间
     */
    private int mDuration = 800;
    /**
     * 预加载个数
     */
    private int mOffscreenPageLimit = 3;
    /**
     * 指示器间距
     */
    private int dotMargin = 2;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private BannerPageClickListener mBannerPageClickListener;
    /**
     * 整体控件宽度
     */
    private int mBannerWidth = 0;
    /**
     * 整体控件高度
     */
    private int mBannerHeight = 0;
    /**
     * 是否显示指示器
     */
    private boolean mIsShowIndicator = true;

    public interface IndicatorAlign {
        int LEFT = 0;
        int CENTER = 1;
        int RIGHT = 2;
    }

    public BannerViewHelper(Context context, float ratio) {
        this(context);
        mBannerWidth = Utils.getScreenWidthInPx(context);
        mBannerHeight = (int) (mBannerWidth / ratio);
    }

    public BannerViewHelper(Context context) {
        super(context);
        getAttrs(context, null);
        init();
    }

    public BannerViewHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        init();
    }

    public BannerViewHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BannerViewHelper(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttrs(context, attrs);
        init();
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPagerHelper);

        setBackgroundColor(typedArray.getColor(R.styleable.BannerViewPagerHelper_banner_background, Color.WHITE));
        setInfiniteLoop(typedArray.getBoolean(R.styleable.BannerViewPagerHelper_banner_can_loop, true));
        setScreenRatio(typedArray.getFloat(R.styleable.BannerViewPagerHelper_banner_width_ratio_height_value, 1.7f));
        setNormalIndicator(typedArray.getResourceId(R.styleable.BannerViewPagerHelper_banner_indicator_normal, R.drawable.banner_indicator_normal));
        setSelectedIndicator(typedArray.getResourceId(R.styleable.BannerViewPagerHelper_banner_indicator_selected, R.drawable.banner_indicator_selected));
        setDotMargin(typedArray.getInt(R.styleable.BannerViewPagerHelper_banner_indicator_margin, 2));
        setAutoScroll(typedArray.getInt(R.styleable.BannerViewPagerHelper_banner_show_delayed_time, 3000));
        setDuration(typedArray.getInt(R.styleable.BannerViewPagerHelper_banner_show_duration_time, 600));
        setOffscreenPageLimit(typedArray.getInt(R.styleable.BannerViewPagerHelper_banner_screen_page_limit, 3));
        isOpenClipMode(typedArray.getBoolean(R.styleable.BannerViewPagerHelper_banner_open_clip_mode, false));
        setIndicatorAlign(typedArray.getInt(R.styleable.BannerViewPagerHelper_indicatorAlign, 1));
        setIndicatorPaddingLeft(typedArray.getDimensionPixelSize(R.styleable.BannerViewPagerHelper_indicatorPaddingLeft, 0));
        setIndicatorPaddingRight(typedArray.getDimensionPixelSize(R.styleable.BannerViewPagerHelper_indicatorPaddingRight, 0));
        setIndicatorPaddingTop(typedArray.getDimensionPixelSize(R.styleable.BannerViewPagerHelper_indicatorPaddingTop, 0));
        setIndicatorPaddingBottom(typedArray.getDimensionPixelSize(R.styleable.BannerViewPagerHelper_indicatorPaddingBottom, 10));
        setClipModelPadding(typedArray.getInt(R.styleable.BannerViewPagerHelper_banner_clip_mode_padding, 10));
        setClipModelPageMargin(typedArray.getInt(R.styleable.BannerViewPagerHelper_banner_clip_mode_page_margin, 8));
        setIndicatorVisible(typedArray.getBoolean(R.styleable.BannerViewPagerHelper_banner_show_indicator, true));
        setIsAutoPlay(typedArray.getBoolean(R.styleable.BannerViewPagerHelper_banner_is_auto_play, true));
        typedArray.recycle();
    }

    private void init() {
        View view = null;
        if (mIsOpenClipMode) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.b_view_clip_viewpager_banner, this, true);
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.b_view_default_viewpager_banner, this, true);
        }

        mIndicatorContainer = (LinearLayout) view.findViewById(R.id.banner_indicator_container);
        mViewPager = (ViewPager) findViewById(R.id.banner_viewpager);
        mViewPager.setOffscreenPageLimit(mOffscreenPageLimit);

        // 初始化Scroller
        initViewPagerScroll();

        if (mBannerWidth == 0) {
            mBannerWidth = Utils.getScreenWidthInPx(getContext());
        }
        if (mIsShowIndicator) {
            mIndicatorContainer.setVisibility(VISIBLE);
        } else {
            mIndicatorContainer.setVisibility(GONE);
        }
    }

    /**
     * 是否开启切割模式
     */
    private void setOpenClipModelEffect() {
        if (mIsOpenClipMode) {
            mViewPager.setPageTransformer(true, new CommonPageTransformer());
        }
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller viewPagerScroller = new ViewPagerScroller(mViewPager.getContext(), mDuration);
            mScroller.set(mViewPager, viewPagerScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {
                if (mIsPlaying) {
                    mCurrentItem = mViewPager.getCurrentItem();
                    mCurrentItem++;
                    if (mCurrentItem == mAdapter.getCount() - 1) {
                        mCurrentItem = 0;
                        mViewPager.setCurrentItem(mCurrentItem, false);
                        mHandler.postDelayed(this, mDelayedTime);
                    } else {
                        mViewPager.setCurrentItem(mCurrentItem);
                        mHandler.postDelayed(this, mDelayedTime);
                    }
                } else {
                    mHandler.postDelayed(this, mDelayedTime);
                }
            }
        }
    };

    /**
     * 初始化指示器Indicator
     */
    private void initIndicator() {
        mIndicatorContainer.removeAllViews();
        mIndicators.clear();
        // 数据小于2 不显示指示器
        if (mIsShowIndicator && mData.size() > 1) {
            mIndicatorContainer.setVisibility(View.VISIBLE);
            for (int i = 0, size = mData.size(); i < size; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setPadding(dotMargin, 0, dotMargin, 0);
                if (i == (mCurrentItem % mData.size())) {
                    imageView.setImageResource(mIndicatorRes[1]);
                } else {
                    imageView.setImageResource(mIndicatorRes[0]);
                }
                mIndicators.add(imageView);
                mIndicatorContainer.addView(imageView);
            }
            // 设置指示器位置
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIndicatorContainer.getLayoutParams();
            if (mIndicatorAlign == IndicatorAlign.LEFT) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else if (mIndicatorAlign == IndicatorAlign.RIGHT) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            }
            mIndicatorContainer.setLayoutParams(layoutParams);
            mIndicatorContainer.setPadding(mIndicatorPaddingLeft, mIndicatorPaddingTop, mIndicatorPaddingRight, mIndicatorPaddingBottom);
        } else {
            mIndicatorContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsCanLoop) {
            return super.dispatchTouchEvent(ev);
        }
        if (mLoopRunnable != null) {
            switch (ev.getAction()) {
                // 按住Banner的时候，停止自动轮播
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_DOWN:
                    stopPlaying();
                    break;
                case MotionEvent.ACTION_UP:
                    startPlaying();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /************************************************************************************************************
     * 下面为公共方法
     ***********************************************************************************************************/
    @Override
    public void startPlaying() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (mAdapter == null) {
            return;
        }
        if (!mIsPlaying && null != mViewPager && mViewPager.getAdapter() != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(mLoopRunnable, mDelayedTime);
            mIsPlaying = true;
        }
    }

    @Override
    public void stopPlaying() {
        mIsPlaying = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void addPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 添加Page点击事件
     *
     * @param bannerPageClickListener {@link BannerPageClickListener}
     */
    @Override
    public void setBannerPageClickListener(BannerPageClickListener bannerPageClickListener) {
        mBannerPageClickListener = bannerPageClickListener;
    }

    /**
     * 返回ViewPager
     *
     * @return {@link ViewPager}
     */
    @Override
    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void setAutoScroll(int intervalInMillis) {
        mDelayedTime = intervalInMillis;
    }

    /**
     * 设置indicator 图片资源
     *
     * @param unSelectRes 未选中状态资源图片
     * @param selectRes   选中状态资源图片
     */
    @Override
    public void setIndicatorRes(@DrawableRes int unSelectRes, @DrawableRes int selectRes) {
        mIndicatorRes[0] = unSelectRes;
        mIndicatorRes[1] = selectRes;
    }

    @Override
    public void setBannerWidth(int bannerWidth) {
        mBannerWidth = bannerWidth;
    }

    @Override
    public void setBannerHeight(int bannerHeight) {
        mBannerHeight = bannerHeight;
    }

    /**
     * 是否无限轮播
     *
     * @param enable enable or disable
     */
    @Override
    public void setInfiniteLoop(boolean enable) {
        mIsCanLoop = enable;
    }

    @Override
    public void setScreenRatio(float ratio) {
        if (mBannerHeight == 0) {
            mBannerHeight = (int) (mBannerWidth / ratio);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(mBannerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mBannerHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public void setNormalIndicator(int resource) {
        mIndicatorRes[0] = resource;
    }

    @Override
    public void setSelectedIndicator(int resource) {
        mIndicatorRes[1] = resource;
    }

    @Override
    public void setDotMargin(int margin) {
        dotMargin = margin;
    }

    @Override
    public void setDuration(int durationInMillis) {
        mDuration = durationInMillis;
    }

    @Override
    public void setOffscreenPageLimit(int count) {
        mOffscreenPageLimit = count;
    }

    @Override
    public void isOpenClipMode(boolean isOpen) {
        mIsOpenClipMode = isOpen;
    }

    /**
     * 设置Indicator 的对齐方式
     *
     * @param indicatorAlign {@link IndicatorAlign#CENTER }{@link IndicatorAlign#LEFT }{@link IndicatorAlign#RIGHT }
     */
    @Override
    public void setIndicatorAlign(int indicatorAlign) {
        mIndicatorAlign = indicatorAlign;
    }

    @Override
    public void setIndicatorPaddingLeft(int indicatorPaddingLeft) {
        mIndicatorPaddingLeft = indicatorPaddingLeft;
    }

    @Override
    public void setIndicatorPaddingRight(int indicatorPaddingRight) {
        mIndicatorPaddingRight = indicatorPaddingRight;
    }

    @Override
    public void setIndicatorPaddingTop(int indicatorPaddingTop) {
        mIndicatorPaddingTop = indicatorPaddingTop;
    }

    @Override
    public void setIndicatorPaddingBottom(int indicatorPaddingBottom) {
        mIndicatorPaddingBottom = indicatorPaddingBottom;
    }

    @Override
    public void setClipModelPadding(int size) {
        mClipModePadding = size;
    }

    @Override
    public void setClipModelPageMargin(int size) {
        mClipModePageMargin = size;
    }

    /**
     * 是否显示Indicator
     *
     * @param visible true 显示Indicator，否则不显示
     */
    @Override
    public void setIndicatorVisible(boolean visible) {
        mIsShowIndicator = visible;
    }

    @Override
    public void setIsAutoPlay(boolean autoPlay) {
        mIsAutoPlay = autoPlay;
    }

    public void setBannerData(List<T> dataList, ViewHolderBannerCreator creator) {
        if (dataList == null || creator == null) {
            return;
        }
        mData = dataList;
        stopPlaying();
        if (mIsOpenClipMode && dataList.size() >= 3) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mViewPager.getLayoutParams();
            layoutParams.setMargins(Utils.dp2px(mClipModePadding), 0, Utils.dp2px(mClipModePadding), 0);
            mViewPager.setLayoutParams(layoutParams);
            setClipChildren(false);
            mViewPager.setClipChildren(false);

            // 设置PageMargin
            mViewPager.setPageMargin(mClipModePageMargin);
        } else {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mViewPager.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            mViewPager.setLayoutParams(layoutParams);
            setClipChildren(true);
            mViewPager.setClipChildren(true);
        }

        setOpenClipModelEffect();
        initIndicator();
        mAdapter = new DefaultBannerPagerAdapter(dataList, creator, mIsCanLoop);
        mAdapter.setUpViewViewPager(mViewPager);
        mAdapter.setPageClickListener(mBannerPageClickListener);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (null != mIndicators && !mIndicators.isEmpty()) {
                    int realPosition = position % mIndicators.size();
                    if (mOnPageChangeListener != null) {
                        mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
                // 切换indicator
                if (null == mIndicators || mIndicators.isEmpty()) {
                    return;
                }
                int realSelectPosition = mCurrentItem % mIndicators.size();
                for (int i = 0, size = mData.size(); i < size; i++) {
                    if (i == realSelectPosition) {
                        mIndicators.get(i).setImageResource(mIndicatorRes[1]);
                    } else {
                        mIndicators.get(i).setImageResource(mIndicatorRes[0]);
                    }
                }
                // 不能直接将mOnPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的position
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(realSelectPosition);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mIsPlaying = false;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        mIsPlaying = true;
                        break;
                    default:
                        break;
                }
            }
        });
        if (mOnPageChangeListener != null) {
            mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        }
    }

    private class DefaultBannerPagerAdapter extends PagerAdapter {
        private List<T> dataList;
        private ViewHolderBannerCreator creator;
        private ViewPager viewPager;
        private boolean canLoop;
        private BannerPageClickListener mPageClickListener;
        private final int mLooperCountFactor = 1000;

        DefaultBannerPagerAdapter(List<T> dataList, ViewHolderBannerCreator creator, boolean isCanLoop) {
            if (this.dataList == null) {
                this.dataList = new ArrayList<>();
            }
            this.dataList.addAll(dataList);
            this.creator = creator;
            this.canLoop = isCanLoop;
        }

        public void setPageClickListener(BannerPageClickListener pageClickListener) {
            mPageClickListener = pageClickListener;
        }

        /**
         * 初始化Adapter和设置当前选中的Item
         */
        public void setUpViewViewPager(ViewPager viewPager) {
            this.viewPager = viewPager;
            this.viewPager.setAdapter(this);
            this.viewPager.getAdapter().notifyDataSetChanged();
            int currentItem = canLoop ? getStartSelectItem() : 0;
            this.viewPager.setCurrentItem(currentItem);
        }

        private int getStartSelectItem() {
            // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
            // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
            int currentItem = getRealCount() * mLooperCountFactor / 2;
            if (currentItem % getRealCount() == 0) {
                return currentItem;
            }
            // 直到找到从0开始的位置
            while (currentItem % getRealCount() != 0) {
                currentItem++;
            }
            return currentItem;
        }

        @Override
        public int getCount() {
            return canLoop ? getRealCount() * mLooperCountFactor : getRealCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getView(position, container);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (canLoop) {
                int position = viewPager.getCurrentItem();
                if (position == getCount() - 1) {
                    position = 0;
                    setCurrentItem(position);
                }
            }
        }

        private void setCurrentItem(int position) {
            try {
                this.viewPager.setCurrentItem(position, false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        private int getRealCount() {
            return dataList == null ? 0 : dataList.size();
        }

        private View getView(int position, ViewGroup container) {

            final int realPosition = position % getRealCount();
            ViewHolderBaseBanner holder = null;
            holder = creator.createViewHolder();
            if (holder == null) {
                throw new RuntimeException("can not return a null holder");
            }
            // create View
            View view = holder.createView(container.getContext());

            if (dataList != null && dataList.size() > 0) {
                holder.onBind(container.getContext(), realPosition, dataList.get(realPosition));
            }

            // 添加点击事件
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPageClickListener != null) {
                        mPageClickListener.onPageClick(v, realPosition);
                    }
                }
            });

            return view;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlaying();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mIsAutoPlay) {
            startPlaying();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE && !mIsPlaying && mIsAutoPlay) {
            startPlaying();
        } else if (visibility == View.GONE) {
            stopPlaying();
        }
    }

    /**
     * Banner page 点击回调
     */
    public interface BannerPageClickListener {
        void onPageClick(View view, int position);
    }
}
