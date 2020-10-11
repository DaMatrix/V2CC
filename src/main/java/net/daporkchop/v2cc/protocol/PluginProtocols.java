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

package net.daporkchop.v2cc.protocol;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.v2cc.protocol.forge.fml.FMLProtocol;
import net.daporkchop.v2cc.protocol.forge.fmlhs.FMLHSProtocol;
import net.daporkchop.v2cc.protocol.forge.fmlmp.FMLMPProtocol;
import net.daporkchop.v2cc.protocol.forge.forge.ForgeProtocol;
import net.daporkchop.v2cc.protocol.minecraft.register.RegisterProtocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Registry class for plugin messaging protocols.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PluginProtocols {
    protected final Map<String, PluginProtocol> PROTOCOLS = new HashMap<>();
    protected final Set<PluginProtocol> DEFAULT_PROTOCOLS = new HashSet<>();

    static {
        register(FMLProtocol.INSTANCE);
        register(FMLHSProtocol.INSTANCE);
        register(FMLMPProtocol.INSTANCE);
        register(ForgeProtocol.INSTANCE);
        register(RegisterProtocol.INSTANCE);
    }

    protected void register(@NonNull PluginProtocol protocol) {
        checkArg(PROTOCOLS.putIfAbsent(protocol.channelName(), protocol) == null, "duplicate plugin channel name: \"%s\"", protocol.channelName());
    }

    static {
        registerDefault(RegisterProtocol.INSTANCE);
    }

    protected void registerDefault(@NonNull PluginProtocol protocol) {
        checkArg(DEFAULT_PROTOCOLS.add(protocol), "duplicate default plugin protocol: %s", protocol);
    }

    /**
     * Gets a {@link PluginProtocol} by its channel name.
     *
     * @param channelName the channel name
     * @return the {@link PluginProtocol}
     * @throws IllegalArgumentException if the given channel name is unknown
     */
    public PluginProtocol protocolForName(@NonNull String channelName) {
        PluginProtocol protocol = PROTOCOLS.get(channelName);
        checkArg(protocol != null, "unknown plugin channel name: \"%s\"", channelName);
        return protocol;
    }

    /**
     * @return the protocols that should be registered by default
     */
    public Set<PluginProtocol> defaultProtocols() {
        return Collections.unmodifiableSet(DEFAULT_PROTOCOLS);
    }
}
