package org.example.expert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.valid.Auth;
import org.example.expert.config.jwt.JwtUtil;
import org.example.expert.domain.user.dto.AuthUser;
import org.example.expert.service.ManagerService;
import org.example.expert.dto.manager.request.ManagerSaveRequest;
import org.example.expert.dto.manager.response.ManagerResponse;
import org.example.expert.dto.manager.response.ManagerSaveResponse;
import org.example.expert.util.api.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/todos/{todoId}/managers")
    public ResponseEntity<ApiResult<ManagerSaveResponse>> saveManager(
            @Auth AuthUser authUser,
            @PathVariable(value = "todoId") Long todoId,
            @Valid @RequestBody ManagerSaveRequest managerSaveRequest
    ) {
        return ResponseEntity.ok(ApiResult.success(managerService.saveManager(authUser, todoId, managerSaveRequest)));
    }

    @GetMapping("/todos/{todoId}/managers")
    public ResponseEntity<ApiResult<List<ManagerResponse>>> getMembers(@PathVariable(value = "todoId") Long todoId) {
        return ResponseEntity.ok(ApiResult.success(managerService.getManagers(todoId)));
    }

    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    public ResponseEntity<ApiResult<String>> deleteManager(
            @Auth AuthUser authUser,
            @PathVariable(value = "todoId") Long todoId,
            @PathVariable(value = "managerId") Long managerId
    ) {
        managerService.deleteManager(authUser, todoId, managerId);
        return ResponseEntity.ok(ApiResult.success("Manager has been successfully deleted"));
    }
}
