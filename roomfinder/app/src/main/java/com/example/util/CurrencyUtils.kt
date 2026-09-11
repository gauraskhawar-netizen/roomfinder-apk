package com.example.util

object CurrencyUtils {
    /**
     * Formats an amount using the Indian numbering system:
     * - Rightmost three digits grouped together
     * - Subsequent groupings in pairs of two
     * e.g. 500 -> 500, 2500 -> 2,500, 15000 -> 15,000, 125000 -> 1,25,000
     */
    fun formatIndianNumber(amount: Double): String {
        return formatIndianNumber(amount.toLong())
    }

    fun formatIndianNumber(amount: Long): String {
        if (amount < 0) return "-" + formatIndianNumber(-amount)
        val s = amount.toString()
        if (s.length <= 3) return s
        val lastThree = s.substring(s.length - 3)
        val remaining = s.substring(0, s.length - 3)
        val sb = StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            sb.append(remaining[i])
            count++
            if (count == 2 && i != 0) {
                sb.append(',')
                count = 0
            }
        }
        return sb.reverse().toString() + "," + lastThree
    }

    /**
     * Formats monthly rent as ₹2,500/month
     */
    fun formatMonthlyRent(amount: Double): String {
        return "₹${formatIndianNumber(amount)}/month"
    }

    /**
     * Formats security deposit in ₹ (e.g. ₹5,000)
     */
    fun formatSecurityDeposit(amount: Double): String {
        return "₹${formatIndianNumber(amount)}"
    }

    /**
     * Formats with ₹ prefix (e.g. ₹2,500)
     */
    fun formatRupees(amount: Double): String {
        return "₹${formatIndianNumber(amount)}"
    }
}
