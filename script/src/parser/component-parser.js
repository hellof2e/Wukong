import fs from 'fs'
import { XmlDocument } from "xmldoc";
import BaseParser from "./base-parser.js";
import ParserFactory from "./parser-factory.js";


export default class ComponentParser extends BaseParser {

  _parseChild(xmlElement) {
    const parser = ParserFactory.createParser(xmlElement, this.subDir)
    return parser.parse()
  }

  analysisDependence() {
    const superDependence = super.analysisDependence()

    const componentFilePath = this.getXmlElementAttrValue('path')

    const dependences = new Array()
    if (superDependence && superDependence.length > 0) {
      dependences.push(...super.analysisDependence())
    }

    if (componentFilePath) {
      console.log(`analysis component path [${componentFilePath}]...`);
      let childrenDependence = []
      const xmlContent = fs.readFileSync(this.generateFilePath(componentFilePath), { encoding: "utf-8" })
      const document = new XmlDocument(xmlContent)
  
      document.children.forEach(xmlElement => {
        if (!this.ignoreXmlElement(xmlElement)) {
          const parser = ParserFactory.createParser(xmlElement, this.subDir)
          const childDependence = parser.analysisDependence()
          if (childDependence && childDependence.length > 0) {
            childrenDependence.push(...childDependence)
          }
        }
      });
      
      if (childrenDependence && childrenDependence.length > 0) {
        dependences.push(...childrenDependence)
      }
      dependences.push(this.generateFilePath(componentFilePath))
    }
    return dependences
  }

  parse() {
    const componentFilePath = this.getXmlElementAttrValue('path')

    const xmlContent = fs.readFileSync(this.generateFilePath(componentFilePath), { encoding: "utf-8" })
    const document = new XmlDocument(xmlContent)

    let componentStyleModel = null
    document.children.forEach(xmlElement => {
      if (!this.ignoreXmlElement(xmlElement)) {
        componentStyleModel = this._parseChild(xmlElement)
      }
    });

    return componentStyleModel
  }
}