package marumasa.minecart_tracking

import com.google.gson.Gson
import marumasa.minecart_tracking.MinecartTracking.Companion.MOD_ID
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Config {
    var TrackingYaw = true
        set(value) {
            field = value
            serialize()
        }
    var TrackingPitch = true
        set(value) {
            field = value
            serialize()
        }
    var SmoothSpeedYaw = 5.0f
        set(value) {
            field = value
            serialize()
        }
    var SmoothSpeedPitch = 5.0f
        set(value) {
            field = value
            serialize()
        }

    private data class JsonModel(
        val TrackingYaw: Boolean,
        val TrackingPitch: Boolean,
        val SmoothSpeedYaw: Float,
        val SmoothSpeedPitch: Float,
    )

    private fun deserialize() {
        val model = loadJSON()
        TrackingYaw = model.TrackingYaw
        TrackingPitch = model.TrackingPitch
        SmoothSpeedYaw = model.SmoothSpeedYaw
        SmoothSpeedPitch = model.SmoothSpeedPitch
    }

    private fun serialize() {
        saveJSON(
            JsonModel(
                TrackingYaw,
                TrackingPitch,
                SmoothSpeedYaw,
                SmoothSpeedPitch
            )
        )
    }

    init {
        val configFile = path.toFile()
        if (!configFile.exists()) {
            serialize()
        } else {
            deserialize()
        }
    }

    companion object {
        private val path: Path = FabricLoader.getInstance().configDir.normalize().resolve(
            "$MOD_ID.json"
        )

        private val gson = Gson()

        private fun loadJSON(): JsonModel {
            try {
                Files.newBufferedReader(path).use { reader ->
                    return gson.fromJson(
                        reader,
                        JsonModel::class.java
                    )
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        private fun saveJSON(model: JsonModel) {
            try {
                Files.newBufferedWriter(path).use { writer ->
                    gson.toJson(model, model.javaClass, writer)
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}