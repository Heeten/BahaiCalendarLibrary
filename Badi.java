package badicalendar;

// Version 1.1
// Copyright 2015 Soroosh Pezeshki
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//     http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Badi{
    // By Soroosh Pezeshki April 2015
    // Calculate current Badi and Gregrian date (from 1900-2064)

    public static final int ZEROTH_BADI_YEAR_AS_GREGORIAN_YEAR = 1843;
    public static final int DAYS_IN_BADI_MONTH = 19;
    public static final int DAYS_BETWEEN_NAWRUZ_AND_AYYAMIHA_START = 18 * DAYS_IN_BADI_MONTH;

    public static int[] Badi2Gregorian(int byear, int bmonth, int bday) {
        // Returns the Gregorian date as an array [year, month, day of month, day of year, holyday]
        // parameters: badi year, badi month, and badi day
        int doy,
            bdoy,
            gyear = byear + 1843,
            yearIndex = gyear - 2014,
            leapyear = isLeapYear(gyear),
            nawRuz = nawRuzParameter(yearIndex),
            d, m, day, ndate, date,
            holyday=-1;

        Calendar calendar = new GregorianCalendar();  

        bdoy = (bmonth-1)*19 + bday;
        // special case Month of Ala after Ayyam'i'Ha
        if (bmonth==20) bdoy = 346 + nawRuzParameter(yearIndex+1)*(1-nawRuz) + bday;
        doy = bdoy + 78 + leapyear + nawRuz;
        if (doy > 365+leapyear){
            doy = bdoy - 287 + nawRuz;
            gyear = byear + 1844;
        }
 
        //update a date
        calendar.set(Calendar.YEAR, gyear);
        calendar.set(Calendar.DAY_OF_YEAR, doy);
        m = calendar.get(Calendar.MONTH)+1;
        d = calendar.get(Calendar.DAY_OF_MONTH);
	
        // Check if date is a Holy day 
        for (int i=0;  i<11; i++){
            int hdday = holydays(i);
            if (yearIndex>0){
                if (i == 7) {
                    hdday = twinBDays(yearIndex);
                } else if (i == 8) {
                    hdday = twinBDays(yearIndex) + 1;
                }
            }
            if (hdday==bdoy) holyday = i;
        }


        int[] tmp = {gyear, m, d, doy, holyday};
        return tmp;
    }

    public static int[] Gregorian2Badi(int year, int month, int day) {
        // Returns an array with the Badi Dates: Day of th month, month, year, day of the year, holyday, year in vahid, vahid, kull-i-shay 
        // Input day of the Gregorian year and Gregorian year
        int badiYear = 0;
        int badiDay = 0, badiMonth = 0, dayOfBadiYear = 0;
        int yearIndex = year - 2014;
        int leapyear = isLeapYear(year);
        int nawRuz = nawRuzParameter(yearIndex);
        int nawRuzLastYear = nawRuzParameter(yearIndex - 1);
        int doy, holyday=-1;
        int vahid, yearInVahid, kull, tmpkull;
        Calendar calendar = new GregorianCalendar();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        doy = calendar.get(Calendar.DAY_OF_YEAR);
        // Calculate day number of the Badi Year
        if (doy < 79 + leapyear + nawRuz) {
            dayOfBadiYear = doy + 287 - nawRuzLastYear;
            badiYear = year - 1844;
        } else {
            dayOfBadiYear = doy - 78 - leapyear - nawRuz;
            badiYear = year - 1843;
        }

        // Calculate current Badi date
        badiDay=dayOfBadiYear%19;
        if(badiDay==0) badiDay=19;
        badiMonth = ((dayOfBadiYear - badiDay) / 19 + 1);
        // Month of Ala (19th month; after Ayyam'i'Ha)
        // Bug will occur in Ala 216!
        if ((dayOfBadiYear > (346 + nawRuz * (1-nawRuzLastYear)))) {
            badiMonth = 20;
            badiDay = dayOfBadiYear - 346 - nawRuz * (1-nawRuzLastYear);
        }

        yearInVahid = badiYear%19;
        if(yearInVahid==0) yearInVahid=19;
        vahid=(badiYear-yearInVahid)/19 + 1;
        tmpkull = badiYear%361;
        if (tmpkull==0) tmpkull=361;
        kull = (badiYear-tmpkull)/361 + 1;


        // Check if date is a Holy day 
        for (int i=0;  i<11; i++){
            int hdday = holydays(i);
            if (yearIndex>0){
                if (i == 7) {
                    hdday = twinBDays(yearIndex);
                } else if (i == 8) {
                    hdday = twinBDays(yearIndex) + 1;
                }
            }
            if (hdday==dayOfBadiYear) holyday = i;
        }

        int[] date = {badiYear,badiMonth,badiDay,dayOfBadiYear, holyday, yearInVahid, vahid, kull};
        return date;
    }

    private static int isLeapYear(int year){
        // Is it a leap year?
        boolean isleapyear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
        return (isleapyear) ? 1 : 0;
    }

    private static int holydays(int i){
        // return the day of the year of a Holy day
        // the values for the Birth of Bab and Baha'u'llah are placeholder and are read from another array
        int[] hdArray = {1, 32, 40, 43, 65, 70, 112, 214, 237, 251, 253};
        return hdArray[i];
    }

    private static final byte[] nrArray = {1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0};

    private static int nawRuzParameter(int yearIndex) {
	if (yearIndex < 0) return 1; //Use the western date for Naw-Ruz prior to 172
	if (yearIndex >= nrArray.length) throw new IllegalArgumentException("Naw-Ruz only defined until " + (2014 + nrArray.length - 1));
	return nrArray[yearIndex];
    }

    static int nawRuzDayOfMarch(int gregorianYear) {
	if (gregorianYear < 1900) throw new IllegalArgumentException("Naw-Ruz only defined for dates after 1900");
	if (gregorianYear < 2014) return 21; //Use the western date for Naw-Ruz prior to 2014
	final int yearIndex = gregorianYear - 2014;
	return 20 + nawRuzParameter(yearIndex);
    }

    private static int twinBDays(int yearIndex) {
        // Return the Day of the year for the Birth of Bab; list for the years 2014-2064
        int[] nrArray = {217,238,227,216,234,223,213,232,220,210,228,217,235,224,214,233,223,211,230,219,238,226,215,234,224,213,232,221,210,228,217,236,225,214,233,223,212,230,219,237,227,215,234,224,213,232,220,209,228,218,236};
        if (yearIndex<nrArray.length) {
            return nrArray[yearIndex];
        }else{
            return -1;
        }
    }
}
