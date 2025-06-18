package com.dataapk.lifeorganizer.ui.theme

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.widget.TextView

/**
 * Typography definitions and utilities for Life Organizer app
 * Provides consistent text styling across the app
 */
object AppTypography {

    /**
     * Font weights
     */
    const val WEIGHT_NORMAL = Typeface.NORMAL
    const val WEIGHT_BOLD = Typeface.BOLD
    const val WEIGHT_ITALIC = Typeface.ITALIC
    const val WEIGHT_BOLD_ITALIC = Typeface.BOLD_ITALIC

    /**
     * Text style definitions
     */
    data class TextStyle(
        val size: Float,
        val color: Int,
        val weight: Int = WEIGHT_NORMAL
    )

    /**
     * Predefined text styles
     */
    fun getHeadingStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_HEADING,
            color = AppTheme.getPrimaryTextColor(context),
            weight = WEIGHT_BOLD
        )
    }

    fun getTitleStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_TITLE,
            color = AppTheme.getPrimaryTextColor(context),
            weight = WEIGHT_BOLD
        )
    }

    fun getSubtitleStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_SUBTITLE,
            color = AppTheme.getPrimaryTextColor(context),
            weight = WEIGHT_NORMAL
        )
    }

    fun getBodyStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_BODY,
            color = AppTheme.getPrimaryTextColor(context),
            weight = WEIGHT_NORMAL
        )
    }

    fun getCaptionStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_CAPTION,
            color = AppTheme.getSecondaryTextColor(context),
            weight = WEIGHT_NORMAL
        )
    }

    fun getButtonStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_BODY,
            color = AppColors.White,
            weight = WEIGHT_BOLD
        )
    }

    fun getLargeNumberStyle(context: Context): TextStyle {
        return TextStyle(
            size = AppTheme.TEXT_SIZE_EXTRA_LARGE,
            color = AppTheme.getPrimaryTextColor(context),
            weight = WEIGHT_BOLD
        )
    }

    /**
     * Apply text style to TextView
     */
    fun applyTextStyle(textView: TextView, style: TextStyle) {
        textView.apply {
            textSize = style.size
            setTextColor(style.color)
            setTypeface(typeface, style.weight)
        }
    }

    /**
     * Create styled text with different colors
     */
    fun createStyledText(
        text: String,
        highlightText: String,
        normalColor: Int,
        highlightColor: Int
    ): SpannableString {
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf(highlightText)

        if (startIndex != -1) {
            val endIndex = startIndex + highlightText.length
            spannableString.setSpan(
                ForegroundColorSpan(highlightColor),
                startIndex,
                endIndex,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }

    /**
     * Create bold text span
     */
    fun createBoldText(text: String, boldText: String): SpannableString {
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf(boldText)

        if (startIndex != -1) {
            val endIndex = startIndex + boldText.length
            spannableString.setSpan(
                StyleSpan(WEIGHT_BOLD),
                startIndex,
                endIndex,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }

    /**
     * Helper functions for common text formatting
     */
    fun formatCurrency(amount: Long): String {
        return "Rp ${String.format("%,d", amount).replace(',', '.')}"
    }

    fun formatPercentage(value: Int): String {
        return "$value%"
    }

    fun formatTaskProgress(completed: Int, total: Int): String {
        return "$completed/$total"
    }

    fun formatStreakDays(days: Int): String {
        return if (days == 1) {
            "$days Hari"
        } else {
            "$days Hari"
        }
    }

    /**
     * Text appearances for different UI elements
     */
    object TextAppearances {
        const val CARD_TITLE = "card_title"
        const val CARD_SUBTITLE = "card_subtitle"
        const val CARD_VALUE = "card_value"
        const val CARD_LABEL = "card_label"
        const val TAB_TEXT = "tab_text"
        const val WELCOME_TITLE = "welcome_title"
        const val WELCOME_MESSAGE = "welcome_message"
    }

    /**
     * Line height multipliers
     */
    const val LINE_HEIGHT_TIGHT = 1.2f
    const val LINE_HEIGHT_NORMAL = 1.4f
    const val LINE_HEIGHT_RELAXED = 1.6f

    /**
     * Apply line height to TextView
     */
    fun applyLineHeight(textView: TextView, lineHeightMultiplier: Float) {
        textView.setLineSpacing(0f, lineHeightMultiplier)
    }
}