#!/bin/bash

pid=`ps aux | grep bankrobot | awk '{print $2}'`
kill -9 $pid
