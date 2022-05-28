package com.scheduler.utils

import com.scheduler.plugins.serverJsonConfiguration
import com.scheduler.profile.models.ProfileInfo
import com.scheduler.shared.models.ImageUrl
import com.scheduler.shared.models.TypedResult
import kotlinx.serialization.encodeToString
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class TypedResultParsingTest {

    @Test
    fun `ok status is present - serialized successfully`() {
        val profileInfo =
            ProfileInfo(avatar = ImageUrl("test/url"), fullName = "Tema temavich", dorm = "4", livingRoom = "127")
        val okResult: TypedResult<ProfileInfo> = TypedResult.Ok(result = profileInfo)

        val json = serverJsonConfiguration
        val resultJson = json.encodeToString(okResult)

        assertThat(resultJson).isEqualTo(okResultJson)
    }

    @Language("JSON")
    private val okResultJson = """
    {
        "result": {
            "avatar": "test/url",
            "fullName": "Tema temavich",
            "dorm": "4",
            "livingRoom": "127"
        },
        "status": "ok"
    }
    """.trimIndent()

    @Test
    fun `bad-request status is present - serialized successfully`() {
        val badRequest = TypedResult.BadRequest("Token is missing")

        val json = serverJsonConfiguration
        val resultJson = json.encodeToString(badRequest)

        assertThat(resultJson).isEqualTo(badRequestResultJson)
    }

    @Language("JSON")
    private val badRequestResultJson = """
    {
        "result": {
            "message": "Token is missing"
        },
        "status": "bad-request"
    }
    """.trimIndent()

    @Test
    fun `internal-error status is present - serialized successfully`() {
        val badRequest = TypedResult.InternalError("Something went wrong")

        val json = serverJsonConfiguration
        val resultJson = json.encodeToString(badRequest)

        assertThat(resultJson).isEqualTo(internalErrorResultJson)
    }

    @Language("JSON")
    private val internalErrorResultJson = """
    {
        "result": {
            "message": "Something went wrong"
        },
        "status": "internal-error"
    }
    """.trimIndent()

}