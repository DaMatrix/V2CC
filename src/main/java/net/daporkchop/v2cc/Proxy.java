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

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.SessionFactory;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.oio.appendable.PAppendable;
import net.daporkchop.lib.binary.oio.reader.UTF8FileReader;
import net.daporkchop.lib.binary.oio.writer.UTF8FileWriter;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.config.PConfig;
import net.daporkchop.lib.config.decoder.PorkConfigDecoder;
import net.daporkchop.v2cc.server.VServer;
import net.daporkchop.v2cc.util.Conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import static net.daporkchop.v2cc.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class Proxy {
    protected final File root;
    protected final Conf config;

    protected final SessionFactory sessionFactory;
    protected final Server server;

    public static void main(String... args) {
        LOG.info("Starting V2CC v%s...", VERSION);

        new Proxy(new File("."));
    }

    public Proxy(@NonNull File root)  {
        this.root = root;
        this.config = this.loadConfig();

        this.sessionFactory = new TcpSessionFactory(this.config.client.proxy.toJavaProxy());
        this.server = new VServer(this);

        new Scanner(System.in).nextLine();
        this.server.close(true);
    }

    protected Conf loadConfig() {
        PConfig manager = new PConfig(new PorkConfigDecoder());
        Conf conf;
        try {
            LOG.info("Loading config...");
            File configFile = new File(this.root, "v2cc.cfg");
            if (PFiles.checkFileExists(configFile)) {
                conf = manager.load(Conf.class, configFile);
            } else { //config file doesn't exist, fall back to default options
                conf = new Conf();
            }
            manager.save(conf, configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return conf;
    }
}
