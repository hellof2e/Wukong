package com.hellobike.magiccube.v2.reports

object Codes {

    const val SUCCESS = 0

    const val AJAX_ERROR = -1001 // ajax 异常
    const val INVALID_URL = -1002 // 无效的url
    const val INVALID_DATA = -1003 // 无效的数据
    const val INVALID_STYLE = -1004 // 无效的样式
    const val ERROR_PARSE_VIEW_MODEL = -1005 // view model解析失败
    const val ERROR_PARSE_NODE_FAILED = -1006 // vnode 解析失败
    const val ERROR_PARSE_VIEW = -1007 // view 解析失败
    const val ERROR_JS_LOAD_FAILED = -1008 // js 加载失败



    const val PRELOAD_TIMEOUT = -2001 // 预加载超时
    const val PRELOAD_ERROR = -2002 // 预加载失败
}