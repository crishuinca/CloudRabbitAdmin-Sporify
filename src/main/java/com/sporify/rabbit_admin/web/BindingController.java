package com.sporify.rabbit_admin.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sporify.rabbit_admin.dto.AdminActionResponse;
import com.sporify.rabbit_admin.dto.BindingRequest;
import com.sporify.rabbit_admin.service.RabbitAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bindings")
public class BindingController {

	private final RabbitAdminService rabbitAdminService;

	public BindingController(RabbitAdminService rabbitAdminService) {
		this.rabbitAdminService = rabbitAdminService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public AdminActionResponse create(@Valid @RequestBody BindingRequest request) {
		rabbitAdminService.createBinding(
				request.queueName(),
				request.exchangeName(),
				request.routingKey(),
				request.exchangeType());
		return new AdminActionResponse(
				"created",
				"binding",
				request.queueName().trim() + " -> " + request.exchangeName().trim());
	}

	@DeleteMapping
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public AdminActionResponse delete(@Valid @RequestBody BindingRequest request) {
		rabbitAdminService.deleteBinding(
				request.queueName(),
				request.exchangeName(),
				request.routingKey(),
				request.exchangeType());
		return new AdminActionResponse(
				"deleted",
				"binding",
				request.queueName().trim() + " -> " + request.exchangeName().trim());
	}
}
