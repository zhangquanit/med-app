#!/usr/bin/env sh

echo "dir=$1"
echo "appId=$2"
echo "appKey=$3"
echo "packageName=$4"
echo "version=$5"
echo "uploadFilePath=$6"

cd $1&&java -jar buglyqq-upload-symbol.jar -appid $2 -appkey $3 -bundleid $4 -version $5 -platform Android -inputMapping $6

echo "############# 符号表上传成功"