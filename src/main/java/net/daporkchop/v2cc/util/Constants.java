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

import com.github.steveice10.mc.protocol.data.game.chunk.Section;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class Constants {
    public static final String VERSION = "0.0.1a";

    public static final Logger LOG = Logging.logger
            .redirectStdOut()
            .setLogAmount(LogAmount.DEBUG)
            .enableANSI();

    public static final String FLAG_PLAYER = "v2cc_player";

    public static final List<String> EXPECTED_SERVER_PLUGIN_CHANNELS = Collections.unmodifiableList(Arrays.asList(
            "FML",
            "FML|HS",
            "FML|MP",
            "FORGE",
            "cubicchunks"
    ));

    public static final Section[] EMPTY_CHUNK_SECTION_ARRAY = new Section[16];

    public <T> T unsafe_call(@NonNull Callable<T> function) {
        try {
            return function.call();
        } catch (Exception e) {
            PUnsafe.throwException(e);
            throw new AssertionError(e);
        }
    }
}
