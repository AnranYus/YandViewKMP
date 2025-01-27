package moe.uni.view

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform