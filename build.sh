#!/bin/bash
time=$(date +%y%m%d_%H%M)
fatJarName=chiaAutoTransfer-1.0-jar-with-dependencies.jar
finalJarName=chiaAutoTransfer.jar
finalReleaseZipName=chiaAutoTransfer-$time.zip

#https://mirrors.tuna.tsinghua.edu.cn/Adoptium/8/jre/x64/windows/OpenJDK8U-jre_x64_windows_hotspot_8u332b09.zip

function buildProject() {
  mvn clean package
  rm -rf release/$finalJarName release/*.zip
  mv target/$fatJarName release/$finalJarName
  cd release && zip -r -q -o $finalReleaseZipName ./* -x "*.DS_Store" -x "__MACOSX" -x ".svn"
}

buildProject
