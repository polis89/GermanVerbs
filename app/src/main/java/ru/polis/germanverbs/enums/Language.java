package ru.polis.germanverbs.enums;

import ru.polis.germanverbs.R;

/**
 * Enum of Languages
 *
 * Created by Dmitrii Poliasnskii on 28.04.2016.
 */
public enum Language {
    ENG("eng", "English", "en", R.drawable.united_kingdom),
    RUS("rus", "Русский", "ru", R.drawable.russia);

    String nameForDB; //Имя столбца в БД
    String description; //Описание на человеческом
    String locale; //Название языка в настройках Андроид
    int imageResID; //ID ресурса изображения

    Language(String nameForDB, String description, String locale, int imageResID) {
        this.nameForDB = nameForDB;
        this.description = description;
        this.locale = locale;
        this.imageResID = imageResID;
    }

    public String getNameForDB() {
        return nameForDB;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResID() {
        return imageResID;
    }

    public String getLocale() {
        return locale;
    }

    public static Language getLanguageByLocale(String langLocale){
        for(Language language:Language.values()){
            if(langLocale.equals(language.locale)){
                return language;
            }
        }
        return null;
    }
}
