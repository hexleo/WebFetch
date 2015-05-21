WebFetch
==========
无依赖极简网络爬虫组件，能在移动设备上运行的微型爬虫。  

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
WebFetch的使用非常简单，让小白用户快速上手，WebFetch为用户配置默认页面处理方法，默认将抓取到的页面信息使用`System.out.print`输出到控制台（通过配置PageHandler修改默认操作）。   

启动代码：

```java
WebFetch webFetch = new WebFetch();
webFetch.addBeginTask("https://github.com").start();
```

停止代码：

```java
webFetch.close();
```

WebFetch再执行`start()`方法后不会阻塞程序执行，可以加入多个网页地址，目前支持http与https，至少需要一个起始地址。  
ps 当前版本（webfetch_v0.0.1）稳定运行环境为Java桌面环境
    
    
WebFetch高级配置
--------
WebFetch提供了许多实用的设置接口，支持使用链式调用方法进行设置。以下给出最常使用方法的说明与示例。  

* `addBeginTask(String url)`  
网页抓取任务的输入方法，可以多次调用加入起始地址，通过传入不同的参数，比如Cookie的设置，网页编码方式设置等，也可以直接传入Request对象（Request包含请求的所有信息，可以通过set的方式设置）。  
```java
//最简单的调用方式是直接传入url
webFetch.addBeginTask("https://github.com");
```

* `addRule(String pattern)`  
添加URL抓取规则，可以加入多个规则，多个规则之间是“与”的关系，采用正则表达式进行验证，解析得到的URL必须通过验证才能加入到新的任务队列。例如：    
```java
//只抓取github.com下，带有p关键字的URL，其他解析出来的URL直接丢弃
webFetch.addRule(".*?github\.com.*?p.*?");
```

* `setMaxPageLayer(int max)`  
设置抓取网页的层次，从0开始为起始地址一直到n，当为负数时则无限层数。这项设置是WebFetch特别设计的，不完全统计其它爬虫基本没有此项功能。网页抓取任务使用队列进行调度，狭义的广度优先（其实就是为了能抓取盗版小说准备的特殊接口）。
```java
//使用层的设置可以很方便的抓取目录内容，
//例如，设置抓取两层内容，以新闻目录页面为入口，
//则其只抓取第一层的新闻目录以及第二层页面的新闻内容，避免写复杂的规则
webFetch.setMaxPageLayer(1);
```
例如：网站结构如下(A,B,C...均代表网页)  

```
layer 0   1   2   3
      A---B  
        |-C---E---F  
        |-D---G
```
A是使用addBeginTask传入的起始地址网页，A为第0层，B,C,D为第1层，E,G为第2层，F为第3层。
```java
	//设置A为起始页面，搜索层次为两层（第一层为0）
	webFetch.addBeginTask("http://A").setMaxPageLayer(1).start();
```
抓取得到的页面集合为{A, B , C , D}。WebFetch会在内存中记录已经访问过的url，避免出现重复循环访问的情况。

* `setPageHandler(PageHandler pageHandler)` 
通过传入实现PageHandler接口对象自定义抓取到页面后的处理方法。WebFetch实现了默认的`DefaultPageHandler`，直接通过`System.out.print`打印到控制台，为小白用户准备的懒人接口，通过传入自定义的PageHandler可以取消默认操作。
```java
public interface PageHandler {
	//finish接口返回抓取到的页面（只返回StatusCode=200的请求页面）
	public void finish(Page page);
	//此借口是为需要使用到数据库进行存储进行设计的，在调用WebFetch.close()后会调用此接口
	public void close();
}
```
`PageHandler`用户可以使用此接口处理被抓取到的页面，例如数据库存储等操作。WebFetch默认开启多个线程进行页面处理（与http请求线程不同），所以用户可以放心的实现此方法，不必担心是否要实现非阻塞的方法（考虑到资源受限，才用线程池进行管理，如果所有线程都阻塞则任务进入等待队列，直到有线程空闲）。

* `setURLParser(URLParser parser)`  
通过传入页面自定义页面URL解析实例改变默认的页面URL抽取规则。
```java
public interface URLParser {
	public Vector<Request> parse(Request parentPage); 
}
```
WebFetch使用正则表达式抽取`<a>`中`href="..."`指定的URL方法，目的是为减少对第三方的依赖，用户可以自行实现此接口，比如加入jsoup进行分析。  
`Request`提供`public static Request createSub(String mUrl ,  Request parentRequest)`方法，实现处理那些相对地址情况。  

比如：http://abc.def/web/index.html 中存在一个连接`<a href="./page.html">`，抓取到的URL为`./page.html`而用此URL发起的http请求是错误的，使用`Request.createSub`方法可以修正此URL为http://abc.def/web/page.html 。  

* 其它设置项目说明：  

```java
public class WebFetch {
	...
	//设置网络爬虫的线程数量，与页面处理分离，实现专一的网络爬取工作，默认线程数为5
	public WebFetch setThreadSize(int max);
	//设置一个页面的重试次数，默认为不重试
	public WebFetch setRetryTimes(int times);
	//代理设置
	public WebFetch setProxy(String host , int port , Proxy.Type type);
	//设置发起http请求的次数（无论正确与错误），默认为不限制
	public WebFetch setMaxTaskSize(int max);
	//设置连接超时时间，默认为8秒
	public WebFetch setConnectionTimeout(int timeout);
	//设置页面读取超时时间，默认为10秒
	public WebFetch setReadTimeout(int timeout);
}
```

关于
----
第一个版本还需要不断改进与完善，希望大家提出宝贵的改进意见，感谢大家的支持。  

联系方式：wanghailiang333@qq.com


