#!/usr/bin/env node
// 第一行一定要添加脚本来指定运行环境（#!/usr/bin/env node）
import { Option, program } from 'commander';
import Parser from './src/parser/parser.js'
import Project from './src/init-project.js';
import Server from './src/server/server.js'


program
  .name('Wukong')
  .description('悟空局部动态卡片工程化管理工具。')
  .version('0.0.1');

program.command('init')
  .description('在当前文件夹下初始化一个工程，也可以在一个工程中初始化子工程。')
  .argument('<project>', '工程名称，必填参数')
  .action((str, options) => {
    const projectName = str
    Project.initProject(projectName)
    process.chdir(projectName)
    Parser.parse()
  });

program.command('parse')
  .description('解析工程并生成对应的产物到build目录中')
  .argument('[subDir]', '子目录名称', undefined)
  .action((str, options) => {
    const subDir = str
    Parser.parse(subDir, false)
  });

program.command('run')
  .description('在工程中启动实时预览和log日志的 server，默认端口：7788，可以通过扩展参数 [port] 修改端口号')
  .argument('[port]', '设置端口号', 7788)
  .action((str, options) => {
    const port = str
    Server.runServer(port)
  })

program.parse();