package com.scheduler.shared.serializer

import com.scheduler.shared.models.TypedResult
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class TypedResultSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<TypedResult<T>> {

    override fun deserialize(decoder: Decoder): TypedResult<T> {
        throw UnsupportedOperationException("Deserialization for type TypedResult is not supported")
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(TypedResult::class.java.canonicalName) {
        element<String>("status")
        element("result", dataSerializer.descriptor, isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: TypedResult<T>) {
        when (value) {
            is TypedResult.Ok -> encoder.encodeSerializableValue(TypedResult.Ok.serializer(dataSerializer), value)
            is TypedResult.BadRequest -> encoder.encodeSerializableValue(TypedResult.BadRequest.serializer(), value)
            is TypedResult.InternalError -> encoder.encodeSerializableValue(
                TypedResult.InternalError.serializer(),
                value
            )
        }
    }

}
