## tag

### beta01
- 这个版本是根据zxing里头的MatrixToImageWriter.java的二维码生成方式改造(`最原始的代码`)
- 这个版本不支持图片底色的二维码

### rc1
- 这个版本抛弃了zxing里头的MatrixToImageWriter.java的思路,自己去根据multiple去重新绘制伸缩后的二维码(`重新改了部分结构,改动稍微大些`)
- 新增了支持使用图片底色的二维码

### rc2
- 修复spring controller自动加载问题
- 完善springboot-starter-qrcode
