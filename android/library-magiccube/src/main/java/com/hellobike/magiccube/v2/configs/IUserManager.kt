package com.hellobike.magiccube.v2.configs

interface IUserManager {

    fun hasLogin(): Boolean

    fun getUserInfo(): HashMap<String, Any?>

    fun gotoLogin(handler: ILoginHandler)

    interface ILoginHandler {
        fun handleLoginSuccess()
        fun handleLoginFailed()
    }
}