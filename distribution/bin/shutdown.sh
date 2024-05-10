#!/bin/bash

cd `dirname $0`/../target
target_dir=`pwd`

pid=`ps ax | grep -i 'coCloud.server' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "no coCloud server running."
        exit -1;
fi

echo "the coCloud server(${pid}) is running..."

kill ${pid}

echo "send shutdown request to coCloud server(${pid}) OK"
