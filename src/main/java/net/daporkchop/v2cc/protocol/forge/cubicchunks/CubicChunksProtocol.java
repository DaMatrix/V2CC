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

package net.daporkchop.v2cc.protocol.forge.cubicchunks;

import net.daporkchop.v2cc.protocol.forge.AbstractForgeProtocol;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.ColumnPacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.CubeBlockChangePacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.CubeSkyLightUpdatesPacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.CubesPacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.CubicWorldDataPacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.HeightmapUpdatePacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.UnloadColumnPacket;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.packet.server.UnloadCubePacket;

/**
 * @author DaPorkchop_
 */
public class CubicChunksProtocol extends AbstractForgeProtocol {
    public static final CubicChunksProtocol INSTANCE = new CubicChunksProtocol();

    protected CubicChunksProtocol() {
        super("cubicchunks");
    }

    @Override
    protected void registerPackets() {
        this.registerIncoming(0, CubesPacket.class, new CubesPacket.Handler());
        this.registerIncoming(1, ColumnPacket.class, new ColumnPacket.Handler());
        this.registerIncoming(2, UnloadColumnPacket.class);
        this.registerIncoming(3, UnloadCubePacket.class);
        this.registerIncoming(4, CubeBlockChangePacket.class);
        this.registerIncoming(5, CubicWorldDataPacket.class);
        this.registerIncoming(6, HeightmapUpdatePacket.class);
        this.registerIncoming(7, CubeSkyLightUpdatesPacket.class);
    }
}
