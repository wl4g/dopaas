package com.wl4g.devops.components.tools.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A common annotation to declare that parameters and return values are to be
 * considered as non-nullable by default for a given package.
 *
 * <p>
 * Leverages JSR-305 meta-annotations to indicate nullability in Java to common
 * tools with JSR-305 support and used by Kotlin to infer nullability of common
 * API.
 *
 * <p>
 * Should be used at package level in association with {@link Nullable}
 * annotations at parameter and return value level.
 *
 * @see NonNullFields
 * @see Nullable
 * @see NonNull
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonNullApi {
}
