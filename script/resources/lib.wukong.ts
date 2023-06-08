////////////////////////////////////////////  Wukong bridge  ///////////////////////////////////////////////////////

interface DeviceInfo {
  /**
   * 动态化卡片运行的宿主环境: Android / iOS 
   */
  platform: string;
}

interface IOnLoginCallback {
  success: () => {};
  error: () => {};
}

interface WukongBridge {
  /**
   * 缓存数据到文件
   * @param file 文件名称，如果是空则不会缓存
   * @param key 缓存 key，如果是空则不会缓存
   * @param value 缓存值，如果是空则不会缓存
   */
  cacheItem(file: string, key: string, value: string) : void;
  /**
   * 读取缓存的数据
   * @param file 文件名称，如果是空则返回 null
   * @param key 缓存 key，如果是空则返回null
   */
  readItem(file: string, key: string): string;

  /**
   * toast 提示
   * @param content toast 提示内容
   */
  toast(content: string): void;

  /**
   * 获取位置信息
   */
  getLocation():  Map<string, any>;

  /**
   * 获取用户信息
   */
  getUserInfo(): Map<string, any>;

  /**
   * 获取设备信息
   */
  getDeviceInfo(): DeviceInfo;

  /**
   * 路由跳转
   * @param url 路由跳转的url
   */
  navigator(url: string): void;

  /**
   * 是否已经登陆
   */
  hasLogin(): boolean

  /**
   * 登陆
   */
  login(callback: IOnLoginCallback): void;

  /**
   * 点击埋点
   * @param params 点击事件埋点信息
   */
  trackClick(params: Map<string, any>): void;

  /**
   * 自定义埋点
   * @param params 自定义埋点信息
   */
  trackCustom(params: Map<string, any>): void;

  /**
   * 自定义保管埋点
   * 支持版本 app version: 6.27.0 dsl version：3.0.1
   * @param params 自定义埋点信息
   */
   trackExpose(params: Map<string, any>): void;
}

declare var Wukong: WukongBridge;





////////////////////////////////////////////  this.wk bridge  ///////////////////////////////////////////////////////


interface Response {
  code: number;
  msg: string;
  data: Map<string, any>;
}

interface AjaxParams {
  type: string | "POST";
  url: string | "";
  data: Map<string, any>;
  error: () => {};
  success: (response: Response) => {};
}

interface SetStateParams {
  /**
   * 数据是否发生改变, 默认为true
   */
  datasetChanged: boolean | true;
}

interface TimeoutDesc {
  /**
   * 
   * @returns 延迟任务的回调
   */
  callback: () => {};
  /**
   * 延迟执行的时间毫秒值
   */
  delayMillis: string;
}

interface WKDialogDescParams {
  /**
   * dsl 样式链接
   */
  style: string;
  /**
   * dsl 数据字典
   */
  data: Map<string, any>;
  /**
   * 弹窗弹出的方位。( top | center | bottom) 默认是： center
   */
  gravity: string;
  /**
   * 触摸弹窗外部是否需要关闭。默认值：true
   */
  canceledOnTouchOutside: boolean;
}

interface WKDialogDesc {
  /**
   * dsl 弹窗的描述信息
   */
  params: WKDialogDescParams;
  /**
   * 自定义事件回调函数，dsl 弹窗中的 callNative 事件会回调到这个函数中。
   * @param params 事件信息
   * @returns 
   */
  onEvent: (params: Map<string, any>) => {};
  /**
   * dsl 弹窗展示的回调
   */
  onShow: () => { };
  /**
   * 弹窗关闭的回调
   */
  onDismiss: () => { };
}

interface WKBridge {

  /**
   * 发送网络请求
   * @param params 网络请求参数
   */
  ajax(params: AjaxParams): void;

  /**
   * 通过这个方法，可以通知当前卡片 数据发生变更，也可以更新当前卡片的ui，同时可以回调数据给 native。
   */
  setState(params: SetStateParams): void;

  /**
   * 
   * @param params 自定义参数
   */
  callNative(params: Map<string, any>): void;

  /**
   * 
   * @param params 设定延迟任务
   */
  setTimeout(params: TimeoutDesc): void;

  /**
   * 展示悟空动态化弹窗。
   * @param params 弹窗的描述信息
   */
  showWKDialog(params: WKDialogDesc): void;
}


interface Object {
  wk: WKBridge;
}






