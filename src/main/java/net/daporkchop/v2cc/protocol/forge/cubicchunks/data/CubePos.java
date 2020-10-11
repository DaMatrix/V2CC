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

package net.daporkchop.v2cc.protocol.forge.cubicchunks.data;

import com.github.steveice10.packetlib.io.NetInput;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class CubePos {
    private static int readSignedVarInt(NetInput in) throws IOException {
        int val = 0;
        int b = in.readUnsignedByte();
        boolean sign = ((b >> 6) & 1) != 0;

        val |= b & ((1 << 6) - 1);
        int shift = 6;
        while ((b & 0x80) != 0) {
            if (shift > Integer.SIZE) {
                throw new RuntimeException("VarInt too big");
            }
            b = in.readUnsignedByte();
            val |= (b & ((1 << 7) - 1)) << shift;
            shift += 7;
        }
        return sign ? ~val : val;
    }

    protected final int x;
    protected final int y;
    protected final int z;

    public CubePos(NetInput in) throws IOException {
        this(readSignedVarInt(in), readSignedVarInt(in), readSignedVarInt(in));
    }
}
