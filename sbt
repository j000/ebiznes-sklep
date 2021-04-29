#!/bin/sh
DIR=$(dirname -- "$(readlink -f "$0")")

mkdir -p .cache
mkdir -p .ivy2
mkdir -p .sbt

exec podman \
	run --rm -it \
	-p 9000:9000 \
	-v ${DIR}/.cache:/home/app/.cache \
	-v ${DIR}/.ivy2:/home/app/.ivy2 \
	-v ${DIR}/.sbt:/home/app/.sbt \
	-v ${DIR}:/home/app/project \
	--uidmap=101:0:1 \
	j000/ebiznes \
	sbt $*

# exec podman \
# 	run --rm -it \
# 	-p 9000:9000  \
# 	-v ${DIR}/.cache:/root/.cache \
# 	-v ${DIR}/.ivy2:/root/.ivy2 \
# 	-v ${DIR}/.sbt:/root/.sbt \
# 	-v ${DIR}:/app \
# 	-w /app \
# 	--userns=keep-id \
# 	mozilla/sbt:8u232_1.4.5 \
# 	sbt $*
