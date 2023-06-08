import { XmlCommentNode, XmlTextNode } from "xmldoc"

export default class Constants {
  static TEMPLATE_FILE_NAME = "template.xml"
  static MOCK_DATA_FILE_NAME = "data.json"
  static STYLE_FILE_NAME = "style.json"
  
  static MAGIC_XSD_FILE_NAME = "magic.xsd"
  static TEMPLATE_JS_FILE_NAME = "template.js"
  static TS_LIB_WUKONG = "lib.wukong.ts"

  static CONFIG_FILE_NAME = "config.magic"

  static DEFAULT_WIDTH_DIMEN = 750
  static WIDTH_DIMEN = 750


  static _ELE_container_ = "container"
  static _ELE_img_ = "img"
  static _ELE_text_ = "text"
  static _ELE_span_ = "span"
  static _ELE_lottie_ = "lottie"
  static _ELE_progress_ = "progress"
  static _ELE_customer_ = "customer"
  static _ELE_list_ = "list"
  static _ELE_item_ = "item"
  static _ELE_counting_ = "counting"
  static _ELE_component_ = "component"

  static _ELE_click_ = "click"
  static _ELE_expose_ = "expose"


  static ignoreXmlElement(xmlElement) {
    return xmlElement instanceof XmlTextNode || xmlElement instanceof XmlCommentNode
  }

  static getXmlElementAttrValue(key, xmlElement) {
    return xmlElement.attr[key]
  }

  static getXmlElementAttrValueWithRPX(key, xmlElement) {
    let value = xmlElement.attr[key]

    if (typeof value === 'string' && value.indexOf('rpx') > 0) {
      let resultArr = [ ]
      value.split(' ').forEach(element => {
        resultArr.push(Constants._convertDimens(element))
      })
      return resultArr.join(' ')
    }
    return value
  }

  static _convertDimens(str) {
    const suffix = "rpx"
    if (!str || str.length <= 0) {
      return str
    }
    if (str.endsWith(suffix)) {
      const dimen = parseFloat(str.substring(0, str.lastIndexOf(suffix)))
      const p = Constants.DEFAULT_WIDTH_DIMEN / Constants.WIDTH_DIMEN
      return `${dimen * p}rpx`
    }
    return str
  }
}