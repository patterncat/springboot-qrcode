## tag

### v0.0.1
- 增加javadoc注释

### rc2
- 修复spring controller自动加载问题
- 完善springboot-starter-qrcode
- 修复jpg下二维码变黑问题,这是ImageIO的bug,[Colors appear wrong in image file if image type is TYPE_INT_ARGB and writing with ImageIO](https://stackoverflow.com/questions/23734280/colors-appear-wrong-in-image-file-if-image-type-is-type-int-argb-and-writing-wit).因而推荐png,效果好点

### rc1
- 这个版本抛弃了zxing里头的MatrixToImageWriter.java的思路,自己去根据multiple去重新绘制伸缩后的二维码(`重新改了部分结构,改动稍微大些`)
- 新增了支持使用图片底色的二维码

### beta01
- 这个版本是根据zxing里头的MatrixToImageWriter.java的二维码生成方式改造(`最原始的代码`)
- 这个版本不支持图片底色的二维码
