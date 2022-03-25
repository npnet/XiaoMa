package com.xiaoma.music.player.view.lyric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/8/13
 *  description :
 * </pre>
 */
public class LyricParser {
    public static final int RATE_60 = 60;
    public static final int RATE_1000 = 1000;
    public static final int NUM_10 = 10;
    public static final String SIGN_POINT = ".";

    public static final String SPLIT_POINT = "\\.";
    public static final String TAG_SONG_NAME = "[ti:";
    public static final String TAG_ARTIST = "[ar:";
    public static final String TAG_ALBUM = "[al:";
    public static final String TAG_LYRIC_BY = "[by:";
    public static final String TAG_LANGUAGE = "[la:";
    public static final String TAG_END = "]";
    public static final String TAG_OFFSET = "[offset:";
    public static final String TAG_ANGLE_BRACKET = "<";

    public static final String SPLIT_NEW_LINE = "\n";

    //    public static final String PATTERN_LYRIC_LINE = "\\d{2}:\\d{2}.\\d{2}";
    public static final String PATTERN_REPLACE = "<\\d+,(-)?\\d+>";
    public static final String PATTERN_LYRIC_DUPLICATE = "\\[\\d{2}:\\d{2}.\\d{2}\\][\\s\\S]*";

    public static final String PATTERN_LYRIC_LINE = "((\\[\\d{2}:\\d{2}.\\d{2}\\])+)(.+)";
    public static final String PATTERN_LYRIC_TIME = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\]";

    private static List<Lyric> sLyricLists = new ArrayList<>();
    private static Lyric sLyric;

    public static List<Lyric> parseLyrics(String lyricContent) {
        sLyricLists.clear();
        if (lyricContent == null) {
            return null;
        }
        if (lyricContent.contains(TAG_ANGLE_BRACKET)) {
            Pattern replacePattern = Pattern.compile(PATTERN_REPLACE);
            Matcher replaceMater = replacePattern.matcher(lyricContent);
            lyricContent = replaceMater.replaceAll("");
        }
        String[] split = lyricContent.split(SPLIT_NEW_LINE);
        for (String s : split) {
            decodeLyric(s.trim());
        }

        Collections.sort(sLyricLists);
        return sLyricLists;
    }

    private static void decodeLyric(String lyricLine) {

        if (lyricLine.startsWith(TAG_SONG_NAME)) {
        } else if (lyricLine.startsWith(TAG_ARTIST)) {

        } else if (lyricLine.startsWith(TAG_ALBUM)) {

        } else if (lyricLine.startsWith(TAG_LYRIC_BY)) {

        } else if (lyricLine.startsWith(TAG_LANGUAGE)) {

        } else if (lyricLine.startsWith(TAG_LANGUAGE)) {

        } else if (lyricLine.startsWith(TAG_OFFSET)) {

        } else {
            Matcher lineMatcher = Pattern.compile(PATTERN_LYRIC_LINE).matcher(lyricLine);
            if (!lineMatcher.matches()) {
                return;
            }
            String timeStr = lineMatcher.group(1);
            String content = lineMatcher.group(3);


            Matcher timeMatcher = Pattern.compile(PATTERN_LYRIC_TIME).matcher(timeStr);
            while (timeMatcher.find()) {
                sLyric = new Lyric();
                long min = Long.parseLong(timeMatcher.group(1));
                long sec = Long.parseLong(timeMatcher.group(2));
                long micSec = Long.parseLong(timeMatcher.group(3));

                long timeLong = min * RATE_60 * RATE_1000 + sec * RATE_1000 + micSec * 10;

                sLyric.setCurTime(timeLong);
                sLyric.setContent(content);
                sLyric.setStrTime(timeStr);

                sLyricLists.add(sLyric);
            }

        }
    }
}