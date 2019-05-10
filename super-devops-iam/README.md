# IAM使用文档：
---
一、五分钟入门教程
> * 客户端引入
    >> 1.1.1、连接IAM认证中心(默认)
    >> 1.1.2、连接外部认证中心(适合已有认证中心系统)

> * 服务端引入
    >> 1.2.1、独立模式(适合系统搭建)
    >> 1.2.2、依赖模式(适合兼容已有认证中心系统)

---
二、原理流程
> * 2.1、客服端/服务端认证交互流程时序图

```seq
title: 客服端/服务端认证交互SocketIO层时序图
User/Brower->WebServerA/IamClient: 1.http://serverA/index

User/Brower->IamServer: 2.https://iam/iam-server/validate?grantTicket=val&application=val

IamServer-->WebServerA/IamClient: 3.Response(No session)

WebServerA/IamClient-->User/Brower: 4.Redirect(https://iam/login?application=val&reponse_type=link&redirect_url=http://serverA/index)

User/Brower->IamServer: 5. https://iam/login?application=val&reponse_type=link&redirect_url=http://serverA/index

IamServer-->User/Brower: 6. Response(https://iam/login)

User/Brower->IamServer: 7. https://iam/login?username=val&passwd=val

IamServer-->User/Brower: 8. Redirect(http://serverA/index?grantTicket=val)

User/Brower->WebServerA/IamClient: 9. http://serverA/index?grantTicket=val

WebServerA/IamClient->IamServer: 10. https://iam/iam-server/validate?grantTicket=val&application=val

IamServer-->WebServerA/IamClient: 11.Response(grantTicket/roles/permits/validUntilDate/validFromDate)

WebServerA/IamClient-->User/Brower: 12. Response(http://serverA/index)
```
参见：IAM认证时序图.jpg
> * 2.2、


---
三、二次开发
> * 3.1、客户端二次开发
> * 3.2、服务端二次开发

###### [Reference](https://www.zybuluo.com/mdeditor)