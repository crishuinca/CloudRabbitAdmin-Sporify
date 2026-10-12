package com.sporify.rabbit_admin.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.rabbit_admin.dto.AdminActionResponse;
import com.sporify.rabbit_admin.dto.CreateQueueRequest;
import com.sporify.rabbit_admin.service.RabbitAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

	private final RabbitAdminService rabbitAdminService;

	public QueueController(RabbitAdminService rabbitAdminService) {
		this.rabbitAdminService = rabbitAdminService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public AdminActionResponse create(@Valid @RequestBody CreateQueueRequest request) {
		boolean durable = request.durable() == null || request.durable();
		boolean exclusive = Boolean.TRUE.equals(request.exclusive());
		boolean autoDelete = Boolean.TRUE.equals(request.autoDelete());
		rabbitAdminService.createQueue(request.name(), durable, exclusive, autoDelete);
		return new AdminActionResponse("created", "queue", request.name().trim());
	}

	@DeleteMapping("/{name}")
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public AdminActionResponse delete(@PathVariable String name) {
		rabbitAdminService.deleteQueue(name);
		return new AdminActionResponse("deleted", "queue", name);
	}
}
