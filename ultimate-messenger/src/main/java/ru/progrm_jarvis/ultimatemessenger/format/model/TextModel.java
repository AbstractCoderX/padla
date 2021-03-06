package ru.progrm_jarvis.ultimatemessenger.format.model;

import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

/**
 * Parsed model of a dynamic text.
 *
 * @param <T> type of object according to which the text model is formatted
 */
@FunctionalInterface
public interface TextModel<T> {

    /**
     * {@code 0} wrapped in {@link OptionalInt}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") OptionalInt OPTIONAL_ZERO = OptionalInt.of(0);

    /**
     * Gets the text formatted for the given target.
     *
     * @param target object according to which the text models gets formatted,
     * if the model is not {@link #isDynamic() dynamic} then it should return the same result for any {@code target}
     * including {@code null}
     * @return text formatted for the given target
     *
     * @throws NullPointerException if the target is {@code null} but this text model is {@link #isDynamic() dynamic}
     */
    @NotNull String getText(T target);

    /**
     * Retrieves whether this {@link TextModel text model} is dynamic.
     *
     * @return {@code true} if this {@link TextModel text model} may produce different values
     * and {@code false} if it produces equal values for all calls to {@link #getText(Object)}
     */
    @Contract(pure = true)
    default boolean isDynamic() {
        return true;
    }

    /**
     * Gets the minimal length of the value returned by {@link #getText(Object)}.
     *
     * @return minimal possible length of the value returned by {@link #getText(Object)} being empty if it is undefined
     */
    @NotNull default OptionalInt getMinLength() {
        return OptionalInt.empty();
    }

    /**
     * Gets the maximal length of the value returned by {@link #getText(Object)}.
     *
     * @return maximal possible length of the value returned by {@link #getText(Object)} being empty if it is undefined
     */
    @NotNull default OptionalInt getMaxLength() {
        return OptionalInt.empty();
    }

    /**
     * Returns an empty static {@link TextModel text model}.
     *
     * @param <T> type of object according to which the text model is formatted
     * @return singleton of an empty static {@link TextModel text model}
     */
    @SuppressWarnings("unchecked")
    @NotNull static <T> TextModel<T> empty() {
        return EmptyTextModel.INSTANCE;
    }

    /**
     * Empty static {@link TextModel text model}.
     */
    final class EmptyTextModel implements TextModel {

        /**
         * Singleton instance of this {@link TextModel text model}
         */
        private static EmptyTextModel INSTANCE = new EmptyTextModel();

        @Override
        @Contract(pure = true)
        @NotNull public String getText(@Nullable final Object target) {
            return ""; // thanks to JVM magic this is always the same object (got using LDC)
        }

        @Override
        @Contract(pure = true)
        @NotNull public OptionalInt getMinLength() {
            return OPTIONAL_ZERO;
        }

        @Override
        @Contract(pure = true)
        @NotNull public OptionalInt getMaxLength() {
            return OPTIONAL_ZERO;
        }

        @Override
        @Contract(pure = true)
        public boolean isDynamic() {
            return false;
        }

        @Override
        @Contract(pure = true)
        public boolean equals(@Nullable final Object object) {
            if (object == this) return true;
            if (object instanceof TextModel) {
                val textModel = (TextModel<?>) object;
                return !textModel.isDynamic() && textModel.hashCode() == 0 && textModel.getText(null).isEmpty();
            }
            return false;
        }

        @Override
        @Contract(pure = true)
        // usual hashcode for empty values
        public int hashCode() {
            return 0;
        }

        @Override
        @Contract(pure = true)
        public String toString() {
            return "Empty TextModel";
        }
    }
}
