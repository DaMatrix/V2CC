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

package net.daporkchop.v2cc;

import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.ConnectionListener;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.SessionFactory;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.github.steveice10.packetlib.tcp.TcpConnectionListener;
import com.github.steveice10.packetlib.tcp.TcpServerSession;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Scanner;

import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class Proxy {
    protected final SessionFactory sessionFactory;
    protected final Server server;

    public static void main(String... args) {
        LOG.info("Starting V2CC v%s...", VERSION);

        new Proxy();
    }

    public Proxy()  {
        this.sessionFactory = new TcpSessionFactory(CONFIG.client.proxy.toJavaProxy());

        LOG.info("Starting server on %s:%d...", CONFIG.server.bind.address, CONFIG.server.bind.port);
        this.server = new Server(CONFIG.server.bind.address, CONFIG.server.bind.port, MinecraftProtocol.class, this.sessionFactory);
        this.server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        this.server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, CONFIG.server.compressionThreshold);
        this.server.bind(false);
        LOG.success("Server started.");

        new Scanner(System.in).nextLine();
        LOG.info("Stopping server...");
        this.server.close(true);
        LOG.success("Server stopped.");
    }
}
