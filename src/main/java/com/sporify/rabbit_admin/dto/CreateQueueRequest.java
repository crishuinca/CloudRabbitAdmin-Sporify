package com.sporify.rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateQueueRequest(
		@NotBlank(message = "name es obligatorio") String name,
		Boolean durable,
		Boolean exclusive,
		Boolean autoDelete) {
}
