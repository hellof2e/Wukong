import Constants from "../constants/constants.js";
import BaseParser from "./base-parser.js";


const PROGRESS_EXTEND_ATTRS = [
  "progress",
  "max-progress",
  "progress-color"
]

export default class CustomerParser extends BaseParser {

  typeName() {
    return this.getXmlElementAttrValue("name")
  }
}