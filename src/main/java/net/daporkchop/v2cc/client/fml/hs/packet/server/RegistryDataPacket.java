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

package net.daporkchop.v2cc.client.fml.hs.packet.server;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.common.function.io.IOBiConsumer;
import net.daporkchop.lib.common.function.io.IOConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
@Getter
public class RegistryDataPacket extends MinecraftPacket {
    protected boolean hasMore;
    protected String name;
    protected Map<String, Integer> ids;
    protected Set<String> substitutions;
    protected Set<String> dummied; //no, this isn't a typo

    @Override
    public void read(NetInput in) throws IOException {
        this.hasMore = in.readBoolean();
        this.name = in.readString();

        this.ids = new HashMap<>();
        for (int i = in.readVarInt() - 1; i >= 0; i--) {
            this.ids.put(in.readString(), in.readVarInt());
        }

        this.substitutions = new HashSet<>();
        for (int i = in.readVarInt() - 1; i >= 0; i--) {
            this.substitutions.add(in.readString());
        }

        this.dummied = new HashSet<>();
        if (in.available() > 0) { //may be absent
            for (int i = in.readVarInt() - 1; i >= 0; i--) {
                this.dummied.add(in.readString());
            }
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeBoolean(this.hasMore);
        out.writeString(this.name);

        out.writeVarInt(this.ids.size());
        this.ids.forEach((IOBiConsumer<String, Integer>) (name, id) -> {
            out.writeString(name);
            out.writeVarInt(id);
        });

        out.writeVarInt(this.substitutions.size());
        this.substitutions.forEach((IOConsumer<String>) out::writeString);

        out.writeVarInt(this.dummied.size());
        this.dummied.forEach((IOConsumer<String>) out::writeString);
    }
}
