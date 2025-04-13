package com.onyell.annotations


/* * This annotation is used to tag classes with metadata.
 * It can be used to provide additional information about the class, such as its name and description.
 *
 * @property name The name of the tag.
 * @property description A brief description of the tag.
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tag(
    val name: String = "",
    val description: String = ""
)