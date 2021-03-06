package ru.progrm_jarvis.javacommons.invoke;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.javacommons.annotation.DontOverrideEqualsAndHashCode;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Simple implementation of {@link InvokeFactory}.
 *
 * @param <F> type of functional interface implemented
 * @param <T> type of target value
 */
@ToString
@DontOverrideEqualsAndHashCode("Class is more of an utility then a POJO")
@NoArgsConstructor(access = AccessLevel.PROTECTED, staticName = "newInstance")
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleInvokeFactory<F, T> implements InvokeFactory<F, T> {

    /**
     * Used lookup factory
     */
    LookupFactory lookupFactory;
    /**
     * Implemented functional interface
     */
    MethodType functionalInterface,
    /**
     * Signature of the functional interface's functional method
     */
    functionalMethodSignature;
    /**
     * Name of the functional interface's functional method
     */
    String functionalMethodName;
    /**
     * Factory creating implementation method handle using the given lookup
     */
    Function<MethodHandles.Lookup, MethodHandle> methodHandleCreator;
    /**
     * Target class containing the implementation method
     */
    Class<? extends T> targetClass;
    /**
     * Target
     */
    @Nullable Object target;

    @Override
    public InvokeFactory<F, T> using(@NonNull final LookupFactory lookupFactory) {
        this.lookupFactory = lookupFactory;

        return this;
    }

    @Override
    public InvokeFactory<F, T> implementing(@NonNull final MethodType functionalInterface,
                                            @NonNull final String functionalMethodName,
                                            @NonNull final MethodType functionalMethodSignature) {
        checkArgument(functionalInterface.parameterCount() == 0, "functionalInterface should have no parameters");

        this.functionalInterface = functionalInterface;
        this.functionalMethodName = functionalMethodName;
        this.functionalMethodSignature = functionalMethodSignature;

        return this;
    }

    @Override
    public InvokeFactory<F, T> via(@NonNull final Class<? extends T> targetClass,
                                   @NonNull final Function<MethodHandles.Lookup, MethodHandle> methodHandleCreator) {
        this.targetClass = targetClass;
        this.methodHandleCreator = methodHandleCreator;

        return this;
    }

    @Override
    public InvokeFactory<F, T> boundTo(@Nullable final T target) {
        this.target = target;

        return this;
    }

    @Override
    public InvokeFactory<F, T> unbound() {
        this.target = null;

        return this;
    }

    @Override
    public F create() throws Throwable {
        checkState(lookupFactory != null, "lookupFactory is not set");
        checkState(functionalInterface != null, "functionalInterface is not set");
        checkState(functionalMethodSignature != null, "functionalMethodSignature is not set");
        checkState(functionalMethodName != null, "functionalMethodName is not set");
        checkState(methodHandleCreator != null, "methodHandleCreator is not set");

        val lookup = lookupFactory.create(targetClass);
        val methodHandle = methodHandleCreator.apply(lookup);
        val bound = target != null;

        val targetMethodHandle = LambdaMetafactory.metafactory(
                lookup, functionalMethodName,
                bound ? functionalInterface.appendParameterTypes(target.getClass()) : functionalInterface,
                functionalMethodSignature, methodHandle,
                bound ? methodHandle.type().dropParameterTypes(0, 1) : methodHandle.type()
        ).getTarget();

        //noinspection unchecked
        return (F) (bound ? targetMethodHandle.invoke(target) : targetMethodHandle.invoke());
    }
}
