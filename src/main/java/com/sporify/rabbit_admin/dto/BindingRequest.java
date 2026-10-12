package com.sporify.rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;

public record BindingRequest(
		@NotBlank(message = "queueName es obligatorio") String queueName,
		@NotBlank(message = "exchangeName es obligatorio") String exchangeName,
		String routingKey,
		String exchangeType) {
}
