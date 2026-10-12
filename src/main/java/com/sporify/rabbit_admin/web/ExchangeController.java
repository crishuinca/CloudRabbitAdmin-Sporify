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
import com.sporify.rabbit_admin.dto.CreateExchangeRequest;
import com.sporify.rabbit_admin.service.RabbitAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

	private final RabbitAdminService rabbitAdminService;

	public ExchangeController(RabbitAdminService rabbitAdminService) {
		this.rabbitAdminService = rabbitAdminService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public AdminActionResponse create(@Valid @RequestBody CreateExchangeRequest request) {
		boolean durable = request.durable() == null || request.durable();
		boolean autoDelete = Boolean.TRUE.equals(request.autoDelete());
		rabbitAdminService.createExchange(request.name(), request.type(), durable, autoDelete);
		return new AdminActionResponse("created", "exchange", request.name().trim());
	}

	@DeleteMapping("/{name}")
	@PreAuthorize("hasAuthority('SCOPE_Music.Write')")
	public AdminActionResponse delete(@PathVariable String name) {
		rabbitAdminService.deleteExchange(name);
		return new AdminActionResponse("deleted", "exchange", name);
	}
}
