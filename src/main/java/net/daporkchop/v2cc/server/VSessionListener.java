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

import com.github.steveice10.mc.protocol.ServerListener;
import com.github.steveice10.mc.protocol.data.handshake.HandshakeIntent;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.v2cc.proxy.Player;

import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class VSessionListener extends ServerListener {
    @NonNull
    protected final Player player;

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        if (event.getPacket() instanceof HandshakePacket) {
            if (((HandshakePacket) event.getPacket()).getIntent() == HandshakeIntent.LOGIN) {
                //TODO: check for \0FML\0 host suffix and serve as a plain network proxy if the player is forge
                this.player.packetHandshake(event.getPacket());
                ((VServer) this.player.proxy().server()).playerCounter().increment();
            } else {
                this.player.serverSession().removeListener(this);
            }
        } else if (event.getPacket() instanceof LoginStartPacket) {
            this.player.packetLoginStart(event.getPacket());
            this.player.createClientAndConnect();
        }
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
    }

    @Override
    public void packetSent(PacketSentEvent event) {
    }

    @Override
    public void connected(ConnectedEvent event) {
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        if (event.getCause() != null)   {
            LOG.alert("vanilla disconnecting", event.getCause());
            if (this.player.clientSession() != null) {
                this.player.clientSession().disconnect(event.getReason(), event.getCause());
            }
        } else {
            LOG.info("vanilla disconnecting: %s", event.getReason());
            if (this.player.clientSession() != null) {
                this.player.clientSession().disconnect(event.getReason());
            }
        }
    }
}
