
import fs from 'fs'
import { XmlDocument } from "xmldoc";
import Constants from '../constants/constants.js';


export default class ActionFileParser {

  constructor(xmlElement) {
    this.xmlElement = xmlElement
  }

  getXmlElementAttrValue(key, xmlElement = this.xmlElement) {
    return Constants.getXmlElementAttrValue(key, xmlElement)
  }

  _parseBusInfoArgs(xmlElement) {
    const obj = { }
    xmlElement.children.forEach(busInfoArgElement => {
      if (!Constants.ignoreXmlElement(busInfoArgElement)) {
        const key = this.getXmlElementAttrValue('key', busInfoArgElement)
        const value = this.getXmlElementAttrValue('value', busInfoArgElement)
        if (key && key.length > 0 && value && value.length > 0) {
          obj[key] = value
        }
      }
    })
    return obj
  }
  _parseReport(xmlElement) {
    const busInfo = this.getXmlElementAttrValue('bus-info', xmlElement)

    const obj = { }


    // 分量解析
    const busInfoArgsModel = this._parseBusInfoArgs(xmlElement)
    if (busInfoArgsModel.hasAttrs()) {
      // const busInfoJson = JSON.stringify(busInfoArgsModel)
      obj['bus-info'] = busInfoArgsModel
    }

    if (busInfo && busInfo.length > 0) {
      obj['bus-info'] = busInfo
    }
    return obj
  }

  _parseClick(xmlElement) {
    const obj = { }
    const url = this.getXmlElementAttrValue('url', xmlElement)
    if (url && url.length > 0) {
      obj['url'] = url

      xmlElement.children.forEach((element) => {
        if (!Constants.ignoreXmlElement(element) && element.name === 'report') {
          const report = this._parseReport(element)
          if (report.hasAttrs()) {
            obj['report'] = report
          }
        }
      })
    }
    return obj
  }

  _parseExpose(xmlElement) {
    const obj = { }

    xmlElement.children.forEach((element) => {
      if (!Constants.ignoreXmlElement(element) && element.name === 'report') {
        const report = this._parseReport(element)
        if (report.hasAttrs()) {
          obj['report'] = report
        }
      }
    })
    return obj
  }

  parse() {
    const obj = { }
    this.xmlElement.children.forEach(xmlElement => {
      if (!Constants.ignoreXmlElement(xmlElement)) {
        if (xmlElement.name === 'click') {
          obj['click'] = this._parseClick(xmlElement)
        } else if (xmlElement.name === 'expose') {
          obj['expose'] = this._parseExpose(xmlElement)
        }
      }
    });
    
    return obj
  }
}