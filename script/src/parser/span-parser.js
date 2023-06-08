import { XmlTextNode } from "xmldoc";
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


export default class SpanParser extends BaseParser {
  typeName() {
    return Constants._ELE_span_
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

    const text = { }

    RICH_TEXT_ATTRS.forEach((key) => {
      const value = this.getXmlElementAttrValue(key)
      if (value) {
        text[key] = value
      }
    })

    const keywords = []
    this.xmlElement.children.forEach(xmlElement => {
      if (!this.ignoreXmlElement(xmlElement)) {
        const child = this._parseChild(xmlElement)
        if (child.hasAttrs()) {
          keywords.push(child)
        }
      }
    });

    if (text.hasAttrs()) {
      obj['text'] = text
    }

    if (keywords.length > 0) {
      obj['keywords'] = keywords
    }
    return obj
  }
}