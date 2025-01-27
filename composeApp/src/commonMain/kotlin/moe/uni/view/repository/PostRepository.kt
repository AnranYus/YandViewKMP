package moe.uni.view.repository

import io.github.aakira.napier.Napier
import moe.uni.view.bean.YandPost


class PostRepository {
    val service = PostService()
    //获取post
    suspend fun fetchPostData(searchTarget: String?, page: Int): ArrayList<YandPost> {
        var target = searchTarget
        var isNum = true

        //如果检索目标是纯数字，则按照id搜索
        try {
            target?.toLong()
        } catch (e: NumberFormatException) {
            isNum = false
        }

        if (isNum) {
            target = "id:$searchTarget"
        }

        val load = Load.Companion.builder(target, page)
        val dataList = service.fetchNewPost(load)
        Napier.d { dataList.toString() }

        return dataList
    }
}