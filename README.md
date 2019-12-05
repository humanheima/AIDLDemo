本文参考《Android开发艺术探索》,将书中的示例分为客户端和服务端。

建议照着代码看文章。[GitHub-完整代码链接](https://github.com/humanheima/AIDLDemo)

Binder的工作机制

![Binder工作机制.png](https://upload-images.jianshu.io/upload_images/3611193-cdc35e419bbc25a9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

服务端：服务端首先要创建一个AIDL文件，将暴露给客户端的方法在这个AIDL文件中声明，接着创建一个Binder类继承AIDL接口中的Stub类，并实现Stub中的抽象方法。然后创建一个Service用来监听客户端的连接请求，在Service的onBind方法中返回这个Binder类的对象。

客户端：客户端首先要绑定服务端的Service，绑定成功后，将服务端返回的Binder对象转成AIDL接口所属的类型，接着就可以调用AIDL中的方法了。

不知道上面的文字在说什么，没关系，下面看个例子。

这个例子实现跨进程获取书籍列表和添加书籍的功能。

### 我们先从服务端开始

定义一个Book类实现Parcelable接口，实现Parcelable接口是为了能够实现跨进程传输。

```
public class Book implements Parcelable {

    private int bookId;
    private String bookName;

    public Book() {
    }

    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    /**
     * 参数是一个Parcel,用它来存储与传输数据
     * @param dest
     */
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        bookId = dest.readInt();
        bookName = dest.readString();
    }

    @Override
    public String toString() {
        return "{bookId:" + bookId + ",bookName:" + bookName + "}";
    }
    //省略getter/setter
   
}

```
如果一个AIDL文件中用到了自定义的Parcelable对象类，那么必须新建一个和它同名的AIDL文件，并在其中声明它为Parcelable类型。

为Book类声明对应的AIDL文件`Book.aidl`
```
// Book.aidl
package com.hm.aidlserver;

parcelable Book;
```
定义一个AIDL文件`IBookManager.aidl`，在其中声明获取书籍列表和添加书籍的方法。
```
// IBookManager.aidl
package com.hm.aidlserver;
//显式引入Book类
import com.hm.aidlserver.Book;

interface IBookManager {

   List<Book>getBookList();
   void addBook(in Book book);
}

```
注意：自定义的Parcelable对象类和AIDL文件必须要显式的import进来，不管它们是否和当前的AIDL文件位于同一个包内。

声明完了AIDL文件，重新build项目即可在build目录下看到系统为我们生成的用于进程间通信的类
```
../build/generated/source/aidl/debug/com/hm/aidlserver/IBookManager.java
```
我将该类调整了一下代码格式方便查看
```
/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/dumingwei/AndroidStudioProjects/AIDLDemo/aidlserver/src/main/aidl/com/hm/aidlserver/IBookManager.aidl
 */
package com.hm.aidlserver;

public interface IBookManager extends android.os.IInterface {

    //抽象方法，获取书籍列表
    public java.util.List<com.hm.aidlserver.Book> getBookList() throws android.os.RemoteException;

    //抽象方法，添加书籍
    public void addBook(com.hm.aidlserver.Book book) throws android.os.RemoteException;

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements com.hm.aidlserver.IBookManager {

        private static final java.lang.String DESCRIPTOR = "com.hm.aidlserver.IBookManager";

        //标志获取书籍列表的方法
        static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        //标志添加书籍的方法
        static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.hm.aidlserver.IBookManager interface,
         * generating a proxy if needed.
         */
        public static com.hm.aidlserver.IBookManager asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            //客户端调用queryLocalInterface(String descriptor)方法，返回的肯定是null
            //服务端调用queryLocalInterface(String descriptor)方法，返回的不是null
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.hm.aidlserver.IBookManager))) {
                return ((com.hm.aidlserver.IBookManager) iin);
            }
            //客户端最终返回的是一个代理对象
            return new com.hm.aidlserver.IBookManager.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_getBookList: {
                    data.enforceInterface(descriptor);
                    java.util.List<com.hm.aidlserver.Book> _result = this.getBookList();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_addBook: {
                    data.enforceInterface(descriptor);
                    com.hm.aidlserver.Book _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = com.hm.aidlserver.Book.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.addBook(_arg0);
                    reply.writeNoException();
                    return true;
                }
                
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        //这个类是给客户端用的
        private static class Proxy implements com.hm.aidlserver.IBookManager {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public java.util.List<com.hm.aidlserver.Book> getBookList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List<com.hm.aidlserver.Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(com.hm.aidlserver.Book.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void addBook(com.hm.aidlserver.Book book) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((book != null)) {
                        _data.writeInt(1);
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}

```
自动生成的`IBookManager.java`中，声明了我们在AIDL文件中定义的方法

```
//抽象方法，获取书籍列表
public java.util.List<com.hm.aidlserver.Book> getBookList() throws android.os.RemoteException;

//抽象方法添加书籍
public void addBook(com.hm.aidlserver.Book book) throws android.os.RemoteException;

```
在`IBookManager.java`文件中，还为我们生成了一个抽象的内部类`Stub`。

```
    public static abstract class Stub extends android.os.Binder implements com.hm.aidlserver.IBookManager {

        //用来标志Binder对象的名字
        private static final java.lang.String DESCRIPTOR = "com.hm.aidlserver.IBookManager";

        //标志获取书籍列表的方法
        static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        //标志添加书籍的方法
        static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        
        //... 
        
        
    }

```

注意：`Stub`抽象类继承了`android.os.Binder`类并实现了`com.hm.aidlserver.IBookManager`接口，但是并没有实现`com.hm.aidlserver.IBookManager`接口中的抽象方法。我们最后在服务端返回的`Binder`对象要继承`Stub`类并实现`com.hm.aidlserver.IBookManager`接口中的抽象方法。

在Stub类中，还为我们生成了一个私有的静态内部类Proxy，客户端就是使用这个类来实现和服务端进行通信的，后面再细说。
```
private static class Proxy implements com.hm.aidlserver.IBookManager {
    //...
}
```

#### 服务端Service的实现
```
/**
 * 服务端的Service
 */
public class BookManagerService extends Service {

    private static final String TAG = BookManagerService.class.getSimpleName();

    //使用CopyOnWriteArrayList，因为可能会有多个客户端同时操作书籍列表造成线程同步的问题
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    
    public BookManagerService() {
    }
    
    /**
     * 实现IBookManager接口中的方法
     */
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //先主动添加两个书籍
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "ios"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        //返回binder对象
        return mBinder;
    }
}
```
在BookManagerService中我们创建了一个Binder对象mBinder，mBinder实现IBookManager.aidl中定义的方法。然后我们在onBind方法中返回mBinder对象即可。

**注意：不要忘了在AndroidManifest.xml文件中注册BookManagerService**
```
<service
      android:name=".BookManagerService"
      android:enabled="true"
      android:exported="true">
</service>
```
到这，一个简单的服务端就实现了，接下来我们实现客户端。

### 客户端的实现

首先把在服务端定义的类和AIDL文件都拷贝到客户端（具体的项目结构可以下载源码看一看），重新build项目即可在build目录下看到系统为我们生成的用于进程间通信的类。

#### 绑定远程服务

```
    //定义IBookManager对象
    private IBookManager bookManager;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                getBookList();
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onServiceConnected: error" + e.getMessage());
            }
        }
        //...
    };
```

绑定服务
```
private void bind() {
    binded = true;
    Intent intent = new Intent();
    intent.setComponent(new ComponentName("com.hm.aidlserver", "com.hm.aidlserver.BookManagerService"));
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
}
```

绑定远程服务以后，我们就可以调用服务端的方法了

获取书籍列表
```
public void getBookList() {
    if (bookManager != null) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取书籍列表
                    bookList = bookManager.getBookList();
                    Log.e(TAG, "query book list,list type:" + bookList.getClass().getCanonicalName());
                    Log.e(TAG, "query book list,list:" + bookList.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    } 
}
```
添加书籍
```
public void addBook() {
    if (bookManager != null) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //添加书籍
                    bookManager.addBook(new Book(bookId, "Android 进阶" + bookId));
                    bookId++;
                    //添加完毕后，重新获取一下书籍列表
                    bookList = bookManager.getBookList();
                    Log.e(TAG, "query book list,list type:" + bookList.getClass().getCanonicalName());
                    Log.e(TAG, "query book list,list:" + bookList.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    } 
}
```
注意：当客户端调用服务端的方法时，如果是耗时方法的话，我们不能在主线程调用，否则可能会导致ANR，所以我们开启新线程来调用服务端的方法。

**现在我们已经可以愉快的调用服务端的方法了，即实现了进程间通信。**

接下来我们研究一下其中的一些细节。先从客户端我们定义的IBookManager对象和ServiceConnection对象。

### 深入细节
```
    //定义IBookManager对象
    private IBookManager bookManager;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //注释1处，
            Log.e(TAG, "onServiceConnected:" + service.getClass().getCanonicalName());
            //注释2处，获取bookManager对象，注意这个传入的service对象是一个BinderProxy对象
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                getBookList();
                bookManager.registerListener(mOnNewBookArriveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onServiceConnected: error" + e.getMessage());
            }
        }
        //...
    };
```
在注释1处，我们打印了`service`的类名`bookManager`的类型，如下所示。
```
onServiceConnected:android.os.BinderProxy
onServiceConnected:com.hm.aidlserver.IBookManager.Stub.Proxy
```
说明当绑定到服务端的Service的时候，服务端返回的`service`对象是一个BinderProxy类型的对象而我们在客户端定义的`bookManager`最终指向了一个`IBookManager.Stub.Proxy`类型的对象。这点要注意，后面还要再说。

在注释2处，调用了IBookManager.Stub的`asInterface(android.os.IBinder obj)`方法。

```
public static com.hm.aidlserver.IBookManager asInterface(android.os.IBinder obj) {
    if ((obj == null)) {
        return null;
    }
    //注释1处，客户端调用IBinder类的queryLocalInterface(String descriptor)方法，返回的是null
    android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
    if (((iin != null) && (iin instanceof com.hm.aidlserver.IBookManager))) {
        return ((com.hm.aidlserver.IBookManager) iin);
    }
    //注释2处，客户端最终返回的是一个代理对象
    return new com.hm.aidlserver.IBookManager.Stub.Proxy(obj);
}

```
注意：asInterface方法的注释1处，客户端调用IBinder类的`queryLocalInterface(String descriptor)`方法，返回的是null。为什么呢？IBinder接口有两个实现类，一个是Binder，一个是BinderProxy。我们在onServiceConnected(ComponentName name, IBinder service)方法中得到的`service`是一个`BinderProxy`类型的对象。

BinderProxy的queryLocalInterface(String descriptor)方法直接返回的是null。

```
public IInterface queryLocalInterface(String descriptor) {
    return null;
}
```
asInterface方法的注释2处，客户端最终返回的是一个代理对象。也就是说我们客户端的bookManager对象是一个`IBookManager.Stub.Proxy`对象。我们获取书籍列表和添加书籍都是调用`IBookManager.Stub.Proxy`类中的方法，接下来看一下。

```
    private static class Proxy implements com.hm.aidlserver.IBookManager {
        //注释1处
        private android.os.IBinder mRemote;

        Proxy(android.os.IBinder remote) {
            mRemote = remote;
        }

        @Override
        public android.os.IBinder asBinder() {
            return mRemote;
        }

        public java.lang.String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public java.util.List<com.hm.aidlserver.Book> getBookList() throws android.os.RemoteException {
            //构建发送到服务端的数据
            android.os.Parcel _data = android.os.Parcel.obtain();
           //构建服务端返回的数据
            android.os.Parcel _reply = android.os.Parcel.obtain();
            //要获取的书籍列表
            java.util.List<com.hm.aidlserver.Book> _result;
            try {
                _data.writeInterfaceToken(DESCRIPTOR);
                //注释1处，标记调用服务端的getBookList方法
                mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                _reply.readException();
               //获取查询到的书籍列表
                _result = _reply.createTypedArrayList(com.hm.aidlserver.Book.CREATOR);
            } finally {
                _reply.recycle();
                _data.recycle();
            }
            //返回结果
            return _result;
        }

        @Override
        public void addBook(com.hm.aidlserver.Book book) throws android.os.RemoteException {
            //构建传到服务端的数据
            android.os.Parcel _data = android.os.Parcel.obtain();
            //构建服务端返回的数据，添加数据其实我们是没有要求返回数据的
            android.os.Parcel _reply = android.os.Parcel.obtain();
            try {
                _data.writeInterfaceToken(DESCRIPTOR);
                if ((book != null)) {
                    _data.writeInt(1);
                    //将book对象写入到data中
                    book.writeToParcel(_data, 0);
                } else {
                    _data.writeInt(0);
                }
                //注释1处，标记调用服务端的addBook方法
                mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

    }
```
注释1处，Proxy类中的mRemote就是一个BinderProxy对象。

BinderProxy的transact(int code, Parcel data, Parcel reply, int flags)方法。
```
public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    //检查数据大小，不能大于800K
    Binder.checkParcel(this, code, data, "Unreasonably large binder buffer");
    //...
    try {
        //注释1处，
        return transactNative(code, data, reply, flags);
    } finally {
        //...
    }
}

```
注释1处，调用了BinderProxy的`transactNative(int code, Parcel data, Parcel reply,
            int flags)`方法，这是一个本地方法，最终会调用服务端Binder对象的transact方法。这个Binder对象就是我们在服务端Service中返回的对象（IBookManager.Stub类型）。

```
     /**
     * 实现IBookManager接口中的方法
     */
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    }
```

我们看一下IBookManager.Stub继承了Binder类，并没有重写transact方法

Binder类transact方法
```
public final boolean transact(int code, @NonNull Parcel data, @Nullable Parcel reply,
            int flags) throws RemoteException {
    
    if (data != null) {
        //读取客户端传递的数据
        data.setDataPosition(0);
    }
    //注释1处，调用onTransact方法
    boolean r = onTransact(code, data, reply, flags);
    if (reply != null) {
        //设置返回给客户端的数据
        reply.setDataPosition(0);
    }
    return r;
}
```
注释1处，调用了onTransact方法，IBookManager.Stub的重写了onTransact方法，我们看一下。

```
@Override
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) 
        throws android.os.RemoteException {
    java.lang.String descriptor = DESCRIPTOR;
    switch (code) {
        case INTERFACE_TRANSACTION: {
            reply.writeString(descriptor);
            return true;
        }
        case TRANSACTION_getBookList: {
            data.enforceInterface(descriptor);
            java.util.List<com.hm.aidlserver.Book> _result = this.getBookList();
            reply.writeNoException();
            //返回数据
            reply.writeTypedList(_result);
            //返回true表示调用成功
            return true;
        }
        case TRANSACTION_addBook: {
            data.enforceInterface(descriptor);
            com.hm.aidlserver.Book _arg0;
            if ((0 != data.readInt())) {
                _arg0 = com.hm.aidlserver.Book.CREATOR.createFromParcel(data);
            } else {
                _arg0 = null;
            }
            //添加书籍
            this.addBook(_arg0);
            reply.writeNoException();
            return true;
        }
        default: {
            return super.onTransact(code, data, reply, flags);
        }
    }
}
```

参考链接

* 《Android开发艺术探索》
* [你真的理解AIDL中的in，out，inout么？](https://www.jianshu.com/p/ddbb40c7a251)
* [写给 Android 应用工程师的 Binder 原理剖析](https://zhuanlan.zhihu.com/p/35519585)


