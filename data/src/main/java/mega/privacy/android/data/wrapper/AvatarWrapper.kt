package mega.privacy.android.data.wrapper

import android.graphics.Bitmap

/**
 * avatar wrapper
 */
interface AvatarWrapper {
    /**
     * get dominant color from bitmap
     */
    fun getDominantColor(bimap: Bitmap): Int

    /**
     * get specific avatar color
     *
     * @param typeColor type of color
     */
    fun getSpecificAvatarColor(typeColor: String): Int

    /**
     * Get the first letter in the [name]
     * @param name
     * @return First letter in the name
     */
    fun getFirstLetter(name: String): String
}