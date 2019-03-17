## version
- v1.0.0
>update dependency version and to jdk11
- v0.0.3
>支持gradient color
- v0.0.2
>修复圆角黑色问题
>重命名util
- v0.0.1
>增加javadoc注释

## deploy
```
mvn clean deploy -P release -Dmaven.test.skip=true -Duser.name=patterncat -Dgpg.passphrase=xxxx
```
