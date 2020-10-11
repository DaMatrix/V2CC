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

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.crypt.PacketEncryption;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.util.PacketHandler;

import java.util.IdentityHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public abstract class PluginProtocol extends PacketProtocol {
    @Getter
    protected final String channelName;

    protected final Map<Class<? extends Packet>, PacketHandler<?>> handlers = new IdentityHashMap<>();

    public PluginProtocol(@NonNull String channelName) {
        this.channelName = channelName;

        this.registerPackets();
    }

    /**
     * Actually registers the packets used by this protocol.
     */
    protected abstract void registerPackets();

    /**
     * The initial packet handler to use for a new connection using this protocol.
     */
    public PacketHandler<Packet> handler() {
        return new DefaultHandler();
    }

    public final <P extends Packet> void register(int id, Class<P> packet, @NonNull PacketHandler<P> handler) {
        super.register(id, packet);
        this.handlers.put(packet, handler);
    }

    public final <P extends Packet> void registerIncoming(int id, Class<P> packet, @NonNull PacketHandler<P> handler) {
        super.registerIncoming(id, packet);
        this.handlers.put(packet, handler);
    }

    @Override
    public final String getSRVRecordPrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketEncryption getEncryption() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void newClientSession(Client client, Session session) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void newServerSession(Server server, Session session) {
        throw new UnsupportedOperationException();
    }

    protected class DefaultHandler implements PacketHandler<Packet> {
        @Override
        public void handle(@NonNull Player player, @NonNull Packet packet) {
            PacketHandler<?> handler = PluginProtocol.this.handlers.get(packet.getClass());
            if (handler == null) {
                LOG.warn("not handling packet: %s", packet);
            } else {
                handler.handle(player, uncheckedCast(packet));
            }
        }
    }
}
