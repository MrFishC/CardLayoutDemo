package com.jack.ycr_rv_cardlayout;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// 辅助类 ---> [ItemTouchHelper.Callback 详解](https://blog.csdn.net/qqqq245425070/article/details/80587271)
public class CardItemTouchHelperCallback<T> extends ItemTouchHelper.Callback {

    private final RecyclerView.Adapter mAdapter;
    private       List<T>              mDataList;
    private       OnSwipeListener<T> mListener;
    private final ConfigManager      mCManager;

    public CardItemTouchHelperCallback(@NonNull RecyclerView.Adapter adapter, @NonNull List<T> dataList, @NonNull ConfigManager manager) {
        this.mAdapter = CheckIsNullHepler.isNull(adapter);
        this.mDataList = CheckIsNullHepler.isNull(dataList);
        this.mCManager = CheckIsNullHepler.isNull(manager);
    }

    public void setOnSwipedListener(OnSwipeListener<T> mListener) {
        this.mListener = mListener;
    }

    /**
     * getMovementFlags：
     * 可以设置RV的Item滑动（或拖拽-这里不考虑）的方向
     * 通过mCManager进行设置：默认支持4个方向，可以自定义
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof CardLayoutManager) {
            swipeFlags = mCManager.getSwipeDirection();
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 继承ItemTouchHelper.Callback，需要重写onMove方法，返回值返回false或ture对滑动没有影响[已测试]
     * drag状态下，在canDropOver()返回true时，会调用该方法让我们拖动换位置的逻辑(需要自己处理变换位置的逻辑)
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * 对于滑动状态，RV的item滑出屏幕之后回调该方法，处理RV的item删除的逻辑
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // 移除onTouchListener,防止触摸 滑动之间冲突
        viewHolder.itemView.setOnTouchListener(null);

        int layoutPosition = viewHolder.getLayoutPosition();
        T remove = mDataList.remove(layoutPosition);

        if (mCManager.isLoopCard()) {
            mDataList.add(remove);
        }

        //主动调用刷新，否则会出现只有顶层卡片才能滑动
        mAdapter.notifyDataSetChanged();

        //使用接口回调进行拓展1
        if (mListener != null) {
            mListener.onSwiped(viewHolder, remove, direction == ItemTouchHelper.LEFT ? mCManager.SWIPED_LEFT : mCManager.SWIPED_RIGHT);
        }

        //使用接口回调进行拓展2
        // 当没有数据时回调 mListener
        if (mAdapter.getItemCount() == 0 && mListener != null && !mCManager.isLoopCard()) {
            mListener.onSwipedClear();
        }
    }

    /**
     * 针对swipe状态，是否允许swipe(滑动)操作,
     * 实际测试发现设置与否都可以滑动,不重写不会影响RV item的滑动
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 针对swipe和drag状态，整个过程中一直会调用这个函数,随手指移动的view就是在super里面做到的(和ItemDecoration里面的onDraw()函数对应)
     * <p>
     * 这里只考虑swipe状态
     * <p>
     * 这里实际上只需要设置mListener.onSwiping即可，子View布局的摆放由自定义的RecyclerView.LayoutManager实现类的onLayoutChildren方法决定。
     * 为了更好的体验效果，添加以下逻辑
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //滑动的比例
            float ratio = dX / mCManager.getThreshold(recyclerView);

            // ratio 最大为 1 或 -1
            if (ratio > 1) {
                ratio = 1;
            } else if (ratio < -1) {
                ratio = -1;
            }

            //旋转的角度
            itemView.setRotation(ratio * mCManager.DEFAULT_ROTATE_DEGREE);
            int childCount = recyclerView.getChildCount();

            //卡片滑动过程中   对view进行缩放处理  [这里的逻辑需要跟自定义的RecyclerView.LayoutManager实现类onLayoutChildren方法对应]        具体的缩放效果可以自行通过计算来尝试
            // 当数据源个数大于最大显示数时
            if (childCount > mCManager.DEFAULT_SHOW_ITEM) {
                //position：从1开始       for循环中定义position的初始值以及其边界，目的是为了让第一张不做处理
                for (int position = 1; position < childCount - 1; position++) {
                    int index = childCount - position - 1;
                    View view = recyclerView.getChildAt(position);
                    //通过调用setScaleX()和setScaleY()方法，可以实现View的缩放
                    view.setScaleX(1 - index * mCManager.DEFAULT_SCALE + Math.abs(ratio) * mCManager.DEFAULT_SCALE);
                    view.setScaleY(1 - index * mCManager.DEFAULT_SCALE + Math.abs(ratio) * mCManager.DEFAULT_SCALE);
                    //此方法用于设置View在水平方向的偏移量，以像素为单位。会引发View重绘
                    //偏移量为正数时，表示View从上向下平移。反之则从下向上平移
                    switch (mCManager.getStackDirection()) {
                        case ConfigManager.UP:
                            view.setTranslationY((index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y); //从下往上层叠
                            break;
                        case ConfigManager.DOWN:
                        default:
                            view.setTranslationY(-(index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y);//从上往下层叠
                    }
                }
            } else {
                // 当数据源个数小于或等于最大显示数时      for循环中定义position的初始值以及其边界，目的是为了让最后一张不做处理
                for (int position = 0; position < childCount - 1; position++) {
                    int index = childCount - position - 1;
                    View view = recyclerView.getChildAt(position);
                    view.setScaleX(1 - index * mCManager.DEFAULT_SCALE + Math.abs(ratio) * mCManager.DEFAULT_SCALE);
                    view.setScaleY(1 - index * mCManager.DEFAULT_SCALE + Math.abs(ratio) * mCManager.DEFAULT_SCALE);
                    switch (mCManager.getStackDirection()) {
                        case ConfigManager.UP:
                            view.setTranslationY((index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y); //从下往上层叠
                            break;
                        case ConfigManager.DOWN:
                        default:
                            view.setTranslationY(-(index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mCManager.DEFAULT_TRANSLATE_Y);//从上往下层叠
                    }
                }
            }
            //由于增加了上下方向 这里 可以按需添加业务逻辑
            if (ratio != 0) {
                if (mListener != null) {
                    mListener.onSwiping(viewHolder, ratio, ratio < 0 ? mCManager.SWIPED_LEFT : mCManager.SWIPED_RIGHT);
                }
            } else {
                if (mListener != null) {
                    mListener.onSwiping(viewHolder, ratio, mCManager.SWIPED_NONE);
                }
            }
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //因为Item View 重用机制[这块没有去研究]，第一层卡片滑出去之后，第二层卡片会出现偏移，处理方案：clearView方法中添加如下代码[重置]
        viewHolder.itemView.setRotation(0f);
    }
}
