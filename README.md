> 免责声明：本工具仅供安全研究和教学目的使用，用户须自行承担因使用该工具而引起的一切法律及相关责任。作者概不对任何法律责任承担责任，且保留随时中止、修改或终止本工具的权利。使用者应当遵循当地法律法规，并理解并同意本声明的所有内容。

# BypassAV-Online

一个基于 Spring Boot 的在线免杀生成平台，还在初期，功能待完善。


## 演示
![image-20231230225022661](README.assets/0.png)

# 快速体验

```
wget https://github.com/yutianqaq/BypassAV-Online/releases/download/%E5%88%9D%E5%A7%8B%E7%89%88%E6%9C%AC/bypassAVOnline.zip

unzip bypassAVOnline.zip

cd bypassAVOnline

curl https://nim-lang.org/choosenim/init.sh -sSf | sh
# 输入 y
echo "PATH=/home/kali/.nimble/bin:$PATH" >> ~/.zshrc
source ~/.zshrc

# winim 依赖
nimble install winim

sudo apt install mingw-w64

java -jar bypass-0.0.1-SNAPSHOT.jar
```

测试

```
curl -X POST -H "Content-Type: application/json" -d '{"code":"0x90", "templateName":"v1"}' http://localhost:8080/v1/compileC

{"code":"200","msg":"操作成功","data":{"downloadLink":"/download/c7af340b-8358-44a3-9985-82189468c36d.exe"}}                                                                                                                                                                                                                                            
┌──(kali㉿kali)-[~/bypassAVOnline]
└─$ wget http://localhost:8080/v1/download/c7af340b-8358-44a3-9985-82189468c36d.exe
--2023-12-30 02:16:07--  http://localhost:8080/v1/download/c7af340b-8358-44a3-9985-82189468c36d.exe
Resolving localhost (localhost)... ::1, 127.0.0.1
Connecting to localhost (localhost)|::1|:8080... connected.
HTTP request sent, awaiting response... 200 
Length: 115534 (113K) [application/octet-stream]
Saving to: ‘c7af340b-8358-44a3-9985-82189468c36d.exe’
```

![image-20231230151703071](README.assets/image-20231230151703071.png)

# 搭建文档

springboot 后端、vue 前端。

## 部署

### 后端

使用 idea 打开，点击 右侧 maven，点击 package

![image-20231230143724413](README.assets/image-20231230143724413.png)



然后在 target 目录会生成一份 `bypass-0.0.1-SNAPSHOT.jar`。

输入下面的命令

```
cd /home/kali
mkdir bypassAVOnline
cd bypassAVOnline
mkdir template
mkdir download
```

新建一个名为 application.yaml 的文件，输入以下内容

```
bypassav:
  templatesDirectory: /home/kali/bypassAVOnline/template/
  templateCMapping:
    v1: v1.c
  templateNIMMapping:
    v1: v1.nim
  compilerC: x86_64-w64-mingw32-gcc
  compilerNIM: nim
  storageDirector: /home/kali/bypassAVOnline/download

```

此时后端工作目录结构如下：

```
┌──(kali㉿kali)-[~/bypassAVOnline]
└─$ tree                                                                                
.
├── application.yaml
├── bypass-0.0.1-SNAPSHOT.jar
├── download
└── template
    ├── v1.c
    └── v1.nim

3 directories, 4 files

```



下载编译器

输入下面的命令

```
curl https://nim-lang.org/choosenim/init.sh -sSf | sh
# 输入 y
export PATH=/home/kali/.nimble/bin:$PATH

sudo apt install mingw-w64
```

此状态为安装成功。

![image-20231230144652397](README.assets/image-20231230144652397.png)



启动后端，成功状态如下

```
curl -X POST -H "Content-Type: application/json" -d '{"code":"0x90", "templateName":"v1"}' http://localhost:8080/v1/compileC

wget http://localhost:8080/v1/download/c7af340b-8358-44a3-9985-82189468c36d.exe
```



![image-20231230145353378](README.assets/image-20231230145353378.png)



## 前端页面部署

修改 `.env.production` 中的内容

```
# 线上环境
NODE_ENV = 'production'

# 暴露必须以VITE开头才能被Vite识别

VITE_BASE_API = '/api'

# 线上环境接口地址
VITE_API_URL = 'http://ip:port/'

```

![image-20231230211045449](README.assets/image-20231230211045449.png)

然后输入 `yarn build`，输入以下命令，复制到 Apache 目录

```
sudo cp -r dist/assets dist/index.html dist/logo.ico /var/www/html/
sudo chown -R www-data:www-data /var/www/html
```

输入以下命令，开启模块

```
sudo a2enmod proxy
sudo a2enmod proxy_http
sudo a2enmod rewrite
```

编辑 `/etc/apache2/sites-available/000-default.conf` 增加以下配置

```
        ProxyPass /api http://localhost:8080
        ProxyPassReverse /api http://localhost:8080
        <Directory /var/www/html>
                Options Indexes FollowSymLinks
                AllowOverride All
                Require all granted
        </Directory>

```

![image-20231230213427998](README.assets/image-20231230213427998.png)

重启 Apache 服务器

```
sudo systemctl restart apache2
```

至此完成后端，前端配置



![demo](README.assets/demo2.gif)




# TODO
- [ ] 优化前端交互
- [ ] 增加更多模板
- [ ] 增加免杀性



# 参考

Vue 基础模板：https://github.com/wsheeny/vite-vue-template

## 开发日志

[免杀-基于 Spring Boot 在线免杀平台开发记录 · yutian's blog](https://yutianqaq.github.io/2023/12/28/免杀-基于-Spring-Boot-在线免杀平台开发记录/)

