## Android图片轮播控件辅助类

## 介绍
现在许多App都会使用轮播图的控件，而对于轮播控件来说，在组件化的情况下，可以对其进行封装抽离成为单独一个类库进行使用。

## 效果图

##### 正常模式
![正常模式](https://github.com/firejunking/BannerHelper/blob/b54401447fb9b67421efc82e8abbde8aac5de075/images/default.gif)

##### 切割模式
![切割模式](https://github.com/firejunking/BannerHelper/blob/b54401447fb9b67421efc82e8abbde8aac5de075/images/clip.gif)

## Attributes属性（布局文件中使用）
|属性|format|describe
|---|---|---|
|banner_background|color|背景色
|banner_can_loop|boolean|是否可无限轮训
|banner_width_ratio_height_value|float|轮播控件宽高比
|banner_indicator_normal|reference|未选中下的指示器
|banner_indicator_selected|reference|选中下的指示器
|banner_indicator_margin|integer|指示器外边距
|banner_show_delayed_time|integer|轮播间隔时间
|banner_show_duration_time|integer|轮播持续时间
|banner_screen_page_limit|integer|屏幕页面限制
|banner_open_clip_mode|boolean|是否开启切割模式
|banner_is_auto_play|boolean|是否开启自动轮播模式
|indicatorPaddingLeft|dimension|指示器左内边距
|indicatorPaddingRight|dimension|指示器右内边距
|indicatorPaddingTop|dimension|指示器上内边距
|indicatorPaddingBottom|dimension|指示器下内边距
|indicatorAlign|enum|指示器在左/中/右
|banner_clip_mode_padding|integer|切割模式的内边距
|banner_clip_mode_page_margin|integer|切割模式的外边距
|banner_show_indicator|boolean|是否显示指示器

## 使用步骤
Gradle依赖
```
dependencies{
    implementation 'com.firejun.bannerviewhelper:1.0.0'
}
```
Adapter写法可多种
```
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
```
xml方式
```
    <com.firejun.bannerviewhelper.BannerViewHelper
        android:id="@+id/clip_banner_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:banner_can_loop="true"
        app:banner_clip_mode_padding="16"
        app:banner_clip_mode_page_margin="8"
        app:banner_is_auto_play="true"
        app:banner_open_clip_mode="true"
        app:banner_show_indicator="true" />
```
## 联系方式
QQ:435559203
