package cracker.com.mantle.model;


import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;

import cracker.com.mantle.util.PreferenceUtil;

public class CalendarModel {

    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
    private boolean isAttend;
    private boolean isToday;
    private boolean isDayInThisMonth;
    private NotiModel noti;

    public NotiModel getNoti() {
        return noti;
    }

    public void setNoti(NotiModel noti) {
        this.noti = noti;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public boolean isAttend() {
        return isAttend;
    }

    public void setAttend(boolean attend) {
        isAttend = attend;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isDayInThisMonth() {
        return isDayInThisMonth;
    }

    public void setDayInThisMonth(boolean dayInThisMonth) {
        isDayInThisMonth = dayInThisMonth;
    }

    public ArrayList<CalendarModel> getData(Context context, String yearMonth) {

        int year = Integer.parseInt(yearMonth.substring(0, 4).trim());
        int month = Integer.parseInt(yearMonth.substring(4, 6).trim());

        ArrayList<CalendarModel> calendars = new ArrayList<>();

        int startDayOfWeek = getStartDayOfWeek(year, month); // 시작 요일
        int endDay = getEndDay(year, month);
        int beforeMonthLastDay = getBeforeMonthLastDay(year, month);

        if (startDayOfWeek != Calendar.SUNDAY) { // 1일의 시작이 일요일이 아닐경우 빈공간 채우기
            for (int i = 0; i < startDayOfWeek - 1; i++) {
                CalendarModel emptyData = new CalendarModel();
                emptyData.setDay(beforeMonthLastDay - ((startDayOfWeek - 2) - i));
                emptyData.setDayOfWeek(i + 1);
                emptyData.setToday(false);
                emptyData.setDayInThisMonth(false);
                emptyData.setAttend(false);
                if(month == 1) {
                    emptyData.setMonth(12);
                    emptyData.setYear(--year);
                } else {
                    emptyData.setMonth(month);
                    emptyData.setYear(year);
                }

                calendars.add(emptyData);
            }
        }

        for (int i = 1; i <= endDay; i++) { // 한달의 날자 수 만큼 데이터 채우기
            if (startDayOfWeek == 8) {
                startDayOfWeek = 1;
            }
            CalendarModel dayInfo = new CalendarModel();
            dayInfo.setDay(i);
            dayInfo.setDayOfWeek(startDayOfWeek++);
            dayInfo.setToday(isToday(year, month, i));
            dayInfo.setDayInThisMonth(true);
            dayInfo.setYear(year);
            dayInfo.setMonth(month);
            dayInfo.setNoti(context, year, month, i);
            calendars.add(dayInfo);
        }

        int nextMonthDay = 1;

        while (startDayOfWeek != Calendar.SUNDAY && (startDayOfWeek <= Calendar.SATURDAY && startDayOfWeek >= Calendar.SUNDAY)) { // 남은 공간 채우기
            CalendarModel emptyData = new CalendarModel();
            emptyData.setDay(nextMonthDay);
            emptyData.setDayOfWeek(startDayOfWeek);
            emptyData.setDayInThisMonth(false);
            emptyData.setToday(false);
            emptyData.setAttend(false);
            if(month == 12) {
                emptyData.setMonth(1);
                emptyData.setYear(++year);
            } else {
                emptyData.setMonth(month);
                emptyData.setYear(year);
            }
            calendars.add(emptyData);
            startDayOfWeek++;
            nextMonthDay++;
            if (startDayOfWeek == 8)
                startDayOfWeek = 1;
        }


        return calendars;
    }


    private int getBeforeMonthLastDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 2, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private int getStartDayOfWeek(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private int getEndDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private boolean isToday(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        int todayYear = cal.get(Calendar.YEAR);
        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);
        return todayYear == year && todayMonth == month && day == todayDay;
    }

    private int[] getHourMinSecLearningTime(int totalLearningTime) {
        int[] totalLearningTimeArray = new int[3];
        int hour = totalLearningTime / 3600;
        int minute = (totalLearningTime % 3600) / 60;
        int second = totalLearningTime % 3600 % 60;

        totalLearningTimeArray[0] = hour;
        totalLearningTimeArray[1] = minute;
        totalLearningTimeArray[2] = second;
        return totalLearningTimeArray;
    }

    private void setNoti(Context context, int year, int month, int day) {
        String yyyyMMdd = String.format("%04d%02d%02d", year, month, day);

        PreferenceUtil preferenceUtil = new PreferenceUtil(context);
        NotiModel notiModel = preferenceUtil.getNotiValue(yyyyMMdd);
        this.noti = notiModel;
    }
}
