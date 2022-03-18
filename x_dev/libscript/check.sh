#!/usr/bin/env sh

dir=$1
cmd=$2
cd $dir
git remote -v
git fetch
git remote prune origin
git pull

###################### 检测创建tag
if [ "$cmd" == "tag" ]; then
   tagName=$3
   echo "创建tag ->$tagName"
   git tag -d $tagName && git push origin :refs/tags/$tagName
   git tag -a $tagName -m "${tagName}版本发布" && git push origin $tagName
   exit
fi

###################### 检测切换分支
branchName=$3
currentBranch=`sh -c 'git branch --no-color 2> /dev/null' | sed -e '/^[^*]/d' -e 's/* \(.*\)/\1/' -e 's/\//\_/g'`
echo "当前分支：$currentBranch"

#检查远程分支
if git rev-parse --verify "origin/${branchName}";then
     echo ">远程仓库存在${branchName}分支"
     if git rev-parse --verify "${branchName}";then
         echo ">>本地存在${branchName}分支，当前分支名为：$currentBranch"
         if [ "$currentBranch" == "${branchName}" ]; then
            echo ">>>当前分支为目标分支，比较是否落后于master";
            ./../libscript/checkIfBehind.sh
         else
            echo ">>>切换到${branchName}"
            git reset --hard && git checkout ${branchName}
          fi
     else
         echo ">>本地不存在${branchName}分支，从master检出本地分支"
         git checkout -b "${branchName}" master
     fi
else
     echo ">远程仓库不存在${branchName}分支 创建分支并提交到远程仓库"
     git reset --hard && git checkout master;
     git branch -D ${branchName};
     git pull;
     git checkout -b "${branchName}" master && git push origin "${branchName}";
fi
