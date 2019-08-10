./env.sh

if [ "$1" == "noconsole" ]
then
	$JAVAW_CMD net.hudup.Listener
else
	$JAVA_CMD net.hudup.Listener
fi