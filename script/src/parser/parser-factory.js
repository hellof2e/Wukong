
import Constants from "../constants/constants.js";
import ContainerParser from "./container-parser.js";
import CustomerParser from "./customer-parser.js";
import ImgParser from "./img-parser.js";
import ListItemParser from "./list-item-parser.js";
import CountingParser from "./counting-parser.js";
import ListParser from "./list-parser.js";
import LottieParser from "./lottie-parser.js";
import ProgressParser from "./progress-parser.js";
import SpanParser from "./span-parser.js";
import TextParser from "./text-parser.js";
import ComponentParser from "./component-parser.js";


export default class ParserFactory {

  static createParser(xmlElement, subDir) {
    if (xmlElement.name == Constants._ELE_container_) {
      return new ContainerParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_img_) {
      return new ImgParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_text_) {
      return new TextParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_span_) {
      return new SpanParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_lottie_) {
      return new LottieParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_progress_) {
      return new ProgressParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_customer_) {
      return new CustomerParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_list_) {
      return new ListParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_item_) {
      return new ListItemParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_counting_) {
      return new CountingParser(xmlElement, subDir)
    } else if (xmlElement.name == Constants._ELE_component_) {
      return new ComponentParser(xmlElement, subDir)
    } else {
      throw Error(`${xmlElement.name} 无法识别!`)
    }
  }
}