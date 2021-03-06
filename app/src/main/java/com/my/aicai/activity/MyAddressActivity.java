package com.my.aicai.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.my.aicai.R;
import com.my.aicai.adapter.AddressListAdapter;
import com.my.aicai.entity.AddressEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gc on 2018/5/12.
 * PackageName: com.my.aicai.activity
 * Desc:
 */

public class MyAddressActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    private ImageView iv_back;
    private SwipeRefreshLayout refreshLayout;
    private ListView lv;
    private TextView tv_add;
    private List<AddressEntity> list=new ArrayList<>();
    private AddressListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        initView();
        initEvent();
    }

    private void initEvent() {
        iv_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MyAddressActivity.this,UpdateAddressActivity.class);
                intent.putExtra("address",list.get(position).getAddress());
                intent.putExtra("shouhuoName",list.get(position).getShouhuoName());
                intent.putExtra("shouhuoPhone",list.get(position).getShouhuoPhone());
                intent.putExtra("shouhuoID",list.get(position).getId());
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        startRefresh();
        initData();
    }

    private void initData() {
        list.clear();
        AVQuery<AVObject> query=new AVQuery<>("AddressEntity");
        query.orderByDescending("createAt");
        query.include("user");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> objects, AVException e) {
                if (objects!=null && objects.size()>0){
                    for (AVObject object : objects){
                        String address=object.getString("address");
                        String shouhuoName=object.getString("shouhuoName");
                        String shouhuoPhone=object.getString("shouhuoPhone");
                        String myId=object.getObjectId();
                        list.add(new AddressEntity(AVUser.getCurrentUser(),shouhuoName,shouhuoPhone,address,myId));
                    }
                    adapter.setData(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        stopRefresh();
    }

    private void initView() {
        iv_back= (ImageView) findViewById(R.id.iv_back);
        refreshLayout= (SwipeRefreshLayout) findViewById(R.id.refresh);
        lv= (ListView) findViewById(R.id.lv);
        tv_add= (TextView) findViewById(R.id.tv_add);
        adapter=new AddressListAdapter(this,list);
        lv.setAdapter(adapter);

    }

    /**
     * 开始刷新
     */
    public void startRefresh(){
        refreshLayout.setRefreshing(false);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setProgressViewOffset(false, 0, 30);
        refreshLayout.setRefreshing(true);
    }

    /**
     * 停止刷新
     */
    private void stopRefresh() {
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add:
                Intent intent=new Intent();
                intent.setClass(this,AddAddressActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onRefresh() {
        initData();
    }
}
