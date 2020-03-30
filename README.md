# Xmusic
Android音乐播放器，仿QQ音乐，音乐数据来源于QQ音乐。
</br>
APP的界面和功能基本上和[xmaster](https://github.com/xuyiyiyi/xmaster)的音乐一致。
</br>
因为原来做的xmaster为了先实现功能，代码结构上太乱了，有些逻辑上存在bug，所以决定抽出时间单独把音乐部分拿出来重构，顺便学习一下MVP框架。
</br>
Xmusic APP使用了MVP框架，优化了重复的获取和解析数据部分的代码，降低了代码的耦合性，把原来Activity/Fragment上业务逻辑的处理封装到了Presenter中，布局上也做了一些简化。
</br>
功能上暂时没有做新增，后续可能会继续模仿QQ音乐，增加新建自定义歌单、下载歌曲等等功能。
</br>
应用截图如下：
</br>
<img src="https://github.com/xuyiyiyi/Xmusic/raw/master/screenshot/xmusic.gif" width="250"/>
</br>
# 基本功能
1.音乐播放、暂停、播放进度。
</br>
2.播放下一首、上一首
</br>
3.获取当前播放列表
</br>
4.切换播放模式：列表循环、单曲循环、随机播放
</br>
5.桌面小组件播放
</br>
6.通知栏播放
</br>
7.歌曲榜单获取：QQ音乐新歌榜、人气榜、热歌榜等等60+个榜
</br>
8.歌词获取，支持自动滚动、跳转，歌词控件来自[lrcview](https://github.com/wangchenyan/lrcview)
</br>
9.音乐搜索，搜索记录的增删改查
</br>
10.搜索当前播放的歌手、专辑
</br>
11.添加音乐到我喜欢
</br>
12.保存音乐到最近播放
