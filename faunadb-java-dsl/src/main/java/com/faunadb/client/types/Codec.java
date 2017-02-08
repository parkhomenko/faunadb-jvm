package com.faunadb.client.types;

import com.faunadb.client.types.Value.*;
import com.faunadb.client.types.time.HighPrecisionTime;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import static java.lang.String.format;

/**
 * Codec is a function that represents an attempt to coerce a {@link Value} to a concrete type.
 * There are pre-defined codecs for each FaunaDB primitive types: {@link Codec#VALUE}, {@link Codec#STRING},
 * {@link Codec#LONG}, {@link Codec#DOUBLE}, {@link Codec#DATE}, {@link Codec#TIME}, {@link Codec#REF},
 * {@link Codec#SET_REF}, {@link Codec#ARRAY}, and {@link Codec#OBJECT}.
 * <p>
 * Codecs return a {@link Result} of the coercion attempt. If it fails to coerce, {@link Result}
 * will contain an error message.
 * <p>
 * It is also possible to create customized codecs to handle complex objects:
 * <pre>{@code
 * class Person {
 *   static final Codec<Person> PERSON = new Codec<Person>() {
 *     public Result<Person> apply(Value value) {
 *       return Result.success(new Person(
 *         value.at("data", "firstName").to(Codec.STRING),
 *         value.at("data", "lastName").to(Codec.STRING)
 *       ));
 *     }
 *   }
 *
 *   static Person fromValue(Value value) {
 *     return value.to(PERSON);
 *   }
 *
 *   final String firstName, lastName;
 *
 *   Person(String firstName, String lastName) {
 *     this.firstName = firstName;
 *     this.lastName = lastName;
 *   }
 * }
 * }</pre>
 *
 * @param <T> desired resulting type
 * @see Result
 */
public interface Codec<T> extends Function<Value, Result<T>> {

  /**
   * Coerce a {@link Value} to itself or fail value is an instance of {@link NullV}.
   */
  Codec<Value> VALUE = new Codec<Value>() {
    @Override
    public Result<Value> apply(Value value) {
      if (value == NullV.NULL)
        return Result.fail("Value is null");

      return Result.success(value);
    }
  };

  /**
   * Coerce a {@link Value} to an instance of {@link RefV}
   */
  Codec<RefV> REF = Cast.mapTo(RefV.class, Functions.<RefV>identity());

  /**
   * Coerce a {@link Value} to an instance of {@link SetRefV}
   */
  Codec<SetRefV> SET_REF = Cast.mapTo(SetRefV.class, Functions.<SetRefV>identity());

  /**
   * Coerce a {@link Value} to a {@link Long}
   */
  Codec<Long> LONG = Cast.mapTo(LongV.class, Cast.<LongV, Long>scalarValue());

  /**
   * Coerce a {@link Value} to an {@link Instant}
   */
  Codec<Instant> TIME = Cast.mapTo(TimeV.class, new Function<TimeV, Instant>() {
    @Override
    public Instant apply(TimeV time) {
      return time.truncated();
    }
  });

  /**
   * Coerce a {@link Value} to an {@link HighPrecisionTime}
   */
  Codec<HighPrecisionTime> HP_TIME = Cast.mapTo(TimeV.class, Cast.<TimeV, HighPrecisionTime>scalarValue());

  /**
   * Coerce a {@link Value} to a {@link String}
   */
  Codec<String> STRING = Cast.mapTo(StringV.class, Cast.<StringV, String>scalarValue());

  /**
   * Coerce a {@link Value} to a {@link Double}
   */
  Codec<Double> DOUBLE = Cast.mapTo(DoubleV.class, Cast.<DoubleV, Double>scalarValue());

  /**
   * Coerce a {@link Value} to a {@link Boolean}
   */
  Codec<Boolean> BOOLEAN = Cast.mapTo(BooleanV.class, Cast.<BooleanV, Boolean>scalarValue());

  /**
   * Coerce a {@link Value} to a {@link LocalDate}
   */
  Codec<LocalDate> DATE = Cast.mapTo(DateV.class, Cast.<DateV, LocalDate>scalarValue());

  /**
   * Coerce a {@link Value} to an {@link ImmutableList} of {@link Value}
   */
  Codec<ImmutableList<Value>> ARRAY = Cast.mapTo(ArrayV.class, new Function<ArrayV, ImmutableList<Value>>() {
    @Override
    public ImmutableList<Value> apply(ArrayV input) {
      return input.values;
    }
  });

  /**
   * Coerce a {@link Value} to an {@link ImmutableMap} of {@link Value}
   */
  Codec<ImmutableMap<String, Value>> OBJECT = Cast.mapTo(ObjectV.class, new Function<ObjectV, ImmutableMap<String, Value>>() {
    @Override
    public ImmutableMap<String, Value> apply(ObjectV input) {
      return input.values;
    }
  });

  /**
   * Coerce a {@link Value} to an array of bytes
   */
  Codec<byte[]> BYTES = Cast.mapTo(BytesV.class, Cast.<BytesV, byte[]>scalarValue());
}

final class Cast {

  static <V extends Value, O> Codec<O> mapTo(final Class<V> clazz, final Function<V, O> extractValue) {
    return new Codec<O>() {
      @Override
      public Result<O> apply(Value input) {
        return cast(clazz, input).map(extractValue);
      }
    };
  }

  private static <T> Result<T> cast(Class<T> clazz, Value value) {
    if (value.getClass() == clazz)
      return Result.success(clazz.cast(value));

    return Result.fail(
      format("Can not convert %s to %s", value.getClass().getSimpleName(), clazz.getSimpleName()));
  }

  static <T extends ScalarValue<R>, R> Function<T, R> scalarValue() {
    return new Function<T, R>() {
      @Override
      public R apply(T input) {
        return input.value;
      }
    };
  }

}