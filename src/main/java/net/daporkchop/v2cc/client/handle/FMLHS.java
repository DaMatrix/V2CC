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

package net.daporkchop.v2cc.client.handle;

import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import lombok.NonNull;
import net.daporkchop.v2cc.protocol.forge.fmlhs.FMLHSProtocol;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.ModListPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.client.ClientHandshakeAckPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.client.ClientHelloPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.RegistryDataPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.ServerHandshakeAckPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.ServerHelloPacket;
import net.daporkchop.v2cc.protocol.forge.forge.ForgeProtocol;
import net.daporkchop.v2cc.protocol.minecraft.register.packet.RegisterPacket;
import net.daporkchop.v2cc.proxy.Player;
import net.daporkchop.v2cc.util.PacketHandler;

import java.util.HashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.v2cc.util.Constants.*;

/**
 * The entire FML handshake sequence implemented as a bunch of hacky enum constants because I don't want to create dozens of files.
 * <p>
 * Honestly I should probably make this into a class-based handler lookup table...
 *
 * @author DaPorkchop_
 */
//TODO: this whole thing should be redone in a way that's less hacky
public enum FMLHS implements PacketHandler<MinecraftPacket> {
    REGISTER_CHANNELS {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            /*if (pck instanceof ServerPluginMessagePacket)   {
                ServerPluginMessagePacket packet = (ServerPluginMessagePacket) pck;
                checkState("REGISTER".equals(packet.getChannel()), "Received plugin message on unexpected channel: \"%s\"", packet.getChannel());

                String payload = new String(packet.getData(), StandardCharsets.US_ASCII);
                Set<String> channels = new HashSet<>(Arrays.asList(payload.split("\0")));
                checkState(channels.equals(this.expectedChannels), "invalid input channels (received=%s, expected=%s)", channels, this.expectedChannels);

                player.registerPlugin(FMLHSProtocol.INSTANCE);
                return SERVER_HELLO;
            }*/
            if (pck instanceof RegisterPacket) {
                RegisterPacket packet = (RegisterPacket) pck;

                checkState(packet.channels().containsAll(EXPECTED_SERVER_PLUGIN_CHANNELS), "register channels (received=%s)", packet.channels());

                player.registerPlugin(FMLHSProtocol.INSTANCE);
                player.registerPlugin(ForgeProtocol.INSTANCE);
                return SERVER_HELLO;
            }
            return null;
        }
    },
    SERVER_HELLO {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            checkArg(pck instanceof ServerHelloPacket, pck);
            ServerHelloPacket packet = (ServerHelloPacket) pck;

            player.clientSession().send(new RegisterPacket(EXPECTED_SERVER_PLUGIN_CHANNELS));
            player.clientSession().send(new ClientHelloPacket(packet.serverProtocolVersion()));

            Map<String, String> mods = new HashMap<>();
            mods.put("cubicchunks", player.proxy().config().debug.cubicChunksVersion);
            player.clientSession().send(new ModListPacket(mods));
            return MOD_LIST;
        }
    },
    MOD_LIST {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            checkArg(pck instanceof ModListPacket, pck);
            ModListPacket packet = (ModListPacket) pck;

            //let's just assume the server has a compatible mod list
            LOG.info("Backend server mods: %s", packet.mods());

            player.clientSession().send(new ClientHandshakeAckPacket(ClientHandshakeAckPacket.State.WAITINGSERVERDATA));
            return REGISTRY;
        }
    },
    REGISTRY {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            checkArg(pck instanceof RegistryDataPacket, pck);
            RegistryDataPacket packet = (RegistryDataPacket) pck;

            if (packet.hasMore())   {
                return null; //continue waiting for the last registry packet
            }

            player.clientSession().send(new ClientHandshakeAckPacket(ClientHandshakeAckPacket.State.WAITINGSERVERCOMPLETE));
            return ACK_0;
        }
    },
    ACK_0 {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            checkArg(pck instanceof ServerHandshakeAckPacket, pck);
            ServerHandshakeAckPacket packet = (ServerHandshakeAckPacket) pck;
            checkArg(packet.phase() == ServerHandshakeAckPacket.State.WAITINGCACK, packet.phase());

            player.clientSession().send(new ClientHandshakeAckPacket(ClientHandshakeAckPacket.State.PENDINGCOMPLETE));
            return ACK_1;
        }
    },
    ACK_1 {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            checkArg(pck instanceof ServerHandshakeAckPacket, pck);
            ServerHandshakeAckPacket packet = (ServerHandshakeAckPacket) pck;
            checkArg(packet.phase() == ServerHandshakeAckPacket.State.COMPLETE, packet.phase());

            player.clientSession().send(new ClientHandshakeAckPacket(ClientHandshakeAckPacket.State.COMPLETE));
            return DONE;
        }
    },
    DONE {
        @Override
        public PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck) {
            return null; //no-op
        }
    };

    @Override
    public abstract PacketHandler<?> handle(@NonNull Player player, @NonNull MinecraftPacket pck);
}
