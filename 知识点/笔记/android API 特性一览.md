# Android 各版本特性一览

### Android 4.0(API 14)

- 如果在主线程中直接请求网络则会崩溃

### Android 4.3(API 18)

- 加入了属性动画
- 加入了 ViewOverlay

### Android 4.4(API 19)

- 使用外存保存应用专用文件不需要权限

### Android 5.0(API 21)

- Activity 过渡 API 适用于 Android 5.0 (API 21) 及更高版本

### Android 6.0 (API 23)

- 在 Java 文件中引用 xml 中定义的颜色由 int color = getResources().getColor(R.color.mycolor) 替换为 int color = getColor(R.color.myColor)
- 增加了动态请求权限，不需要安装时授予权限

### Android 8.1 (API 27)

- 限制了非加密的流量请求导致出现 not permitted by network security policy