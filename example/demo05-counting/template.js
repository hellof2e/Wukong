import './lib.wukong'
export default {
  data: {
    message: "hello WuKong"
  },
  beforeCreate() {
    this.data.deadline = Date.now() + 43200000;
  },
  clockFinish() {
    console.log(this.data.message)
  }
}