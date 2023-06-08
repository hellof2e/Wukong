import Constants from "../constants/constants.js";
import BaseParser from "./base-parser.js";
import JSFileParser from "./js-file-parser.js";
import ParserFactory from "./parser-factory.js";


export default class DocumentParser extends BaseParser {

  _parseChild(xmlElement) {
    const parser = ParserFactory.createParser(xmlElement, this.subDir)
    return parser.parse()
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

    const logicFilePath = this.getXmlElementAttrValue('logic')

    const dependences = new Array()
    if (superDependence && superDependence.length > 0) {
      dependences.push(...superDependence)
    }
    if (childrenDependence && childrenDependence.length > 0) {
      dependences.push(...childrenDependence)
    }
    if (logicFilePath) {
      console.log(`analysis js path [${logicFilePath}]...`);
      dependences.push(this.generateFilePath(logicFilePath))
    }
    return dependences
  }

  parse() {
    const lifecycle = this.getXmlElementAttrValue('lifecycle')
    const jsFilePath = this.getXmlElementAttrValue('logic')
    const width = this.getXmlElementAttrValue('width')

    if (width && width.length > 0) { // 修改设计稿尺寸
      Constants.WIDTH_DIMEN = parseInt(width)
    }

    if (width) {
      Object.prototype._widthDimen = width
    }

    let styleModel = { }
    this.xmlElement.children.forEach(xmlElement => {
      if (!this.ignoreXmlElement(xmlElement)) {
        styleModel = this._parseChild(xmlElement)
      }
    });


    if (styleModel.hasAttrs()) {

      if (lifecycle) {
        styleModel.lifecycle = lifecycle === 'true'
      }
      

      if (jsFilePath && jsFilePath.length > 0) {
       const jsFileParser = new JSFileParser(this.generateFilePath(jsFilePath))
       const jsContent = jsFileParser.parse() 
       if (jsContent && jsContent.length > 0) {
          styleModel.logic = jsContent
       }
      }
    }
    return styleModel
  }
}