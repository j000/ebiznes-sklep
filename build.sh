#!/bin/sh

set -e # exit on error

IMAGE=${1:-ebiznes-sklep}
DIR=$(dirname $0)

cd ${DIR}

docker build . -f Dockerfile.back-end -t ${IMAGE}:back-end
docker build . -f Dockerfile.front-end -t ${IMAGE}:front-end
