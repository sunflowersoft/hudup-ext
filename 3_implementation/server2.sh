. env.sh

if [ "$1" == "service" ]
then
	eval $JAVAW_CMD net.hudup.Server2 $1 $2 $3 $4 &
else
	eval $JAVA_CMD net.hudup.Server2 $1 $2 $3 $4
fi

