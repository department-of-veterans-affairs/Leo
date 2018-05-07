/*
 *  Copyright 2010 United States Department of Veterans Affairs
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to convert to ASCII characters that are non-ascii. For instance, converting an ASCII8
 * string to ASCII7.
 *
 * @author Doug Redd
 */
public class AsciiService {

    /**
     * Map of ascii 7 characters.
     */
    private static Map<Character, Character> ascii7Map;
    /**
     * Map of ascii 8 characters.
     */
    private static Map<Character, Character> ascii8Map;
    /**
     * Logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

    /**
     * This map is focused on correcting OCR mistakes, so the mappings are
     * performed by visual similarity. Where nothing with visual similarity
     * is available then an underscore is used.
     * @return the ascii 7 map.
     */
    protected static Map<Character, Character> getASCII7Map() {
        if (ascii7Map == null) {
            ascii7Map = Collections
                    .unmodifiableMap(new HashMap<Character, Character>(
                            getASCII8Map()) {
                        private static final long serialVersionUID = 1L;

                        {
                            put((char) 128, '_'); // 128
                            put((char) 129, '_'); // 129
                            put((char) 130, ','); // 130
                            put((char) 131, 'f'); // 131
                            put((char) 132, ','); // 132
                            put((char) 133, '_'); // 133
                            put((char) 134, 't'); // 134
                            put((char) 135, 'c'); // 135
                            put((char) 136, '^'); // 136
                            put((char) 137, '_'); // 137
                            put((char) 138, 'S'); // 138
                            put((char) 139, '<'); // 139
                            put((char) 140, '_'); // 140
                            put((char) 141, '_'); // 141
                            put((char) 142, 'Z'); // 142
                            put((char) 143, '_'); // 143
                            put((char) 144, '_'); // 144
                            put((char) 145, '`'); // 145
                            put((char) 146, '\''); // 146
                            put((char) 147, '"'); // 147
                            put((char) 148, '"'); // 148
                            put((char) 149, '_'); // 149
                            put((char) 150, '-'); // 150
                            put((char) 151, '-'); // 151
                            put((char) 152, '~'); // 152
                            put((char) 153, '_'); // 153
                            put((char) 154, 's'); // 154
                            put((char) 155, '>'); // 155
                            put((char) 156, '_'); // 156
                            put((char) 157, '_'); // 157
                            put((char) 158, 'z'); // 158
                            put((char) 159, 'Y'); // 159
                            put((char) 160, ' '); // 160, non-breaking space
                            put((char) 161, 'i'); // 161
                            put((char) 162, 'c'); // 162
                            put((char) 163, 'L'); // 163
                            put((char) 164, '_'); // 164
                            put((char) 165, 'Y'); // 165
                            put((char) 166, '|'); // 166
                            put((char) 167, 'S'); // 167
                            put((char) 168, '_'); // 168
                            put((char) 169, 'c'); // 169
                            put((char) 170, '_'); // 170
                            put((char) 171, '<'); // 171
                            put((char) 172, '-'); // 172
                            put((char) 173, '-'); // 173
                            put((char) 174, 'r'); // 174
                            put((char) 175, '_'); // 175
                            put((char) 176, '_'); // 176
                            put((char) 177, '_'); // 177
                            put((char) 178, '_'); // 178
                            put((char) 179, '_'); // 179
                            put((char) 180, '\''); // 180
                            put((char) 181, 'u'); // 181
                            put((char) 182, '_'); // 182
                            put((char) 183, '_'); // 183
                            put((char) 184, ','); // 184
                            put((char) 185, '_'); // 185
                            put((char) 186, '_'); // 186
                            put((char) 187, '>'); // 187
                            put((char) 188, '_'); // 188
                            put((char) 189, '_'); // 189
                            put((char) 190, '_'); // 190
                            put((char) 191, '_'); // 191
                            put((char) 192, 'A'); // 192
                            put((char) 193, 'A'); // 193
                            put((char) 194, 'A'); // 194
                            put((char) 195, 'A'); // 195
                            put((char) 196, 'A'); // 196
                            put((char) 197, 'A'); // 197
                            put((char) 198, '_'); // 198
                            put((char) 199, 'C'); // 199
                            put((char) 200, 'E'); // 200
                            put((char) 201, 'E'); // 201
                            put((char) 202, 'E'); // 202
                            put((char) 203, 'E'); // 203
                            put((char) 204, 'I'); // 204
                            put((char) 205, 'I'); // 205
                            put((char) 206, 'I'); // 206
                            put((char) 207, 'I'); // 207
                            put((char) 208, 'D'); // 208
                            put((char) 209, 'N'); // 209
                            put((char) 210, 'O'); // 210
                            put((char) 211, 'O'); // 211
                            put((char) 212, 'O'); // 212
                            put((char) 213, 'O'); // 213
                            put((char) 214, 'O'); // 214
                            put((char) 215, 'x'); // 215
                            put((char) 216, '_'); // 216
                            put((char) 217, 'U'); // 217
                            put((char) 218, 'U'); // 218
                            put((char) 219, 'U'); // 219
                            put((char) 220, 'U'); // 220
                            put((char) 221, 'Y'); // 221
                            put((char) 222, '_'); // 222
                            put((char) 223, 'B'); // 223
                            put((char) 224, 'a'); // 224
                            put((char) 225, 'a'); // 225
                            put((char) 226, 'a'); // 226
                            put((char) 227, 'a'); // 227
                            put((char) 228, 'a'); // 228
                            put((char) 229, 'a'); // 229
                            put((char) 230, '_'); // 230
                            put((char) 231, 'c'); // 231
                            put((char) 232, 'e'); // 232
                            put((char) 233, 'e'); // 233
                            put((char) 234, 'e'); // 234
                            put((char) 235, 'e'); // 235
                            put((char) 236, 'i'); // 236
                            put((char) 237, 'i'); // 237
                            put((char) 238, 'i'); // 238
                            put((char) 239, 'i'); // 239
                            put((char) 240, 'd'); // 240
                            put((char) 241, 'n'); // 241
                            put((char) 242, 'o'); // 242
                            put((char) 243, 'o'); // 243
                            put((char) 244, 'o'); // 244
                            put((char) 245, 'o'); // 245
                            put((char) 246, 'o'); // 246
                            put((char) 247, '/'); // 247
                            put((char) 248, '_'); // 248
                            put((char) 249, 'u'); // 249
                            put((char) 250, 'u'); // 250
                            put((char) 251, 'u'); // 251
                            put((char) 252, 'u'); // 252
                            put((char) 253, 'y'); // 253
                            put((char) 254, '_'); // 254
                            put((char) 255, 'y'); // 255
                        }
                    });
        }
        return ascii7Map;
    }

    /**
     * Get the ascii 8 map.
     *
     * @return  the ascii 8 map.
     */
    protected static Map<Character, Character> getASCII8Map() {
        if (ascii8Map == null) {
            ascii8Map = Collections
                    .unmodifiableMap(new HashMap<Character, Character>() {
                        private static final long serialVersionUID = 8161868266083916944L;

                        {
                            put((char) 353, 's'); // 353
                            put((char) 381, 'Z'); // 381
                            put((char) 710, '^'); // 710
                            put((char) 8211, '-'); // 8211
                            put((char) 8212, '_'); // 8212
                            put((char) 8216, '\''); // 8216
                            put((char) 8217, '\''); // 8217
                            put((char) 8218, ','); // 8218
                            put((char) 8221, '"'); // 8221
                            put((char) 8225, 'c'); // 8225
                            put((char) 8230, '_'); // 8230
                            put((char) 8250, '>'); // 8250
                        }
                    });
        }
        return ascii8Map;
    }

    /**
     * Creates a new string, substituting ASCII-7 characters for all non-ASCII-7
     * characters in the argument string. Substitutions are based on visual
     * similarity, aimed at correcting mistakes by OCR software.
     *
     * @param text The string whose non-ASCII-7 characters will be replaced.
     * @return A new string with all non-ASCII-7 characters replaced with
     *         ASCII-7 characters.
     */
    public static String toASCII7(final String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char src = text.charAt(i);
            if ((int) src < 128) {
                sb.append(src);
            } else {
                Character newch = null;
                if ((int) src < 255) {
                    newch = getASCII7Map().get(src);
                } else {
                    newch = getASCII8Map().get(src);
                }
                if (newch == null) {
                    newch = '_';
                    String message ="No ASCII-8 mapping found for char " + src + " (" + ((int) src) + ")";
                    LOG.warn(LeoUtils.getHeaderManipulationSafeString(message));
                }
                sb.append(newch.charValue());
            }
        }
        return sb.toString();
    }

    /**
     * Creates a new string, substituting ASCII-7 characters for all non-ASCII-8
     * characters in the argument string. Substitutions are based on visual
     * similarity, aimed at correcting mistakes by OCR software.
     *
     * @param text The string whose non-ASCII-8 characters will be replaced.
     * @return A new string with all non-ASCII-8 characters replaced with
     *         ASCII-7 characters.
     */
    public static String toASCII8(final String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char src = text.charAt(i);
            if ((int) src < 256) {
                sb.append(src);
            } else {
                Character newch = getASCII8Map().get(src);
                if (newch == null) {
                    newch = '_';
                    String message ="No ASCII-8 mapping found for char " + src + " (" + ((int) src) + ")"; 
                    LOG.warn(LeoUtils.getHeaderManipulationSafeString(message));
                }
                sb.append(newch.charValue());
            }
        }
        return sb.toString();
    }
}
