package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.common.validator.ClimbingHistorySize;
import coLaon.ClaonBack.common.validator.PostContentsSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto {
    @ClimbingHistorySize(message = "1개 이상 10개 이하의 등반 기록을 입력 가능합니다.")
    private List<ClimbingHistoryRequestDto> climbingHistories;
    private String content;
    @PostContentsSize(message = "이미지 혹은 동영상 1개 이상 10개 이하로 업로드해야 합니다.")
    private List<PostContentsDto> contentsList;
}