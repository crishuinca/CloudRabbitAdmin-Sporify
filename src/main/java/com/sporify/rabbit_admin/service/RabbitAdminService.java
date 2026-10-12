package com.sporify.rabbit_admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Encapsula creación/eliminación de colas, exchanges y bindings .
 * Los controladores solo llaman a estos métodos de alto nivel.
 */
@Service
public class RabbitAdminService {

	private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

	private final RabbitAdmin rabbitAdmin;

	public RabbitAdminService(RabbitAdmin rabbitAdmin) {
		this.rabbitAdmin = rabbitAdmin;
	}

	public void createQueue(String name, boolean durable, boolean exclusive, boolean autoDelete) {
		requireName(name, "queue");
		Queue queue = new Queue(name.trim(), durable, exclusive, autoDelete);
		rabbitAdmin.declareQueue(queue);
		log.info("Queue creada name={} durable={} exclusive={} autoDelete={}", name, durable, exclusive, autoDelete);
	}

	public void deleteQueue(String name) {
		requireName(name, "queue");
		boolean removed = Boolean.TRUE.equals(rabbitAdmin.deleteQueue(name.trim()));
		if (!removed) {
			throw new IllegalArgumentException("No se pudo eliminar la cola (no existe o no está vacía): " + name);
		}
		log.info("Queue eliminada name={}", name);
	}

	public void createExchange(String name, String type, boolean durable, boolean autoDelete) {
		requireName(name, "exchange");
		String t = type == null ? "" : type.trim().toLowerCase();
		Exchange exchange = switch (t) {
			case "direct" -> new DirectExchange(name.trim(), durable, autoDelete);
			case "topic" -> new TopicExchange(name.trim(), durable, autoDelete);
			case "fanout" -> new FanoutExchange(name.trim(), durable, autoDelete);
			case "headers" -> new HeadersExchange(name.trim(), durable, autoDelete);
			default -> throw new IllegalArgumentException(
					"Tipo de exchange inválido: '" + type + "'. Use direct, topic, fanout o headers");
		};
		rabbitAdmin.declareExchange(exchange);
		log.info("Exchange creado name={} type={} durable={} autoDelete={}", name, t, durable, autoDelete);
	}

	public void deleteExchange(String name) {
		requireName(name, "exchange");
		boolean removed = rabbitAdmin.deleteExchange(name.trim());
		if (!removed) {
			throw new IllegalArgumentException("No se pudo eliminar el exchange (no existe): " + name);
		}
		log.info("Exchange eliminado name={}", name);
	}

	public void createBinding(
			String queueName,
			String exchangeName,
			String routingKey,
			String exchangeType) {
		requireName(queueName, "queue");
		requireName(exchangeName, "exchange");
		String key = routingKey == null ? "" : routingKey.trim();
		String t = exchangeType == null ? "direct" : exchangeType.trim().toLowerCase();

		Queue queue = new Queue(queueName.trim());
		Binding binding = switch (t) {
			case "direct" -> BindingBuilder.bind(queue).to(new DirectExchange(exchangeName.trim())).with(key);
			case "topic" -> BindingBuilder.bind(queue).to(new TopicExchange(exchangeName.trim())).with(key);
			case "fanout" -> BindingBuilder.bind(queue).to(new FanoutExchange(exchangeName.trim()));
			default -> throw new IllegalArgumentException(
					"Tipo de exchange para binding inválido: '" + exchangeType
							+ "'. Use direct, topic o fanout");
		};
		rabbitAdmin.declareBinding(binding);
		log.info(
				"Binding creado queue={} exchange={} routingKey={} type={}",
				queueName,
				exchangeName,
				key,
				t);
	}

	public void deleteBinding(
			String queueName,
			String exchangeName,
			String routingKey,
			String exchangeType) {
		requireName(queueName, "queue");
		requireName(exchangeName, "exchange");
		String key = routingKey == null ? "" : routingKey.trim();
		String t = exchangeType == null ? "direct" : exchangeType.trim().toLowerCase();

		Queue queue = new Queue(queueName.trim());
		Binding binding = switch (t) {
			case "direct" -> BindingBuilder.bind(queue).to(new DirectExchange(exchangeName.trim())).with(key);
			case "topic" -> BindingBuilder.bind(queue).to(new TopicExchange(exchangeName.trim())).with(key);
			case "fanout" -> BindingBuilder.bind(queue).to(new FanoutExchange(exchangeName.trim()));
			default -> throw new IllegalArgumentException(
					"Tipo de exchange para binding inválido: '" + exchangeType
							+ "'. Use direct, topic o fanout");
		};
		rabbitAdmin.removeBinding(binding);
		log.info(
				"Binding eliminado queue={} exchange={} routingKey={} type={}",
				queueName,
				exchangeName,
				key,
				t);
	}

	private static void requireName(String name, String kind) {
		if (!StringUtils.hasText(name) || !StringUtils.hasText(name.trim())) {
			throw new IllegalArgumentException("El nombre de " + kind + " no puede estar vacío");
		}
	}
}
