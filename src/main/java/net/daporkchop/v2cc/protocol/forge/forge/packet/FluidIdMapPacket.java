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

package net.daporkchop.v2cc.protocol.forge.forge.packet;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.common.function.io.IOBiConsumer;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.v2cc.protocol.PluginPacket;
import net.daporkchop.v2cc.protocol.PluginProtocol;
import net.daporkchop.v2cc.protocol.forge.forge.ForgeProtocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
@Getter
public class FluidIdMapPacket extends PluginPacket {
    protected Map<String, Integer> fluidIds;
    protected Set<String> defaultFluids;

    @Override
    public void read(NetInput in) throws IOException {
        int size = in.readInt();

        this.fluidIds = new HashMap<>();
        for (int i = 0; i < size; i++) {
            this.fluidIds.put(in.readString(), in.readInt());
        }

        this.defaultFluids = new HashSet<>();
        if (in.available() > 0) { //this field may be absent
            for (int i = 0; i < size; i++) {
                this.defaultFluids.add(in.readString());
            }
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        checkState(this.fluidIds.size() == this.defaultFluids.size(), "fluidIds (%d) must be the same size as defaultFluids (%d)!", this.fluidIds.size(), this.defaultFluids.size());

        out.writeInt(this.fluidIds.size());

        this.fluidIds.forEach((IOBiConsumer<String, Integer>) (name, id) -> {
            out.writeString(name);
            out.writeInt(id);
        });

        this.defaultFluids.forEach((IOConsumer<String>) out::writeString);
    }

    @Override
    public PluginProtocol getProtocol() {
        return ForgeProtocol.INSTANCE;
    }
}
