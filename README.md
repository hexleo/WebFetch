WebFetch
==========
无依赖极简网页爬取组件，能在移动设备上运行的微型爬虫。  

WebFetch要达到的目标：  

* 没有第三方依赖jar包
* 减少内存使用
* 提高CPU利用率
* 加快网络爬取速度
* 简洁明了的api接口
* 能在Android设备上稳定运行
* 小巧灵活可以方便集成的网页抓取组件

使用文档
---------
WebFetch的使用非常简单，让小白用户快速上手，WebFetch默认为用户配置的页面处理方法，默认将抓取到的页面信息使用`System.out.print`输出到控制台（通过配置PageHandler修改默认操作）。   

启动代码：

    WebFetch webFetch = new WebFetch();
    webFetch.addBeginTask("https://github.com").start();

停止代码：
    
    webFetch.close();
    
WebFetch再执行`start()`方法后不会阻塞程序执行，可以加入多个网页地址，目前支持http与https，至少需要一个起始地址。  
ps 当前版本（webfetch_v0.0.1）稳定运行环境为Java桌面环境
    
    
WebFetch高级配置
--------
WebFetch提供了许多实用的设置接口，支持使用链式调用方法进行设置。以下给出最常使用方法的说明与示例。  
`addBeginTask(String url)`是网页抓取任务的输入方法，可以多次调用加入起始地址，通过传入不同的参数，比如Cookie的设置，网页编码方式设置等，也可以直接传入Request对象（Request包含请求的所有信息，可以通过set的方式设置）。  
	
	//最简单的调用方式是直接传入url
	webFetch.addBeginTask("https://github.com");

`addRule(String pattern)`添加URL抓取规则，可以加入多个规则，多个规则之间是“与”的关系，采用正则表达式进行验证，解析得到的URL必须通过验证才能加入到新的任务队列。例如：    

	//只抓取github.com下，带有p关键字的URL，其他解析出来的URL直接丢弃
	webFetch.addRule(".*?github\.com.*?p.*?");
`setMaxPageLayer(int max)`


