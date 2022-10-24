import org.junit.Test
import org.junit.Before
import kotlin.test.assertEquals

class WallServiceTest {

    @Test
    fun add() {
        val testPost = Post(0, 22102022, 11, "Test post", likes = 0)
        WallService.add(testPost)
        val result = WallService.postId
        assertEquals(1, result)
    }

    @Before
    fun clearBeforeUpdateExisting() {
        WallService.clear()
    }

    @Test
    fun updateExisting() {
        val testPost = Post(0, 22102022, 11, "Test post", likes = 0)
        WallService.add(testPost)
        val result = WallService.update(testPost)
        assertEquals(true, result)
    }

    @Before
    fun clearBeforeNonexistent() {
        WallService.clear()
    }

    @Test
    fun updateNonexistent() {
        val testPost = Post(0, 22102022, 11, "Test post", likes = 0)
        val result = WallService.update(testPost)
        assertEquals(false, result)
    }
}