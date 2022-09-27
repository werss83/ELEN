#!/bin/bash

set -e

tag="${1:-latest}"

echo "Building project with the tag $tag"
echo "================"
sbt stage --warn
echo "Build docker image [dontpanic57/elen:$tag]"
echo "================"
docker build -t dontpanic57/elen:$tag .
echo "Tag the image [dontpanic57/elen:$tag] as [registry.jackson42.com/dontpanic57/elen:$tag]"
echo "================"
docker tag dontpanic57/elen:$tag registry.jackson42.com/dontpanic57/elen:$tag
echo "Push the image [registry.jackson42.com/dontpanic57/elen:$tag]"
echo "================"
docker push registry.jackson42.com/dontpanic57/elen:$tag
echo "Clean the images [dontpanic57/elen:$tag] and [registry.jackson42.com/dontpanic57/elen:$tag]"
echo "================"
docker image remove registry.jackson42.com/dontpanic57/elen:$tag
docker image remove dontpanic57/elen:$tag
