#!/bin/bash

rm -rf ~/.wukong

cp -r ./ ~/.wukong

cd ~/.wukong


# 安装
sudo npm install

sudo npm link