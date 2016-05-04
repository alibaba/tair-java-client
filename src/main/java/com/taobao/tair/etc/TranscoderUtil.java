/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.etc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TranscoderUtil {
    private static final Log log = LogFactory.getLog(TranscoderUtil.class);

    public static byte[] encodeLong(long number) {
        byte[] rt = new byte[8];

        rt[7] = (byte) (number & 0xFF);
        rt[6] = (byte) ((number >> 8) & 0xFF);
        rt[5] = (byte) ((number >> 16) & 0xFF);
        rt[4] = (byte) ((number >> 24) & 0xFF);
        rt[3] = (byte) ((number >> 32) & 0xFF);
        rt[2] = (byte) ((number >> 40) & 0xFF);
        rt[1] = (byte) ((number >> 48) & 0xFF);
        rt[0] = (byte) ((number >> 56) & 0xFF);
        return rt;
    }

    public static long decodeLong(byte[] data) {
        long rv = 0;

        for (byte i : data) {
            rv = (rv << 8) | ((i < 0) ? (256 + i)
                                      : i);
        }

        return rv;
    }

    public static byte[] encodeInt(int number) {
        byte[] fg = new byte[4];

        fg[3] = (byte) (number & 0xFF);
        fg[2] = (byte) ((number >> 8) & 0xFF);
        fg[1] = (byte) ((number >> 16) & 0xFF);
        fg[0] = (byte) ((number >> 24) & 0xFF);
        return fg;
    }

    public static int decodeInt(byte[] data) {
        assert data.length <= 4 : "Too long to be an int (" + data.length + ") bytes";
        return (int) decodeLong(data);
    }

    public static int getInt(byte[] data, int offset) {
    	int rv = 0;
    	rv = ((data[offset+3] < 0) ? (256 + data[offset+3]) : data[offset+3]);
    	rv = (rv << 8) | ((data[offset+2] < 0) ? (256 + data[offset+2]) : data[offset+2]);
    	rv = (rv << 8) | ((data[offset+1] < 0) ? (256 + data[offset+1]) : data[offset+1]);
    	rv = (rv << 8) | ((data[offset] < 0) ? (256 + data[offset]) : data[offset]);
    	return rv;
    }

    public static byte[] encodeByte(byte in) {
        return new byte[] { in };
    }

    public static byte decodeByte(byte[] in) {
        assert in.length <= 1 : "Too long for a byte";

        byte rv = 0;

        if (in.length == 1) {
            rv = in[0];
        }

        return rv;
    }

    public static byte[] encodeBoolean(boolean b) {
        byte[] rv = new byte[1];

        rv[0] = (byte) (b ? '1'
                          : '0');
        return rv;
    }

    public static boolean decodeBoolean(byte[] in) {
        assert in.length == 1 : "Wrong length for a boolean";
        return in[0] == '1';
    }

    public static byte[] compress(byte[] in) {
        if (in == null) {
            throw new NullPointerException("Can't compress null");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream      gz  = null;

        try {
            gz = new GZIPOutputStream(bos);
            gz.write(in);
        } catch (IOException e) {
            throw new RuntimeException("IO exception compressing data", e);
        } finally {
            try {
                gz.close();
                bos.close();
            } catch (Exception e) {
                // should not happen
            }
        }

        byte[] rv = bos.toByteArray();

        if (log.isInfoEnabled()) {
            log.info("compressed value, size from [" + in.length + "] to [" + rv.length + "]");
        }

        return rv;
    }

    public static byte[] decompress(byte[] in) {
        ByteArrayOutputStream bos = null;

        if (in != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(in);

            bos = new ByteArrayOutputStream();

            GZIPInputStream gis = null;

            try {
                gis = new GZIPInputStream(bis);

                byte[] buf = new byte[8192];
                int    r   = -1;

                while ((r = gis.read(buf)) > 0) {
                    bos.write(buf, 0, r);
                }
            } catch (IOException e) {
                bos = null;
                throw new RuntimeException(e);
            } finally {
                try {
                    gis.close();
                    bos.close();
                } catch (Exception e) {
                }
            }
        }

        return (bos == null) ? null
                             : bos.toByteArray();
    }

    public static byte[] serialize(Object o) {
        if (o == null) {
            throw new NullPointerException("Can't serialize null");
        }

        byte[] rv = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream    os  = new ObjectOutputStream(bos);

            os.writeObject(o);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        }

        return rv;
    }

    public static Object deserialize(byte[] in) {
        Object rv = null;

        try {
            if (in != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(in);
                ObjectInputStream    is  = new ObjectInputStream(bis);

                rv                       = is.readObject();
                is.close();
                bis.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("deserialize failed", e);
        }

        return rv;
    }

    public static String decodeString(byte[] data, String charset) {
        String rv = null;

        try {
            if (data != null) {
                rv = new String(data, charset);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rv;
    }

    /**
     * Encode a string into the current character set.
     */
    public static byte[] encodeString(String in, String charset) {
        byte[] rv = null;

        try {
            rv = in.getBytes(charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rv;
    }
}
