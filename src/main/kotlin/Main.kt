data class Post(
    var id: Int,
    val date: Int,
    val createdBy: Int,
    val text: String,
    val postType: String = "post",
    val canPin: Boolean = true,
    val canDelete: Boolean = true,
    val canEdit: Boolean = true,
    val markedAsAds: Boolean = false,
    val likes: Int
)

object WallService {

    private var posts = emptyArray<Post>()
    private var postId = 0


    fun clear() {
        posts = emptyArray()
        postId = 0
    }

    fun add(post: Post): Post {
        post.id = postId + 1
        postId += 1
        posts += post
        return posts.last()
    }

    fun update(post: Post): Boolean {
        for ((index, item) in posts.withIndex()) {
            if (post.id == item.id) {
                posts[index] = post.copy(id = item.id, date = item.date, createdBy = 33, text = "change post", canDelete = false, likes = 0)
                return true
            }
        }
        return false
    }

    fun printPosts() {
        println(posts.contentToString())
    }
}

fun main() {
    val firstPost = Post(0, 22102022, 11, "First post", likes = 0)
    val secondPost = Post(0, 22102022, 22, "Second post", likes = 0)

    WallService.add(firstPost)
    WallService.add(secondPost)

    WallService.update(firstPost)
    WallService.printPosts()
}