WebFetch
==========
无依赖极简网页爬取组件，能在移动设备上运行的微型爬虫。  
更新中...
使用文档
---------

    WebFetch wf = new WebFetch();
    wf.addRule(".*")
      .addBeginTask(new Request("http://localhost/webfetch/a.html"))
      .setMaxPageLayer(-1)
      .setConnectionTimeout(10*1000)
      .setReadTimeout(20*1000)
      .start();
