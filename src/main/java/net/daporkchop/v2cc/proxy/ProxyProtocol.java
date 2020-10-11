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

package net.daporkchop.v2cc.proxy;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.v2cc.Proxy;
import net.daporkchop.v2cc.client.CCClient;
import net.daporkchop.v2cc.client.CCSessionListener;

import static net.daporkchop.v2cc.util.Constants.*;

/**
 * Extension of {@link MinecraftProtocol} which creates a new player for incoming connections.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class ProxyProtocol extends MinecraftProtocol {
    @NonNull
    protected final Proxy proxy;

    public ProxyProtocol(@NonNull Proxy proxy, String username) {
        super(username);

        this.proxy = proxy;
    }

    /**
     * @deprecated only exists for debug purposes, should probably be removed later
     */
    @Deprecated
    public ProxyProtocol(@NonNull Proxy proxy, String username, String password) throws RequestException {
        super(username, password);

        this.proxy = proxy;
    }

    @Override
    public void newClientSession(Client clientIn, Session session) {
        CCClient client = (CCClient) clientIn;
        session.setFlag(FLAG_PLAYER, client.player());

        if (this.getProfile() != null) {
            session.setFlag(MinecraftConstants.PROFILE_KEY, this.getProfile());
            session.setFlag(MinecraftConstants.ACCESS_TOKEN_KEY, this.getAccessToken());
        }
        this.setSubProtocol(this.getSubProtocol(), true, session);

        session.addListener(new CCSessionListener(client.player()));
    }

    @Override
    public void newServerSession(Server server, Session session) {
        super.newServerSession(server, session);
        session.setFlag(FLAG_PLAYER, new Player(this.proxy, session));
    }

    @Override
    public void setSubProtocol(SubProtocol subProtocol, boolean client, Session session) {
        super.setSubProtocol(subProtocol, client, session);
    }
}
