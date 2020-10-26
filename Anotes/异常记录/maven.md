
### java启动异常：java.lang.ClassNotFoundException: com.fasterxml.jackson.core.StreamReadFeature

* 描述：类分明是存在的，但启动的时候就是报错找不到
* 分析：这种情况多半是依赖的版本不对或者jar包冲突导致的
* 解决方式：找到相对应的依赖包，变更至合适版本
* 技巧：
  * **mvn dependency:tree** 可以查看项目的依赖树，结合查找功能可以快速定位冲突或者版本不匹配的依赖
  * 本地调试时为排除IDEA工具的影响，可以打成jar包后以java -jar的方式运行测试
