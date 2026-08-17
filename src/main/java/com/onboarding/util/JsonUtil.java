package com.onboarding.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal hand-rolled JSON helpers: escaping for output, and a flat-object
 * parser for request bodies of the form {"key":"value", "key2":123}.
 * No nesting/arrays on the parse side -- that's all this app needs.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static Map<String, String> parseFlatObject(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        if (json == null) {
            return result;
        }
        String s = json.trim();
        if (s.startsWith("{")) {
            s = s.substring(1);
        }
        if (s.endsWith("}")) {
            s = s.substring(0, s.length() - 1);
        }

        int i = 0;
        int len = s.length();
        while (i < len) {
            while (i < len && Character.isWhitespace(s.charAt(i))) i++;
            if (i >= len) break;

            if (s.charAt(i) != '"') {
                int comma = s.indexOf(',', i);
                if (comma < 0) break;
                i = comma + 1;
                continue;
            }

            int[] keyEnd = new int[1];
            String key = readJsonString(s, i, keyEnd);
            i = keyEnd[0];

            while (i < len && Character.isWhitespace(s.charAt(i))) i++;
            if (i < len && s.charAt(i) == ':') i++;
            while (i < len && Character.isWhitespace(s.charAt(i))) i++;

            String value;
            if (i < len && s.charAt(i) == '"') {
                int[] valEnd = new int[1];
                value = readJsonString(s, i, valEnd);
                i = valEnd[0];
            } else {
                int stop = i;
                while (stop < len && s.charAt(stop) != ',' && s.charAt(stop) != '}') stop++;
                value = s.substring(i, stop).trim();
                if (value.equals("null")) value = "";
                i = stop;
            }

            result.put(key, value);

            while (i < len && Character.isWhitespace(s.charAt(i))) i++;
            if (i < len && s.charAt(i) == ',') i++;
        }
        return result;
    }

    private static String readJsonString(String s, int start, int[] endOut) {
        StringBuilder sb = new StringBuilder();
        int i = start + 1;
        int len = s.length();
        while (i < len && s.charAt(i) != '"') {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'u':
                        if (i + 5 < len) {
                            String hex = s.substring(i + 2, i + 6);
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        }
                        break;
                    default: sb.append(next);
                }
                i += 2;
            } else {
                sb.append(c);
                i++;
            }
        }
        endOut[0] = i + 1;
        return sb.toString();
    }
}
