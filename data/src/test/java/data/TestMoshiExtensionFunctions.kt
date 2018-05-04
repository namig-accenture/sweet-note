package data

import com.fernandocejas.arrow.optional.Optional
import com.squareup.moshi.Moshi
import data.extensions.fromJsonAsList
import data.extensions.fromJsonAsObject
import data.extensions.toJson
import domain.model.UserModel
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class TestMoshiExtensionFunctions : KoinTest {
    private val moshi: Moshi by inject()

    @Before
    fun setUp() {
        startKoin(dataModule)
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun serializeUserModelSucceed() {
        val userModel = UserModel(1, "a@b.com", "abc", "1234")
        val json = userModel.toJson(moshi)
        val userModel2 = json.fromJsonAsObject<UserModel>(moshi)
        assertThat("Assertion Failed", userModel2.isPresent && userModel == userModel2.get())
    }

    @Test
    fun deserializeWithNullString() {
        val json: String? = null
        val userModel: Optional<UserModel> = json.fromJsonAsObject(moshi)
        val list: Optional<List<UserModel>> = json.fromJsonAsList(moshi)
        assertThat("Assertion Failed", !userModel.isPresent && !list.isPresent)
    }

    @Test
    fun serializeListOfUserModuleIsSucceed() {
        val list = listOf(
                UserModel(1, "a@b.com", "abc", "1234"),
                UserModel(2, "a@b.com", "abc", "1234"),
                UserModel(3, "a@b.com", "abc", "1234"),
                UserModel(4, "a@b.com", "abc", "1234"),
                UserModel(5, "a@b.com", "abc", "1234")
        )
        val json = list.toJson(moshi)
        val list2 = json.fromJsonAsList<UserModel>(moshi)
        assertThat("Assertion Failed", list2.isPresent && list.first() == list2.get().first())
    }
}