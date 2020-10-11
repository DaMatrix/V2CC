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

package net.daporkchop.v2cc.protocol.forge.fmlhs;

import net.daporkchop.v2cc.protocol.forge.AbstractForgeProtocol;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.ModListPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.client.ClientHandshakeAckPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.client.ClientHelloPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.HandshakeResetPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.RegistryDataPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.ServerHandshakeAckPacket;
import net.daporkchop.v2cc.protocol.forge.fmlhs.packet.server.ServerHelloPacket;

/**
 * @author DaPorkchop_
 */
public class FMLHSProtocol extends AbstractForgeProtocol {
    public static final FMLHSProtocol INSTANCE = new FMLHSProtocol();

    protected FMLHSProtocol() {
        super("FML|HS");
    }

    @Override
    protected void registerPackets() {
        this.registerIncoming(0, ServerHelloPacket.class);
        this.registerOutgoing(1, ClientHelloPacket.class);
        this.register(2, ModListPacket.class);
        this.registerIncoming(3, RegistryDataPacket.class);
        this.registerIncoming(254, HandshakeResetPacket.class);
        this.registerOutgoing(255, ClientHandshakeAckPacket.class);
        this.registerIncoming(255, ServerHandshakeAckPacket.class);
    }
}
