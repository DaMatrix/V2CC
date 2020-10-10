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

package net.daporkchop.v2cc.server;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerBoundEvent;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.event.server.ServerClosingEvent;
import com.github.steveice10.packetlib.event.server.ServerListener;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.v2cc.Proxy;
import net.daporkchop.v2cc.proxy.ProxyProtocol;

import java.util.concurrent.atomic.LongAdder;

import static net.daporkchop.v2cc.util.Constants.*;

/**
 * Extension of {@link Server} which allows me to pass a {@link Proxy} to the {@link PacketProtocol} constructor.
 *
 * @author DaPorkchop_
 */
@Getter
public class VServer extends Server implements ServerListener, ServerInfoBuilder {
    protected final Proxy proxy;

    protected final LongAdder playerCounter = new LongAdder(); //doesn't really need to be super atomic, but why not lol

    public VServer(@NonNull Proxy proxy) {
        super(proxy.config().server.bind.address, proxy.config().server.bind.port, null, proxy.sessionFactory());

        this.proxy = proxy;

        LOG.info("Starting server on %s:%d...", this.proxy.config().server.bind.address, this.proxy.config().server.bind.port);
        this.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        this.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, this.proxy.config().server.compressionThreshold);
        this.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, this);
        this.addListener(this);
        this.bind(false);
    }

    @Override
    public Class<? extends PacketProtocol> getPacketProtocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketProtocol createPacketProtocol() {
        return new ProxyProtocol(this.proxy);
    }

    //ServerListener methods

    @Override
    public void serverBound(ServerBoundEvent event) {
        LOG.success("Server started.");
    }

    @Override
    public void serverClosing(ServerClosingEvent event) {
        LOG.info("Closing server...");
    }

    @Override
    public void serverClosed(ServerClosedEvent event) {
        LOG.success("Server closed.");
    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        event.getSession().addListener(new VSessionListener(event.getSession().getFlag(FLAG_PLAYER)));
    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
        if (((MinecraftProtocol) event.getSession().getPacketProtocol()).getSubProtocol() != SubProtocol.STATUS) {
            this.playerCounter.decrement();
        }
    }

    //ServerInfoBuilder

    @Override
    public ServerStatusInfo buildInfo(Session session) { //TODO: make a thing to periodically ping the backend server and return that instead
        return new ServerStatusInfo(
                new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                new PlayerInfo(this.proxy.config().server.maxPlayers, this.playerCounter.intValue(), new GameProfile[0]),
                "A V2CC proxy",
                null,
                true
        );
    }
}
