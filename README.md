# AgeraSample
本文主要是通过例子来学习google开源的，轻量级响应式编程库argera。它主要提供api来帮助coder编写rx风格的代码，例如，把数据的获取到数据的展示用rx风格编写出来，使得业务逻辑非常清晰，过程清晰，步骤的职责也很清晰，易于维护扩展。[地址点这里}(https://github.com/google/agera)
## 基本设计思想
下面是个人认为比较重要的设计思想：

1. 基于观察者模式的pull模式，将一个“事件”的传递和该“事件”对应的数据分离，某种意义上来说，“事件”不一定对应着一个数据。由此思想而设计出了基本的接口，看源码就知道。
2. 抽象出基本的***数据处理流***模式，比如数据的获取（产生），合并，变换，筛选，映射，列表生成等。
3. 事件的异步处理，在处理数据时，可以设置处理的上下文环境，通过Java的Executor来实现线程的切换。

## 和Android的关系
竟然这个类库是在Android下使用的，其实也没有说一定在Android下用，只是它的一个特性适合在Android下使用，那就是cancel机制。我们知道界面Activity是有生命周期的，而数据的获取则没有所谓的生命周期的概念，但是这样的结果会导致两个问题：

1. 数据获取的线程会持有界面的对象引用（所以要确保后台线程不能一直持有界面对象）。
2. 当后台线程工作完成，数据处理工作切换回界面线程时，此时界面可能已经destroy了，尴尬的空指针随时可能发生。

显然问题1是确保线程的退出机制，这个不是这里的重点。问题2，那就是需要在界面生命周期的特定时候去取消关注“事件”的发生，而且取消动作和数据处理的回调在同一个线程完成，那么确保这两者肯定是一前一后的，关键是我们可以告诉agera，当监听者被全部移除掉后，已经发生的“事件“将不会被传递。

## Agera的默认实现
基于之前提到的一些基本思想（包括但不限于上面三条），Agera提供了自己的默认实现，但是它定义的接口可以由我们自己来实现。下面简单说明它实现的原理：

1. 函数式的指令执行：Agera规定了flow处理的函数顺序（应该是从大量实践中总结出来的，我们只需要将我们要处理的过程安插在它给定的过程中即可），那么就是根据这些顺序将指令存放在一个数组当中，当事件发生时按数组中的顺序执行即可。这些顺序给定在过程中的线程切换如何实现呢？看2
2. 当执行到需要切换运行上下文时，它会将当前指令所在的位置记录下来，等切换到指定线程后继续执行剩下的指令。
3. 观察者接收到事件通知时，执行的环境就是它调用被观察者的addUpdate方法时的线程环境，一般情况下都是主线程，当然，若是有一个工作线程也想监听某事件，那么这个线程在执行addUpdate方法前确保它已经拥有自己的Looper对象。否则就crash。
4. 我们知道事件流的处理可以是异步的，那么当一事件流没有处理结束，而新的事件流又开始了会是怎么一方情形呢？特别是当线程切换的时候，会保留全局的上下文（既当前执行流的指令），这就存在竞争问题。这里的处理很简单，就是在设置指令执行顺序的index初始时加锁处理，剩下的就是基于这个index继续执行。

## Sample 讲解
目前这个项目里面有两个，对应了两个uri，一个是sample://rxui,另一个是sample://rxfunc，可以通过 adb shell am start -a android.intent.action.VIEW "sample://xxx" 来启动对应的界面。

1. rxui对应的sample查询手机号码所在地，突出的含义时如何响应一个event，以及数据的获取。
2. rxfunc对应的sample更加突出对数据流的处理，即将一种数据转换成另外一种。
