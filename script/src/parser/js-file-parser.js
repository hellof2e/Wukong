import fs from 'fs'
import jsmin from 'jsmin'

export default class JSFileParser  {

  constructor(jsFilePath) {
    this.jsFilePath = jsFilePath
  }

  parse() {
    let jsContent = fs.readFileSync(this.jsFilePath, { encoding: "utf-8" })

    let index = jsContent.indexOf('export default')
    
    // 删除开头的
    jsContent = jsContent.substring(index).replace(/^export default/, "")

    let content = `(function() {
        'use strict';
        var main = ${jsContent};
        return main;
      })()`
    return jsmin.jsmin(content)
  }
}