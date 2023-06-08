package com.hellobike.magiccube.demo.preview.controller

import java.lang.Exception

interface IPreviewCallback {

    fun onFailure(e: Exception)

    fun onSuccess(style: String, data: String)
}