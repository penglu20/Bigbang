Bigbang 是老罗设计的一种高效的文字处理方式。

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

你可以下载应用体验：

[酷安市场](http://www.coolapk.com/apk/com.forfan.bigbang )

[GooglePlay](https://play.google.com/store/apps/details?id=com.forfan.bigbang)


也可以看应用截图：

![点击触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/click.gif)
点击触发


![复制触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/copy.gif)
复制触发


![全局复制触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/ucopy.gif)
全局复制触发


![截屏OCR触发](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/ocr.gif)
截屏OCR触发


![分词界面设置](https://raw.githubusercontent.com/l465659833/Bigbang/master/gif/setting.gif)
分词界面设置





感谢：

首先要感谢[@shang1101](https://github.com/shang1101) ，Bigbang是由我们俩配合完成的！

https://github.com/baoyongzhang/BigBang 分词界面是基于鲍永章童鞋的代码修改的

http://www.coolapk.com/apk/com.camel.corp.universalcopy 全局复制功能参考了这里的实现


相关文章，可以帮助你阅读和理解本项目：

[Android上如何实现矩形区域截屏](http://www.jianshu.com/p/0462dae4c808)

[Android如何判断NavigationBar是否显示（获取屏幕真实的高度）](http://www.jianshu.com/p/84d951b3f079)

[如何在Bitmap截取任意形状](http://www.jianshu.com/p/d64cf9f69d05)
