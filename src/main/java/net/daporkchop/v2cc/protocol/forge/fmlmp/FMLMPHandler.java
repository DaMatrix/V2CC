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

package net.daporkchop.v2cc.protocol.forge.fmlmp;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.daporkchop.v2cc.protocol.forge.fmlmp.packet.MultipartPacket;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.util.PacketHandler;

import java.io.IOException;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public class FMLMPHandler implements PacketHandler<Packet> {
    protected boolean active = false;

    protected int nextPart;
    protected int nParts;
    protected String channel;
    protected ByteBuf payload;

    @Override
    public void handle(@NonNull Player player, @NonNull Packet pck) {
        checkArg(pck instanceof MultipartPacket, pck);
        ByteBuf data = Unpooled.wrappedBuffer(((MultipartPacket) pck).data());

        if (!this.active) {
            this.active = true;
            this.nextPart = 0;

            try {
                NetInput input = new ByteBufNetInput(data);
                this.channel = input.readString();
                this.nParts = input.readUnsignedByte();
                this.payload = Unpooled.wrappedBuffer(new byte[input.readInt()]).writerIndex(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            checkState(data.readUnsignedByte() == this.nextPart++);
            this.payload.writeBytes(data);
            if (this.nextPart == this.nParts) { //packet is complete
                this.active = false;
                player.clientSession().callEvent(new PacketReceivedEvent(player.clientSession(), new ServerPluginMessagePacket(this.channel, this.payload.array())));
                this.channel = null;
                this.payload = null;
            }
        }
    }
}
