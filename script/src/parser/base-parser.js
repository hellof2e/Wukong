import Path from "path";
import { XmlCommentNode, XmlDocument, XmlTextNode } from "xmldoc";
import Constants from "../constants/constants.js";
import fs from 'fs'
import ActionFileParser from "./action-file-parser.js";

/**
 * 通用的layout属性
 */
const COMMON_LATOUT_ATTRS = [
  "display",
  "flex-direction",
  "justify-content",
  "align-content",
  "align-items",
  "align-self",
  "flex-wrap",
  "flex",
  "flex-shrink",
  "margin-left",
  "margin-top",
  "margin-right",
  "margin-bottom",
  "margin-start",
  "margin-end",
  "margin-horizontal",
  "margin-vertical",
  "padding-left",
  "padding-top",
  "padding-right",
  "padding-bottom",
  "padding-start",
  "padding-end",
  "padding-horizontal",
  "padding-vertical",
  "width",
  "height",
  "max-width",
  "max-height",
  "min-width",
  "min-height",
  "position",
  "top",
  "left",
  "right",
  "bottom",
  "text-align",
  "aspect-ratio",
  "line-height",
  "vertical-align",
  "clip-children"
];


/**
 * 通用的 style 属性
 */
const COMMON_STYLE_ATTRS = [
  "border-radius",
  "background",
  "border-color",
  "border-width",
  "border-style",
  "opacity"
]

/**
 * 通用的 active-style 属性
 */
const COMMON_ACTIVE_STYLE_ATTRS = [
  "active-background",
  "active-border-color",
  "active-opacity"
]

/**
 * 通用的组件属性
 */
const COMMON_COMPONENT_ATTRS = [
  "m-for",
  "m-if",
  "node-id"
]



export default class BaseParser {

  constructor(xmlElement, subDir) {
    this.xmlElement = xmlElement
    this.subDir = subDir
  }

  generateFilePath(filePath) {
    let path = filePath
    if (this.subDir && this.subDir.length > 0) {
      path = Path.join(this.subDir, filePath)
    } else {
      path = filePath
    }
    return path
  }

  ignoreXmlElement(xmlElement) {
    return Constants.ignoreXmlElement(xmlElement)
  }

  /**
   * 分析依赖的文件路径
   * @returns Array<string>
   */
  analysisDependence() {
    const actionFilePath = this.getXmlElementAttrValue('action')
    if (actionFilePath) {
      console.log(`analysis action path [${actionFilePath}]...`);
      return [this.generateFilePath(actionFilePath)]
    } else {
      return []
    }
  }


  /**
   * 标签名称
   * @returns string
   */
  typeName() {
    return ""
  }

  /**
   * 组件属性
   * @returns Array<string>
   */
  _componentAttrs() {
    return COMMON_COMPONENT_ATTRS
  }

  /**
   * layout 中的属性集
   * @returns Array<string>
   */
  layoutAttrs() {
    return COMMON_LATOUT_ATTRS;
  }

  /**
   * style 中的属性集
   * @returns Array<string>
   */
  styleAttrs() {
    return COMMON_STYLE_ATTRS;
  }

  /**
   * active-style 中的属性集
   * @returns Array<string>
   */
  activeStyleAttrs() {
    return COMMON_ACTIVE_STYLE_ATTRS;
  }

  /**
   * 扩展 layout 属性
   * @returns Array<string>
   */
  extendLayoutAttrs() {
    return [];
  }

  /**
   * 扩展 style 属性
   * @returns Array<string>
   */
  extendStyleAttrs() {
    return [];
  }

  /**
   * 扩展 active-style 属性
   * @returns Array<string>
   */
  extendActiveStyleAttrs() {
    return [];
  }

  /**
   * 扩展和组件名称平级的属性
   * @returns Array<string>
   */
  extendAttrs() {
    return []
  }

  /**
   * 在 action 中扩展事件属性
   * @returns Map<string, Object>
   */
  extendActionObject() {
    return new Map()
  }
  /**
   * 获取xmlElement中的属性配置信息，如果包含 rpx 会做尺寸转换
   * @param {*} key 属性key
   * @param {*} xmlElement XML 标签
   * @returns 
   */
  getXmlElementAttrValue(key, xmlElement = this.xmlElement) {
    return Constants.getXmlElementAttrValueWithRPX(key, xmlElement)
  }

  _parseLayout() {
    const layoutAttrs = this.layoutAttrs();
    const extendLayoutAttrs = this.extendLayoutAttrs();
    const attrs = [...layoutAttrs, ...extendLayoutAttrs];

    const obj = { }
    attrs.forEach((attr) => {
      const key = attr
      const value = this.getXmlElementAttrValue(key)
      if (value) {
        obj[key] = value
      }
    })
    return obj
  }

  _parseStyle() {
    const styleAttrs = this.styleAttrs();
    const extendStyleAttrs = this.extendStyleAttrs();
    const attrs = [...styleAttrs, ...extendStyleAttrs];

    const obj = { }
    attrs.forEach((attr) => {
      const key = attr
      const value = this.getXmlElementAttrValue(key)
      if (value) {
        obj[key] = value
      }
    })
    return obj
  }

  _parseActiveStyle() {
    const activeStyleAttrs = this.activeStyleAttrs();
    const extendActiveStyleAttrs = this.extendActiveStyleAttrs();
    const attrs = [...activeStyleAttrs, ...extendActiveStyleAttrs];

    const obj = { }
    attrs.forEach((attr) => {
      const key = attr
      const value = this.getXmlElementAttrValue(key)
      if (value) {
        let keyTemp = key;
        if (keyTemp.indexOf('active-') >= 0) {
          keyTemp = keyTemp.replace('active-', '');
        }
        obj[keyTemp] = value
      }
    })
    return obj
  }

  _parseExtendAttrs() {
    const componentAttrs = this._componentAttrs()
    const extendAttrs = this.extendAttrs();
    const attrs = [...componentAttrs, ...extendAttrs];
    const obj = { }
    attrs.forEach((attr) => {
      const key = attr
      const value = this.getXmlElementAttrValue(key)
      if (value) {
        obj[key] = value
      }
    })
    return obj
  }

  // 处理内联样式的点击事件配置
  _parseInnerClickActionAttrs() {
    const url = this.getXmlElementAttrValue('url')
    const busInfo = this.getXmlElementAttrValue('bus-info')
    
    // 处理点击事件以及埋点
    const click = { }
    if (url && url.length > 0) { // 有点击事件
      click['url'] = url
    }

    if (click.hasAttrs()) {
      const report = { }
     
      if (busInfo && busInfo.length > 0) {
        report['bus-info'] = busInfo
      }

      if (report.hasAttrs()) {
        click['report'] = report
      }
    }
    return click
  }

  // 处理内联样式的曝光事件配置
  _parseInnerExposeActionAttrs() {
    const busInfo = this.getXmlElementAttrValue('bus-info')

    const exopse = { }
    const report = { }
    
    if (busInfo && busInfo.length > 0) {
      report['bus-info'] = busInfo
    }

    if (report.hasAttrs()) {
      exopse['report'] = report
    }
    return exopse
  }

  _parseInnerActionAttrs() {
    const action = { }
    const click = this._parseInnerClickActionAttrs()
    if (click.hasAttrs()) {
      action['click'] = click
    }
    
    const expose = this._parseInnerExposeActionAttrs()
    if (expose.hasAttrs()) {
      action['expose'] = expose
    }
    return action
  }

  _parseActionFileAttrs() {
    const actionFilePath = this.getXmlElementAttrValue('action')
    if (actionFilePath) {
        const xmlContent = fs.readFileSync(this.generateFilePath(actionFilePath), { encoding: "utf-8" })
        const document = new XmlDocument(xmlContent)
        const actionFileParser = new ActionFileParser(document)
        return actionFileParser.parse()
    }
    return {} 
  }

  _parseAction() {
     // 优先解析 action 引入的文件
     let action = this._parseActionFileAttrs()
     if (!action.hasAttrs()) { // 解析内部配置
       action = this._parseInnerActionAttrs()
     }
 
     // 解析 click-event
     const clickEvent = this.getXmlElementAttrValue('click-event')
     if (clickEvent && clickEvent.length > 0) {
       action['click-event'] = clickEvent
     }
 
     // 扩展 action 
     const extendAction = this.extendActionObject()
     if (extendAction && extendAction.size > 0) {
       extendAction.forEach((value, key) => {
         action[key] = value
       })
     }
     return action
  }

  /**
   * @returns Object
   */
  parse() {
    const typeName = this.typeName();

    if(!typeName || typeName.length <= 0) {
      return null
    }

    const layout = this._parseLayout() // 解析layout属性
    const style = this._parseStyle() // 解析style属性
    const activeStyle = this._parseActiveStyle() // 解析 active-style属性
    const action = this._parseAction() // 解析 action

    let obj = { } 

    obj['type'] = typeName

    if (layout.hasAttrs()) {
      obj['layout'] = layout
    }
    if (style.hasAttrs()) {
      obj['style'] = style
    }
    if (activeStyle.hasAttrs()) {
      obj['active-style'] = activeStyle
    }
    if (action.hasAttrs()) {
      obj['action'] = action
    }

    let extendAttrs = this._parseExtendAttrs()
    if (extendAttrs.hasAttrs()) {
      const keys = Object.keys(extendAttrs)
      keys.forEach((key) => {
        obj[key] = extendAttrs[key]
      })
    }

    return obj;
  }
}