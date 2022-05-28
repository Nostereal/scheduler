package com.scheduler.shared.models

import kotlinx.serialization.Serializable

@Serializable
open class ErrorWithMessage(val message: String)