import Constants from "../constants/constants.js";
import BaseParser from "./base-parser.js";


const IMG_EXTEND_ATTRS = [
  "src",
  "fit"
]

export default class ImgParser extends BaseParser {

  typeName() {
    return Constants._ELE_img_
  }
  
  extendAttrs() {
    return IMG_EXTEND_ATTRS;
  }
}