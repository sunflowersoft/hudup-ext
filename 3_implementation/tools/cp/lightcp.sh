cd ../..
./env.sh

if [ "$1" == "console" ]
then
	$JAVA_CMD net.hudup.core.client.LightRemoteServerCP console
else
	$JAVA_CMD net.hudup.core.client.LightRemoteServerCP
fi

cd tools/cp
