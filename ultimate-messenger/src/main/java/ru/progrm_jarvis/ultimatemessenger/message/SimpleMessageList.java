package ru.progrm_jarvis.ultimatemessenger.message;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Simple {@link MessageList} implementation which delegates its {@link List list operations}
 * to its inner {@link List list} {@link #messages}
 *
 * @param <R> type of message receivers
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) // allow extension
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SimpleMessageList<R> implements MessageList<R> {

    /**
     * {@link List} of messages to whom this message list delegates its methods
     */
    @Delegate @NonNull List<Message<R>> messages;

    /**
     * Creates a new {@link SimpleMessageList} using the given list of messages.
     *
     * @param messages list of messages to use for this message list's backend
     * @param <R> type of messages receivers
     * @return creates simple message list
     *
     * @implNote the created  message list will use the given list as its internal storage of messages
     * and so any changes to it from outside may also affect this message list
     */
    public static <R> SimpleMessageList<R> from(@NonNull final List<Message<R>> messages) {
        return new SimpleMessageList<>(messages);
    }
}
