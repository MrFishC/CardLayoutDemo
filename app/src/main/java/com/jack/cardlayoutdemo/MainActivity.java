package com.jack.cardlayoutdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.jack.ycr_rv_cardlayout.ConfigManager;
import com.jack.ycr_rv_cardlayout.CardItemTouchHelperCallback;
import com.jack.ycr_rv_cardlayout.CardLayoutManager;
import com.jack.ycr_rv_cardlayout.OnSwipeListener;
import java.util.ArrayList;
import java.util.List;

//https://www.jianshu.com/p/d07fd08f72db   推荐侧滑 todo

//滑动卡片  三种类型
//加载更多
//无线循环
//加载固定数量

//打磨成插件 使用者可以自定义配置信息 快速集成

public class MainActivity1 extends AppCompatActivity {

    private List<Integer> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initView();
        initData();
    }

    private void initView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        final ConfigManager manager = new ConfigManager();
        CardItemTouchHelperCallback<Integer> cardCallback = new CardItemTouchHelperCallback<>(adapter, list, manager);
        cardCallback.setOnSwipedListener(new OnSwipeListener<Integer>() {

            @Override
            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {

                System.out.println("滑动操作 ");

                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
                viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
//                if (direction == manager.SWIPING_LEFT) {
//                    myHolder.dislikeImageView.setAlpha(Math.abs(ratio));
//                } else if (direction == manager.SWIPING_RIGHT) {
//                    myHolder.likeImageView.setAlpha(Math.abs(ratio));
//                } else {
//                    myHolder.dislikeImageView.setAlpha(0f);
//                    myHolder.likeImageView.setAlpha(0f);
//                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, Integer o, int direction) {
                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
                viewHolder.itemView.setAlpha(1f);
//                myHolder.dislikeImageView.setAlpha(0f);
//                myHolder.likeImageView.setAlpha(0f);
                Toast.makeText(MainActivity1.this, direction == manager.SWIPED_LEFT ? "swiped left" : "swiped right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipedClear() {
                Toast.makeText(MainActivity1.this, "data clear", Toast.LENGTH_SHORT).show();
//                可以实现加载更多
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 3000L);
            }

        });
        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
        final CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper,manager);
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
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
//            ImageView avatarImageView = ((MyViewHolder) holder).avatarImageView;
//            avatarImageView.setImageResource(list.get(position));
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
