import org.junit.Test
import org.junit.Before
import kotlin.test.assertEquals

class WallServiceTest {

    @Test
    fun add() {
        val testPost = Post(null, 22102022, 11, 29102022, "Test post", 5)
        val (result) = WallService.add(testPost)
        assertEquals(1, result)
    }

    @Before
    fun clearBeforeUpdateExisting() {
        WallService.clear()
    }

    @Test
    fun updateExisting() {
        val testPost = Post(null, 22102022, 11, 29102022, "Test post", 5)
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
        val testPost = Post(null, 22102022, 11, 29102022, "Test post", 5)
        val result = WallService.update(testPost)
        assertEquals(false, result)
    }

    @Test
    fun createComment() {
        val testPost = Post(null, 11, 111, 30102022, "Test post", 3)
        WallService.add(testPost)
        val id = 1
        val comment = Comment(id, 111, 6112022, "New comment", 11)
        val result = WallService.createComment(id, Comment(date = 6112022, text = "New comment"))
        assertEquals(comment, result)
    }

    @Test(expected = PostNotFoundException::class)
    fun shouldThrowPost() {
        val testPost = Post(null, 11, 111, 30102022, "Test post", 3)
        WallService.add(testPost)
        val postId = 2
        WallService.createComment(
            postId,
            Comment(date = 6112022, text = "New comment")
        ) ?: throw PostNotFoundException("No post with id: $postId")
    }

    @Test
    fun createReportComment() {
        val testPost = Post(null, 11, 111, 30102022, "Test post", 3)
        WallService.add(testPost)
        val postId = 1
        WallService.createComment(postId, Comment(postId, 111, 6112022, "New comment", 11))
        val commentId = 1
        val reportComment = ReportComment(commentId, 111, 1, Reasons.ABUSE)
        val result = WallService.createReportComment(commentId, Reasons.ABUSE)
        assertEquals(reportComment, result)
    }

    @Before
    fun clearBeforeShouldThrowComment() {
        WallService.clear()
    }

    @Test(expected = CommentNotFoundException::class)
    fun shouldThrowComment() {
        val testPost = Post(null, 11, 111, 30102022, "Test post", 3)
        WallService.add(testPost)
        val postId = 1
        WallService.createComment(
            postId,
            Comment(date = 6112022, text = "New comment")
        )
        val commentId = 2
        WallService.createReportComment(commentId, Reasons.DRUG_PROPAGANDA)
            ?: throw CommentNotFoundException("No comment with id: $commentId")
    }
}