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

package net.daporkchop.v2cc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.daporkchop.lib.config.Config;

import java.net.InetSocketAddress;

/**
 * V2CC's configuration.
 *
 * @author DaPorkchop_
 */
@ToString
public class Conf {
    @Config.Comment({
            "Options for V2CC's client.",
            "The client initiates connections to the Cubic Chunks backend server."
    })
    public Client client = new Client();

    @Config.Comment({
            "Options for V2CC's server.",
            "The server accepts connections from vanilla Minecraft clients."
    })
    public Server server = new Server();

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static final class Client {
        @Config.Comment("The backend server that the client will connect to.")
        public Backend backend = new Backend();

        @Config.Comment("An optional SOCKS5 proxy that will be used for connecting to the backend server.")
        public Proxy proxy = new Proxy();

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @ToString
        public static final class Backend {
            @Config.Comment("The hostname of the backend server.")
            public String host = "mc.example.com";

            @Config.Comment("The port number of the backend server.")
            public int port = 25565;
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @ToString
        public static final class Proxy {
            @Config.Comment("Whether or not to use a proxy.")
            public boolean use = false;

            @Config.Comment("The hostname of the proxy.")
            public String host = "localhost";

            @Config.Comment("The port number of the proxy.")
            public int port = 1080;

            public java.net.Proxy toJavaProxy() {
                if (this.use)   {
                    return new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(this.host, this.port));
                } else {
                    return null;
                }
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static final class Server {
        public Bind bind = new Bind();

        @Config.Comment({
                "The maximum number of players that may connect to the proxy at once.",
                "If ≤ 0, no limit will be enforced."
        })
        public int maxPlayers = 20;

        @Config.Comment({
                "The maximum size (in bytes) of a packet before compressing it.",
                "Packets smaller than this will be sent uncompressed.",
                "Set to -1 to completely disable packet compression."
        })
        public int compressionThreshold = 256;

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @ToString
        public static final class Bind {
            @Config.Comment({
                    "The address to bind to.",
                    "This should always be 0.0.0.0 unless you know EXACTLY what you're doing!"
            })
            public String address = "0.0.0.0";

            @Config.Comment("The TCP port number to bind to.")
            public int port = 25566;
        }
    }
}
