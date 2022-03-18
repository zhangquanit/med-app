#!/usr/bin/python  
#-*-coding:utf-8-*-

#keystore信息
#Windows 下路径分割线请注意使用\\转义
keystorePath = "medlinker.keystore"
keyAlias = "medlinker"
keystorePassword = "medlinker"
keyPassword = "medlinker"

#加固后的源文件名（未重签名）
protectedSourceApkName = ""
#加固后的源文件所在文件夹路径(...path),注意结尾不要带分隔符，默认在此文件夹根目录
protectedSourceApkDirPath = "apk-jiagu"
#渠道包输出路径，默认在此文件夹channels目录下
channelsOutputFilePath = "channels"
#渠道名配置文件路径，默认在此文件夹根目录
channelFilePath = ""
#额外信息配置文件（绝对路径，例如/Users/mac/Desktop/walle360/config.json）
#配置信息示例参看https://github.com/Meituan-Dianping/walle/blob/master/app/config.json
extraChannelFilePath = ""
#Android SDK buidtools path , please use above 25.0+
# sdkBuildToolPath = "/Users/macbook/Library/Android/sdk/build-tools/26.0.2"
# sdkBuildToolPath = "/Users/macbook/workspace/android-sdk/build-tools/26.0.2"
sdkBuildToolPath = "lib"
