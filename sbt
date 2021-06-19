#!/bin/sh
DIR=$(dirname -- "$(readlink -f "$0")")

mkdir -p \
	${DIR}/target \
	${DIR}/project/project \
	${DIR}/project/target

exec podman \
	run --rm -it \
	-p 9000:9000 \
	-v java-cache:/home/sbt/.cache \
	-v java-cache:/home/sbt/.ivy2 \
	-v java-cache:/home/sbt/.sbt \
	-v ${DIR}:/home/sbt/app \
	--tmpfs /home/sbt/app/.git \
	-v java-target:/home/sbt/app/target \
	-v java-target:/home/sbt/app/project/project \
	-v java-target:/home/sbt/app/project/target \
	--uidmap=101:0:1 \
	-e GOOGLE_ID -e GOOGLE_SECRET \
	-e GITHUB_ID -e GITHUB_SECRET \
	j000/ebiznes \
	sbt $*
