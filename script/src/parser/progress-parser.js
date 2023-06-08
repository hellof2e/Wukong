import Constants from "../constants/constants.js";
import BaseParser from "./base-parser.js";


const PROGRESS_EXTEND_ATTRS = [
  "progress",
  "max-progress",
  "progress-color"
]

export default class ProgressParser extends BaseParser {

  typeName() {
    return Constants._ELE_progress_
  }
  
  extendAttrs() {
    return PROGRESS_EXTEND_ATTRS;
  }
}