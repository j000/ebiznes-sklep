#!/bin/sh
DIR=$(dirname -- "$(readlink -f "$0")")

mkdir -p ${DIR}/.cache
mkdir -p ${DIR}/.ivy2
mkdir -p ${DIR}/.sbt

exec podman \
	run --rm -it \
	-p 9000:9000 \
	-v ${DIR}/.cache:/home/app/.cache \
	-v ${DIR}/.ivy2:/home/app/.ivy2 \
	--tmpfs /home/app/.cache/JNA \
	--tmpfs /home/app/.bsp \
	-v ${DIR}:/home/app/project \
	--tmpfs /home/app/project/target/global-logging \
	--tmpfs /home/app/project/target/scala-2.13/update/update_cache_2.13 \
	--tmpfs /home/app/project/target/scala-2.13/routes \
	--tmpfs /home/app/project/target/streams \
	--tmpfs /home/app/project/target/task-temp-directory \
	--tmpfs /home/app/project/target/web \
	--tmpfs /home/app/project/project \
	--uidmap=101:0:1 \
	j000/ebiznes \
	sbt $*
