import Constants from "../constants/constants.js";
import BaseParser from "./base-parser.js";

const RICH_TEXT_ATTRS = [
  'content',
  'color',
  'font-size',
  'font-weight',
  'text-decoration-line',
  'font-family',
  'i-font-family',
  'a-font-family',
]

export default class TextParser extends BaseParser {
  typeName() {
    return Constants._ELE_text_
  }
  
  extendAttrs() {
    return ['max-rows']
  }

  _parseChild(xmlElement) {
    const obj = {}
    const keys = Object.keys(xmlElement.attr)
    keys.forEach((key) => {
      const value = this.getXmlElementAttrValue(key, xmlElement)
      if (value) {
        obj[key] = value
      }
    })
    return obj
  }

  parse() {
    const obj = super.parse()
    const text = []
    this.xmlElement.children.forEach(xmlElement => {
      if (!this.ignoreXmlElement(xmlElement)) {
        const child = this._parseChild(xmlElement)
        if (child.hasAttrs()) {
          text.push(child)
        }
      }
    });

    if (text.length > 0) {
      obj['text'] = text
    }
    return obj
  }
}