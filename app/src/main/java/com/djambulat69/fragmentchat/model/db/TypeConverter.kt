package com.djambulat69.fragmentchat.model.db

abstract class TypeConverter<T, F> {

    abstract fun convert(value: T): F

    abstract fun retrieve(value: F): T

}
