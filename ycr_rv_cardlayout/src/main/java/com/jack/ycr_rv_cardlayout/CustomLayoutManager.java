package com.jack.ycr_rv_cardlayout;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author yuqirong
 */

//通过自定义LayoutManager可以实现很多炫酷的功能
public class CardLayoutManager extends RecyclerView.LayoutManager {

    private       RecyclerView    mRecyclerView;
    private       ItemTouchHelper mItemTouchHelper;
    private final ConfigManager   mCManager;

    public CardLayoutManager(@NonNull RecyclerView recyclerView, @NonNull ItemTouchHelper itemTouchHelper, @NonNull ConfigManager manager) {
        this.mRecyclerView = checkIsNull(recyclerView);
        this.mItemTouchHelper = checkIsNull(itemTouchHelper);
        this.mCManager = checkIsNull(manager);
    }

    private <T> T checkIsNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//        创建CardLayoutManager 继承于LayoutManager，必须重写方法generateDefaultLayoutParams()，默认返回RecyclerView.LayoutParams。
//        一般情况下，像下面这样写即可。
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //    绘制RecyclerView子View           刚打开页面的时候 onLayoutChildren执行了两次
    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 先移除所有view
        removeAllViews();
        // 在布局之前，将所有的子 View 先 Detach 掉，放入到 Scrap 缓存中
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();

        // 当数据源个数大于最大显示数时
        if (itemCount > mCManager.DEFAULT_SHOW_ITEM) {
            // 把数据源倒着循环，这样，第0个数据就在屏幕最上面了            为什么倒序就可以让第0个数据在屏幕最上面  原理是什么
            for (int position = mCManager.DEFAULT_SHOW_ITEM; position >= 0; position--) {
                //从缓冲池中获取到itemView
                final View view = recycler.getViewForPosition(position);
                // 将 Item View 加入到 RecyclerView 中
                addView(view);
                // 测量 Item View
                measureChildWithMargins(view, 0, 0);
                // getDecoratedMeasuredWidth(view) 可以得到 Item View 的宽度
                // 所以 widthSpace 就是除了 Item View 剩余的值
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                // 同理
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                // recyclerview 布局 // 在这里默认布局是放在 RecyclerView 中心      layoutDecoratedWithMargins: 将child显示在RecyclerView上面，left，top，right，bottom规定了显示的区域
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                // 其实屏幕上有四张卡片，但是我们把第三张和第四张卡片重叠在一起，这样看上去就只有三张
                // 第CardConfig.DEFAULT_SHOW_ITEM + 1张卡片主要是为了保持动画的连贯性
                if (position == mCManager.DEFAULT_SHOW_ITEM) {
                    view.setScaleX(1 - (position - 1) * mCManager.DEFAULT_SCALE);
                    view.setScaleY(1 - (position - 1) * mCManager.DEFAULT_SCALE);

                    switch (mCManager.getStackDirection()) {
                        case ConfigManager.UP:
                            view.setTranslationY((position - 1) * view.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y); //从下往上层叠
                            break;
                        case ConfigManager.DOWN:
                        default:
                            view.setTranslationY(-(position - 1) * view.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y);//从上往下层叠
                    }
                } else if (position > 0) {
                    view.setScaleX(1 - position * mCManager.DEFAULT_SCALE);
                    view.setScaleY(1 - position * mCManager.DEFAULT_SCALE);

                    switch (mCManager.getStackDirection()) {
                        case ConfigManager.UP:
                            view.setTranslationY(position * view.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y); //从下往上层叠
                            break;
                        case ConfigManager.DOWN:
                        default:
                            view.setTranslationY(-position * view.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y);//从上往下层叠
                    }
                } else {
//                    只有顶层的卡片才能滑动
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        } else {
            // 当数据源个数小于或等于最大显示数时
            for (int position = itemCount - 1; position >= 0; position--) {
                final View view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                // recyclerview 布局
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                if (position > 0) {
                    view.setScaleX(1 - position * mCManager.DEFAULT_SCALE);
                    view.setScaleY(1 - position * mCManager.DEFAULT_SCALE);
                    switch (mCManager.getStackDirection()) {
                        case ConfigManager.UP:
                            view.setTranslationY(position * view.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y); //从下往上层叠
                            break;
                        case ConfigManager.DOWN:
                        default:
                            view.setTranslationY(-position * view.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y);//从上往下层叠
                    }
                } else {
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        }
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                // 把触摸事件交给 mItemTouchHelper，让其处理卡片滑动事件              这里的原理需要了解一下 转移触摸事件
                mItemTouchHelper.startSwipe(childViewHolder);
            }
            return false;
        }
    };

}
