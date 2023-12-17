package com.github.kr328.clash

import android.content.Intent
import com.github.kr328.clash.design.ServerDesign
import com.github.kr328.clash.lambda.fetchStringUrlFromLambda
import kotlinx.coroutines.isActive
import com.github.kr328.clash.core.model.*
import kotlinx.serialization.json.Json

class ServerActivity : BaseActivity<ServerDesign>() {
    override suspend fun main() {
        val serverMetadata = try {
            val serverMetadataJson = fetchStringUrlFromLambda(this, "https://3sm6xoow37.execute-api.us-east-2.amazonaws.com/airport_server_metadata_1")
            Json.decodeFromString(ServerMetadata.serializer(), serverMetadataJson)
        } catch (error: Exception) {
            ServerMetadata(
                title = "Cloud Yu",
                subtitle = "A server with limited English language support 🥹\n点击跳转，推荐“中转套餐”",
                url = "https://cloudyu.top/#/register?code=ycOEE8sN"
            )
        }
        val design = ServerDesign(this, serverMetadata) {
            startActivity(Intent(Intent.ACTION_VIEW).setData(it))
        }

        setContentDesign(design)

        while (isActive) {
            events.receive()
        }
    }
}