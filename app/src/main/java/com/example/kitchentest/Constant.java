package com.example.kitchentest;

public class Constant {
    public final static String USER_NAME = "kitchen_attendant name";
    public final static String USER_ID = "kitchen_attendant id";
    public final static String USER_IDGROUP = "kitchen_attendant idgroup";
    public final static String KITCHEN_ATTENDANT_KEY = "kitchen_attendant";
    public final static String PERSON_ON_DUTY_KEY = "Person_On_Duty";
    public final static String WATCHER_KEY = "Watcher";
    public final static String STATUS_KEY = "Status";
    public final static String GROUP_KEY = "Group";
    public final static String PERSON_ON_DUTY_ROLE = "PersonOnDuty";
    public final static String WATCHER_ROLE = "Watcher";

    public static String createFormate(String str){
        String dayOfWeek = " ", month = " ";
        StringBuilder itog = new StringBuilder();
        itog.append(str);
        try{
            if(itog.charAt(itog.length()-2) == '.'){
                dayOfWeek = String.valueOf(itog.charAt(itog.length() - 3));
                month = itog.substring(itog.length()-1, itog.length());
                itog.delete(itog.length()-4, itog.length());
            }else{
                dayOfWeek = String.valueOf(itog.charAt(itog.length() - 4));
                month = itog.substring(itog.length()-1, itog.length());
                itog.delete(itog.length()-5, itog.length());
            }
        }catch (Exception e){
        }

        switch (dayOfWeek){
            case("0"): dayOfWeek = " воскресенье"; break;
            case("1"): dayOfWeek = " понедельник"; break;
            case("2"): dayOfWeek = " вторник"; break;
            case("3"): dayOfWeek = " среда"; break;
            case("4"): dayOfWeek = " четерг"; break;
            case("5"): dayOfWeek = " пятница"; break;
            case("6"): dayOfWeek = " суббота"; break;
        }
        switch (month){
            case("0"): month = " января"; break;
            case("1"): month = " февраля"; break;
            case("2"): month = " марта"; break;
            case("3"): month = " апреля"; break;
            case("4"): month = " мая"; break;
            case("5"): month = " июня"; break;
            case("6"): month= " июля"; break;
            case("7"): month= " августа"; break;
            case("8"): month= " сентября"; break;
            case("9"): month= " октября"; break;
            case("10"): month = " ноября"; break;
            case("11"): month = " дерабря"; break;
        }

        itog.append(month);
        itog.append(dayOfWeek);
        return String.valueOf(itog);
    }
    public static String createFormateForReplaceDay(String str){

        String dayOfWeek = "1", month = "1";
        StringBuilder itog = new StringBuilder();
        itog.append(str);
        for(int i =0; i<itog.length(); i++){
            if(itog.charAt(i) == ':'){
                itog.delete(0, i+1);
            }
        }
        char c = '0';
        int l = 0;

        try {
            c = itog.charAt(itog.length() - 2);
            l = itog.length();
        }catch(StringIndexOutOfBoundsException e){

        }

        if(c== '.'){
            dayOfWeek = String.valueOf(itog.charAt(l - 3));
            month  = String.valueOf(itog.charAt(l-1));
            itog.delete(l-4, l);
        }

        switch (dayOfWeek){
            case("0"): dayOfWeek = " (воскресенье)"; break;
            case("1"): dayOfWeek = " (понедельник)"; break;
            case("2"): dayOfWeek = " (вторник)"; break;
            case("3"): dayOfWeek = " (среда)"; break;
            case("4"): dayOfWeek = " (четерг)"; break;
            case("5"): dayOfWeek = " (пятница)"; break;
            case("6"): dayOfWeek = " (суббота)"; break;
        }
        switch (month){
            case("0"): month = " января"; break;
            case("1"): month = " февраля"; break;
            case("2"): month = " марта"; break;
            case("3"): month = " апреля"; break;
            case("4"): month = " мая"; break;
            case("5"): month = " июня"; break;
            case("6"): month= " июля"; break;
            case("7"): month= " августа"; break;
            case("8"): month= " сентября"; break;
            case("9"): month= " октября"; break;
            case("10"): month = " ноября"; break;
            case("11"): month = " дерабря"; break;
        }

        itog.append(month);
        itog.append(dayOfWeek);
        return String.valueOf(itog);
    }
}
