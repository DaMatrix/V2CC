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

package net.daporkchop.v2cc.protocol.forge.fml.packet;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.daporkchop.v2cc.protocol.PluginPacket;
import net.daporkchop.v2cc.protocol.PluginProtocol;
import net.daporkchop.v2cc.protocol.forge.fml.FMLProtocol;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
@Getter
public class OpenGuiPacket extends PluginPacket {
    protected int windowId;
    protected String modId;
    protected int modGuiId;
    protected int x;
    protected int y;
    protected int z;

    @Override
    public void read(NetInput in) throws IOException {
        this.windowId = in.readInt();
        this.modId = in.readString();
        this.modGuiId = in.readInt();
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeInt(this.windowId);
        out.writeString(this.modId);
        out.writeInt(this.modGuiId);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
    }

    @Override
    public PluginProtocol getProtocol() {
        return FMLProtocol.INSTANCE;
    }
}
