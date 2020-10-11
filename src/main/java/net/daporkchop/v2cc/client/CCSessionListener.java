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

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.ClientListener;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.handshake.HandshakeIntent;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.v2cc.client.handle.FMLHS;
import net.daporkchop.v2cc.protocol.PluginProtocols;
import net.daporkchop.v2cc.protocol.minecraft.register.RegisterProtocol;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.proxy.ProxyProtocol;
import net.daporkchop.v2cc.util.PacketHandler;

import java.io.IOException;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class CCSessionListener extends ClientListener {
    @NonNull
    protected final Player player;

    protected PacketHandler<?> handler = FMLHS.REGISTER_CHANNELS;

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        super.packetReceived(event);

        Packet pck = event.getPacket();

        if (pck instanceof ServerPluginMessagePacket) { //decode plugin message
            ServerPluginMessagePacket packet = (ServerPluginMessagePacket) pck;
            PacketProtocol pluginProtocol = this.player.pluginChannels().get(packet.getChannel());
            if (pluginProtocol != null) {
                try {
                    NetInput in = new ByteBufNetInput(Unpooled.wrappedBuffer(packet.getData()));
                    (pck = pluginProtocol.createIncomingPacket(pluginProtocol.getPacketHeader().readPacketId(in))).read(in);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        LOG.debug("cc received packet: %s", pck);

        if (this.handler != null) {
            PacketHandler<?> nextHandler = this.handler.handle(this.player, uncheckedCast(pck));
            if (nextHandler != null) {
                this.handler = nextHandler;
            }
        }
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
        //check if the packet is a plugin message and if so, encode it as such
        this.player.pluginChannels().forEach((channel, protocol) -> {
            if (protocol.hasOutgoing(event.getPacket().getClass())) {
                LOG.debug("cc sent packet: %s", event.<Packet>getPacket());

                ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                try {
                    //encode packet body
                    NetOutput out = new ByteBufNetOutput(buf);
                    protocol.getPacketHeader().writePacketId(out, protocol.getOutgoingId(event.getPacket().getClass()));
                    event.getPacket().write(out);

                    //copy data into array
                    byte[] arr = new byte[buf.readableBytes()];
                    buf.readBytes(arr);
                    event.setPacket(new ClientPluginMessagePacket(channel, arr));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    buf.release();
                }
            }
        });
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        Packet pck = event.getPacket();
        if (!(pck instanceof ClientPluginMessagePacket && this.player.pluginChannels().containsKey(((ClientPluginMessagePacket) pck).getChannel()))) {
            LOG.debug("cc sent packet: %s", pck);
        }
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
                this.player.proxy().config().client.backend.host + "\0FML\0", //TODO: support bungeecord IP forwarding, will require another host suffix
                this.player.proxy().config().client.backend.port,
                HandshakeIntent.LOGIN));

        protocol.setSubProtocol(SubProtocol.LOGIN, true, event.getSession());
        event.getSession().send(new LoginStartPacket(event.getSession().<GameProfile>getFlag(MinecraftConstants.PROFILE_KEY).getName()));

        PluginProtocols.defaultProtocols().forEach(this.player::registerPlugin);
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        if (event.getCause() != null) {
            LOG.alert("cc disconnected", event.getCause());
            this.player.serverSession().disconnect(event.getReason(), event.getCause());
        } else {
            LOG.info("cc disconnected: %s", event.getReason());
            this.player.serverSession().disconnect(event.getReason());
        }
    }
}
