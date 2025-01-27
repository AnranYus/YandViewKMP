package moe.uni.view.repository

import moe.uni.view.bean.YandPost
import moe.uni.view.network.request


class PostService {

    //设置列表数据
    suspend fun fetchNewPost(load: Load): ArrayList<YandPost> {
        return request("post.json"){
            put("tags",load.tags.toString())
            put("limit","20")
            put("page",load.page.toString())
        }
    }
}

data class Load(
    val tags: String?,
    val page: Int,
) {
    companion object {
        fun builder(tags: String?, page: Int): Load {
            return Load(tags, page)
        }
    }

}