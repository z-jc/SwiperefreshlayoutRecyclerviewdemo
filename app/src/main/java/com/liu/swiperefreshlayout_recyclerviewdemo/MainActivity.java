package com.liu.swiperefreshlayout_recyclerviewdemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.swiperefreshlayout_recyclerviewdemo.adapter.RefreshAdapter;
import com.liu.swiperefreshlayout_recyclerviewdemo.view.RefreshItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    List<String> mDatas = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mTvList;
    private TextView mTvGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLUE, Color.BLUE);
        mTvList = (TextView) findViewById(R.id.tv_list);
        mTvList.setOnClickListener(this);
        mTvGrid = (TextView) findViewById(R.id.tv_grid);
        mTvGrid.setOnClickListener(this);
    }

    /**
     * 初始添加数据
     */
    private void initData() {
        for (int i = 0; i < 10; i++) {
            mDatas.add(" Item " + i);
        }
        initRecylerView();
    }

    private void initRecylerView() {
        mRefreshAdapter = new RefreshAdapter(this, mDatas);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new RefreshItemDecoration(this, RefreshItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRefreshAdapter);
    }

    private void initListener() {
        initPullRefresh();
        initLoadMoreListener();
    }

    /**
     * 下拉更新
     */
    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        Log.e("TAG", "这是下拉操作...");
                        for (int i = 0; i < 10; i++) {
                            mDatas.add("Heard Item " + i);
                        }
                        Log.e("TAG", "data:" + mDatas.size());
                        mRefreshAdapter.AddItem(mDatas);
                        //刷新完成
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRefreshAdapter.changeMoreStatus(mRefreshAdapter.NO_LOAD_MORE);
                        Toast.makeText(MainActivity.this, "更新了 " + mDatas.size() + " 条目数据", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
        });
    }

    /**
     * 上拉加载
     */
    private void initLoadMoreListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mRefreshAdapter.changeMoreStatus(mRefreshAdapter.LOADING_MORE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRefreshAdapter.getItemCount()) {
                    //设置正在加载更多
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDatas.clear();
                            Log.e("TAG", "正在执行上拉加载...");
                            for (int i = 0; i < 10; i++) {
                                mDatas.add("footer  item" + i);
                            }
                            Log.e("TAG", "当前data的数据..." + mDatas.size());
                            mRefreshAdapter.AddFooterItem(mDatas);
                            //设置回到上拉加载更多
                            mRefreshAdapter.changeMoreStatus(mRefreshAdapter.PULLUP_LOAD_MORE);
                            Toast.makeText(MainActivity.this, "更新了 " + mDatas.size() + " 条目数据", Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_list:
                mTvList.setTextColor(Color.WHITE);
                mTvList.setTextSize(20);
                mTvGrid.setTextColor(Color.WHITE);
                mTvGrid.setTextSize(16);

                mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                break;
            case R.id.tv_grid:
                mTvGrid.setTextColor(Color.WHITE);
                mTvGrid.setTextSize(20);
                mTvList.setTextColor(Color.WHITE);
                mTvList.setTextSize(16);

                GridLayoutManager manager = new GridLayoutManager(this, 2);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                break;
        }
    }
}