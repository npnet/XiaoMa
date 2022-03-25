####使用说明
1. 首先，需要在application调用XmSkinManager中的init()方法初始化
2. 需要加载皮肤包的时候调用XmSkinManager中loadSkin方法，入参为皮肤包名称和皮肤包加载监听listener
3. 需要恢复默认皮肤，调用XmSkinManager中restoreDefaultTheme方法

####注意：
1. 代码中使用new View()创建对象，框架无法知道该控件的存在，所以得让业务代码统一使用View.inflate()的方式来动态实例化View对象
2. 自定义view的换肤可以通过继承widget下XmSkin...已有控件来实现换肤
3. 待续...
4. 如果要使用第三方库提供控件换肤，则可以参考以下[链接](https://github.com/ximsfei/Android-skin-support/blob/master/third-part-support/circleimageview/src/main/java/skin/support/circleimageview/widget/SkinCompatCircleImageView.java)来实现
5. [Android-Skin-Support的readme](https://github.com/ximsfei/Android-skin-support#%E8%87%AA%E5%AE%9A%E4%B9%89view%E6%8D%A2%E8%82%A4)

