package com.hellobike.magiccube.v2.configs


interface IReportEvent {

    fun trackClick(params: Map<String, Any?>?)

    fun trackCustom(params: Map<String, Any?>?)

    fun trackExpose(params: Map<String, Any?>?)
}