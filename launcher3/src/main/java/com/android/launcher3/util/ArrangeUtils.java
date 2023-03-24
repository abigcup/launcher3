package com.android.launcher3.util;

import android.text.TextUtils;
import android.util.Log;

import com.android.Utils.GsonUtil;
import com.android.gallery3d.glrenderer.Texture;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherSettings;
import com.blankj.utilcode.util.RegexUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArrangeUtils {

    /**
     * 对图标进行排序
     * 1、预置应用图标 > 安装应用图标
     * 2、安装应用的图标，英文应用名 > 中文应用名
     * 3、英文应用名都根据首字母排序，中文应用名根据拼音首字母排序
     *
     * @param allItemInfoList
     * @return
     */
    public static List<ItemInfo> arrange(List<ItemInfo> allItemInfoList) {
        if (allItemInfoList == null || allItemInfoList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<ItemInfo> resultList = new ArrayList<>(allItemInfoList.size());
        //英文文件夹
        List<ItemInfo> englishFolderList = new ArrayList<>();
        //中文文件夹
        List<ItemInfo> chineseFolderList = new ArrayList<>();
        //英文预置应用列表
        List<ItemInfo> englishPresetList = new ArrayList<>();
        //中文预置应用列表
        List<ItemInfo> chinesePresetList = new ArrayList<>();
        //英文安装应用列表
        List<ItemInfo> englishAppList = new ArrayList<>();
        //中文预置应用列表
        List<ItemInfo> chineseAppList = new ArrayList<>();

        for (ItemInfo itemInfo : allItemInfoList) {
            if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
                    || itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET) {
                continue;
            }

            /**
             * {@link com.android.launcher3.LauncherSettings.BaseLauncherColumns.ITEM_TYPE_PRESET_SHORTCUT}
             */
            //排预置
            if (itemInfo.itemType == 2000) {
                if (RegexUtils.isZh(String.valueOf(itemInfo.title.charAt(0)))) {
                    chinesePresetList.add(itemInfo);
                } else {
                    englishPresetList.add(itemInfo);
                }
            } else if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
                //排文件夹
                if (TextUtils.isEmpty(itemInfo.title)) {
                    englishFolderList.add(itemInfo);
                } else if (RegexUtils.isZh(String.valueOf(itemInfo.title.charAt(0)))) {
                    chineseFolderList.add(itemInfo);
                } else {
                    englishFolderList.add(itemInfo);
                }
            } else {
                //拍其他
                if (RegexUtils.isZh(String.valueOf(itemInfo.title.charAt(0)))) {
                    chineseAppList.add(itemInfo);
                } else {
                    englishAppList.add(itemInfo);
                }
            }
        }

        Comparator<ItemInfo> englishComparator = new Comparator<ItemInfo>() {
            @Override
            public int compare(ItemInfo o1, ItemInfo o2) {
                if (TextUtils.isEmpty(o1.title)) {
                    return -1;
                } else if (TextUtils.isEmpty(o2.title)) {
                    return 1;
                } else if (TextUtils.isEmpty(o1.title) && TextUtils.isEmpty(o2.title)) {
                    return 0;
                }

                char c1 = o1.title.charAt(0);
                char c2 = o2.title.charAt(0);
                if (c1 == c2) {
                    return o1.title.length() - o2.title.length();
                } else {
                    return c1 - c2;
                }
            }
        };

        Comparator<ItemInfo> chineseComparator = new Comparator<ItemInfo>() {
            @Override
            public int compare(ItemInfo o1, ItemInfo o2) {
                if (TextUtils.isEmpty(o1.title)) {
                    return -1;
                } else if (TextUtils.isEmpty(o2.title)) {
                    return 1;
                } else if (TextUtils.isEmpty(o1.title) && TextUtils.isEmpty(o2.title)) {
                    return 0;
                }

                String c1h = getFirstSpell(o1.title.toString());
                String c2h = getFirstSpell(o2.title.toString());

                if (c1h == null) {
                    return 1;
                } else if (c2h == null) {
                    return -1;
                }

                int len1 = o1.title.length();
                int len2 = o2.title.length();
                int lim = Math.min(len1, len2);

                int k = 0;
                while (k < lim) {
                    char c1 = c1h.charAt(k);
                    char c2 = c2h.charAt(k);
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                    k++;
                }
                return len1 - len2;
            }
        };

        //分别排序
        Collections.sort(englishFolderList, englishComparator);
        Collections.sort(chineseFolderList, chineseComparator);
        Collections.sort(englishPresetList, englishComparator);
        Collections.sort(chinesePresetList, chineseComparator);
        Collections.sort(englishAppList, englishComparator);
        Collections.sort(chineseAppList, chineseComparator);

        //集合结果
        resultList.addAll(englishFolderList);
        resultList.addAll(chineseFolderList);
        resultList.addAll(englishPresetList);
        resultList.addAll(chinesePresetList);
        resultList.addAll(englishAppList);
        resultList.addAll(chineseAppList);

        return resultList;
    }

    private static String getFirstSpell(String chinese) {
        StringBuilder pybd = new StringBuilder();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : arr) {
            if (c > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                    if (temp != null && temp.length > 0) {
                        pybd.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybd.append(c);
            }
        }
        return pybd.toString().replaceAll("\\W", "").trim();
    }

}
