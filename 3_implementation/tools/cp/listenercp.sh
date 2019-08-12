cd ../..
./env.sh

if [ "$1" == "console" ]
then
	$JAVA_CMD net.hudup.listener.ui.ListenerCP console
else
	$JAVA_CMD net.hudup.listener.ui.ListenerCP
fi

cd tools/cp

