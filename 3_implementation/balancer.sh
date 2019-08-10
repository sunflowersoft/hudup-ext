./env.sh

if [ "$1" == "noconsole" ]
then
	$JAVAW_CMD net.hudup.Balancer
else
	$JAVA_CMD net.hudup.Balancer
fi

