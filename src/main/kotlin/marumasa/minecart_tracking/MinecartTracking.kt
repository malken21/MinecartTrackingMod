package marumasa.minecart_tracking

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger

class MinecartTracking : ModInitializer {

    override fun onInitialize() {
        LOGGER.info("Start: $MOD_ID")
    }

    companion object {
        val LOGGER: Logger = LogUtils.getLogger()
        const val MOD_ID: String = "minecart_tracking"
    }
}
