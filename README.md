# OkDowload
download任务在异界面更新进度条并对任务进行控制
该应用包括四大部分，分别是：
<1>启动下载界面 MainActivity
<2>进度条更新界面 DownloadActivity
<3>下载任务控制队列 Downloadlist
<4>下载功能实现方法 DownloadManager
<5>数据库同步功能 SqlTool

   该应用利用CachedThreadPool来管理下载进程，并使用okHttp来进行下载请求，较好的实现了关于异步同步进度条，及解决item之间的数据污染问题
具体的应用结构文档尚在整理，作者会尽快发布。
   该应用源代码适合刚入门及对下载有需求的开发者阅读，对于回调及事件触发以及一定的应用编写流程具有一定的指导性
