package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.service.PostCommentService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostCommentRepository postCommentRepository;
    @Mock
    PostRepository postRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostCommentService postCommentService;

    private User writer;
    private User writer2;
    private Post post;
    private PostComment postComment;
    private PostComment postComment2;
    private PostComment childPostComment;
    private PostComment childPostComment2;
    private PostComment childPostComment3;
    private PostComment childPostComment4;
    private Center center;

    @BeforeEach
    void setUp() {
        this.writer = User.of(
                "testUserId",
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.writer2 = User.of(
                "testUserId2",
                "test123@gmail.com",
                "1234567890",
                "test2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.center = Center.of(
                "center1",
                "testCenter",
                "testAddress",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge("자유 패키지", "330,000")),
                "charge img test",
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );

        this.post = Post.of(
                "testPostId",
                center,
                "testContent",
                writer,
                null,
                null
        );

        this.postComment = PostComment.of(
                "testCommentId",
                "testContent1",
                writer,
                post,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.childPostComment = PostComment.of(
                "testChildId1",
                "testChildContent1",
                writer,
                post,
                postComment,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.childPostComment2 = PostComment.of(
                "testChildId2",
                "testChildContent2",
                writer,
                post,
                postComment,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.postComment2 = PostComment.of(
                "testCommentId2",
                "testContent2",
                writer,
                post,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.childPostComment3 = PostComment.of(
                "testChildId3",
                "testChildContent3",
                writer,
                post,
                postComment2,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.childPostComment4 = PostComment.of(
                "testChildId4",
                "testChildContent4",
                writer,
                post,
                postComment,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                    "testContent1",
                    null
            );

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

            mockedPostComment.when(() -> PostComment.of(
                    "testContent1",
                    this.writer,
                    this.post,
                    null
            )).thenReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content")
                    .contains(false, "testContent1");
        }
    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testChildContent1", postComment.getId());

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

            mockedPostComment.when(() -> PostComment.of(
                    "testChildContent1",
                    this.writer,
                    this.post,
                    postComment
            )).thenReturn(this.childPostComment);

            given(this.postCommentRepository.save(this.childPostComment)).willReturn(this.childPostComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content", "parentCommentId")
                    .contains(false, "testChildContent1", "testCommentId");
        }
    }

    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        Page<PostComment> parents = new PageImpl<>(List.of(postComment, postComment2), pageable, 2);
        List<PostComment> children1 = List.of(childPostComment, childPostComment2, childPostComment4);
        List<PostComment> children2 = List.of(childPostComment3);

        given(this.postCommentRepository.findByPostAndParentCommentIsNullAndIsDeletedFalse(post, pageable)).willReturn(parents);
        given(this.postCommentRepository.findTop3ByParentCommentAndIsDeletedFalseOrderByCreatedAt(postComment)).willReturn(children1);
        given(this.postCommentRepository.findTop3ByParentCommentAndIsDeletedFalseOrderByCreatedAt(postComment2)).willReturn(children2);
        given(this.postCommentRepository.countAllByParentCommentAndIsDeletedFalse(postComment)).willReturn((long) children1.size());
        given(this.postCommentRepository.countAllByParentCommentAndIsDeletedFalse(postComment2)).willReturn((long) children2.size());

        // when
        Pagination<CommentFindResponseDto> commentFindResponseDto = this.postCommentService.findCommentsByPost("testUserId", "testPostId", pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(CommentFindResponseDto::getContent, CommentFindResponseDto::getCommentCount)
                .containsExactly(
                        tuple("testContent1", 3L),
                        tuple("testContent2", 1L)
                );
    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<PostComment> children = new PageImpl<>(List.of(childPostComment, childPostComment2), pageable, 2);

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findByIdAndIsDeletedFalse("testCommentId")).willReturn(Optional.of(postComment));
        given(this.postCommentRepository.findAllByParentCommentAndIsDeletedFalse(postComment, pageable)).willReturn(children);

        // when
        Pagination<ChildCommentResponseDto> commentFindResponseDto = this.postCommentService.findAllChildCommentsByParent("testUserId", "testCommentId", pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(ChildCommentResponseDto::getContent, ChildCommentResponseDto::getIsDeleted)
                .containsExactly(
                        tuple("testChildContent1", false),
                        tuple("testChildContent2", false)
                );
    }

    @Test
    @DisplayName("Success case for update comment")
    void successUpdateComment() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        given(this.postCommentRepository.save(postComment)).willReturn(postComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.updateComment("testUserId", "testCommentId", commentUpdateRequestDto);

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "content")
                .contains("testCommentId", "updateContent");
    }

    @Test
    @DisplayName("Failure case for update comment because update by other user")
    void failUpdateComment_Unauthorized() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(writer2));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.updateComment("testUserId2", "testCommentId", commentUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for delete comment")
    void successDeleteComment() {
        // given
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findById("testChildId1")).willReturn(Optional.of(childPostComment));

        given(this.postCommentRepository.save(childPostComment)).willReturn(childPostComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.deleteComment("testUserId", "testChildId1");

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "isDeleted")
                .contains("testChildId1", true);
    }

    @Test
    @DisplayName("Failure case for delete comment because delete by other user")
    void failDeleteComment_Unauthorized() {
        // given
        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(writer2));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.deleteComment("testUserId2", "testCommentId")
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}
