import fs from "fs";
import path from "path";
import {fileURLToPath} from 'url';
import Constants from "./constants/constants.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename)

function _initProject(projectName) {
  // 检查目录是否存在
  const exists = fs.existsSync(projectName)
  if (exists) {
    console.log(`project 创建失败! [${projectName}] 已经存在!`);
    return
  }

  initDir(projectName)
}



function initDir(projectName) {
  console.log(`初始化工程 ${projectName}...`);
  fs.mkdirSync(projectName)
  
  console.log(`创建 build 文件夹`);
  fs.mkdirSync(`./${projectName}/build`)

  console.log(`初始化 xsd 约束文件...`);
  const magicXsdContent = fs.readFileSync(`${__dirname}/../resources/${Constants.MAGIC_XSD_FILE_NAME}`)
  fs.appendFileSync(`./${projectName}/${Constants.MAGIC_XSD_FILE_NAME}`, "")
  fs.writeFileSync(`./${projectName}/${Constants.MAGIC_XSD_FILE_NAME}`, magicXsdContent)

  console.log(`初始化 template.xml`);
  const templateXMLContent = fs.readFileSync(`${__dirname}/../resources/${Constants.TEMPLATE_FILE_NAME}`)
  fs.appendFileSync(`./${projectName}/${Constants.TEMPLATE_FILE_NAME}`, "")
  fs.writeFileSync(`./${projectName}/${Constants.TEMPLATE_FILE_NAME}`, templateXMLContent)

  console.log(`创建 data.json`);
  fs.appendFileSync(`./${projectName}/${Constants.MOCK_DATA_FILE_NAME}`, "")
  fs.writeFileSync(`./${projectName}/${Constants.MOCK_DATA_FILE_NAME}`, JSON.stringify({message: 'hello Wukong', keyword: 'Wukong'}, null, '  '))

  console.log(`初始化 template.js`);
  const templateJSContent = fs.readFileSync(`${__dirname}/../resources/${Constants.TEMPLATE_JS_FILE_NAME}`)
  fs.appendFileSync(`./${projectName}/${Constants.TEMPLATE_JS_FILE_NAME}`, "")
  fs.writeFileSync(`./${projectName}/${Constants.TEMPLATE_JS_FILE_NAME}`, templateJSContent)

  console.log(`初始化 lib.wukong.ts`);
  const wukongTSContent = fs.readFileSync(`${__dirname}/../resources/${Constants.TS_LIB_WUKONG}`)
  fs.appendFileSync(`./${projectName}/${Constants.TS_LIB_WUKONG}`, "")
  fs.writeFileSync(`./${projectName}/${Constants.TS_LIB_WUKONG}`, wukongTSContent)

  console.log(`初始化完成`);
}

export default {
  initProject: _initProject
}
