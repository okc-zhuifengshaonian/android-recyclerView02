package com.example.recycler02;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.recycler02.adapters.ListViewAdapter;
import com.example.recycler02.adapters.BaseAdapter;
import com.example.recycler02.beans.Datas;
import com.example.recycler02.beans.ItemBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<ItemBean> mData;
    private BaseAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找到控件
        mRecyclerView = findViewById(R.id.recycler_view);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        initData();
        //设置默认的显示样式为ListView的效果
        showList(true, false);

        //处理下啦刷新
        handlerDownPullUpdate();
    }

    private void handlerDownPullUpdate() {
        mRefreshLayout.setColorSchemeResources(R.color.teal_200);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //当我们在顶部,下拉的时候 ,这个方法就会被出发
                ItemBean data = new ItemBean();
                data.title = "我是新添加的数据...";
                data.icon = R.mipmap.pic_07;
                mData.add(0, data);
                //更新UI
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //这里要做两件事,一件是让刷新停止,另外一件则是要更新列表
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //这里处理条目的点击事件,该干嘛就干嘛,跳转的就跳转...
                Toast.makeText(MainActivity.this, "您点击的是第" + position + "个条目", Toast.LENGTH_SHORT).show();
            }
        });

        //这里面去处理上拉加载更多
        ((ListViewAdapter) mAdapter).setOnRefreshListener(new ListViewAdapter.OnRefreshListener() {
            @Override
            public void onUpPullRefresh(final ListViewAdapter.LoaderMoreHolder loaderMoreHolder) {
                //更新UI
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();

                        if (random.nextInt() % 2 == 0) {
                            ItemBean data = new ItemBean();
                            data.title = "我是新添加的数据...";
                            data.icon = R.mipmap.pic_06;
                            mData.add(data);

                            //这里要做两件事,一件是让刷新停止,另外一件则是要更新列表
                            mAdapter.notifyDataSetChanged();
                            loaderMoreHolder.update(loaderMoreHolder.LOADER_STATE_NORMAL);
                        } else {
                            loaderMoreHolder.update(loaderMoreHolder.LOADER_STATE_RELOAD);
                        }
                    }
                }, 1000);
            }
        });
    }

    //这个方法用于模拟数据
    private void initData() {
        mData = new ArrayList<>();

        //创建模拟数据
        for (int i = 0; i < Datas.icons.length; i++) {
            //创建数据对象
            ItemBean data = new ItemBean();
            data.icon = Datas.icons[i];
            data.title = "我是第 " + i + " 个条目";
            //添加到集合里头
            mData.add(data);
        }
    }

    //这个方法用于现实ListView一样的效果
    private void showList(boolean isVertical, boolean isReverse) {
        //RecyclerView需要设置样式,其实就是设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器来控制
        //设置水平还是垂直
        layoutManager.setOrientation(isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
        //设置标准(正向)还是反向的
        layoutManager.setReverseLayout(isReverse);

        mRecyclerView.setLayoutManager(layoutManager);
        //创建适配器
        mAdapter = new ListViewAdapter(mData);
        //设置到RecyclerView里头
        mRecyclerView.setAdapter(mAdapter);

        //初始化事件
        initListener();
    }
}