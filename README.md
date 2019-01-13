# 进程间通通信

**AIDL中定向tag in out inout的结论**


AIDL中的定向 tag 表示了在跨进程通信中数据的流向，
其中 in 表示数据只能由客户端流向服务端， out 表示数据只能由服务端流向客户端，而 inout 则表示数据可在服务端与客户端之间双向流通。
其中，数据流向是指在客户端中调用远程方法的传入的对象而言的。
in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；
out 的话表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；
inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，并且在服务端对接收到的对象有任何修改之后客户端将会同步变动；
详情请参考
[你真的理解AIDL中的in，out，inout么？](https://www.jianshu.com/p/ddbb40c7a251)
