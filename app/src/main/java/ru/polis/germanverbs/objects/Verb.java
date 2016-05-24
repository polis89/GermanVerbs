package ru.polis.germanverbs.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Verb Class
 *
 * Created by Dmitrii on 12.05.2016.
 */
public class Verb implements Parcelable{
    //Общая инфа про глагол
    private int id;

    private String infinitive;
    private String infinitive_3_person;
    private String prateritum;
    private String perfekt;

    //Перевод
    private String translate;

    //Ошибочные варианты
    private String prateritum_1;
    private String prateritum_2;
    private String prateritum_3;
    private String perfekt_1;
    private String perfekt_2;
    private String perfekt_3;
    public Verb(int id, String infinitive, String infinitive_3_person, String prateritum, String perfekt, String translate, String prateritum_1, String prateritum_2, String prateritum_3, String perfekt_1, String perfekt_2, String perfekt_3) {
        this.id = id;
        this.infinitive = infinitive;
        this.infinitive_3_person = infinitive_3_person;
        this.prateritum = prateritum;
        this.perfekt = perfekt;
        this.translate = translate;
        this.prateritum_1 = prateritum_1;
        this.prateritum_2 = prateritum_2;
        this.prateritum_3 = prateritum_3;
        this.perfekt_1 = perfekt_1;
        this.perfekt_2 = perfekt_2;
        this.perfekt_3 = perfekt_3;
    }

    @Override
    public String toString() {
        return "Verb{" +
                "id=" + id +
                ", infinitive='" + infinitive + '\'' +
                ", infinitive_3_person='" + infinitive_3_person + '\'' +
                ", prateritum='" + prateritum + '\'' +
                ", perfekt='" + perfekt + '\'' +
                ", translate='" + translate + '\'' +
                ", prateritum_1='" + prateritum_1 + '\'' +
                ", prateritum_2='" + prateritum_2 + '\'' +
                ", prateritum_3='" + prateritum_3 + '\'' +
                ", perfekt_1='" + perfekt_1 + '\'' +
                ", perfekt_2='" + perfekt_2 + '\'' +
                ", perfekt_3='" + perfekt_3 + '\'' +
                '}';
    }

    public String getInfinitive() {
        return infinitive;
    }

    public String getInfinitive_3_person() {
        return infinitive_3_person;
    }

    public String getPrateritum() {
        return prateritum;
    }

    public String getPerfekt() {
        return perfekt;
    }

    public String getTranslate() {
        return translate;
    }

    public String getPrateritum_1() {
        return prateritum_1;
    }

    public String getPrateritum_2() {
        return prateritum_2;
    }

    public String getPrateritum_3() {
        return prateritum_3;
    }

    public String getPerfekt_1() {
        return perfekt_1;
    }

    public String getPerfekt_2() {
        return perfekt_2;
    }

    public String getPerfekt_3() {
        return perfekt_3;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(infinitive);
        dest.writeString(infinitive_3_person);
        dest.writeString(prateritum);
        dest.writeString(perfekt);
        dest.writeString(translate);
        dest.writeString(prateritum_1);
        dest.writeString(prateritum_2);
        dest.writeString(prateritum_3);
        dest.writeString(perfekt_1);
        dest.writeString(perfekt_2);
        dest.writeString(perfekt_3);
    }

    public static final Parcelable.Creator<Verb> CREATOR = new Parcelable.Creator<Verb>() {
        // распаковываем объект из Parcel
        public Verb createFromParcel(Parcel in) {
            return new Verb(in);
        }

        public Verb[] newArray(int size) {
            return new Verb[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private Verb(Parcel parcel) {
        id = parcel.readInt();
        infinitive = parcel.readString();
        infinitive_3_person = parcel.readString();
        prateritum = parcel.readString();
        perfekt = parcel.readString();
        translate = parcel.readString();
        prateritum_1 = parcel.readString();
        prateritum_2 = parcel.readString();
        prateritum_3 = parcel.readString();
        perfekt_1 = parcel.readString();
        perfekt_2 = parcel.readString();
        perfekt_3 = parcel.readString();
    }
}
