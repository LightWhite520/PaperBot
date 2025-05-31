import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class LightAppContactCard(
    @JsonNames("app")
    val appId: String, // com.tencent.troopsharecard

    @JsonNames("view")
    val view: String, // contact

    @JsonNames("prompt")
    val prompt: String, // 推荐群聊: ...

    @JsonNames("meta")
    val meta: MetaData
)

@Serializable
data class MetaData(
    @JsonNames("contact")
    val contact: ContactData
)

@Serializable
data class ContactData(
    @JsonNames("nickname")
    val nickname: String,

    @JsonNames("tag")
    val tag: String,

    @JsonNames("jumpUrl")
    val jumpUrl: String,

    @JsonNames("avatar")
    val avatar: String? = null,

    @JsonNames("contact")
    val contactDesc: String? = null
)
