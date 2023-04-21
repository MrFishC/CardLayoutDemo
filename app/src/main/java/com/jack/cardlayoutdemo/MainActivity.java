package com.jack.cardlayoutdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.jack.ycr_rv_cardlayout.ConfigManager;
import com.jack.ycr_rv_cardlayout.CustomItemTouchHelperCallBackImp;
import com.jack.ycr_rv_cardlayout.CustomLayoutManager;
import com.jack.ycr_rv_cardlayout.OnItemSwipeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//https://www.jianshu.com/p/d07fd08f72db   推荐侧滑 todo

//滑动卡片  三种类型
//加载更多
//无线循环
//加载固定数量

//打磨成插件 使用者可以自定义配置信息 快速集成

public class MainActivity extends AppCompatActivity {

    private List<Integer> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        final ConfigManager manager = new ConfigManager();
        CustomItemTouchHelperCallBackImp<Integer> callBackImp = new CustomItemTouchHelperCallBackImp<>(adapter, list, manager);
        callBackImp.setOnSwipedListener(new OnItemSwipeListener<Integer>() {

            @Override
            public void onItemSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
                if (direction == manager.getSWIPING_LEFT()) {
                    System.out.println("向左侧滑动");
                } else if (direction == manager.getSWIPING_RIGHT()) {
                    System.out.println("向右侧滑动");
                } else {
                    System.out.println("向未知方向滑动");
                }
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, Integer o, int direction) {
                if (direction == manager.getSWIPED_UP()) {
                    System.out.println("从上方滑出");
                } else if (direction == manager.getSWIPED_DOWN()) {
                    System.out.println("从下方滑出");
                } else if (direction == manager.getSWIPED_LEFT()) {
                    System.out.println("从左侧滑出");
                } else if (direction == manager.getSWIPED_RIGHT()) {
                    System.out.println("从右侧滑出");
                } else {
                    System.out.println("从未知方向滑出");
                }
            }

            @Override
            public void onSwipedAllItem() {
                System.out.println("卡片全部滑出");
                //根据实际业务来实现 加载更多
                recyclerView.postDelayed(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        initData();
                        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                    }
                }, 1500L);
            }

        });
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callBackImp);
        final CustomLayoutManager cardLayoutManager = new CustomLayoutManager(recyclerView, touchHelper, manager);
        recyclerView.setLayoutManager(cardLayoutManager);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initData() {
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
        list.add(R.drawable.icon_common_bg);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_rv_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            MyViewHolder(View itemView) {
                super(itemView);
            }

        }
    }

}
