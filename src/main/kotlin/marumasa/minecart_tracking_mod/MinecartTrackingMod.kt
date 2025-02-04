package marumasa.minecart_tracking_mod

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger

class MinecartTrackingMod : ModInitializer {

    override fun onInitialize() {
        LOGGER.info("Start: $MOD_ID")
    }

    companion object {
        val LOGGER: Logger = LogUtils.getLogger()
        val MOD_ID: String = "minecart_tracking_mod"
    }
}
