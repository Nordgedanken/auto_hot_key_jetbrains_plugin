package de.nordgedanken.auto_hotkey;

import com.intellij.lang.Language;

public class AHKLanguage extends Language {
    public static final AHKLanguage INSTANCE = new AHKLanguage();

    private AHKLanguage() {
        super("AutoHotKey");
    }
}