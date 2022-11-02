interface Attachment {
    val type: String
}

data class Video(
    val vid: Int,
    val ownerId: Int,
    val title: String,
    val description: String,
    val duration: Int,
    val player: String
)

data class VideoAttachment(val video: Video) : Attachment {
    override val type = "video"
}

data class Photo(val id: Int, val albumId: Int, val ownerId: Int, val userId: Int, val text: String)
data class PhotoAttachment(val photo: Photo) : Attachment {
    override val type = "photo"
}

data class Audio(
    val aid: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val duration: Int,
    val album: Int
)

data class AudioAttachment(val audio: Audio) : Attachment {
    override val type = "audio"
}

data class Doc(val id: Int, val ownerId: Int, val title: String, val size: Int, val type: Int)

data class DocAttachment(val doc: Doc) : Attachment {
    override val type = "doc"
}

data class Note(val id: Int, val ownerId: Int, val title: String, val text: String, val comments: Int)

data class NoteAttachment(val note: Note) : Attachment {
    override val type = "note"
}



data class Post(
    var id: Int?,
    val ownerId: Int,
    val fromId: Int,
    val date: Int,
    val text: String?,
    val likes: Int,
    val replyPostId: Int? = null,
    val replyOwnerId: Int? = null,
    val createdBy: Int? = null,
    val friendsOnly: Boolean = false,
    val postType: String = "post",
    val canPin: Boolean = true,
    val canDelete: Boolean = true,
    val canEdit: Boolean = true,
    val markedAsAds: Boolean = false,
    val isFavorite: Boolean = false,
    val attachments: Array<Attachment> = emptyArray()
)

object WallService {

    private var posts = emptyArray<Post>()
    private var postId = 0

    fun clear() {
        posts = emptyArray()
        postId = 0
    }

    fun add(post: Post, repost: Post? = null): Post {
        if (repost == null) {
            post.id = postId + 1
            postId += 1
            posts += post
        } else {
            val (_, ownerId, _, _, _, postLikes) = post
            val testPost = repost.copy(
                id = postId + 1,
                replyOwnerId = ownerId,
                replyPostId = postId,
                likes = repost.likes + postLikes
            )
            postId += 1
            posts += testPost
        }
        return posts.last()
    }

    fun update(post: Post): Boolean {
        for ((index, item) in posts.withIndex()) {
            if (post.id == item.id) {
                posts[index] = post.copy(
                    id = item.id,
                    date = item.date,
                    createdBy = 33,
                    text = "change post",
                    canDelete = false,
                    likes = 0
                )
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
    val firstPost = Post(0, 11, 111, 30102022, "First post", 3)
    WallService.add(firstPost)
    val repost = Post(0, 22, 222, 31102022, "Repost", 5, postType = "repost")

    WallService.add(firstPost, repost)

    WallService.update(firstPost)
    WallService.printPosts()

    val note = Note(1,11, "Note", "Simple text", 1)
    val noteAttachment = NoteAttachment(note)

    val audio = Audio(1, 11, "Audio", "Simple audio", 152, 35)
    val audioAttachment = AudioAttachment(audio)

    var attachments = emptyArray<Attachment>()
    attachments += noteAttachment
    attachments += audioAttachment

    val postWithAttachments = Post(0, 44, 444, 5325325, "Test Post", 3, attachments = attachments)
    println(postWithAttachments)

}