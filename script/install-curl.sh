#!/bin/bash

rm -rf ~/.wukong
git clone https://github.com/hellof2e/Wukong.git ~/.wukong
cd ~/.wukong/script
# 安装
sudo npm install
sudo npm link
