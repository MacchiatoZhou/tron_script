# Tron Script
本项目提供了用于测试波场p2p网络的脚本工具。功能包括波场节点发现协议消息收发和修改、节点连接、p2p网络消息监听和修改、p2p消息解析和打包等功能。
## Module 1-MyConnection
该模块实现了波场p2p网络中各种消息类型的构造、打包、解析功能，p2p连接的握手、连接信道保活等机制。该模块支持自定义消息、参数修改、消息中继流程自定义、常量更改。

该模块可以用于测试波场全节点连接、探测波场节点信息、调试波场全节点连接等。

### 编译
编译需要`git`和64位的`Oracle JDK 1.8`，其他版本的JDK尚不支持。`Windows`和`Linux`均可

1. clone仓库
```bash
$ git clone https://github.com/klaypexwcf/tron_script.git
$ cd ./src/myConnection
$ javac MyChannelManager.java
```
2. 在当前目录下创建一个名为`MANIFEST.MF`的文件

内容如下
> Main-Class: myConnection.MyChannelManager

3. 打包文件
```bash
$ jar cfm TronConnector.jar MANIFEST.MF MyChannelManager.class
```
### 运行
```bash
$ java -jar ./TronConnector.jar 
```
目前可选的命令行参数
- --localId 指定本地模拟节点的nodeId
- --remoteIp 指定需要连接的节点的nodeId
- 后续将支持更多命令行参数...
## Module 2-MyDiscover
该模块实现了波场的节点发现协议中各种消息类型的构造、打包、解析，进而支持了波场节点中发现协议模块的模拟。

该模块可以用于调试波场全节点的节点表、探测收集波场p2p网络中节点的信息（包括IP、nodeId、区块高度等）。


### 编译
编译需要`git`和64位的`Oracle JDK 1.8`，其他版本的JDK尚不支持。`Windows`和`Linux`均可

1. clone仓库
```bash
$ git clone https://github.com/klaypexwcf/tron_script.git
$ cd ./src/myDiscover
$ javac Main.java
```
2. 在当前目录下创建一个名为`MANIFEST.MF`的文件

内容如下
> Main-Class: myDiscover.Main

3. 打包文件
```bash
$ jar cfm TronDiscoverSim.jar MANIFEST.MF Main.class
```
### 运行
```bash
$ java -jar ./TronDiscover.jar 
```
运行之后，会向指定的节点持续发送Kad_ping消息，每条消息中会包含不同的nodeId

目前可选的命令行参数
- --localId 指定本地模拟节点的nodeId
- --remoteIp 指定需要连接的节点的nodeId
- --network 指定网络号
- 后续将支持更多命令行参数...