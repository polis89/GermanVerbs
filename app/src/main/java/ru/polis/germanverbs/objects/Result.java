package ru.polis.germanverbs.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for game results
 *
 * Created by Dmitrii on 24.05.2016.
 */
public class Result implements Parcelable {
    private int progress; //Набранный прогресс (может быть отрицательным)
    private int answerTrueCount; //Количество правильных ответов
    private int answerFalseCount; //Количество не правильных ответов

    public Result() {
    }

    public void addProgress(int summ){
        progress += summ;
    }

    public void addTrueAnswer(){
        answerTrueCount++;
    }

    public void addFalseAnswer(){
        answerFalseCount++;
    }

    public int getProgress() {
        return progress;
    }

    public int getAnswerTrueCount() {
        return answerTrueCount;
    }

    public int getAnswerFalseCount() {
        return answerFalseCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(progress);
        dest.writeInt(answerTrueCount);
        dest.writeInt(answerFalseCount);
    }

    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        // распаковываем объект из Parcel
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private Result(Parcel parcel) {
        progress = parcel.readInt();
        answerTrueCount = parcel.readInt();
        answerFalseCount = parcel.readInt();
    }

    @Override
    public String toString() {
        return "Result{" +
                "progress=" + progress +
                ", answerTrueCount=" + answerTrueCount +
                ", answerFalseCount=" + answerFalseCount +
                '}';
    }
}
