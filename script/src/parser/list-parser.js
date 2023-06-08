import Constants from "../constants/constants.js";
import ContainerParser from "./container-parser.js";


const LIST_EXTEND_ATTRS = [
  "list-data",
  "orientation",
  "item-key"
]

export default class ListParser extends ContainerParser {

  typeName() {
    return Constants._ELE_list_
  }
  
  extendAttrs() {
    return [...super.extendAttrs(), ...LIST_EXTEND_ATTRS];
  }
}