import Constants from "../constants/constants.js";
import ContainerParser from "./container-parser.js";


const LIST_ITEM_EXTEND_ATTRS = [
  "item-type"
]

export default class ListItemParser extends ContainerParser {

  typeName() {
    return Constants._ELE_item_
  }
  
  extendAttrs() {
    return [...super.extendAttrs(), ...LIST_ITEM_EXTEND_ATTRS];
  }
}