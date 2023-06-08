import Constants from "../constants/constants.js";
import BaseParser from "./base-parser.js";


const LOTTIE_EXTEND_ATTRS = [
  "src",
  "fit"
]

export default class LottieParser extends BaseParser {

  typeName() {
    return Constants._ELE_lottie_
  }
  
  extendAttrs() {
    return LOTTIE_EXTEND_ATTRS;
  }
}