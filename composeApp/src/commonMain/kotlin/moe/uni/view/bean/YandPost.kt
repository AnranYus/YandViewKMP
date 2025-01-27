package moe.uni.view.bean

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
class YandPost(
    @SerialName("file_url") val fileUrl: String,
    @SerialName("sample_url") val sampleUrl: String,
    @SerialName("sample_height") val sampleHeight: Int,
    @SerialName("sample_width") val sampleWidth: Int,
    @SerialName("id") val postId: String,
    @SerialName("tags") val tags: String?,
)
