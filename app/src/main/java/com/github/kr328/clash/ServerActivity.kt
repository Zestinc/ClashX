package com.github.kr328.clash

import android.content.Intent
import com.github.kr328.clash.design.ServerDesign
import com.github.kr328.clash.lambda.fetchStringUrlFromLambda
import kotlinx.coroutines.isActive
import com.github.kr328.clash.core.model.*
import kotlinx.serialization.json.Json

class ServerActivity : BaseActivity<ServerDesign>() {
    override suspend fun main() {
        val serverMetadataJson = fetchStringUrlFromLambda(this, "https://3sm6xoow37.execute-api.us-east-2.amazonaws.com/airport_server_metadata_1")
        val serverMetadata = Json.decodeFromString(ServerMetadata.serializer(), serverMetadataJson)
        val design = ServerDesign(this, serverMetadata) {
            startActivity(Intent(Intent.ACTION_VIEW).setData(it))
        }

        setContentDesign(design)

        while (isActive) {
            events.receive()
        }
    }
}