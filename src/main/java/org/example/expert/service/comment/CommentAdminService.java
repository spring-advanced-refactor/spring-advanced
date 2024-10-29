package org.example.expert.service.comment;

import lombok.RequiredArgsConstructor;
import org.example.expert.aop.auth.RequireAuthenticatedUser;
import org.example.expert.domain.comment.CommentRepository;
import org.example.expert.domain.user.dto.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentAdminService {

    private final CommentRepository commentRepository;

    @Transactional
    @RequireAuthenticatedUser
    public void deleteComment(AuthUser authUser, Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
