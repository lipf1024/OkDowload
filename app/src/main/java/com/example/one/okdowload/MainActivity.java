package com.example.one.okdowload;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.one.okdowload.MySql.SqlTool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SqlTool tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent=new Intent(MainActivity.this,DownloadActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        initView();
        initWeb();

        tool=SqlTool.getSqlTool(this);//只需要一次初始化可在应用中随处使用
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void  initView(){
        webView=findViewById(R.id.web);
    }
    /*
    初始化webview
     */
    private void  initWeb(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s, String s1, String s2, String s3, long l) {
                Log.d("MyS1",s);//下载链接
                Log.d("MyS2",s1);//UA
                Log.d("MyS3",s2);
                Log.d("MyS4",s3);

                Factory.getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Future<FileBean> future=Factory.getCachedThreadPool().submit(new GetFileParameter(s));
                            final FileBean bean=future.get(10000, TimeUnit.MILLISECONDS);
                            if(bean.getSize()!=0){
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AskForDownload(bean);
                                    }
                                });

                                 // Factory.getDownloadList().add(bean);

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                String Url = url.toLowerCase();
                if (Url.startsWith("http")) {
                    return false;
                }
                return true;
            }


        });
        webView.loadUrl("http://wap.baidu.com");
    }


     /*
     询问是否下载对话框
    */
    private void AskForDownload(final FileBean bean){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle(bean.getName());
        normalDialog.setMessage(bean.getSize()/1024+"k");
        normalDialog.setPositiveButton("下载",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bean.setId(tool.AddDownload(bean)); //加入数据库同时返回id
                        Log.d("BeanID",bean.getId()+"");
                        Factory.getDownloadList().add(bean);//加入下载队列
                    }
                });
        normalDialog.setNegativeButton("拒绝",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    /*

    网页返回监听
     */
    @Override
    public void onBackPressed() {
         if(webView.canGoBack()){
             webView.goBack();
         }
    }
}
