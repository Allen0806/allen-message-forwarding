# 项目名称
allen-message-forwarding

## 介绍
消息转发系统

## 项目说明

### 功能清单（核心功能点）
- 功能说明1
- 功能说明2

### 项目组成

- allen-message-forwarding-client：包含对外提供的feign调用的接口。
- allen-message-forwarding-server：web工程，包含核心的业务逻辑，包括dao、sevice、controller及定时任务处理包含对外提供的接口。

### 当前最新发布版本
1.0.0

### 环境依赖

|名称|版本|
| ---  | -- |
|Oracle JDK|1.8.x|
|maven|3.x.x|
|allen-parent|1.1.0|
|nacos|2.1.x|

### 网络依赖（需要开通的网络）

**1. 开发环境**

| 系统名称  | IP及端口号或域名      |
|-------|----------------|
| xxx系统 | 127.0.0.1:8080 |

**2. 测试环境**

| 系统名称  | IP及端口号或域名      |
|-------|----------------|
| xxx系统 | 127.0.0.1:8080 |

**3. 用户测试环境**

| 系统名称  | IP及端口号或域名      |
|-------|----------------|
| xxx系统 | 127.0.0.1:8080 |

**4. 生产环境**

| 系统名称  | IP及端口号或域名      |
|-------|----------------|
| xxx系统 | 127.0.0.1:8080 |

### 编译、打包及部署
- 生产环境nacos配置文件里的 swagger.enable 设置为false·
- 通过执行 mvn package 方式编译及打包
- _如果不同环境需要指定profile，需要描述_
- _目前默认是安装-Pprod，-Ptest, -Pdev, -Puat 方式打包，如果有不同，需要在文档里说明_
- _或者说要求明确编译打包命令。比如可能是 `mvn clean package assembly:single` ，有的可能是 `mvn clean package` ，有的可能是 `mvn clean package assembly:assembly`_

### k8s相关说明
- 运行时监听的端口：    **8080**
- 是否可以允许多副本运行（即是否支持集群部署）：**是**
- 监听的端口是否需要在集群外监听（如果需要集群外的服务访问，则需要，否则不需要）：**是**
- 如果是tomcat项目，是否需 项目本身创建目录（比如 cas 项目，就在 webapps 目录下会创建 cas 目录，并放置相关程序）：**内置tomcat，通过jar包运行**

### 运行命令
- 通过 java -jar 命令启动，启动时需要指定环境配置： -Dspring.profiles.active=`<env>`  或  --spring.profiles.active=`<env>`
- 环境变量env说明：

|环境| 变量值  |
|---|------|
|开发环境| dev  |
|测试环境| test |
|用户验收测试环境| uat  |
|生产环境| prod |

- 需要指定的其他参数

| 参数名称 | 参数值 |
|------|-----|
| 暂无   | 暂无  |

- 参考启动命令：

1. 开发、测试、用户验收测试

   nohup java -Xms512M -Xmx512M -Xmn128M -XX:MaxMetaspaceSize=128M -XX:MetaspaceSize=128M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:ErrorFile=./logs/hs_err_pid%p.log -Xloggc:./logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:HeapDumpPath=./logs -XX:+HeapDumpOnOutOfMemoryError -jar allen-demo-1.0.0-SNAPSHOT.jar -Dspring.profiles.active={环境变量} >/dev/null 2>&1 &

2. 生产

   nohup java -Xms1024M -Xmx1024M -Xmn256M -XX:MaxMetaspaceSize=256M -XX:MetaspaceSize=256M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:ErrorFile=./logs/hs_err_pid%p.log -Xloggc:./logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:HeapDumpPath=./logs -XX:+HeapDumpOnOutOfMemoryError -jar allen-demo-1.0.0.jar -Dspring.profiles.active=prod >/dev/null 2>&1 &

### 访问地址
1. 项目里的 context-path 统一为：/，不要加任何固定前缀
2. 在k8s集群内全部以固定方式访问，访问方式：仓库名.仓库组名:端口号（默认固定为：8080）
   - 本项目的集群内访问地址：allen-demo.allen-platform:8080
3. 本项目提供k8s集群外内网访问，访问地址如下表所示。k8s集群外内网访问规则如下（如要提供，需发邮件到运维申请）：
   - 开发环境：http://localhost:909X + /项目组名称/项目名称
   
4. 本项目提供公网访问地址（如果不提供外网访问地址，需要说明），外网访问地址组成规则：
   - 开发环境：https://dev.allen.com/backend + /服务名
   
   - https://xxx.allen.com/backend 为后端服务固定域名前缀，所有后端服务相同
   - 服务由各后端服务定义，名称要简洁，不允许重复
5. 本项目支持 feign client 方式调用，需要引用：allen-demo-client-x.x.x(版本号).jar，引用pom如下：

   `<dependency>`

   &nbsp;&nbsp;&nbsp;&nbsp;`<groupId>com.allen</groupId>`

   &nbsp;&nbsp;&nbsp;&nbsp;`<artifactId>allen-demo-client</artifactId>`

   &nbsp;&nbsp;&nbsp;&nbsp;`<version>1.0.0</version>`

   `</dependency>`
6. 本项目各环境访问地址：
   | 环境       | 公网访问地址 | k8s集群外内网访问地址       | k8s集群内访问地址                             |
   |---|------------------------------------|--------------------------------------|----------------------------------------|
   |开发环境| https://dev.allen.com/backend/demo | 需要确定 | http://allen-demo.allen-platform-dev:8080 |

### 接口文档
- 开发环境swagger访问地址：http://localhost:909X/allen-platform/allen-demo/swagger-ui/index.html
- 其他环境暂不提供swagger访问地址(如要提供特殊说明)
- 生产环境不开放，需要在配置文件里配置： swagger.enable=false
- 如果接口路径包含/inner/，则改接口不允许公网访问，只允许内网访问

### nacos说明
1. 开发、测试、用户验收测试统一使用一套nacos环境，通过namespace来区分不同环境。
    - 集群内地址：localhost:8848
    - 集群外地址：localhost:38848
2. 生产环境独立使用一套nacos
    - 集群内地址：
    - 集群外地址：
3. 各环境namespace及group配置信息

|环境|namespace|group|
|---|---|---|
|开发环境|allen-dev|allen-dev|
|测试环境|allen-test|allen-test|
|用户验收测试环境|allen-uat|allen-uat|
|生产环境|allen-prod|allen-prod|

### 数据库说明
1. 开发、测试、用户验收测试使用一套mysql数据库，通过库名区分不同环境，库名组成规则：环境变量+英文下划线+应用名（简写）
    - 数据库地址：mysql.allen-platform:3306
    - 用户名：demo
    - 密码：xxx
2. 生产环境独立一套mysql数据，库名组成规则：应用名（简写）
    - 数据库地址：mysql.allen-platform:3306
    - 用户名：demo
    - 密码：xxx
3. 本项目数据库信息

|环境|数据库名|
|---|---|
|开发环境|dev_demo|
|测试环境|test_demo|
|用户验收测试环境|uat_demo|
|生产环境|demo|


### redis说明
1. 本项目需要redis环境
2. 开发、测试、用户验收测试环境使用一套Redis环境，不同环境使用不同的db，默认：开发-0，测试-1，用户验收测试-2
3. 生产单独使用一套Redis环境，db默认为：0

| 环境 | 地址                       |密码| db  |
|---|--------------------------|---|-----|
|开发环境| redis.allen-platform:6379 |redis@123| 0   |


### RocketMQ说明
1. 本项目需要RocketMQ环境
2. 开发、测试、用户验收测试环境使用一套RocketMQ环境，通过在Topic、Group等命名前加对应环境变量前缀区分，命名示例：test-allen-save-demo-topic、test-allen-save-demo-consumer-group、test-allen-demo-group
3. 生产单独使用一套RocketMQ环境，Topic、Group等命名前不加环境变量前缀，命名示例：allen-save-demo-topic、allen-save-demo-consumer-group、allen-demo-group

| 环境 | 地址                      | 环境变量前缀 |
|---|-------------------------|--------|
|开发环境| rocketmq.localhost:9876 | dev    |


### XXL-Job说明
1. 本项目需要XXL-Job环境
2. 开发、测试、用户验收测试环境使用一套XXL-Job环境，通过在执行器命名前加对应环境变量前缀区分，命名示例：test-allen-demo-job-executor
3. 生产单独使用一套XXL-Job环境，执行器命名前不加环境变量前缀，命名示例：allen-demo-job-executor

| 环境 | 地址                                         | 端口号  | accessToken                                | 环境变量前缀  |
|---|--------------------------------------------|------|--------------------------------------------|---------|
|开发环境| http://localhost:8080/xxl-job-admin        | 8010 | 123456                                     | dev     |

### 其他说明
- （如果有其他说明需要补充，在此补充）

## 常见问题