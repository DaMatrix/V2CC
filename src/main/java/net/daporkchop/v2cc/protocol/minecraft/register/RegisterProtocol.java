/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.v2cc.protocol.minecraft.register;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketHeader;
import lombok.NonNull;
import net.daporkchop.v2cc.protocol.NoPacketHeader;
import net.daporkchop.v2cc.protocol.PluginProtocol;
import net.daporkchop.v2cc.protocol.minecraft.register.packet.RegisterPacket;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.util.PacketHandler;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class RegisterProtocol extends PluginProtocol implements PacketHandler<Packet> {
    public static final RegisterProtocol INSTANCE = new RegisterProtocol();

    protected RegisterProtocol() {
        super("REGISTER");
    }

    @Override
    protected void registerPackets() {
        this.register(0, RegisterPacket.class);
    }

    @Override
    public PacketHandler<Packet> handler() {
        return this;
    }

    @Override
    public PacketHeader getPacketHeader() {
        return NoPacketHeader.INSTANCE;
    }

    @Override
    public void handle(@NonNull Player player, @NonNull Packet pck) {
        checkArg(pck instanceof RegisterPacket, pck);
        RegisterPacket packet = (RegisterPacket) pck;

        packet.channels().stream().distinct().forEach(player::registerPluginByName);
    }
}
