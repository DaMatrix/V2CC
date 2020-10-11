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

package net.daporkchop.v2cc.client.fml;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.crypt.PacketEncryption;
import com.github.steveice10.packetlib.packet.PacketHeader;
import com.github.steveice10.packetlib.packet.PacketProtocol;

/**
 * Abstract implementation of {@link PacketProtocol} for plugin channels.
 *
 * @author DaPorkchop_
 */
public abstract class FMLProtocol extends PacketProtocol {
    public FMLProtocol() {
        this.registerPackets();
    }

    /**
     * Actually registers the packets used by this protocol.
     */
    protected abstract void registerPackets();

    @Override
    public final String getSRVRecordPrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketHeader getPacketHeader() {
        return FMLPacketHeader.INSTANCE;
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
}