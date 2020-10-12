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

package net.daporkchop.v2cc.proxy;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Section;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.v2cc.Proxy;
import net.daporkchop.v2cc.client.CCClient;
import net.daporkchop.v2cc.client.CCSessionListener;
import net.daporkchop.v2cc.protocol.PluginProtocol;
import net.daporkchop.v2cc.protocol.PluginProtocols;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.Column;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.ColumnPos;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.Cube;
import net.daporkchop.v2cc.protocol.forge.cubicchunks.data.CubePos;
import net.daporkchop.v2cc.server.VSessionListener;
import net.daporkchop.v2cc.util.PacketHandler;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.v2cc.util.Constants.*;

/**
 * The actual player, a tunnel between a vanilla client and a Forge server.
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
public class Player {
    protected final Proxy proxy;

    protected final Session serverSession;
    @Setter(AccessLevel.NONE)
    protected Session clientSession;

    @NonNull
    @Setter(AccessLevel.NONE)
    protected Client client;

    @NonNull
    protected HandshakePacket packetHandshake;
    @NonNull
    protected LoginStartPacket packetLoginStart;

    protected final CCSessionListener clientListener = new CCSessionListener(this);
    protected final VSessionListener serverListener = new VSessionListener(this);

    protected final Map<String, PluginProtocol> pluginChannels = new HashMap<>();
    protected final Map<PluginProtocol, PacketHandler<Packet>> pluginHandlers = new IdentityHashMap<>();

    protected final Map<ColumnPos, Column> columns = new HashMap<>();
    protected final Map<CubePos, Cube> cubes = new HashMap<>();

    public Player(@NonNull Proxy proxy, @NonNull Session serverSession) {
        this.proxy = proxy;
        this.serverSession = serverSession;
    }

    public void createClientAndConnect() {
        this.client = new CCClient(this.proxy, this);
        (this.clientSession = this.client.getSession()).connect(false);
    }

    public void registerPlugin(@NonNull PluginProtocol protocol) {
        checkState(this.pluginChannels.putIfAbsent(protocol.channelName(), protocol) == null, "duplicate plugin channel name: \"%s\"", protocol.channelName());
        this.pluginHandlers.put(protocol, protocol.handler());
    }

    public void registerPluginByName(@NonNull String channelName) {
        this.registerPlugin(PluginProtocols.protocolForName(channelName));
    }

    public void putColumn(@NonNull Column column) {
        this.columns.put(column.pos(), column);
        this.serverSession.send(new ServerChunkDataPacket(new Chunk(
                column.x(), column.z(),
                EMPTY_CHUNK_SECTION_ARRAY,
                column.biomes(),
                new CompoundTag[0])));
    }

    public void putCube(@NonNull Cube cube) {
        this.cubes.put(cube.pos(), cube);

        if ((cube.y() & 0xF) == cube.y() //TODO: debug: only send cubes in vanilla range
            && this.columns.containsKey(new ColumnPos(cube.x(), cube.z()))) {
            //send chunk data packet with ground-up-continuous flag set to false (containing only a single section)
            Section[] sections = EMPTY_CHUNK_SECTION_ARRAY.clone();
            sections[cube.y()] = cube.section();
            this.serverSession.send(new ServerChunkDataPacket(new Chunk(
                    cube.x(), cube.z(),
                    sections,
                    null,
                    new CompoundTag[0] //TODO: tile entities
            )));
        }
    }
}
