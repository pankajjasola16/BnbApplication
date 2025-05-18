package com.airbnb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Local date concept of java

public class DateUtil {
    public static void main(String[] args) {

        LocalDate checkInDate = LocalDate.of(2024, 11, 20);
        LocalDate checkOutDate = LocalDate.of(2024, 11, 23);

        List<LocalDate> datesBetween = getDatesBetween(checkInDate, checkOutDate);

        for (LocalDate date : datesBetween) {
            System.out.println(date);
        }
    }

        public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
            List<LocalDate> dates = new ArrayList<>();
            LocalDate currentDate = startDate;

            // currentDate.isAfter(endDate) --> means current date is  20 and end date is 23
            // so here it is asking --> is 20 after 23 --> no so condition is false but if we write:
            // !currentDate.isAfter(endDate --> true --> because we are using --> ! --> not
            while (!currentDate.isAfter(endDate)) {

                dates.add(currentDate);

                currentDate = currentDate.plusDays(1);
            }

            return dates;
        }
    }
// when above condition is true --> then below I am adding the current date to
// --> Dates List
// I have created a lis of Local dates --> List<LocalDate> (above) --> and to that
// I am adding the current date --> dates.add(currentDate) --> (below)

// below I am incrementing the current date by 1 --> so we have built in method
// called --> plusDays --> so just add 1 into it

