package com.devdroid.sleepassistant.base;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.devdroid.sleepassistant.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by p1p1us on 17/10/18.
 */

//异步加载图片
public class ImageLoaderTool {

    /**
     * 加载圆形图片
     */
    public static void loadCircleImage(String imgUrl, ImageView imageView) {
        ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_icon_default)// 添加正在加载中的图片
                .showImageForEmptyUri(R.drawable.user_icon_default)// 设置当链接为空的图标
                .showImageOnFail(R.drawable.user_icon_default).cacheInMemory(true)// 设置缓存
                .cacheOnDisk(true).considerExifParams(true)//
                .displayer(new RoundedBitmapDisplayer(117)).build();// 90表示圆角
        ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
                animateFirstListener);
    }

    /**
     * 加载圆形图片
     */
    public static void loadCircleImageWithDefaultRes(String imgUrl, ImageView imageView, int resId) {
        ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(resId)// 添加正在加载中的图片
                .showImageForEmptyUri(resId)// 设置当链接为空的图标
                .showImageOnFail(resId).cacheInMemory(true)// 设置缓存
                .cacheOnDisk(true).considerExifParams(true)//
                .displayer(new RoundedBitmapDisplayer(117)).build();// 90表示圆角
        ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
                animateFirstListener);
    }

    /**
     * 加载图片，根据自己的角度去加载
     */
    public static void loadImageWithAngle(String imgUrl, ImageView imageView,
                                          int angle) {
        ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_icon_default)// 添加正在加载中的图片
                .showImageForEmptyUri(R.drawable.user_icon_default)// 设置当链接为空的图标
                .showImageOnFail(R.drawable.user_icon_default).cacheInMemory(true)// 设置缓存
                .cacheOnDisk(true).considerExifParams(true)//
                .displayer(new RoundedBitmapDisplayer(angle)).build();// 90表示圆角
        ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
                animateFirstListener);
    }

    /**
     * 加载图片根据自己的定义去加载
     */
    public static void loadImageWithDisplayImageOptions(String imgUrl,
                                                        ImageView imageView, DisplayImageOptions options) {
        ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
                animateFirstListener);
    }

    /**
     * 动画加载效果
     *
     * @author Administrator
     *
     */
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        // 图片加载
        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 2000);// 设置动画渐变时间
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
