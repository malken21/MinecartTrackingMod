package marumasa.minecart_tracking_mod.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.vehicle.AbstractMinecartEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.IEEErem
import kotlin.math.atan2

class MinecartTrackingModClient : ClientModInitializer {

    private var currentYaw = 0.0
    private var lastRenderTime = 0.0
    val smoothSpeed = 5 // 追従速度

    override fun onInitializeClient() {
        WorldRenderEvents.START.register {
            val player = MinecraftClient.getInstance().player

            if (player == null || player.vehicle !is AbstractMinecartEntity) {
                currentYaw = 0.0;
                return@register
            }

            val currentTime = System.nanoTime() / 1000000000.0 // 秒単位

            val deltaTime = currentTime - lastRenderTime
            lastRenderTime = currentTime

            val minecart = player.vehicle as AbstractMinecartEntity
            val minecartVelocity = minecart.velocity

            var targetYaw = 0.0
            if (!minecartVelocity.equals(Vec3d.ZERO)) {
                targetYaw = Math.toDegrees(atan2(-minecartVelocity.x, minecartVelocity.z))
            }

            var deltaYaw = targetYaw - currentYaw

            // -180から180度の範囲に調整
            deltaYaw = deltaYaw.IEEErem(360.0)

            //deltaTimeをかけることでフレームレートに依存しない追従速度になる
            currentYaw += deltaYaw * smoothSpeed * deltaTime

            player.yaw = currentYaw.toFloat()
        }
    }
}
