import Constants from "../constants/constants.js";
import ContainerParser from "./container-parser.js";


const COUNTING_EXTEND_ATTRS = [
  "clock-offset",
  "deadline",
  "interval",
  "stop",
  "counting-type"
]

export default class CountingParser extends ContainerParser {

  typeName() {
    return Constants._ELE_counting_
  }
  
  extendActionObject() {
    const map = new Map()
    const finishEvent = this.getXmlElementAttrValue('finish-event')
    if (finishEvent && finishEvent.length > 0) {
      map.set('counting-finish-event', finishEvent)
    }
    return map
  }
  extendAttrs() {
    return [...super.extendAttrs(), ...COUNTING_EXTEND_ATTRS];
  }
}