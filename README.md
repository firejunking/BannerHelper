## Android图片轮播控件辅助类

## 介绍
现在许多App都会使用轮播图的控件，而对于轮播控件来说，在组件化的情况下，可以对其进行封装抽离成为单独一个类库进行使用。

## 效果图

![正常模式](https://github.com/firejunking/BannerHelper/blob/b54401447fb9b67421efc82e8abbde8aac5de075/images/default.gif)

![切割模式](https://github.com/firejunking/BannerHelper/blob/b54401447fb9b67421efc82e8abbde8aac5de075/images/clip.gif)
## 方法

## Attributes属性（布局文件中使用）

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
