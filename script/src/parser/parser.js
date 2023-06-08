import Constants from '../constants/constants.js';
import fs from 'fs'
import path from "path";
import { XmlDocument } from "xmldoc";
import DocumentParser from './document-parser.js';

import {fileURLToPath} from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename)


Object.prototype.hasAttrs = function() {
  return this && Object.keys(this) && Object.keys(this).length > 0
}


/**
 * 解析逻辑
 * @param {string} subDir 子目录名称
 */
function _parse(subDir, compress = false) {
  let fileName = `${Constants.TEMPLATE_FILE_NAME}`
  if (subDir && subDir.length > 0) {
    fileName = `./${subDir}/${Constants.TEMPLATE_FILE_NAME}`
  }
  const xmlContent = fs.readFileSync(fileName, { encoding: "utf-8" })
  const document = new XmlDocument(xmlContent)
  _handleDocument(document, subDir, compress);
}

function _handleDocument(document, subDir, compress = false) {
  console.log('开始解析');
  const documentParser = new DocumentParser(document, subDir);
  const styleModel = documentParser.parse()
  
  console.log(JSON.stringify(styleModel, null, compress ? '' : '  '));

  console.log('同步 xsd 约束');
  const magicXsdContent = fs.readFileSync(`${__dirname}/../../resources/${Constants.MAGIC_XSD_FILE_NAME}`)
  let xsdFilePath = `./${Constants.MAGIC_XSD_FILE_NAME}`
  if (subDir && subDir.length > 0) {
    xsdFilePath = `./${subDir}/${Constants.MAGIC_XSD_FILE_NAME}`
  }
  fs.appendFileSync(xsdFilePath, "")
  fs.writeFileSync(xsdFilePath, magicXsdContent)

  console.log('同步 ts 文件');
  const tsFileContent = fs.readFileSync(`${__dirname}/../../resources/${Constants.TS_LIB_WUKONG}`)
  let tsFilePath = `./${Constants.TS_LIB_WUKONG}`
  if (subDir && subDir.length > 0) {
    tsFilePath = `./${subDir}/${Constants.TS_LIB_WUKONG}`
  }
  fs.appendFileSync(tsFilePath, "")
  fs.writeFileSync(tsFilePath, tsFileContent)

  console.log('检查build目录');
  const exists = fs.existsSync('build')
  if (!exists) { // 如果不存在则先创建 build 文件夹
    fs.mkdirSync('build')
  }

  const buildDataFilePath = `./build/${Constants.MOCK_DATA_FILE_NAME}`
  const buildStyleFilePath = `./build/${Constants.STYLE_FILE_NAME}`
  
  console.log('解析产物');
  // build 文件夹中创建 style.json 文件，同时把json数据写入进去
  fs.appendFileSync(buildStyleFilePath, "")
  fs.writeFileSync(buildStyleFilePath, JSON.stringify(styleModel, null, compress ? '' : '  '))

  console.log('获取 mock 数据');
  // 把 data.json 的数据 写入到 build 文件夹下
  let dataFilePath = `./${Constants.MOCK_DATA_FILE_NAME}`
  if (subDir && subDir.length > 0) {
    dataFilePath = `./${subDir}/${Constants.MOCK_DATA_FILE_NAME}`
  }
  const modkDataContent = fs.readFileSync(dataFilePath)
  fs.appendFileSync(buildDataFilePath, "")
  fs.writeFileSync(buildDataFilePath, modkDataContent)

  console.log('解析成功！');
}

export default {
  parse: _parse
}
