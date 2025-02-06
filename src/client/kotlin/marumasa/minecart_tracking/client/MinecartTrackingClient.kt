package marumasa.minecart_tracking.client

import marumasa.minecart_tracking.MinecartTracking.Companion.CONFIG
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.vehicle.AbstractMinecartEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.IEEErem
import kotlin.math.asin
import kotlin.math.atan2

class MinecartTrackingClient : ClientModInitializer {

    private var player: ClientPlayerEntity? = null
    private var minecartVectorManager: MinecartVectorManager? = null
    private var deltaTimeManager: DeltaTimeManager? = null

    override fun onInitializeClient() {
        WorldRenderEvents.START.register { _ ->
            val player = this.player ?: return@register
            val minecartVectorManager = this.minecartVectorManager ?: return@register
            val deltaTimeManager = this.deltaTimeManager ?: return@register

            // deltaTime 更新
            deltaTimeManager.update()

            val targetVec = minecartVectorManager.targetVector
            val deltaTime = deltaTimeManager.delta

            if (targetVec == Vec3d.ZERO)
                return@register

            if (CONFIG.TrackingYaw)
                player.yaw = genYaw(targetVec, player, CONFIG.SmoothSpeedYaw, deltaTime)
            if (CONFIG.TrackingPitch)
                player.pitch = genPitch(targetVec, player, CONFIG.SmoothSpeedPitch, deltaTime)
        }

        ClientTickEvents.START_CLIENT_TICK.register { client ->
            player = client.player ?: return@register
            minecartVectorManager?.update(player!!)
        }
        ClientPlayConnectionEvents.JOIN.register { _, _, client ->
            player = client.player ?: return@register
            minecartVectorManager = MinecartVectorManager()
            deltaTimeManager = DeltaTimeManager()
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            player = null
            minecartVectorManager = null
            deltaTimeManager = null
        }
    }

    class MinecartVectorManager {
        var targetVector: Vec3d = Vec3d.ZERO
            private set
        private var lastPos: Vec3d? = null
        fun update(player: ClientPlayerEntity) {
            if (player.vehicle is AbstractMinecartEntity) {
                val minecart = player.vehicle as AbstractMinecartEntity
                targetVector = minecart.pos.subtract(lastPos ?: minecart.pos).normalize()
                lastPos = minecart.pos
            } else {
                targetVector = Vec3d.ZERO
                lastPos = Vec3d.ZERO
            }
        }
    }

    class DeltaTimeManager {
        var delta: Double = 0.0
            private set
        private var lastRenderTime: Double? = null
        fun update() {
            val currentTime = System.nanoTime() / 1000000000.0 // 秒単位
            delta = currentTime - (lastRenderTime ?: currentTime)
            lastRenderTime = currentTime
        }
    }

    companion object {
        fun genYaw(targetVec: Vec3d, player: ClientPlayerEntity, smoothSpeed: Float, deltaTime: Double): Float {
            // 目的の値
            val targetYaw = Math.toDegrees(atan2(-targetVec.x, targetVec.z))
            // 現在の値
            var currentYaw = player.yaw.toDouble()
            // 差分の値
            val deltaYaw = (targetYaw - currentYaw).IEEErem(360.0)// -180から180度の範囲
            //deltaTimeをかけることでフレームレートに依存しない追従速度になる
            currentYaw += deltaYaw * smoothSpeed * deltaTime
            // -180から180度の範囲にして Float
            return currentYaw.IEEErem(360.0).toFloat()
        }

        fun genPitch(targetVec: Vec3d, player: ClientPlayerEntity, smoothSpeed: Float, deltaTime: Double): Float {
            // 目的の値
            val targetPitch = Math.toDegrees(asin(-targetVec.y))
            // 現在の値
            var currentPitch = player.pitch.toDouble()
            // 差分の値
            val deltaPitch = (targetPitch - currentPitch).IEEErem(360.0)// -180から180度の範囲
            //deltaTimeをかけることでフレームレートに依存しない追従速度になる
            currentPitch += deltaPitch * smoothSpeed * deltaTime
            // -180から180度の範囲にして Float
            return currentPitch.IEEErem(360.0).toFloat()
        }
    }
}
