package com.jianyue.lightning.boot.starter.util.mongo.query;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;
import org.springframework.data.mongodb.InvalidMongoDbApiUsageException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author ZPM
 * @date 2022/1/5
 * @des 简单封装Criteria
 * (non-Javadoc)
 * @see org.springframework.data.mongodb.core.query.Criteria
 */
public final class MongoQueryExt {

	private Criteria criteria;

	public MongoQueryExt(Criteria criteria) {
		this.criteria = criteria;
	}

	public MongoQueryExt() {
		this(new Criteria());
	}

	public static MongoQueryExt builder() {
		return new MongoQueryExt();
	}

	public MongoQueryExt eq(String key, Object value) {
		criteria.and(key).is(value);
		return this;
	}

	/**
	 * 满足条件才继续
	 */
	public MongoQueryExt eq(String key, Object value, boolean ifTrue) {
		if (ifTrue) {
			criteria.and(key).is(value);
		}
		return this;
	}

	/**
	 * 满足条件才继续
	 */
	public <T> MongoQueryExt eq(FieldGetter<T, ?> fieldGetter, Object value, boolean ifTrue) {
		return this.eq(fieldGetter.getFieldName(), value, ifTrue);
	}

	/**
	 * 满足条件才继续
	 */
	public <T> MongoQueryExt eq(FieldGetter<T, ?> fieldGetter, Object value) {
		return this.eq(fieldGetter.getFieldName(), value);
	}


	/**
	 * 满足条件才继续
	 */
	public <T> MongoQueryExt eq(String key, T value, Predicate<T> predicate) {
		if (predicate.test(value)) {
			criteria.and(key).is(value);
		}
		return this;
	}

	/**
	 * 满足条件才继续
	 */
	public MongoQueryExt eq(String key, Supplier<Object> supplier, boolean ifTrue) {
		if (ifTrue) {
			criteria.and(key).is(supplier.get());
		}
		return this;
	}

	public MongoQueryExt exists(String key, boolean isExist) {
		criteria.and(key).exists(isExist);
		return this;
	}

	public MongoQueryExt ne(String key, @Nullable Object o) {
		criteria.and(key).ne(o);
		return this;
	}

	public <T> MongoQueryExt ne(FieldGetter<T, ?> fieldGetter, @Nullable Object o) {
		return this.ne(fieldGetter.getFieldName(), o);
	}


	public MongoQueryExt lt(String key, Object o) {
		criteria.and(key).lt(o);
		return this;
	}


	public MongoQueryExt lte(String key, Object o) {
		criteria.and(key).lte(o);
		return this;
	}

	public <T> MongoQueryExt lt(FieldGetter<T, ?> fieldGetter, Object o) {
		return this.lt(fieldGetter.getFieldName(), o);
	}


	public <T> MongoQueryExt lte(FieldGetter<T, ?> fieldGetter, Object o) {
		return this.lte(fieldGetter.getFieldName(), o);
	}


	/**
	 * ?<=x<=?
	 *
	 * @param key
	 * @param pre
	 * @param after
	 * @return
	 */
	public MongoQueryExt between(String key, Object pre, Object after) {
		return between(key, pre, after, true);
	}

	/**
	 * ?<=x<=?
	 * 根据条件来判断是否加入该值
	 *
	 * @param key
	 * @param pre
	 * @param after
	 * @return
	 */
	public MongoQueryExt betweenAutoChaneOtherCondition(String key, Object pre, Object after, boolean contains) {
		if (Objects.isNull(pre) && Objects.isNull(after)) {
			return this;
		}
		if (Objects.nonNull(pre) && Objects.nonNull(after)) {
			return between(key, pre, after, contains);
		}
		//大于pre即可
		if (Objects.nonNull(pre)) {
			return contains ? gte(key, pre) : gt(key, pre);
		} else {
			return contains ? lte(key, after) : lt(key, after);
		}
	}


	/**
	 * ?<=x<=?
	 * 根据条件来判断是否加入该值
	 *
	 * @param key
	 * @param pre
	 * @param after
	 * @return
	 */
	public MongoQueryExt betweenAutoChaneOtherCondition(String key, Object pre, Object after) {
		return betweenAutoChaneOtherCondition(key, pre, after, true);
	}

	/**
	 * between
	 *
	 * @param key
	 * @param pre
	 * @param after
	 * @param contains 是否包含等于符号
	 * @return
	 */
	public MongoQueryExt between(String key, Object pre, Object after, boolean contains) {
		if (contains) {
			criteria.and(key).gte(pre).lte(after);
		} else {
			criteria.and(key).gt(pre).lt(after);
		}
		return this;
	}


	/**
	 * between
	 *
	 * @param pre
	 * @param after
	 * @return
	 */
	public <T> MongoQueryExt between(FieldGetter<T, ?> fieldGetter, Object pre, Object after) {
		return this.between(fieldGetter.getFieldName(), pre, after);
	}

	/**
	 * between
	 *
	 * @param pre
	 * @param after
	 * @param contains 是否包含等于符号
	 * @return
	 */
	public <T> MongoQueryExt between(FieldGetter<T, ?> fieldGetter, Object pre, Object after, boolean contains) {
		return this.between(fieldGetter.getFieldName(), pre, after, contains);
	}

	public MongoQueryExt gt(String key, Object o) {
		criteria.and(key).gt(o);
		return this;
	}

	public <T> MongoQueryExt gt(FieldGetter<T, ?> fieldGetter, Object o) {
		criteria.and(fieldGetter.getFieldName()).gt(o);
		return this;
	}

	public MongoQueryExt regex(String key, String value) {
		criteria.and(key).regex(value);
		return this;
	}

	public MongoQueryExt regex(String key, String value, boolean ifTrue) {
		if (ifTrue) {
			criteria.and(key).regex(value);
		}
		return this;
	}

	public <T> MongoQueryExt regex(FieldGetter<T, ?> fieldGetter, String value) {
		return this.regex(fieldGetter.getFieldName(), value);
	}

	public MongoQueryExt regex(String key, String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			criteria.and(key).regex(value);
		}
		return this;
	}

	public <T> MongoQueryExt regex(FieldGetter<T, ?> fieldGetter, String value, Predicate<String> predicate) {
		return this.regex(fieldGetter.getFieldName(), value, predicate);
	}

	public Query build() {
		return new Query(this.criteria);
	}

	/**
	 * @param key    key
	 * @param value  value
	 * @param option 操作,正则
	 * @return query
	 * @see <a href="https://docs.mongodb.com/manual/reference/operator/query/regex/">MongoDB Query operator: $regex</a>
	 */
	public MongoQueryExt regex(String key, String value, @Nullable String option) {
		criteria.and(key).regex(value, option);
		return this;
	}

	public MongoQueryExt regex(String key, Pattern value) {
		criteria.and(key).regex(value);
		return this;
	}

	public Criteria getCriteria() {
		return this.criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public MongoQueryExt gte(String key, Object o) {
		criteria.and(key).gte(o);
		return this;
	}

	public <T> MongoQueryExt gte(FieldGetter<T, ?> fieldGetter, Object o) {
		return this.gte(fieldGetter.getFieldName(), o);
	}

	public MongoQueryExt in(String key, Object... o) {
		if (o.length > 1 && o[1] instanceof Collection) {
			throw new InvalidMongoDbApiUsageException(
					"You can only pass in one argument of type " + o[1].getClass().getName());
		}
		criteria.and(key).in(Arrays.asList(o));
		return this;
	}

	public MongoQueryExt in(String key, List<?> list) {
		if (CollectionUtils.isEmpty(list) || list.get(0) instanceof Collection) {
			throw new InvalidMongoDbApiUsageException(
					"You can only pass in one argument of type " + list.get(0).getClass().getName());
		}
		criteria.and(key).in(list);
		return this;
	}


	public <T> MongoQueryExt in(FieldGetter<T, ?> fieldGetter, Object... o) {
		if (o.length > 1 && o[1] instanceof Collection) {
			throw new InvalidMongoDbApiUsageException(
					"You can only pass in one argument of type " + o[1].getClass().getName());
		}
		criteria.and(fieldGetter.getFieldName()).in(Arrays.asList(o));
		return this;
	}

	public <T> MongoQueryExt in(FieldGetter<T, ?> fieldGetter, List<?> list) {
		if (CollectionUtils.isEmpty(list) || list.get(0) instanceof Collection) {
			throw new InvalidMongoDbApiUsageException(
					"You can only pass in one argument of type " + list.get(0).getClass().getName());
		}
		criteria.and(fieldGetter.getFieldName()).in(list);
		return this;
	}


	public MongoQueryExt in(String key, Collection<?> c) {
		criteria.and(key).in(c);
		return this;
	}


	public MongoQueryExt nin(String key, Object... o) {
		return nin(key, Arrays.asList(o));
	}


	public MongoQueryExt nin(String key, Collection<?> o) {
		criteria.and(key).nin(o);
		return this;
	}

	public <T> MongoQueryExt nin(FieldGetter<T, ?> fieldGetter, Object... o) {
		return nin(fieldGetter.getFieldName(), Arrays.asList(o));
	}


	public <T> MongoQueryExt nin(FieldGetter<T, ?> fieldGetter, Collection<?> o) {
		criteria.and(fieldGetter.getFieldName()).nin(o);
		return this;
	}

	public MongoQueryExt or(Criteria... orCriteria) {
		criteria.orOperator(orCriteria);
		return this;
	}

	public MongoQueryExt and(Criteria... orCriteria) {
		criteria.andOperator(orCriteria);
		return this;
	}

	/**
	 * 只针对java不针对kotlin
	 *
	 * @param <T>
	 * @param <R>
	 */
	interface FieldGetter<T, R> extends Function<T, R>, Serializable {
		String GET = "get";
		String IS = "is";

		@SneakyThrows
		default String getFieldName() {
			Method method = ReflectUtil.getMethodByName(this.getClass(), "writeReplace");
			method.setAccessible(true);
			SerializedLambda serializedLambda = (SerializedLambda) method.invoke(this);
			String methodName = serializedLambda.getImplMethodName();
			if (methodName.startsWith(GET)) {
				methodName = methodName.substring(3);
			} else if (methodName.startsWith(IS)) {
				methodName = methodName.substring(2);
			}
			// 首字母变小写
			return CharSequenceUtil.lowerFirst(methodName);
		}

	}
}
