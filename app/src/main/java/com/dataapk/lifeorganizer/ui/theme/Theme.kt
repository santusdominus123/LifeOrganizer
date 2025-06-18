package com.dataapk.lifeorganizer.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat

/**
 * Theme utilities for Life Organizer app
 * Provides consistent styling across the app
 */
object AppTheme {

    /**
     * Card corner radius in dp
     */
    const val CARD_CORNER_RADIUS = 16f
    const val SMALL_CARD_CORNER_RADIUS = 12f

    /**
     * Standard margins and paddings in dp
     */
    const val MARGIN_SMALL = 8
    const val MARGIN_MEDIUM = 16
    const val MARGIN_LARGE = 24
    const val MARGIN_EXTRA_LARGE = 32

    const val PADDING_SMALL = 8
    const val PADDING_MEDIUM = 12
    const val PADDING_LARGE = 16
    const val PADDING_EXTRA_LARGE = 24

    /**
     * Text sizes in sp
     */
    const val TEXT_SIZE_CAPTION = 12f
    const val TEXT_SIZE_BODY = 14f
    const val TEXT_SIZE_SUBTITLE = 16f
    const val TEXT_SIZE_TITLE = 18f
    const val TEXT_SIZE_HEADING = 20f
    const val TEXT_SIZE_LARGE = 24f
    const val TEXT_SIZE_EXTRA_LARGE = 32f

    /**
     * Elevation values in dp
     */
    const val ELEVATION_CARD = 2f
    const val ELEVATION_BUTTON = 4f
    const val ELEVATION_FAB = 8f

    /**
     * Create a circular background drawable
     */
    fun createCircleDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    /**
     * Create a rounded rectangle drawable
     */
    fun createRoundedDrawable(color: Int, cornerRadius: Float = CARD_CORNER_RADIUS): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            setCornerRadius(cornerRadius)
        }
    }

    /**
     * Create a rounded rectangle drawable with stroke
     */
    fun createRoundedDrawableWithStroke(
        backgroundColor: Int,
        strokeColor: Int,
        strokeWidth: Int,
        cornerRadius: Float = CARD_CORNER_RADIUS
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            setStroke(strokeWidth, strokeColor)
            setCornerRadius(cornerRadius)
        }
    }

    /**
     * Check if the current theme is dark mode
     */
    fun isDarkTheme(context: Context): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Get appropriate text color based on theme
     */
    fun getPrimaryTextColor(context: Context): Int {
        return if (isDarkTheme(context)) {
            AppColors.White
        } else {
            AppColors.Gray900
        }
    }

    /**
     * Get appropriate secondary text color based on theme
     */
    fun getSecondaryTextColor(context: Context): Int {
        return if (isDarkTheme(context)) {
            AppColors.Gray400
        } else {
            AppColors.Gray600
        }
    }

    /**
     * Get appropriate background color based on theme
     */
    fun getBackgroundColor(context: Context): Int {
        return if (isDarkTheme(context)) {
            AppColors.Gray900
        } else {
            AppColors.BackgroundPrimary
        }
    }

    /**
     * Get appropriate card background color based on theme
     */
    fun getCardBackgroundColor(context: Context): Int {
        return if (isDarkTheme(context)) {
            AppColors.Gray800
        } else {
            AppColors.BackgroundCard
        }
    }

    /**
     * Apply elevation to a view
     */
    fun applyElevation(view: View, elevation: Float) {
        view.elevation = elevation
    }

    /**
     * Convert dp to pixels
     */
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    /**
     * Convert sp to pixels
     */
    fun spToPx(context: Context, sp: Float): Float {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return sp * scaledDensity
    }
}