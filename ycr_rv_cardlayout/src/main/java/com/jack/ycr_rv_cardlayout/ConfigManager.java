package com.jack.ycr_rv_cardlayout;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jack
 * @time 19-12-10 下午9:30
 * @describe
 */
public class ConfigManager {
    /**
     * 显示可见的卡片数量
     */
    public int DEFAULT_SHOW_ITEM = 3;

    /**
     * 默认缩放的比例
     */
    public float DEFAULT_SCALE         = 0.15f;
    /**
     * 卡片Y轴偏移量时按照12等分计算
     */
    public int   DEFAULT_TRANSLATE_Y   = 12;
    /**
     * 卡片滑动时默认倾斜的角度
     */
    public float DEFAULT_ROTATE_DEGREE = 15f;
    /**
     * 卡片滑动时不偏左也不偏右
     */
    public int   SWIPED_NONE           = -1;

    /**
     * 卡片从左边滑出
     */
    public int SWIPED_LEFT  = 1 << 1;
    /**
     * 卡片从右边滑出
     */
    public int SWIPED_RIGHT = 1 << 2;

    //卡片的层叠方式
    public static final int UP   = 2;
    public static final int DOWN = 2 << 1;

    /**
     * 支持的滑动方向
     */
    public int getSwipeDirection() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }

    /**
     * 不使用系统默认的方法[系统的方法默认返回0.5f，RecyclerView的item移动50%将被视为滑动。] 单独抽离出来 方便自定义该值
     */
    public float getSwipeThreshold() {
        return 0.5F;
    }

    public float getThreshold(RecyclerView recyclerView) {
        return recyclerView.getWidth() * getSwipeThreshold();
    }

    /**
     * 是否无限循环
     */
    public boolean isLoopCard() {
        return false;
    }

    /**
     * 卡片堆叠的方向           目前只支持上下方向
     */
    public int getStackDirection() {
        return DOWN;
    }
}
