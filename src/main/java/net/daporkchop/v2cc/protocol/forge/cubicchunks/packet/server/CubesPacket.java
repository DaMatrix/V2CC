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

import com.github.steveice10.mc.protocol.data.game.chunk.Section;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.v2cc.protocol.PluginPacket;
import net.daporkchop.v2cc.protocol.PluginProtocol;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.CubicChunksProtocol;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.Cube;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.CubePos;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.util.Constants;
import net.daporkchop.v2cc.util.PacketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
@Getter
public class CubesPacket extends PluginPacket {
    private static final byte[] HEIGHTMAP_SKIP_BUFFER = new byte[16 * 16 * Integer.BYTES];
    private static final byte[] CUSTOM_BIOMES_SKIP_BUFFER = new byte[8 * 1 * 8];

    protected List<Cube> cubes;

    @Override
    public void read(NetInput in) throws IOException {
        int count = in.readUnsignedShort();

        this.cubes = new ArrayList<>(count);

        //positions
        for (int i = 0; i < count; i++) {
            this.cubes.add(new Cube(in));
        }

        DATA:
        {
            ByteBuf data = Unpooled.wrappedBuffer(in.readBytes(in.readInt()));
            Exception e = null;
            try {
                this.readChunks(new ByteBufNetInput(data), count, false);
            } catch (Exception e0)    {
                e = e0;
            }

            if (data.isReadable())  { //there is data remaining, this (probably) means that there is sky light data available
                for (int i = 0; i < count; i++) {
                    this.cubes.get(i).section(null);
                }
                this.readChunks(new ByteBufNetInput(data.readerIndex(0)), count, true);
            } else {
                PUnsafe.throwException(e);
            }
        }

        for (int i = 0; i < count; i++) {
            int numTiles = in.readInt();
            List<CompoundTag> tags = new ArrayList<>(numTiles);
            for (int j = 0; j < numTiles; j++)  {
                tags.add(NetUtil.readNBT(in));
            }
            this.cubes.get(0).tileEntities(tags);
        }
    }

    protected void readChunks(NetInput in, int count, boolean skyLight) throws IOException{
        boolean[] isEmpty = new boolean[count];
        boolean[] hasStorage = new boolean[count];
        boolean[] hasCustomBiomeMap = new boolean[count];

        //read flags
        for (int i = 0; i < count; i++) {
            int flags = in.readUnsignedByte();
            boolean isNull = this.cubes.get(i) == null;
            isEmpty[i] = (flags & 1) != 0 || isNull;
            hasStorage[i] = (flags & 2) != 0 && !isNull;
            hasCustomBiomeMap[i] = (flags & 4) != 0 && !isNull;
        }

        //allocate sections
        for (int i = 0; i < count; i++) {
            if (hasStorage[i]) {
                this.cubes.get(i).section(new Section(skyLight));
            }
        }

        //blocks
        for (int i = 0; i < count; i++) {
            if (!isEmpty[i]) {
                this.cubes.get(i).section().getBlocks().read(in);
            }
            }

        //block light
        for (int i = 0; i < count; i++) {
            if (hasStorage[i]) {
                in.readBytes(this.cubes.get(i).section().getBlockLight().getData());
            }
        }

        //sky light
        if (skyLight) {
            for (int i = 0; i < count; i++) {
                if (hasStorage[i]) {
                    in.readBytes(this.cubes.get(i).section().getSkyLight().getData());
                }
            }
        }

        //heightmaps
        //this data isn't actually used anywhere, but we need to read it anyway
        for (int i = 0; i < count; i++) {
            if (!isEmpty[i]) {
                in.readBytes(HEIGHTMAP_SKIP_BUFFER);
            }
        }

        //biomes
        //this data isn't actually used anywhere, but we need to read it anyway
        for (int i = 0; i < count; i++) {
            if (hasCustomBiomeMap[i]) {
                in.readBytes(CUSTOM_BIOMES_SKIP_BUFFER);
            }
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginProtocol getProtocol() {
        return CubicChunksProtocol.INSTANCE;
    }

    public static class Handler implements PacketHandler<CubesPacket>   {
        @Override
        public void handle(@NonNull Player player, @NonNull CubesPacket packet) {
            packet.cubes().forEach(player::putCube);
        }
    }
}
