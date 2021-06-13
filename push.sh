#!/bin/sh

set -e # exit on error

IMAGE=${1:-docker.io/j000/ebiznes-sklep}
DIR=$(dirname $0)

cd ${DIR}

./build.sh ${IMAGE}
docker push ${IMAGE}:back-end
docker push ${IMAGE}:front-end
