WebFetch
==========
无依赖极简网页爬取组件，能在移动设备上运行的微型爬虫。  
目标：
* 没有第三方依赖jar包
* 减少内存使用
* 提高CPU利用率
* 加快网络爬去速度
* 最终能在Android设备上稳定运行

使用文档
---------
WebFetch的使用非常简单，不需要配置数据库，默认抓取到的网页通过System.out.println方式在控制台中打印日志。  
启动代码：

    WebFetch webFetch = new WebFetch();
    webFetch.addBeginTask("https://github.com").start();
停止代码：
    
    webFetch.close();
    
注意：至少有一个开始任务才能启动
    
    
设置项说明
--------
WebFetch实现了一个默认额内存数据库HashtableMemoryDB，默认存储在内存中，
