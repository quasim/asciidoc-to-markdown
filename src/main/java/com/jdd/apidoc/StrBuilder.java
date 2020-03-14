package com.jdd.apidoc;

public class StrBuilder {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final StringBuilder sb;

    public StrBuilder() {
        sb = new StringBuilder();
    }

    public StrBuilder append(CharSequence... strs) {
        if (strs == null) {
            return this;
        }
        for (CharSequence str : strs) {
            if (str == null) {
                continue;
            }
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if ('\n' == c || '\r' == c) {
                    sb.append(System.getProperty("line.separator"));
                } else {
                    sb.append(c);
                }
            }
        }
        return this;
    }

    public StrBuilder appendLine(CharSequence... strs) {
        for (CharSequence str : strs) {
            this.append(str);
            sb.append(LINE_SEPARATOR);
        }
        return this;
    }

    public StrBuilder appendBlock(CharSequence... strs) {
        for (CharSequence str : strs) {
            sb.append(LINE_SEPARATOR);
            this.appendLine(str);
        }
        return this;
    }

    public StrBuilder newLine() {
        sb.append(LINE_SEPARATOR);
        return this;
    }

    public StrBuilder newLine(int n) {
        for (int i = 0; i < n; i++) {
            sb.append(LINE_SEPARATOR);
        }
        return this;
    }

    public StrBuilder newLine(CharSequence cs) {
        sb.append(LINE_SEPARATOR);
        this.append(cs);
        return this;
    }

    public StrBuilder backLine() {
        while (true) {
            char c = sb.charAt(sb.length() - 1);
            if (c == '\r' || c == '\n') {
                sb.deleteCharAt(sb.length() - 1);
            } else {
                break;
            }
        }
        return this;
    }

    public StrBuilder prepareAppendFragment() {
        backLine();
        int index = sb.lastIndexOf(System.lineSeparator());
        String s = sb.substring(index).trim();
        if (s.startsWith("#")) {
            if (!StringUtils.isContainChinese(s)) {
                return this;
            }
        } else {
            newLine();
        }
        return newLine();
    }

    public String toString() {
        return sb.toString();
    }

}
