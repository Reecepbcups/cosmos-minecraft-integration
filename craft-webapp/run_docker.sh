VERSION=0.0.1

docker build -t reecepbcups/feeshare:$VERSION .
docker run --detach --rm --name=feeshare -p 6001:6001 reecepbcups/feeshare:$VERSION