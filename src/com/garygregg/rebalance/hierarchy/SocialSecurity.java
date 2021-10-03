package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

class SocialSecurity extends CpiAnnuity {

    @Override
    protected @NotNull Currency getMonthlyIncome(@NotNull PortfolioDescription
                                                    description) {
        return description.getSocialSecurityMonthly();
    }

    @Override
    protected Date getStartDate(Date referenceDate) {

        /*
         * Declare and initialize the start date. Is the reference date not
         * null?
         */
        Date startDate = null;
        if (null != referenceDate) {

            /*
             * The reference date is not null. We here interpret the reference
             * date as a birthdate. Get a calendar instance, and initialize it
             * with the reference date.
             */
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(referenceDate);

            /*
             * Add 62 years to the reference date to obtain the Social
             * Security start date. Get the date.
             */
            calendar.add(Calendar.YEAR, 62);
            startDate = calendar.getTime();
        }

        // Return the social security start date.
        return startDate;
    }
}
