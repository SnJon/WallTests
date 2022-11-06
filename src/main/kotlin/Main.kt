import java.lang.RuntimeException

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

enum class Reasons {
    SPAM,
    CHILD_PORNOGRAPHIC,
    EXTREMISM,
    VIOLENCE,
    DRUG_PROPAGANDA,
    ADULT_MATERIAL,
    ABUSE,
    CALLS_FOR_SUICIDE
}

data class ReportComment(val id: Int, val ownerId: Int?, val commentId: Int?, val reason: Reasons)

data class Comment(
    var id: Int? = null,
    val fromId: Int? = null,
    val date: Int,
    val text: String = "empty comment",
    val replyToUser: Int? = null,
    val replyToComment: Int? = null,
    val attachments: Array<Attachment> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Comment

        if (id != other.id) return false
        if (fromId != other.fromId) return false
        if (date != other.date) return false
        if (text != other.text) return false
        if (replyToUser != other.replyToUser) return false
        if (replyToComment != other.replyToComment) return false
        if (!attachments.contentEquals(other.attachments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (fromId ?: 0)
        result = 31 * result + date
        result = 31 * result + text.hashCode()
        result = 31 * result + (replyToUser ?: 0)
        result = 31 * result + (replyToComment ?: 0)
        result = 31 * result + attachments.contentHashCode()
        return result
    }
}

class PostNotFoundException(message: String) : RuntimeException(message)
class CommentNotFoundException(message: String) : RuntimeException(message)

object WallService {
    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()
    private var reportsComment = emptyArray<ReportComment>()
    private var postIdCounter = 0
    private var commentIdCounter = 0
    private var reportCommentIdCounter = 0

    fun clear() {
        posts = emptyArray()
        postIdCounter = 0
        comments = emptyArray()
        commentIdCounter = 0
    }

    fun add(post: Post, repost: Post? = null): Post {
        if (repost == null) {
            post.id = postIdCounter + 1
            postIdCounter += 1
            posts += post
        } else {
            val (_, ownerId, _, _, _, postLikes) = post
            val newPost = repost.copy(
                id = postIdCounter + 1,
                replyOwnerId = ownerId,
                replyPostId = postIdCounter,
                likes = repost.likes + postLikes
            )
            postIdCounter += 1
            posts += newPost
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

    fun createComment(postId: Int, comment: Comment): Comment? {
        for (item in posts) {
            if (item.id == postId) {
                commentIdCounter += 1
                val newComment =
                    comment.copy(id = commentIdCounter, fromId = item.fromId, replyToUser = item.ownerId)
                comments += newComment
                return comments.last()
            }
        }
        return null
    }

    fun createReportComment(commentId: Int, reason: Reasons): ReportComment? {
        for (comment in comments) {
            if (comment.id == commentId) {
                reportCommentIdCounter += 1
                val reportComment = ReportComment(reportCommentIdCounter, comment.fromId, comment.id, reason)
                reportsComment += reportComment
                return reportsComment.last()
            }
        }
        return null
    }

    fun printPosts() {
        println(posts.contentToString())
    }

    fun printComments() {
        println(comments.contentToString())
    }

    fun printReportsComment() {
        println(reportsComment.contentToString())
    }
}

fun main() {
    val firstPost = Post(null, 11, 111, 30102022, "First post", 3)
    WallService.add(firstPost)
    val repost = Post(null, 22, 222, 31102022, "Repost", 5, postType = "repost")

    WallService.add(firstPost, repost)
    WallService.update(firstPost)

    val note = Note(1, 11, "Note", "Simple text", 1)
    val noteAttachment = NoteAttachment(note)

    val audio = Audio(1, 11, "Audio", "Simple audio", 152, 35)
    val audioAttachment = AudioAttachment(audio)

    var attachments = emptyArray<Attachment>()
    attachments += noteAttachment
    attachments += audioAttachment

    val postWithAttachments = Post(0, 44, 444, 5325325, "Test Post", 3, attachments = attachments)

    val postId = 2

    WallService.createComment(
        postId,
        Comment(date = 3112022, text = "New comment")
    ) ?: throw PostNotFoundException("No post with id: $postId")

    val commentId = 1
    WallService.createReportComment(commentId, Reasons.DRUG_PROPAGANDA)
        ?: throw CommentNotFoundException("No comment with id: $commentId")

    WallService.printPosts()
    WallService.printComments()
    WallService.printReportsComment()
}