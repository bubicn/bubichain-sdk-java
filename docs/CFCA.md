# __CFCA 使用文档__

## 1 用途
介绍CFCA的申请流程，证书使用，和开发使用

## 2 申请流程
### 2.1 下载申请表
打开下面URL
```http
http://www.cfca.com.cn/zhengshuzizhu/
```
在申请表下载一栏下，点击个人证书申请表或企业证书申请表

### 2.2 填写申请表
注意： 

1. 证书种类，若要求UKey证书，选择其他种类证书，并注明UKey证书；否则，选择普通证书，则生成软件证书；其他选项无效，默认普通证书
2. 签名算法，目前只支持SHA256withRSA和SM3withSM2
3. 企业营业执照，目前只支持社会统一信用代码，选择其他选项无效

### 2.3 发送申请表
发送到布比运维人员的邮箱：tangxiaoyue@bubi.cn

### 2.4 审核并发送证书
#### 2.4.1 普通软证书
1. 耗时1-2天，布比运维人员会回复证书的两码：证书参考号（即证书序列号）和证书授权码
2. 打开下面URL 
```http
https://cs.cfca.com.cn
```
3. 输入证书序列号和授权码，选择Microsoft Enhanced Cryptographic Provider v1.0，点击下载，软证书会安装到证书列表中

#### 2.4.2 UKey证书
耗时1-2周时间，布比会通过邮局邮递申请表所填写的联系地址处，请注意查收


## 3 证书使用
包括软证书和UKey证书的使用
### 3.1 软证书
只能用于布诺平台的登录，查看一些记录，无法进行任何操作
###3.2 UKey证书
可用于布诺平台的任何操作，有权限控制


## 4 开发使用
CFCA证书的使用，包括前端和后端
### 4.1 前端开发 
前端开发，即js开发，需要安装前端插件，具体接口调用，请查看文档《CFCA 证书工具包(Ultimate版)接口使用手册.pdf》

### 4.2 后端开发 
后端开发，即java开发，需要引得包jar包，引用maven如下
```pom
<dependency>
	<groupId>cn.cfca.third</groupId>
	<artifactId>sadk</artifactId>
	<version>3.2.3.0.RELEASE</version>
</dependency>
```
具体接口调用，请查看《SADK工具包软件接口说明书（详细版）.pdf》