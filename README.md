##Bigbang 是老罗设计的一种高效的文字处理方式。

遗憾的是Bigbang只能在锤子手机上使用。

为了让更多人体验到这种高效的交互方式，我们做了这个小应用[下载地址](http://www.coolapk.com/apk/com.forfan.bigbang)。

由于不具备系统权限，所以我们无法完全做到在锤子手机上的使用体验，但是我们也试图通过其他方法来进行补足。

本应用中使用到的技术包括：

1. 通过辅助模式，实现单击、长按、双击来进行选词；

2. 通过系统复制进行选词；

3. 通过辅助模式，实现全局复制进行选词；

4. 使用5.0以上的系统接口，进行截图OCR进行选词；

5. 通过悬浮窗、通知栏进行控制；

6. 通过xposed模块，实现单击、长按、双击来进行选词


后期我们会总结开发过程中使用到的技术和学习到的经验，写几篇技术博客，如果能对其他人有所帮助我们就心满意足了。

##下载地址

[酷安市场](http://www.coolapk.com/apk/com.forfan.bigbang )

[GooglePlay](https://play.google.com/store/apps/details?id=com.forfan.bigbang)


###应用内截图：

######点击触发：
![点击触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/click.gif)

######复制触发：

![复制触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/copy.gif)

######全局复制触发：
![全局复制触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/ucopy.gif)


######截屏OCR触发：
![截屏OCR触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/ocr.gif)


######分词界面设置：
![分词界面设置](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/setting.gif)






##感谢：


首先要感谢[@shang1101](https://github.com/shang1101) ，Bigbang是由我们俩配合完成的！

分词界面是基于baoyongzhang大神的代码修改的，在此郑重感谢，这是他的github地址： https://github.com/baoyongzhang/BigBang 

全局复制功能参考了这个应用的实现：http://www.coolapk.com/apk/com.camel.corp.universalcopy


##相关文章


我们在做这个项目的过程中，学习到了一些知识，遇到了很多坑，记录整理成文，希望可以帮助你阅读和理解本项目：

[开发《全能分词》（又名《锤子Bigbang》）的心路历程](http://www.jianshu.com/p/6e068fca111b)

[通过辅助模式获取点击的文字](http://www.jianshu.com/p/60758b3f2c7c)

[使用辅助服务实现全局复制](http://www.jianshu.com/p/c34cbef4d68e)

[使用辅助服务监听系统按键](http://www.jianshu.com/p/03904692b76b)

[如何通过Xposed框架获取点击的文字](http://www.jianshu.com/p/d7083c6e83bb)

[使用Xposed框架实现全局复制](http://www.jianshu.com/p/9dda421d23e4)

[在onLayout中实现简单的微动效](http://www.jianshu.com/p/93463ab36df9)

[如何使用Android的拖拽接口实现拖拽功能](http://www.jianshu.com/p/5001d0b42e10)

[通过ContentProvider多进程共享SharedPreferences数据](http://www.jianshu.com/p/bdebf741221e)

[Android上如何实现矩形区域截屏](http://www.jianshu.com/p/0462dae4c808)

[Android如何判断NavigationBar是否显示（获取屏幕真实的高度）](http://www.jianshu.com/p/84d951b3f079)

[如何在Bitmap截取任意形状](http://www.jianshu.com/p/d64cf9f69d05)

[4种获取前台应用的方法（肯定有你不知道的）](http://www.jianshu.com/p/a513accd40cd)

[android7.0 通过代码 分享图片到朋友圈](http://www.jianshu.com/p/5b0e0310d93f)

[Android中如何正确的获得所有App列表](http://www.jianshu.com/p/aee07cbb0cae)

[Android的supportV7中默认按钮的颜色设置](http://www.jianshu.com/p/98214d31318d)

[Android沉浸式与SearchView的坑](http://www.jianshu.com/p/f5d6bf2fc634)

[Android中“强制停止”和广播保活的一个小坑](http://www.jianshu.com/p/c632f5de465f)

[Xposed大法好,教你实现ForceTouch炫酷功能](http://www.jianshu.com/p/e7ea5e3bdb47)

[如何实现android炫酷悬浮球菜单](http://www.jianshu.com/p/56abca9fb592)


##License


![DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE](http://www.wtfpl.net/wp-content/uploads/2012/12/logo-220x1601.png)


```
Copyright © 2016 l465659833 <l465659833@gmail.com>
This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See the COPYING file for more details.

```
