
JVM运行时数据区域三大核心
Heap Area
1.存储全部都是Object对象实例,对象实例中的一般都包含了其数据成员以及与该对象对应的class信息
2.一个JVM实例在运行的时候只有一个Heap区域,该区域被所有的线程共享

Young Gegeration(年轻代): Object产生和基本活跃区
Eden
Survivor(Form,To)  内存大小一致,延缓衰老,To满了去到From,默认15 age

有一组相同的age,总和且达到survivor一半的时候,可以直接进入老年代
Old Gegeration(老年代): 经过几次GC后依旧存在(from区的gc次数到底阈值),大对象



Method Area
1.方法区域又名静态成员区域,包含整个程序的Class,static成员等
2.方法区被所有的线程共享

Permanent Generation(永久代) -> Java8  Metaspace (Native Memory Space os级别物理机器上的内存,动态伸缩 gc的时候自动调整大小,能延缓下一次gc的到来)
有可能发生Gc,当一个类的对象全部被回收,类加载也被回收掉了



Stack Area
1.stack区域属于线程私有,每个线程都会包含一个Stack区域,Stack区域中含有基本的数据类型以及对象引用,其他线程均不能直接访问该区域
2.分为三大部分:基本数据类型区域,操作指令区域,上下文等




多线程的java应用程序:为了让每个线程正常工作就提出了程序计数器(Program Counter Register),每个线程都有自己的程序计数器,这样当线程执行切换的时候就可以在上次执行的基础上继续执行,
仅仅从一条线程线性执行的角度而言,代码是一条一条的往下执行的,这个时候就是Program Counter Register
JVM就是通过Program Counter Register的值来决定该线程下一条需要执行的字节码指令,进而进行选择语句、循环、异常处理等。


线程
从OOP而言,相当于一个对象,该对象中具有执行代码,同时也有要处理的数据,数据包含Thread工作时候要访问的数据,同时也包含现在的Stack,在Stack中包含了Thread本地的数据,也包含了拷贝的全局数据,
从面向过程的角度而言:线程=代码+数据


-Xss  每个线程堆栈大小
-Xms  JVM初始分配的堆内存
-Xmx  JVM最大允许分配的堆内存
-Xms ,-Xmx 一般相同,避免不同时JVM分配内存不稳定
-XX:NewSize 新生代初始化,越大越好,不允许比老年代大
-XX:MaxNewSize 新生代最大,越大越好,不允许比老年代大
-XX:NewRatio  大于1,说的是老年代与新生代的比例
-XX:SurvivorRatio Eden和其中一个SurivorRatio的比例,比如5,表示Eden占整个Young的5/7,From和To各占1/7


Heap 分配内存越大越好,缓存命中率越高越好
如果不想增加gc的次数,又不想让新生代的对象很快的进入老年代,需要监控检测
age相同的对象

gc
System.gc();
Runtime.gc();

gc log

[GC [PSYoungGen: 2560K->501K(3072K)] 2560K->1617K(10240K), 0.0022960 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
PSYoungGen minor gc类型
2560K->501K(3072K) gc前后heap中young区占有量(总量)
2560K->1617K(10240K) gc前后heap占有量(总量)
0.0022960 secs gc消耗时间
user=0.01 sys=0.00 用户空间,系统空间消耗时间
real小,占user+sys的1% 可能是线程,并发消耗

[Full GC [PSYoungGen: 512K->0K(3072K)] [ParOldGen: 6052K->5814K(7168K)] 6564K->5814K(10240K) [PSPermGen: 3002K->3001K(21504K)], 0.0889860 secs] [Times: user=0.23 sys=0.00, real=0.09 secs]
PSYoungGen: 512K->0K(3072K) gc前后heap中young区占有量(总量)
ParOldGen: 6052K->5814K(7168K) gc前后heap中old区占有量(总量)
6564K->5814K(10240K) gc前后heap占有量(总量)
PSPermGen: 3002K->3001K(21504K) gc前后metaspace永久区占有量(总量)
0.0022960 secs gc消耗时间
user=0.01 sys=0.00 用户空间,系统空间消耗时间


gc 根搜索算法
标记-清除算法 用根搜索算法,如果从root开始遍历可达就标记成1,如果不可达就标记成0,然后回收标记为0的,并把标记为1的重置成0
递归整个堆,效率低,释放内存空间不连续

复制算法
把标记的可达对象复制到空闲区,然后把活动区的都清除掉,把空闲区变成活动区
耗内存

标记-整理算法

