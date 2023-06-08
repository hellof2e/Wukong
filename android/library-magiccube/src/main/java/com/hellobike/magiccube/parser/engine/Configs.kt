package com.hellobike.magiccube.parser.engine


object Configs {
    const val ENGINE_FLEX = "flex"
    const val ENGINE_YOGA = "yoga"
}

enum class Engine(val type: Int) {
    NONE(-1), FLEX(0), YOGA(1);

    companion object {
        fun createEngineType(name: String?): Engine = when (name) {
            Configs.ENGINE_FLEX -> FLEX
            Configs.ENGINE_YOGA -> YOGA
            else -> NONE
        }
    }
}