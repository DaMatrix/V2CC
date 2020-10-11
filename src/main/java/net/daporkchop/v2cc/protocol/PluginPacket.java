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

package net.daporkchop.v2cc.protocol;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.common.misc.string.PStrings;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public abstract class PluginPacket extends MinecraftPacket {
    @Override
    public abstract void read(NetInput in) throws IOException;

    @Override
    public abstract void write(NetOutput out) throws IOException;

    /**
     * @return the {@link PluginProtocol} that this packet belongs to
     */
    public abstract PluginProtocol getProtocol();

    @Override
    public String toString() {
        return PStrings.lightFormat("PluginMessagePacket(channel=%s, data=%s)", this.getProtocol().channelName(), super.toString());
    }

    /**
     * Implementation for a packet class with an unknown/unimplemented body.
     *
     * @author DaPorkchop_
     */
    @Getter
    @Setter
    public static abstract class Unknown extends PluginPacket {
        protected byte[] data;

        @Override
        public void read(NetInput in) throws IOException {
            this.data = in.readBytes(in.available());
        }

        @Override
        public void write(NetOutput out) throws IOException {
            out.writeBytes(this.data);
        }
    }
}
