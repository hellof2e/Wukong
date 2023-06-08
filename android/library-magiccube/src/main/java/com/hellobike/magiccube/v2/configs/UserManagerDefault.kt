package com.hellobike.magiccube.v2.configs


class UserManagerDefault: IUserManager {

    override fun hasLogin(): Boolean = true

    override fun getUserInfo(): HashMap<String, Any?> {
        return HashMap()
    }

    override fun gotoLogin(handler: IUserManager.ILoginHandler) {
        handler.handleLoginSuccess()
    }
}