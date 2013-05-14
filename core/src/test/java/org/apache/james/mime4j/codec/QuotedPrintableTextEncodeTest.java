/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mime4j.codec;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.Charsets;
import org.apache.james.mime4j.io.InputStreams;
import org.apache.james.mime4j.util.ContentUtil;
import org.junit.Assert;
import org.junit.Test;

public class QuotedPrintableTextEncodeTest {

    @Test
    public void testEscapedSoftBreak() throws Exception {
        byte[] content = new byte[500];
        Arrays.fill(content, (byte) 0x18);
        byte[] expected = new byte[1557];
        int index = 0;
        for (int l = 0; l < 20; l++) {
            for (int i = 0; i < 25; i++) {
                expected[index++] = '=';
                expected[index++] = '1';
                expected[index++] = '8';
            }
            if (l < 19) {
                expected[index++] = '=';
                expected[index++] = '\r';
                expected[index++] = '\n';
            }
        }
        check(content, expected);
    }

    @Test
    public void testPlainAsciiSoftBreak() throws Exception {
        byte[] content = new byte[500];
        Arrays.fill(content, (byte) 0x29);
        byte[] expected = new byte[518];
        Arrays.fill(expected, (byte) 0x29);
        expected[75] = '=';
        expected[76] = '\r';
        expected[77] = '\n';
        expected[153] = '=';
        expected[154] = '\r';
        expected[155] = '\n';
        expected[231] = '=';
        expected[232] = '\r';
        expected[233] = '\n';
        expected[309] = '=';
        expected[310] = '\r';
        expected[311] = '\n';
        expected[387] = '=';
        expected[388] = '\r';
        expected[389] = '\n';
        expected[465] = '=';
        expected[466] = '\r';
        expected[467] = '\n';
        check(content, expected);
    }

    @Test
    public void testPlainASCII() throws Exception {
        checkRoundtrip("Thisisaverysimplemessage.Thisisaverysimplemessage.Thisisaverysimplemessage." +
                "Thisisaverysimplemessage.Thisisaverysimplemessage.Thisisaverysimplemessage." +
                "Thisisaverysimplemessage.Thisisaverysimplemessage.Thisisaverysimplemessage." +
                "Thisisaverysimplemessage.Thisisaverysimplemessage.Thisisaverysimplemessage.");
    }

    @Test
    public void testEncodeSpace() throws Exception {
        checkRoundtrip("                 A");
    }

    @Test
    public void testLetterEncoding() throws Exception {
        for (byte b = 0; b < Byte.MAX_VALUE; b++) {
            byte[] content = {b};
            // White space is only escaped when followed by CRLF
            if (b != 10 && b != 13 && b != 32 && b != 9) {
                checkRoundtrip(content);
            }
        }
    }

    @Test
    public void testCRLFShouldResetLineCount() throws Exception {
        StringBuilder buffer = new StringBuilder(4096);
        for (int i = 0; i < 1000; i++) {
            buffer.append("Hugo\r\n");
        }
        String longLine = buffer.toString();
        check(longLine, longLine);
    }

    @Test
    public void testDontEscapeLF() throws Exception {
        check("Ready\nFor\n", "Ready\nFor\n");
    }

    @Test
    public void testDontEscapeCR() throws Exception {
        check("Ready\rFor\r", "Ready\rFor\r");
    }

    @Test
    public void testEscapeSpaceAtLineEnd() throws Exception {
        check("      \r\n", "     =20\r\n");
    }

    @Test
    public void testDontEscapeSpaceBeforeLineEnd() throws Exception {
        check("      ", "      ");
    }

    @Test
    public void testDontEscapeTabsBeforeLineEnd() throws Exception {
        check("\t\t\t\t", "\t\t\t\t");
    }

    @Test
    public void testDontWhiteSpaceBeforeLineEnd() throws Exception {
        check("  \t\t  \t", "  \t\t  \t");
    }

    private void checkRoundtrip(String content) throws Exception {
        checkRoundtrip(content, Charsets.US_ASCII);
    }

    private void checkRoundtrip(String content, Charset charset) throws Exception {
        checkRoundtrip(charset.encode(content).array());
    }

    private void checkRoundtrip(byte[] content) throws Exception {
        InputStream in = InputStreams.create(content);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EncoderUtil.encodeQ(in, out);
        // read back through decoder
        in = new QuotedPrintableInputStream(InputStreams.create(out.toByteArray()));
        out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        assertEquals(content, out.toByteArray());
    }

    private void check(String content, String expected) throws Exception {
        check(ContentUtil.toAsciiByteArray(content), ContentUtil.toAsciiByteArray(expected));
    }

    private void check(byte[] content, byte[] expected) throws Exception {
        InputStream in = InputStreams.create(content);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EncoderUtil.encodeQ(in, out);
        assertEquals(expected, out.toByteArray());
    }

    private void assertEquals(byte[] expected, byte[] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < actual.length; i++) {
            Assert.assertEquals("Mismatch@" + i, expected[i], actual[i]);
        }
    }
}
