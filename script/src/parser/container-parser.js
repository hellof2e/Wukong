import BaseParser from "./base-parser.js";
import Constants from "../constants/constants.js";
import ParserFactory from './parser-factory.js'

export default class ContainerParser extends BaseParser {

  typeName() {
    return Constants._ELE_container_
  }


  analysisDependence() {
    const superDependence = super.analysisDependence()

    let childrenDependence = []
    this.xmlElement.children.forEach(xmlElement => {
      if (!this.ignoreXmlElement(xmlElement)) {
        const parser = ParserFactory.createParser(xmlElement, this.subDir)
        const childDependence = parser.analysisDependence()
        if (childDependence && childDependence.length > 0) {
          childrenDependence.push(...childDependence)
        }
      }
    });

    const dependences = new Array()
    if (superDependence && superDependence.length > 0) {
      dependences.push(...superDependence)
    }
    if (childrenDependence && childrenDependence.length > 0) {
      dependences.push(...childrenDependence)
    }
    return dependences
  }

  extendAttrs() {
    return [ 'active-background-image', 'background-image' ]
  }

  _parseChild(xmlElement) {
    const parser = ParserFactory.createParser(xmlElement, this.subDir)
    return parser.parse()
  }

  parse() {
    const obj = super.parse()

    const children = new Array()
    this.xmlElement.children.forEach(xmlElement => {
        if (!this.ignoreXmlElement(xmlElement)) {
          const child = this._parseChild(xmlElement)
          if (child.hasAttrs()) {
            children.push(child)
          }
        }
    });
    if (children.length > 0) {
      obj['children'] = children
    }
    return obj
  }
}