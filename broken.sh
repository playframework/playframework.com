#!/usr/bin/env bash


for page in `cat 404.txt`
do
    status=`curl -L -s -k -o /dev/null -w %{http_code} ${page}`
    if [ "$status" != "200" ]; then
        echo "[Failed][$status]: $page"
    fi
done
