package ru.polis.germanverbs.objects;

import java.io.Serializable;

/**
 * Verb Class
 *
 * Created by Dmitrii on 12.05.2016.
 */
public class Verb implements Serializable{
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
}
