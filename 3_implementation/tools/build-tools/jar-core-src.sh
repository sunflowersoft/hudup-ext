cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib-src=$1 jar-core-src
else
	./build.sh jar-core-src
fi
cd tools/build-tools
