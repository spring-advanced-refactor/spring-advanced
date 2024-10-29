package org.example.expert.dto.comment.response;

import lombok.Getter;
import org.example.expert.domain.comment.Comment;
import org.example.expert.dto.user.response.UserResponse;

@Getter
public class CommentSaveResponse {

    private final Long id;
    private final String contents;
    private final UserResponse user;

    public CommentSaveResponse(Comment comment, UserResponse user) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.user = user;
    }
}
