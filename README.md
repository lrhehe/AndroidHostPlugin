# AndroidHostPlugin
The most simple android plugin framework that fouse on module split and dynamic update.

针对模块拆分和动态更新的 Android 插件化开发最简框架。



###针对场景

1. 模块拆分：将工程中模块（application 或者 library）打包成独立插件 （plugin），宿主（Host）启动后进行加载
2. 动态更新：可下载插件，对原插件进行动态更新（重启应用可生效）

### 原理

1. 使用 DexClassLoader 加载插件代码
2. 给每个插件建立一套 AssetManager 和  Resources，借鉴 [Dynamic-load-Apk](https://github.com/singwhatiwanna/dynamic-load-apk) 项目
3. 代理 Instrumentation（借鉴 [Small](https://github.com/wequick/Small) 项目），在启动插件 Activity 的时候设置对应的资源

具体参见 [wiki](https://github.com/lrhehe/AndroidHostPlugin/wiki/Android-%E6%8F%92%E4%BB%B6%E5%8C%96%E5%BC%80%E5%8F%91)

###优势

采用最简单的方案，引入最少的坑

1. 不用对现有代码做修改
2. 不用对资源做修改
3. 原生的 Activity 启动方式（存在局限，见局限1）

###局限

1. 插件的 Activity 和 Service 要在宿主中进行注册
2. 由于每个插件一套独立资源，宿主，插件之间不能够互相访问资源



###进度：

* 0.1.0

  实现了简单demo：一个宿主（host），一个插件（plugin）， 一个插件库（pluginlib）

  plugin 依赖 pluginlib，host 启动后加载 plugin.apk 和 pluginlib.apk

  从 host 可以打开 plugin 的 MainActivity

  从 plugin 可以打开 pluginlib 的 PluginLibActivity

  在 plugin 可以使用 pluginlib 的 PluginLibUtils

  ​

###感谢

感谢 [Piasy](https://github.com/Piasy)，其对技术的追求和在 github 上的活跃很激励我，在知道我在寻找插件化方案后，向我推荐了 Small

感谢 Small 项目作者[林广亮]( https://github.com/galenlin)，在我调研过程中的答疑解惑