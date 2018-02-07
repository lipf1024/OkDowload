package com.example.one.okdowload;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.one.okdowload.MySql.SqlTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ONE on 2018/2/4.
 */

public class DownloadActivity extends AppCompatActivity {
    private ProgressListener listener;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<FileBean> mData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dowload);

        initData();
        initView();
    }
    private void initView(){
        recyclerView=findViewById(R.id.download);

        adapter=new mAdapt(this,mData);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

    }
    private void initData(){
        mData= SqlTool.getSqlTool(null).getList();
        //Log.d("mData",mData.size()+"");
    }

    private long readNowsize(String name) throws IOException {
        File file=new File(getVar.getPath(),name+".my");
        RandomAccessFile accessFile = new RandomAccessFile(file, "r");//记录进度的文件
        return accessFile.readLong();
    }


    public class mViewHolder extends RecyclerView.ViewHolder{

        public mViewHolder(View itemView) {
            super(itemView);
        }
        ProgressBar bar;
        ImageView status;
        TextView name;
        TextView nowsize;
    }

    public class mAdapt extends RecyclerView.Adapter<mViewHolder>{
        private ArrayList<FileBean> mData;
        private LayoutInflater mInflater;
        public  mAdapt(Context context,ArrayList<FileBean> mData){
            this.mData=mData;
            mInflater=LayoutInflater.from(context);
        }

        @Override
        public mViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            View view=mInflater.inflate(R.layout.downloaditem,parent,false);
            final mViewHolder viewHolder=new mViewHolder(view);
            viewHolder.bar=view.findViewById(R.id.progressBar);
            viewHolder.status=view.findViewById(R.id.status);
            viewHolder.name=view.findViewById(R.id.down_name);
            viewHolder.nowsize=view.findViewById(R.id.down_size);
            /*
            设置暂停，开始，重新下载的监听
             */
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=viewHolder.getAdapterPosition();
                    FileBean bean=mData.get(position);
                    if(bean.getStatus()==0){//文件未下载完成
                        /*
                        文件未下载完成分为以下情况
                         1. .my文件存在
                           ①.存在下载线程
                              Ⅰ.若此时状态为stop
                                 触发线程中的start
                              Ⅱ.若此时状态为start
                                 不做改变
                            ②.若不存在下载线程
                              创建该下载线程
                          2. .my文件不存在
                                创建下载线程
                         */
                        try {
                            readNowsize(bean.getName());
                            //存在.my文件
                            final DownloadManager manager=Factory.getDownloadList().get(bean.getId());
                            if(manager!=null){
                                //存在下载线程
                                if(!manager.getStatus()){
                                    //状态为下载中
                                    //触发stop方法

                                           manager.onStop();


                                    viewHolder.status.setImageResource(R.drawable.stop);

                                }else{
                                    //状态为暂停
                                    //触发start方法

                                            manager.onStart();

                                    viewHolder.status.setImageResource(R.drawable.start);
                                }
                            }else{
                                //不存在下载线程
                                //创建该下载线程
                                Factory.getDownloadList().add(bean);
                                viewHolder.status.setImageResource(R.drawable.start);
                                adapter.notifyDataSetChanged();
                                adapter.notifyItemChanged(position);
                            }
                        } catch (IOException e) {
                            //不存在.my文件
                            //创建下载线程
                            Factory.getDownloadList().add(bean);
                            viewHolder.status.setImageResource(R.drawable.start);
                            adapter.notifyItemChanged(position);
                        }

                    }
                }
            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final mViewHolder holder, final int position) {

            final FileBean bean=mData.get(position);





            /*
            由于recyclerview item间会出现数据污染现象
            所以在显示前要进行初始化
             */
            Object tag=holder.itemView.getTag();
            if(tag!=null){
                int key=(int)tag;
                DownloadManager manager=Factory.getDownloadList().get(key);
                if(manager!=null){
                    manager.removeListener();
                }
            }
            holder.bar.setProgress(0);
            holder.name.setText("");
            holder.nowsize.setText("");
            holder.name.setTextColor(Color.BLACK);
            holder.nowsize.setTextColor(Color.BLACK);
            holder.nowsize.postInvalidate();
            holder.itemView.setTag(bean.getId());
            holder.status.setImageResource(R.drawable.stop);

           // final int nowposition=holder.itemView.getTag()==null?position:(int)holder.itemView.getTag();
         //   final int nowposition=(int)holder.itemView.getTag();


            holder.bar.setMax((int)bean.getSize());
            holder.name.setText(bean.getName());
            if(bean.getStatus()==1) {
                holder.bar.setProgress((int) bean.getSize());
                holder.nowsize.setText(bean.getSize()+"/"+bean.getSize());

            }else{
                try {
                    long size=readNowsize(bean.getName());
                    holder.bar.setProgress((int) size);
                    holder.nowsize.setText(size+"/"+bean.getSize());
                } catch (IOException e) {
                    FileBean newBean=SqlTool.getSqlTool(null).getFilebean(bean.getId());
                    if(newBean.getStatus()==1){
                       DownloadActivity.this.mData.set(position,newBean);
                       holder.bar.setProgress((int) bean.getSize());
                       holder.nowsize.setText(bean.getSize()+"/"+bean.getSize());
                    }else{
                        holder.nowsize.setText("文件损坏是否重新下载");
                        holder.nowsize.setTextColor(Color.RED);
                    }
                }
            }
            final DownloadManager manager=Factory.getDownloadList().get(bean.getId());
            if(manager!=null){
                if(!manager.getStatus()){
                    holder.status.setImageResource(R.drawable.start);
                }
                manager.setListener(new ProgressListener() {
                    @Override
                    public void Progress(final long progress) {
                        holder.bar.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.bar.setProgress((int)progress);
                                if(progress==bean.getSize())
                                    holder.status.setImageResource(R.drawable.stop);
                            }
                        });
                        holder.nowsize.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.nowsize.setText(progress+"/"+bean.getSize()+"");
                            }
                        });

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
