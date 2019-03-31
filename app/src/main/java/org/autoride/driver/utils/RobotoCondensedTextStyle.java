package org.autoride.driver.utils;

public enum RobotoCondensedTextStyle implements TextStyle {

    NORMAL("normal", "Roboto-Regular.ttf"),
    LIGHT("light", "RobotoCondensed-Light.ttf"),
    BOLD("bold", "RobotoCondensed-Bold.ttf"),
    BOLD_ITALIC("boldItalic", "RobotoCondensed-BoldItalic.ttf"),
    ITALIC("italic", "RobotoCondensed-Italic.ttf"),
    REGULAR("regular", "Roboto-Regular.ttf"),
    MONOSPACE("monospace", "RobotoCondensed-Light.ttf"),
    SERIF("serif", "Roboto-Regular.ttf"),
    SANSERIF("sansSerif", "Roboto-Regular.ttf"),
    SANS("sans", "Roboto-Regular.ttf");

    private String mName;
    private String mFontName;

    RobotoCondensedTextStyle(String name, String fontName) {
        mName = name;
        mFontName = fontName;
    }

    @Override
    public String getFontName() {
        return mFontName;
    }

    @Override
    public String getName() {
        return mName;
    }
}