#!/usr/bin/python  
#-*-coding:utf-8-*-

# /**
#  * ================================================
#  * 作    者：JayGoo
#  * 版    本：1.0.1
#  * 更新日期：2017/12/29
#  * 邮    箱: 1015121748@qq.com
#  * ================================================
#  */

import os
import sys
import makeChannels.config
import platform
import shutil

#获取脚本文件的当前路径
def curFileDir():
     #获取脚本路径
     path = sys.path[0]
     #判断为脚本文件还是py2exe编译后的文件，
     #如果是脚本文件，则返回的是脚本的目录，
     #如果是编译后的文件，则返回的是编译后的文件路径
     if os.path.isdir(path):
         return path
     elif os.path.isfile(path):
         return os.path.dirname(path)

#判断当前系统
def isWindows():
  sysstr = platform.system()
  if("Windows" in sysstr):
    return 1
  else:
    return 0

#兼容不同系统的路径分隔符
def getBackslash():
	if(isWindows() == 1):
		return "\\"
	else:
		return "/"


# 清空临时资源
def cleanTempResource():
  try:
    # os.remove(zipalignedApkPath)
    # os.remove(signedApkPath)
    if os.path.exists(tempDir):
       shutil.rmtree(tempDir)
    pass
  except Exception:
    pass
 
 # 清空渠道信息
def cleanChannelsFiles():
  try:
    if os.path.exists(channelsOutputFilePath):
       shutil.rmtree(channelsOutputFilePath)
    os.makedirs(channelsOutputFilePath)
    pass
  except Exception:
    pass

# 创建Channels输出文件夹
def createChannelsDir():
  try:
    os.makedirs(channelsOutputFilePath)
    pass
  except Exception:
    pass

def createApktoolDir():
  try:
    if os.path.exists(tempDir):
       shutil.rmtree(tempDir)
    os.makedirs(tempDir)
    pass
  except Exception:
    pass

def createArmFiles(apkPath,arm):
  try:
    zipalignedApkPath = apkPath[0 : -4] + "_aligned.apk"
    signedApkPath = zipalignedApkPath[0 : -4] +"_signed.apk"
    #对齐
    zipalignShell = buildToolsPath + "zipalign -v 4 " + apkPath + " " + zipalignedApkPath
    os.system(zipalignShell)

    #签名
    signShell = buildToolsPath + "apksigner sign --ks "+ keystorePath + " --ks-key-alias " + keyAlias + " --ks-pass pass:" + keystorePassword + " --key-pass pass:" + keyPassword + " --out " + signedApkPath + " " + zipalignedApkPath
    os.system(signShell)
    print(signShell)

    #检查V2签名是否正确
    checkV2Shell = "java -jar " + checkAndroidV2SignaturePath + " " + signedApkPath
    os.system(checkV2Shell)

    outDir=channelsOutputFilePath+getBackslash()+arm
    os.makedirs(outDir)
    #写入渠道
    if len(makeChannels.config.extraChannelFilePath) > 0:
      writeChannelShell = "java -jar " + walleChannelWritterPath + " batch2 -f " + makeChannels.config.extraChannelFilePath + " " + signedApkPath + " " + outDir
    else:
      writeChannelShell = "java -jar " + walleChannelWritterPath + " batch -f " + channelFilePath + " " + signedApkPath + " " + outDir

    os.system(writeChannelShell)
    pass
  except Exception:
    pass


    
#当前脚本文件所在目录
parentPath = curFileDir() + getBackslash()
print("parentPath="+parentPath)

#config
libPath = parentPath + "makeChannels/lib/"
# buildToolsPath =  makeChannels.config.sdkBuildToolPath + getBackslash()
buildToolsPath = libPath
checkAndroidV2SignaturePath = libPath + "CheckAndroidV2Signature.jar"
walleChannelWritterPath = libPath + "walle-cli-all.jar"
apktoolPath = libPath + "apktool.jar"
keystorePath = makeChannels.config.keystorePath
keyAlias = makeChannels.config.keyAlias
keystorePassword = makeChannels.config.keystorePassword
keyPassword = makeChannels.config.keyPassword
channelsOutputFilePath = parentPath + "makeChannels/"+makeChannels.config.channelsOutputFilePath
channelFilePath = parentPath + "makeChannels/channel"
tempDir = parentPath + "makeChannels/apktool"
# protectedSourceApkPath = parentPath + config.protectedSourceApkName
makeChannels.config.protectedSourceApkDirPath="x_scripts/makeChannels/"+makeChannels.config.protectedSourceApkDirPath

# 检查自定义路径，并作替换
if len(makeChannels.config.protectedSourceApkDirPath) > 0:
  for fpath, dirname, fnames in os.walk(makeChannels.config.protectedSourceApkDirPath):
    print(fnames)
    break

  for item in fnames:
    if item.endswith(".apk"): #过滤apk文件
      makeChannels.config.protectedSourceApkName = fnames[0]
      break

  protectedSourceApkPath = makeChannels.config.protectedSourceApkDirPath + getBackslash() + makeChannels.config.protectedSourceApkName

# if len(makeChannels.config.channelsOutputFilePath) > 0:
#   channelsOutputFilePath = makeChannels.config.channelsOutputFilePath

if len(makeChannels.config.channelFilePath) > 0:
  channelFilePath = makeChannels.config.channelFilePath


#清空Channels输出文件夹
cleanChannelsFiles()
createApktoolDir()


#apktool解压 分别打包 'arm64-v8a','armeabi-v7a'
apkName=makeChannels.config.protectedSourceApkName
tempApk=tempDir+getBackslash()+apkName
unzipDir=tempDir+getBackslash()+"app"

shutil.copy(protectedSourceApkPath,tempApk)
print("复制apk成功 dir="+tempDir)
unzipShell="java -jar "+apktoolPath+" d -f "+tempApk+" -o "+unzipDir
print("apktool开始解压："+unzipShell)
os.system(unzipShell)
v7aSourceDir=unzipDir+getBackslash()+"lib"+getBackslash()+"armeabi-v7a"
v7aTargetDir=tempDir+getBackslash()+"armeabi-v7a"
v7aOutput=tempDir+getBackslash()+apkName[0 : -4]+"-v7a.apk"
v7aExist=False
if os.path.exists(v7aSourceDir):
  v7aExist=True
  shutil.copytree(v7aSourceDir,v7aTargetDir)
  shutil.rmtree(v7aSourceDir)

v8aSourceDir=unzipDir+getBackslash()+"lib"+getBackslash()+"arm64-v8a"
v8aTargetDir=tempDir+getBackslash()+"arm64-v8a"
v8aOutput=tempDir+getBackslash()+apkName[0 : -4]+"-v8a.apk"
v8aExist=False
if os.path.exists(v8aSourceDir):
  v8aExist=True
  print("############ apktool压缩arm64-v8a包 #############")
  zipShell="java -jar "+apktoolPath+" b "+unzipDir+" -o "+v8aOutput
  os.system(zipShell)
  print("压缩成功 开始zipalign->签名->生成多渠道包")
  shutil.rmtree(v8aSourceDir)
  createArmFiles(v8aOutput,"arm64-v8a")

if v7aExist:
  print("############ apktool压缩armeabi-v7a包 #############")
  shutil.copytree(v7aTargetDir,v7aSourceDir)
  zipShell="java -jar "+apktoolPath+" b "+unzipDir+" -o "+v7aOutput
  os.system(zipShell)
  print("压缩成功 开始zipalign->签名->生成多渠道包")
  createArmFiles(v7aOutput,"armeabi-v7a")

cleanTempResource()

print ("\n**** =============================多渠道打包完成=================================== ****\n")
print ("\n↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓    请在channels目录下查看   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓\n")
print ("\n"+channelsOutputFilePath+"\n")
print ("\n**** =============================多渠道打包结束=================================== ****\n")


