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

package net.daporkchop.v2cc.client;

import com.github.steveice10.mc.protocol.ClientListener;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.handshake.HandshakeIntent;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.proxy.ProxyProtocol;

import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class CCSessionListener extends ClientListener {
    @NonNull
    protected final Player player;

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        LOG.debug("cc received packet: %s", event.<Packet>getPacket());

        super.packetReceived(event);
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        LOG.debug("cc sent packet: %s", event.<Packet>getPacket());
    }

    @Override
    public void connected(ConnectedEvent event) {
        ProxyProtocol protocol = (ProxyProtocol) event.getSession().getPacketProtocol();
        if (protocol.getSubProtocol() != SubProtocol.LOGIN) {
            super.connected(event);
            return;
        }

        LOG.info("cc connecting: %s", protocol.getSubProtocol());

        protocol.setSubProtocol(SubProtocol.HANDSHAKE, true, event.getSession());
        event.getSession().send(new HandshakePacket(
                MinecraftConstants.PROTOCOL_VERSION,
                this.player.proxy().config().client.backend.host + "\0FML\0",
                this.player.proxy().config().client.backend.port,
                HandshakeIntent.LOGIN));
        protocol.setSubProtocol(SubProtocol.LOGIN, true, event.getSession());
        event.getSession().send(this.player.packetLoginStart());
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        if (event.getCause() != null)   {
            LOG.alert("cc disconnected", event.getCause());
            this.player.serverSession().disconnect(event.getReason(), event.getCause());
        } else {
            LOG.info("cc disconnected: %s", event.getReason());
            this.player.serverSession().disconnect(event.getReason());
        }
    }
}
