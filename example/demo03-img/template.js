import './lib.wukong'
export default {
  data: {
    message: "hello WuKong"
  },
  doClickEvent() {
    console.log(this.data.message)
  }
}