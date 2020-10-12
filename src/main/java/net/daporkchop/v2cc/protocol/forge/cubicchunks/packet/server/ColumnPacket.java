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

package net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.v2cc.protocol.PluginPacket;
import net.daporkchop.v2cc.protocol.PluginProtocol;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.CubicChunksProtocol;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.Column;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.util.PacketHandler;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
@Getter
public class ColumnPacket extends PluginPacket {
    protected Column column;

    @Override
    public void read(NetInput in) throws IOException {
        this.column = new Column(in.readInt(), in.readInt());

        in.readInt(); //discard
        in.readBytes(this.column.biomes());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginProtocol getProtocol() {
        return CubicChunksProtocol.INSTANCE;
    }

    public static class Handler implements PacketHandler<ColumnPacket> {
        @Override
        public void handle(@NonNull Player player, @NonNull ColumnPacket packet) {
            player.putColumn(packet.column);
        }
    }
}
