package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewStatusRequest {

    @NotNull(message = "Trạng thái kiểm duyệt không được để trống")
    private ReviewStatus status;

    @Size(max = 500, message = "Lý do kiểm duyệt tối đa 500 ký tự")
    private String rejectionReason;
}
