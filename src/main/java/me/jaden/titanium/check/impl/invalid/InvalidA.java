package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientVehicleMove;
import com.google.common.primitives.Floats;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

// PaperMC
// net/minecraft/server/network/ServerGamePacketListenerImpl.java:515
// net/minecraft/server/network/ServerGamePacketListenerImpl.java:1283
public class InvalidA implements PacketCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

            if (!wrapper.hasPositionChanged()) return;

            Location location = wrapper.getLocation();
            if (this.containsInvalidValues(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch())) {
                flag(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
            WrapperPlayClientVehicleMove wrapper = new WrapperPlayClientVehicleMove(event);

            Vector3d position = wrapper.getPosition();
            if (this.containsInvalidValues(position.getX(), position.getY(), position.getZ(), wrapper.getYaw(), wrapper.getPitch())) {
                flag(event);
            }
        }
    }

    private boolean containsInvalidValues(double x, double y, double z, float yaw, float pitch) {
        return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || !Floats.isFinite(pitch) || !Floats.isFinite(yaw);
    }
}
