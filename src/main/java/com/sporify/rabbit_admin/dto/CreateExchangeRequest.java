package com.sporify.rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateExchangeRequest(
		@NotBlank(message = "name es obligatorio") String name,
		@NotBlank(message = "type es obligatorio (direct, topic, fanout, headers)") String type,
		Boolean durable,
		Boolean autoDelete) {
}
