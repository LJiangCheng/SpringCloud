#!/bin/bash
#jenkins执行脚本
#拉取最新代码
git checkout ${branch}
git pull
mvn clean install -Dmaven.test.skip=true
port=8080
#根据不同模块进入不同的工作目录
#
if [ "${module}" = "organization" ]; then
	cd ./sysadmin/organization/target
    port=8010
#
elif [ "${module}" = "gateway-web" ];  then
	cd ./gateway/gateway-web/target
    port=8443
#
elif [ "${module}" = "gateway-admin" ];  then
	cd ./gateway/gateway-admin/target
    port=8445
#
elif [ "${module}" = "authentication-server" ];  then
	cd ./auth/authentication-server/target
    port=8001
#
elif [ "${module}" = "authorization-server" ];  then
	cd ./auth/authorization-server/target
    port=8000
fi
#copy Dockerfile到当前目录
cp ../src/main/docker/Dockerfile ./Dockerfile
#清理docker 查询containerId，然后使用$(a)判断是否为空
#判断是否存在运行中的容器
containerId=$(docker ps -aq -f name=${module});
runningId=$(docker ps -q -f name=${module});
if [ "${containerId}" != "" ]; then
	#判断容器是否运行
	if [ "${runningId}" != "" ]; then
		docker stop ${module}
    fi
	docker rm ${module}
fi
#判断镜像是否存在
imageId=$(docker images -q -f name=${module});
if [ "${imageId}" != "" ]; then
	docker rmi ${module}
fi
#构建并运行
docker build -t="${module}" .
docker run -d -p $port:$port --name="${module}" ${module}