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

package net.daporkchop.v2cc.client.fml;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.PacketHeader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * This is probably not even needed, but it can't hurt to implement the whole API.
 * <p>
 * ...
 * <p>
 * "implement" my ass. who am i kidding? this is just useless lol
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class FMLPacketHeader implements PacketHeader {
    public static final FMLPacketHeader INSTANCE = new FMLPacketHeader();

    @Override
    public boolean isLengthVariable() {
        return false;
    }

    @Override
    public int getLengthSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLengthSize(int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readLength(NetInput in, int available) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeLength(NetOutput out, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readPacketId(NetInput in) throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public void writePacketId(NetOutput out, int packetId) throws IOException {
        out.writeByte(packetId);
    }
}
