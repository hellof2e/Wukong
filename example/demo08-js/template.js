import './lib.wukong'

export default {
  data: {
    message: "hello WuKong"
  },
  showToast() {
    Wukong.toast("Hello Wukong!");
  },
  ajaxGET() {
    this.wk.ajax({
      type: "GET",
      url: "http://172.17.159.75:8080/app2/test",
      data: {
        adCode: "adCode",
        cityCode: "cityCode",
        activityCode: "activityCode"
      },
      error: () => {
        Wukong.toast("请求失败")
      },
      success: (response) => {
          this.data.message = JSON.stringify(response);
          this.wk.setState({})
      }
    })
  },
  ajaxPOST() {
    this.wk.ajax({
      type: "POST",
      url: "http://172.17.159.75:8080/app/test",
      data: {
        adCode: "adCode",
        cityCode: "cityCode",
        activityCode: "activityCode"
      },
      error: () => {
        Wukong.toast("请求失败")
      },
      success: (response) => {
          this.data.message = JSON.stringify(response);
          this.wk.setState({})
      }
    })
  },
  timeoutTask() {
    this.data.message = "setTimeout 1000...";
    this.wk.setState({});
    this.wk.setTimeout({
      callback: () => {
        this.data.message = "setTimeout end!";
        this.wk.setState({});
      },
      delayMillis: 1000
    })
  },
  saveCache() {
      Wukong.cacheItem("file01", "key01", "value01");
      Wukong.toast("存储成功");
  },
  readCache() {
    let value = Wukong.readItem("file01", "key01");
    this.data.message = value;
    this.wk.setState({});
  },
}